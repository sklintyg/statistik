#!/usr/bin/env python
# -*- mode: Python; fill-column: 75; comment-column: 70; coding: utf-8 ;-*-
#
# Copyright (C) 2016 Inera AB (http://www.inera.se)
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
from lib import *

THRESHOLD = 5
DBUSER = 'root'
DBNAME = 'statistik'

def usage():
    print '''Användning: python report.py -i <start-intervall:slut-intervall> [options] <rule>
options:
    -t              Använd tröskelvärde (%s) för vårdgivare and man/kvinna
    -i <start:end>  Intervall för sjukfall, måste vara enligt följande:
                       YYYY-MM-DD:YYYY-MM-DD
    -p              Validera personid
    -w <lösenord>   Databasens lösenord
    -f <filename>   Namn på resultatfil från databas
    -b <hostname>   Databasens host-name
    -q <database-name>   Databasens host-name
    -v <vårdgivar-hsa>  Vårdgivare ID
    -c <enhet-hsa>   Vårdenhet ID

rule:
    -s      Gruppera på sjukfall
    -d      Gruppera på diagnos (kapitel)
    -a      Gruppera på ålder
    -g      Gruppera på sjukskrivningsgrad
    -l      Gruppera på sjukskrivningslängd
    -n      Gruppera på län
    -k      Gruppera på läkarbefattning
    -K      Gruppera på läkarålder och kön
    -L <enhet>  Gruppera på läkare för en specifik enhet
    -E <enhet>  Gruppera på sjukfall och specifik enhet
    -e      Gruppera på sjukfall och enheter
    -N      Gruppera på sjukfall längre än 90 dagar
    -9 <enhet>  Group on sjukfall längre än 90 dagar för enhet
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


def get_data(file_name, start, end, caregiver, careunit, host, password, dbname):
    ''' Extracts data from the database and stores it to file_name '''

    # Start nedan är antalet dagar efter 2000 som är en tidräkning som statistiktjänsten använder sig av i databasen.
    # Villkoret "correlationid not in ..." är viktigt för det plockar bort intyg som blivit annulerade.

    if caregiver:
        print "INFO: Utför sökning med vårdgivare"
        sql_cmd = 'mysql --user %s --password=%s %s -e "select * from wideline w where ((startdatum >= %s and startdatum <= %s) or '  % (DBUSER,password,dbname, start, end)
        sql_cmd += '(slutdatum >= %s and slutdatum <= %s) or (startdatum < %s and slutdatum > %s)) and sjukskrivningsgrad > 0 ' % (start, end, start, end)
        sql_cmd += 'and correlationid not in (select correlationid from wideline where intygtyp=1) '
        sql_cmd += "and w.vardgivareid='%s'" % caregiver
        sql_cmd += '"'

    elif careunit:
        print "INFO: Utför sökning med vårdenhet"
        sql_cmd = 'mysql --user %s --password=%s %s -e "select * from wideline w where ((startdatum >= %s and startdatum <= %s) or '  % (DBUSER,password,dbname, start, end)
        sql_cmd += '(slutdatum >= %s and slutdatum <= %s) or (startdatum < %s and slutdatum > %s)) and sjukskrivningsgrad > 0 ' %  (start, end, start, end)
        sql_cmd += 'and correlationid not in (select correlationid from wideline where intygtyp=1) '
        sql_cmd += "and w.enhet='%s'" % careunit
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
    start_limit = datetime(2010,1,1) - alpha
    start_interval = None
    end_interval   = None
    interval = None
    rule = None
    result_file = None
    caregiver = None
    careunit = None
    db_host = None
    db_password = None
    db_name = DBNAME

    opts, args = getopt.getopt(argv,"tdapglni:ekKL:hsE:Nj:9:f:v:c:w:b:q:")
    for opt, arg in opts:
        if opt == '-t':
            threshold = True
        elif opt == '-e':
            rule = RuleSjukfallEnheter()
        elif opt == '-E':
            rule = RuleSjukfallEnhet(arg.strip())
        elif opt == '-s':
            rule = RuleSjukfall()
        elif opt == '-d':
            rule = RuleDiagnos()
        elif opt == '-a':
            rule = RuleAlder()
        elif opt == '-p':
            check_personid = True
        elif opt == '-g':
            rule = RuleSjukgrad()
        elif opt == '-l':
            rule = RuleSjuklangd()
        elif opt == '-n':
            rule = RuleLan()
        elif opt == '-N':
            assert(interval != None)
            data = interval[0].split('-')
            rule = RuleSjuklangd90(int(data[0]),int(data[1]),18)
        elif opt == '-9':
            assert(interval != None)
            data = interval[0].split('-')
            rule = RuleSjuklangd90(int(data[0]),int(data[1]),18, arg.strip())
        elif opt == '-j':
            rule = RuleJamfor(arg.strip())
        elif opt == '-i':
            interval = arg.split(':')
            assert(len(interval) == 2)
            start_interval = date2days(interval[0], alpha)
            end_interval   = date2days(interval[1], alpha)
        elif opt == '-k':
            rule = RuleLakarbefattning()
        elif opt == '-K':
            rule = RuleLakaralderKon()
        elif opt == '-L':
            rule = RuleLakare(arg.strip())
        elif opt == '-h':
            return usage()
        elif opt == '-f':
            result_file = arg
        elif opt == '-v':
            caregiver = arg
        elif opt == '-c':
            careunit = arg
        elif opt == '-w':
            db_password = arg
        elif opt == '-b':
            db_host = arg
        elif opt == '-q':
            print arg
            db_name = arg

    if start_interval is None or end_interval is None:
        print "Intervall saknas"
        usage()
        return

    if result_file:
        if not caregiver:
            print "INFO: Vårdgivare saknas"
            if not careunit:
                print "ERROR: Vårdgivare eller vårdenhet saknas"
                usage()
                return
        if not db_host:
            print "Ange db-host:"
            db_host = raw_input()
        if not db_password:
            print "Ange db-lösenord för användare %s:" % (DBUSER)
            db_password = raw_input()

        get_data(result_file, start_interval, end_interval, caregiver, careunit, db_host, db_password,db_name)
        return

    if rule is None:
        print "Grupperings-regel saknas"
        usage()
        return

    # Read the column names, must be first line
    wideline = Wideline(sys.stdin.readline())

    # Check that the right columns was included for this test
    rule.check(wideline)

    vgmap = {}
    for line in sys.stdin:
        intyg = Intyg(line, wideline)


		# Check that slutdatum is not less than startdatum
        if intyg.slut < intyg.start:
            print "disregarding intyg: ", intyg.start, intyg.slut
            continue

        if not re.search('[\d]{8}-[\d]{4}', intyg.id):
            print "Invalid patientid: ", intyg.id
            continue

        # Make a simple check of personid
        if check_personid:          
            year = int(intyg.id[0:4])
            month = int(intyg.id[4:6])
            day = int(intyg.id[6:8])
            if month == 0 or day == 0:
                print "Invalid personid: " + intyg.id
                continue
            if month > 12 or day > 31 or year > 2015:
                print "Invalid personid2: " + intyg.id
                continue

        # Start- eller slutdatum för sjukskrivningsperioden får inte vara mer än fem år fram i tiden.
        # Ingen idé att kolla startdatum för då skulle inte intyget kommit med i urvalet
        if intyg.slut > end_limit.days:
            print "Ignoring intyg with bad end date: ", intyg.slut
            continue

        # Startdatumet för sjukskrivningsperioden får inte vara före 2010-01-01
        if intyg.start < start_limit.days:
            print "Ignoring intyg with invalid start date: ", intyg.start
            continue

        if intyg.vardgivare in vgmap:
            vg = vgmap[intyg.vardgivare]
            vg.add(intyg)
        else:
            vg = VG(intyg.vardgivare)
            vg.add(intyg)

        vgmap[intyg.vardgivare] = vg

    tot = 0
    agg = Group()
    #print "Threshold {}".format(threshold)
    # Summarize all data from all vårdgivare
    for k,v in vgmap.items():
        tot += v.eval(Group(rule, threshold, agg, start_interval, end_interval))

    print 'Totalt: %d' % tot

    agg.log()

if __name__ == "__main__":
    main(sys.argv[1:])
