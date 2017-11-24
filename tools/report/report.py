#!/usr/bin/env python
# -*- mode: Python; fill-column: 75; comment-column: 70; coding: utf-8 ;-*-
#
# Copyright (C) 2017 Inera AB (http://www.inera.se)
#
# This file is part of statistik (https://github.com/sklintyg/statistik).
#
# statistik is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# statistik is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.
#
# -------------------------------------------------------------------------------
import getopt
import re
import subprocess
import sys
from datetime import datetime, timedelta
from lib.rules import *
from lib.vg import *
from lib.wideline import Wideline
from lib.intyg import Intyg
from lib.group import Group
from lib.ranges import *

THRESHOLD = 5
DBUSER = 'root'
DBNAME = 'statistik_ip20'
DBHOST = 'mysql.ip20.nordicmedtest.se'
STANDARD_INTERVALL = '2016-01-01:2016-12-31'
DUMP_FILE = 'statistik_dump.txt'


def usage():
    # -f <filename>    Namn på resultatfil från databas
    print '''Användning: python report.py -i <start-intervall:slut-intervall> [konfiguration] [filter] <gruppering>
Konfiguration:
    -p               Validera personid
    -w <lösenord>    Databasens lösenord

    -b <hostnamn>    Databasens host
    -q <databasnamn> Databasens namn
    --dump           Skapa en databasdump
Filter:
    -i <start:end>   Intervall för sjukfall, måste vara enligt följande:
                     YYYY-MM-DD:YYYY-MM-DD
    -m <month-range> Månads intervall, format: YYYY-MM:YYYY-MM
    -v <hsaid>       Vårdgivare ID
    -c <hsaid>       Vårdenhet ID
    -8 <lanskod>     Länskod (2siffror)
    -t               Använd tröskelvärde (%s) för vårdgivare and man/kvinna
    -T               Använd tvärsnitt istället för tidsserie. För vissa
                     rapporter sker urvalet annorlunda för tvärsnitt, tex
                     för diagnos.

Gruppera på:
    -s               Sjukfall, totalt
    -d               Diagnos (Enskilt diagnoskapitel)
    -a               ÅldersGrupp
    -g               Sjukskrivningsgrad
    -l               Sjukskrivningslängd
    -7               Differentierat intygande
    -n               Län
    -k               Läkarbefattning
    -K               Läkarålder och kön
    -L <enhet>       Läkare för en specifik enhet
    -E <enhet>       Sjukfall och specifik enhet
    -e               Sjukfall och enheter
    -N               Sjukfall längre än 90 dagar
    -9 <enhet>       Sjukfall längre än 90 dagar för enhet

    ''' % THRESHOLD

def get_enheter(filename): 
    """Return all vårdenheter, key is vårdgivare+vårdenhet"""
    fd = open(filename)
    # Skip first line
    fd.readline()
    res = {}
    for line in fd:
        data = line.split('\t')
        # Vardgivare + Enhet
        key = data[3].strip() + data[1].strip()
        if key not in res:
            res[key] = data[5].strip()
        else:
            assert(0 == 1)
            enhet = res[key]
            assert(enhet == data[5])
    return res


def get_caregiver_id_from_user(host, password, dbname):
    sql_cmd = 'SELECT * FROM Landsting;'
    ssh_cmd = 'mysql --user %s --password=%s %s -e "%s"' % (DBUSER, password, dbname, sql_cmd)
    cmd = ['ssh', '-q', 'nmt@%s' % host, ssh_cmd]
    print sql_cmd
    try:
        output = subprocess.check_output(cmd)
        vgs = output.decode('iso8859-1').splitlines()

        print '\n'.join(vgs)
        print 'Ange id på det landsting du är intresserad av:'
        vg_id = input()

        if vg_id <= 0 or vg_id > (len(vgs)-1):
            print 'Det finns inget landsting med det id:t'
            sys.exit()

        vg = vgs[vg_id].split('\t')
        print 'Du har valt ' + vg[1]
        return vg[2].encode('utf-8')

    except Exception as exc:
        print exc
        raise

