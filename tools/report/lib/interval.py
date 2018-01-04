#!/usr/bin/env python
# -*- mode: Python; fill-column: 75; comment-column: 70; coding: utf-8 ;-*-
#
# Copyright (C) 2018 Inera AB (http://www.inera.se)
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
from period import Period

class Interval:
    def __init__(self, intyg):
        self.start = intyg.start
        self.slut  = intyg.slut
        self.intyg = [Period(self.start, self.slut)]

    def update(self, start, slut):
        if start < self.start:
            self.start = start
        if slut > self.slut:
            self.slut = slut

        period = Period(start,slut)
        for i in self.intyg:
            i.update(period)
        self.intyg.append(period)
        
        return True

    def match(self, intyg):
        if intyg.start >= self.start and intyg.start <= self.slut:
            return self.update(intyg.start, intyg.slut)
        if intyg.slut >= self.start and intyg.slut <= self.slut:
            return self.update(intyg.start, intyg.slut)
        if intyg.start < self.start and intyg.slut > self.slut:
            return self.update(intyg.start, intyg.slut)

        return False

    def days(self):
        return self.slut - self.start + 1

    def days_in_period(self, start, slut):
        if self.slut < start:
            return 0
        if self.start > slut:
            return 0

        first = self.start
        last  = self.slut

        if start > first:
            first = start
        if slut < last:
            last  = slut

        return last - first + 1
