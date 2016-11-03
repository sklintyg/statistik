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

from datetime import datetime, timedelta
from interval import Interval

class Sjukfall:
    alpha_time = datetime(2000, 1, 1)

    def __init__(self, intyg):
        self.intyg = [intyg]
        self.start = intyg.start
        self.slut = intyg.slut

    def __str__(self):
        s = ''
        for i in self.intyg:
            s = "{0}:{1}".format(s,i)
        return i

    def sjukgrad(self, start, slut):
        """ Get the sjukgrad from the 'last' intyg
        """
        last = self.last_intyg(start, slut)
        return last.sjukgrad

    def last_intyg(self, start, slut):
        """ Get the last (latest) intyg
        """
        last = None
        for i in self.intyg:
            if i.valid(start, slut):
                if last is None:
                    last = i
                elif i.isnewer(last):
                    last = i
        assert(last != None)
        return last

    def kon(self):
        return self.intyg[0].kon

    def match_diagnos(self, diagnos, start, slut):
        for i in self.intyg:
            if i.valid(start, slut) and i.diagnoskategori == diagnos:
                return True
        return False

    def update(self, intyg):
        self.intyg.append(intyg)
        if intyg.start < self.start:
            self.start = intyg.start
        if intyg.slut > self.slut:
            self.slut = intyg.slut
        return True

    def befattningar(self, start, slut):
        """ Get all lakarbefattningar where intyg is in the period 
        """
        lakare = {}
        
        for i in self.intyg:
            if i.valid(start,slut):
                if i.lakareid not in lakare:
                    lakare[i.lakareid] = i
                else:
                    intyg = lakare[i.lakareid]
                    if i.isnewer(intyg):
                        lakare[i.lakareid] = i
        res = {}
        for k,v in lakare.items():
            l = []
            for b in v.befattning.split(','):
                l.append(b.strip())
            res[k] = l

        assert(len(res) > 0)

        return res

    def valid(self, start, slut):
        """ Check if this sjukfall is within the interval
        """
        if self.slut < start:
            return False
        if self.start > slut:
            return False

        return True

    def age(self, start, slut):
        """ Välj den högsta åldern från sjukfall, enligt K30. Notera
            att enligt K50 så väljs åldern utifrån hela sjukfallet
        """
        alder = None
        for i in self.intyg:
            #if i.valid(start, slut):
            if True:
                if alder is None:
                    alder = i.alder
                elif i.alder > alder:
                    alder = i.alder

        assert(alder != None)

        return alder

    def match(self, intyg):
        # Check if intyg belongs in this group
        for i in self.intyg:
            if intyg.start >= i.start - 6 and intyg.start <= i.slut + 6:
                return self.update(intyg)
            if intyg.slut >= i.start - 6 and intyg.slut <= i.slut + 6:
                return self.update(intyg)
            if intyg.start < i.start and intyg.slut > i.slut:
                return self.update(intyg)

        return False

    def diagnos(self, start,slut):
        last = self.last_intyg(start, slut)
        if last:
            return last.diagnos

        return None

    def diagnosis(self, start, slut):
        """ Get all diagnosis for the period """
        res = {}
        for i in self.intyg:
            if i.valid(start, slut):
                res[i.diagnos] = 1

        return res.keys()

    def get_intervals(self):
        """ Calculate number of sjukfall days for the given period
        """
        intyg = [ i for i in self.intyg ]
        intervals = []
        # Split the intyg in intervals
        while len(intyg) > 0:
            interval = Interval(intyg.pop())
            intervals.append(interval)
            match = True
            # Keep matching until no matches
            while match:
                match = False
                tmp = []
                for i in intyg:
                    if interval.match(i):
                        match = True
                    else:
                        tmp.append(i)
    
                intyg = tmp
        return intervals

    def get_enhet_intervals(self, enhet):
        """ Calculate number of sjukfall days for the given period
        """
        intyg = [ i for i in self.intyg if i.enhet == enhet ]
        #for i in intyg:
        #   print i.enhet
        intervals = []
        # Split the intyg in intervals
        while len(intyg) > 0:
            interval = Interval(intyg.pop())
            intervals.append(interval)
            match = True
            # Keep matching until no matches
            while match:
                match = False
                tmp = []
                for i in intyg:
                    if interval.match(i):
                        match = True
                    else:
                        tmp.append(i)
    
                intyg = tmp
        return intervals


    def days(self, start, slut):
        """ Enligt krav K50 ska hela sjukfallets längd räknas in
        """
        intervals = self.get_intervals()
        tot = 0
        for i in intervals:
            tot += i.days()
        return tot

    def days_in_period(self, periods, enhet=None):
        result = []
        index  = 0
        if enhet:           
            intervals = self.get_enhet_intervals(enhet)
        else:
            intervals = self.get_intervals()
        for period in periods:
            days = 0
            for interval in intervals:
                days += interval.days_in_period(period[0], period[1])
            result.append((days, period[2]))
        return result

    def lan(self, start, slut):
        lan = None
        intyg = None
        for i in self.intyg:
            if i.valid(start, slut):
                if lan is None or i.start > intyg.start:
                    lan = i.lan
                    intyg = i
                elif i.start == intyg.start and lan != i.lan:
                    print "Collision", lan,i.lan,i.enhet,intyg.enhet
                    #assert(i.enhet != intyg.enhet)
        assert(lan != None)
        return lan

    def lakare_alderkon(self, start, slut):
        """ Get all unqiue lakare
        """
        res = {}
        for i in self.intyg:
            if not i.valid(start, slut):
                continue
            
            if i.lakareid not in res:
                res[i.lakareid] = (i.lakarkon, i.lakaralder, i.start)
            else:
                tup = res[i.lakareid]
                starttime = tup[2]
                if i.start > starttime:
                    res[i.lakareid] = (i.lakarkon, i.lakaralder, i.start)
            
        return res

    def lakare(self, enhet, start, slut):
        res = []
        for i in self.intyg:    
            assert(len(i.lakareid) > 0)
            assert(i.lakareid != None)
            if i.valid(start, slut) and i.enhet == enhet:
                if i.lakareid not in res:
                    res.append(i.lakareid)
        return res

    def match_enhet(self, start, slut, enhet):
        for i in self.intyg:
            if i.valid(start, slut) and i.enhet == enhet:
                return True
        return False

    def enheter(self, start, slut):
        res = []
        for i in self.intyg:
            if i.valid(start, slut):
                if i.enhet not in res:
                    res.append(i.enhet) 
        return res

    def over_90(self, periods):
        result = []
        intervals = self.get_intervals()
        intervals.sort(key=lambda i : i.start)
        days = 0
        tot  = 0
        for interval in intervals:
            days = interval.days()
            test = 0
            prev = 0
            interval.intyg.sort(key=lambda i : i.start)
            # Make som basic checks that the periods add up
            for i in interval.intyg:
                test += i.days()
                assert (prev < i.start)
            assert(test == interval.days())

            for i in interval.intyg:
                tot += i.days()
                if tot > 90:
                    # Include the sjukfall from the intygs
                    # start to the sjukfalls slut
                    for p in periods:
                        if self.slut < p[0]:
                            continue
                        if i.intyg_start > p[1]:
                            continue

                        result.append(p[2])
                    return result
        return []   