def get_data(file_name, start, end, caregiver, careunit,lanskod, host, password, dbname):
    ''' Extracts data from the database and stores it to file_name '''

    # Start nedan är antalet dagar efter 2000 som är en tidräkning som intygsstatistik använder sig av i databasen.
    # Villkoret "correlationid not in ..." är viktigt för det plockar bort intyg som blivit annulerade.

    sql_cmd = 'mysql --user %s --password=%s %s -e "select * from wideline w where ((startdatum >= %s and startdatum <= %s) or '  % (DBUSER,password,dbname, start, end)
    sql_cmd += '(slutdatum >= %s and slutdatum <= %s) or (startdatum < %s and slutdatum > %s)) and sjukskrivningsgrad > 0 ' % (start, end, start, end)
    sql_cmd += 'and correlationid not in (select correlationid from wideline where intygtyp=1) '

    if caregiver:
        print 'INFO: Hämtar data för vårdgivare %s' % caregiver
        sql_cmd += "and w.vardgivareid='%s'" % caregiver

    elif careunit:
        print "INFO: Hämtar data för vårdenhet %s" % careunit
        sql_cmd += "and w.enhet='%s'" % careunit

    elif lanskod:
        print "INFO: Hämtar data för län %s" % lanskod
        sql_cmd += "and lkf LIKE '%s%%'" % lanskod

    sql_cmd += '"'

    cmd = ['ssh', '-q', 'nmt@%s' % host, sql_cmd]
    print sql_cmd
    try:
        output = subprocess.check_output(cmd)

        with open(file_name, 'w') as aFile:
            aFile.write(output)

    except Exception as exc:
        print exc
        raise



