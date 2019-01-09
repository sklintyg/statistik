#!/usr/bin/env python
# -*- mode: Python; fill-column: 75; comment-column: 70; coding: utf-8 ;-*-
#
# Copyright (C) 2019 Inera AB (http://www.inera.se)
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

class Intyg:
    def __init__(self, data, wideline):
        values = data.split('\t')
        self.id			= wideline.get(values, 'patientid', True)
        if self.id.find('-') != -1:
            self.id = self.id.replace('-', '')
        self.vardgivare = wideline.get(values, 'vardgivareid', True)
        self.start      = int(wideline.get(values, 'startdatum', True))
        self.slut       = int(wideline.get(values, 'slutdatum', True))
        self.diagnos    = wideline.get(values, 'diagnoskapitel', True)
        self.kon        = int(wideline.get(values, 'kon', True))
        self.alder      = int(wideline.get(values, 'alder', True))
        self.sjukgrad   = wideline.get(values, 'sjukskrivningsgrad')
        if self.sjukgrad:
            self.sjukgrad = int(self.sjukgrad)
        self.befattning = wideline.get(values, 'lakarbefattning')
        self.lakaralder = wideline.get(values, 'lakaralder')
        if self.lakaralder:
            self.lakaralder = int(self.lakaralder)
        self.lakareid   = wideline.get(values, 'lakareid')
        self.lakarkon   = wideline.get(values, 'lakarkon')
        if self.lakarkon:
            self.lakarkon = int(self.lakarkon)
        self.enhet      = wideline.get(values, 'enhet')
        self.lkf = wideline.get(values, 'lkf')
        self.enkelt = wideline.get(values, 'enkelt')
        if self.enkelt:
            self.enkelt = int(self.enkelt)
        if self.lkf:
            self.lan = self.lkf[0:2]
        self.diagnoskategori = wideline.get(values, 'diagnoskategori')
        self.lakarintyg   = wideline.get(values, 'lakarintyg')
        if self.lakarintyg:
            self.lakarintyg  = int(self.lakarintyg)
        
    def valid(self, start, slut):
        """ Check if this sjukfall is within the interval
        """
        if self.slut < start or self.start > slut:
            return False

        return True

    def isnewer(self, intyg):
        """ Compare the intyg and return true if this is considered
        newer than 'intyg' """
        if self.start > intyg.start:
            return True
        elif self.start == intyg.start:
            if self.lakarintyg > intyg.lakarintyg:
                return True
            elif (self.lakarintyg == intyg.lakarintyg and
                self.sjukgrad > intyg.sjukgrad):
                return True
        return False

    def tostring(self):
        return "[{0},{1},{2},{3},{4},{5}]".format(self.id,self.start,self.slut,self.alder,self.sjukgrad, self.enhet)

    def __str__(self):
        return "[{0},{1}]".format(self.start,self.slut)

