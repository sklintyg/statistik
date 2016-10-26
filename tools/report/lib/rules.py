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

class RuleLakare:
    def __init__(self, enhet):
        self.enhet = enhet

    def key(self, sjukfall, start, slut):
        return sjukfall.lakare(self.enhet, start, slut)

    def check(self, wideline):
        assert(wideline.index('lakareid') != None)
        

class RuleLakaralderKon:
    def key(self, sjukfall, start, slut):
        lakare = sjukfall.lakare_alderkon(start, slut)
        list = []
        for kon,tup in lakare.items():
            list.append(self.alder(tup[0], tup[1]))
        return list

    def alder(self, kon, age):
        prefix = ''
        if kon == 1:
            prefix = "Manlig läkare "
        elif kon == 2:
            prefix = "Kvinnlig läkare "
        else:
            return "Okänt kön and ålder"

        if age < 30:
            return prefix + "under 30 år"
        if age >= 30 and age <= 39:
            return prefix + "30-39"
        if age >= 40 and age <= 49:
            return prefix + "40-49"
        if age >= 50 and age <= 59:
            return prefix + "50-59"
        return prefix + "över 59"

    def check(self, wideline):
        assert(wideline.index('lakareid') != None)
        assert(wideline.index('lakaralder') != None)
        assert(wideline.index('lakarkon') != None)


class RuleLakarbefattning:
    def __init__(self, includeinternal=True):
        self.befattningar = { "201010" : "Överläkare", "201011" : "Distriktsläkare/Specialist allmänmedicin", "201012" : "Skolläkare", "201013" : "Företagsläkare", "202010" : "Specialistläkare", "203010" : "Läkare legitimerad, specialiseringstjänstgöring", "203090" : "Läkare legitimerad, annan", "204010" : "Läkare ej legitimerad, allmäntjänstgöring", "204090" : "Läkare ej legitimerad, annan" } 
        if includeinternal:
            self.befattningar["-1"] = "Ej läkarbefattning"
            self.befattningar["-2"] = "Okänd befattning"

    def key(self, sjukfall, start, slut):
        befattningar = sjukfall.befattningar(start, slut)
        list = []
        for lakare,befattning in befattningar.items():
            self.befattning(list, befattning)
        return list

    def befattning(self, list, key):
        """ Om läkaren har en läkarbefattning och en icke-läkarbefattning 
        ska icke-läkarbefattningen inte räknas med
        """
        ejlakare = False
        lakare = False
        i = 0
        for b in key:
            i = 1
            if b in self.befattningar:
                lakare = True
                list.append(self.befattningar[b])
            else:
                ejlakare = True
        assert(i == 1)
        if ejlakare and not lakare:
            list.append("Ej läkarbefattning")

    def check(self, wideline):
        assert(wideline.index('lakarbefattning') != None)
        assert(wideline.index('lakaralder') != None)

class RuleSjuklangd90:
    def __init__(self, year, month, months, enhet=None):
        self.enhet = enhet
        print "Enhet=",enhet
        self.periods = self.make_periods(year, month, months)

    def make_periods(self, year, month, months):
        alpha = datetime(2000, 1, 1)
        i = 0
        mlist = []
        prev = 0
        tag = "2000/1"
        while i < months + 1:
            i += 1
            if month > 12:
                year += 1
                month = 1
            beta  = datetime(year, month, 1)
            delta = beta - alpha
            mlist.append((prev, delta.days - 1,tag))
            tag = str(year) + '/' + str(month)
            prev = delta.days
            month += 1

        for tup in mlist:
            print alpha + timedelta(tup[0]),alpha + timedelta(tup[1])

        return mlist

    def key(self, sjukfall, start, slut):
        return sjukfall.over_90(self.periods)
        '''
        months = sjukfall.days_in_period(self.periods, self.enhet)
        num = months[0][0]
        result = []
        for i in months[1:]:
            num += i[0]
            assert(i[0] <= 31)
            if num > 90 and i[0] > 0:
                result.append(i[1])

        return result
        '''

    def check(self, wideline):
        pass