def main(argv):
    threshold = False
    check_personid = False
    alpha = datetime(2000, 1, 1)
    now = datetime.now()
    end_limit = datetime(now.year + 5, now.month, now.day) - alpha
    start_limit = datetime(2010, 1, 1) - alpha
    interval = None
    rule = None
    dump_file = DUMP_FILE
    caregiver = None
    careunit = None
    lanskod = None
    db_host = None
    db_password = None
    db_name = None
    make_dbdump = None
    internalbefattning = True
    ranges = None
    tvarsnitt = False

    opts, args = getopt.getopt(argv, "tdapgl7ni:ekKL:hsE:Nj:9:v:8:c:w:b:q:m:Tf:",['dump', 'nointernalbefattning'])
    for opt, arg in opts:
        if opt == '-t':
            threshold = True
        elif opt == '-m':
            interval = arg
            ranges = month_range(arg)
        elif opt == '-e':
            rule = RuleSjukfallEnheter(tvarsnitt)
        elif opt == '-E':
            rule = RuleSjukfallEnhet(arg.strip())
        elif opt == '-s':
            rule = RuleSjukfall()
        elif opt == '-d':
            rule = RuleDiagnos(tvarsnitt)
        elif opt == '-a':
            rule = RuleAlder()
        elif opt == '-p':
            check_personid = True
        elif opt == '-g':
            rule = RuleSjukgrad()
        elif opt == '-l':
            rule = RuleSjuklangd()
        elif opt == '-7':
            rule = RuleDifferentierat()
        elif opt == '-n':
            rule = RuleLan()
        elif opt == '-N':
            assert(interval != None)
            periods = interval.split(':')
            data = periods[0].split('-')
            # 18 is the number of months forward from the start month
            rule = RuleSjuklangd90(int(data[0]),int(data[1]),18)
        elif opt == '-9':
            assert(interval != None)
            periods = interval.split(':')
            data = periods[0].split('-')
            # 18 is the number of months forward from the start month
            rule = RuleSjuklangd90(int(data[0]),int(data[1]),18, arg.strip())
        elif opt == '-j':
            rule = RuleJamfor(arg.strip())
        elif opt == '-i':
            interval = arg
            ranges = interval_range(interval)
        elif opt == '-k':
            rule = RuleLakarbefattning(internalbefattning, tvarsnitt)
        elif opt == '-K':
            rule = RuleLakaralderKon(tvarsnitt)
        elif opt == '-L':
            rule = RuleLakare(arg.strip())
        elif opt == '-h':
            return usage()
        elif opt == '--dump':
            make_dbdump = 1
        elif opt == '-f':
            dump_file = arg
        elif opt == '-q':
            db_name = arg
        elif opt == '-b':
            db_host = arg
        elif opt == '-w':
            db_password = arg
        elif opt == '-v':
            caregiver = arg
        elif opt == '-8':
            lanskod = arg
        elif opt == '-c':
            careunit = arg
        elif opt == '--nointernalbefattning':
            internalbefattning = False
        elif opt == '-T':
            tvarsnitt = True

    if not ranges:
        interval = STANDARD_INTERVALL
        print('Intervall saknas. Ange intervall på formen YYYY-MM-DD:YYYY-MM-DD  (%s)' % STANDARD_INTERVALL)
        given_interval = raw_input()
        if given_interval:
            ranges = ranges.interval_range(given_interval)

    if make_dbdump:
        if not db_host:
            db_host = DBHOST
            print('Ange databashost (%s)' % DBHOST)
            given_db_host = raw_input()
            if(given_db_host):
                db_host = given_db_host

        if not db_name:
            db_name = DBNAME
            print('Ange databasens namn (%s)' % DBNAME)
            given_db_name = raw_input()
            if(given_db_name):
                db_name = given_db_name

        if not db_password:
            print "Ange db-lösenord för användare %s:" % (DBUSER)
            db_password = raw_input()
            if not db_password:
                'Lösenord saknas!'
                return 

        # if not caregiver:
        #     if not careunit:
        #         if not lanskod:
        #             print "INFO: Vårdgivare, vårdenhet eller länskod ej angiven"
        #             caregiver = get_caregiver_id_from_user(db_host, db_password, db_name)
        
        get_data(dump_file, ranges.start, ranges.slut, caregiver, careunit,lanskod, db_host, db_password, db_name)
        return

    if rule is None:
        print "Grupperings-regel saknas"
        usage()
        return

    # Read the column names, must be first line
    with open(dump_file, 'rb') as f:
        wideline = Wideline(f.readline())
        # wideline = sys.stdin.readline()

        # Check that the right columns was included for this test
        rule.check(wideline)

        vgmap = {}
        for line in f:
            intyg = Intyg(line, wideline)

            # Check that slutdatum is not less than startdatum
            if intyg.slut < intyg.start:
                print "Ignorerar intyg: ", intyg.start, intyg.slut
                continue

            if not re.search('[\d]{12}', intyg.id):
                print "Ogiltigt patientid: ", intyg.id
                continue

            # Make a simple check of personid
            if check_personid:          
                year = int(intyg.id[0:4])
                month = int(intyg.id[4:6])
                day = int(intyg.id[6:8])
                if month == 0 or day == 0:
                    print "Ogiltigt personid: " + intyg.id
                    continue
                if month > 12 or day > 31 or year > now.year:
                    print "Ogiltigt personid2: " + intyg.id
                    continue

            # Start- eller slutdatum för sjukskrivningsperioden får inte vara mer än fem år fram i tiden.
            # Ingen idé att kolla startdatum för då skulle inte intyget kommit med i urvalet
            if intyg.slut > end_limit.days:
                print "Ignorerar intyg med ogiltigt slutdatum: ", intyg.slut
                continue

            # Startdatumet för sjukskrivningsperioden får inte vara före 2010-01-01
            if intyg.start < start_limit.days:
                print "Ignorerar intyg med ogiltigt startdatum: ", intyg.start
                continue

            if intyg.vardgivare in vgmap:
                vg = vgmap[intyg.vardgivare]
                vg.add(intyg)
            else:
                vg = VG(intyg.vardgivare)
                vg.add(intyg)

            vgmap[intyg.vardgivare] = vg

    # Apply the rule to all ranges
    for r in ranges.items():
        tot = 0
        # The aggregated result
        agg = Group()
        for vg in vgmap.itervalues():
            tot += vg.eval(Group(rule, threshold, agg, r.start, r.slut))

        print "Totalt: {0} - {1}".format(tot, r.name)

        agg.log()
        print "\n"

if __name__ == "__main__":
    main(sys.argv[1:])