class RuleJamfor:
    def __init__(self, diagnos):
        self.diagnos = [diagnos]
        
        if diagnos.find('-') != -1:
            self.diagnos = []
            data = diagnos.split('-')
            self.diagnos.append(data[0])
            m = int(data[1])
            i = int(data[0][1:])
            d = data[0][0]
            print i,m,d
            i += 1
            while i < m:
                if i < 10:
                    self.diagnos.append(d + '0' + str(i))
                else:
                    self.diagnos.append(d + str(i))
                i += 1
            print self.diagnos
                

    def key(self, sjukfall, start, slut):
        res = []
        for diagnos in self.diagnos:
            if sjukfall.match_diagnos(diagnos, start, slut):
                res.append(diagnos)
        return res

    def check(self, wideline):
        assert(wideline.index('diagnoskategori') != None)

class RuleSjuklangd:
    def key(self, sjukfall, start, slut):
        days = sjukfall.days(start, slut)
        if days < 15:
            return '1) <15'
        elif days >= 15 and days <= 30:
            return '2) 15-30'
        elif days >= 31 and days <= 60:
            return '3) 31-60'
        elif days >= 61 and days <= 90:
            return '4) 61-90'
        elif days >= 91 and days <= 180:
            return '5) 91-180'
        elif days >= 181 and days <= 365:
            return '6) 181-365'
        else:
            return '7) >365'

    def check(self, wideline):
        pass
    
class RuleLan:
    def key(self, sjukfall, start, slut):
        key = sjukfall.lan(start, slut)
        return self.lan(key)

    def lan(self, key):
        if key == "10":
            return "Blekinge län"
        if key == "20":
            return "Dalarnas län"
        if key == "13":
            return "Hallands län"
        if key == "08":
            return "Kalmar län" 
        if key == "07":
            return "Kronobergs län"
        if key == "09":
            return "Gotlands län" 
        if key == "21":
            return "Gävleborgs län"
        if key == "23":
            return "Jämtlands län"
        if key == "06":
            return "Jönköpings län"
        if key == "25":
            return "Norrbottens län"
        if key == "12":
            return "Skåne län"
        if key == "01":
            return "Stockholms län"
        if key == "04":
            return "Södermanlands län"
        if key == "03":
            return "Uppsala län"
        if key == "17":
            return "Värmlands län"
        if key == "24":
            return "Västerbottens län"
        if key == "22":
            return "Västernorrlands län"
        if key == "19":
            return "Västmanlands län"
        if key == "14":
            return "Västra Götalands län"
        if key == "18":
            return "Örebro län"
        if key == "05":
            return "Östergötlands län"
        assert(key == "00")
        return "Okänt län"

    def check(self, wideline):
        assert(wideline.index('lkf') != None)

class RuleSjukfallEnhet:
    def __init__(self, enhet):
        self.enhet = enhet
        
    def key(self, sjukfall, start, slut):
        if sjukfall.match_enhet(start, slut, self.enhet):
            return self.enhet
        return None

    def check(self, wideline):
        assert(wideline.index('enhet') != None)

class RuleSjukfallEnheter:
    def key(self, sjukfall, start, slut):
        return sjukfall.enheter(start, slut)

    def check(self, wideline):
        assert(wideline.index('enhet') != None)

class RuleSjukfall:
    def key(self, sjukfall, start, slut):
        return "default"

    def check(self, wideline):
        pass

class RuleDiagnos:
    def key(self, sjukfall, start, slut):
        return sjukfall.diagnos(start,slut)

    def check(self, wideline):
        assert(wideline.index('diagnoskapitel'))

class RuleSjukgrad:
    def key(self, sjukfall, start, slut):
        grad = sjukfall.sjukgrad(start,slut)
        if grad != 25 and grad != 50 and grad != 75 and grad != 100:
            raise Exception("Invalid sjukgrad {}".format(grad))
        return str(grad)

    def check(self, wideline):
        assert(wideline.index('sjukskrivningsgrad') != None)


class RuleAlder:
    def key(self, sjukfall, start, slut):
        age = sjukfall.age(start, slut)

        if age < 21:
            return '<21'
        elif age >= 21 and age <= 25:
            return '21-25'
        elif age >= 26 and age <= 30:
            return '26-30'
        elif age >= 31 and age <= 35:
            return '31-35'
        elif age >= 36 and age <= 40:
            return '36-40'
        elif age >= 41 and age <= 45:
            return '41-45'
        elif age >= 46 and age <= 50:
            return '46-50'
        elif age >= 51 and age <= 55:
            return '51-55'
        elif age >= 56 and age <= 60:
            return '56-60'
        else:
            return '>60'

    def check(self, wideline):
        assert(wideline.index('alder') != None)
