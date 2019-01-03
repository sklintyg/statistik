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

class Period:
    def __init__(self, start, slut):
        self.intyg_start = start
        self.start = start
        self.slut  = slut
        self.valid  = True

    def update(self, period):
        """ Update the periods with possible new start point.
            Period might be invalidated in case of overlap. Note
            that both periods may be updated
        """
        if self.slut < period.start:
            return
        if self.start > period.slut:
            return

        if not self.valid or not period.valid:
            return

        if period.start <= self.start and period.slut >= self.slut:
            self.valid = False
            return

        # Update start
        if period.start < self.start and period.slut <= self.slut:
            self.start = period.slut + 1

        if period.start >= self.start and period.slut <= self.slut:
            period.valid = False

        if period.slut > self.slut and period.start > self.start:
            period.start = self.slut + 1

        #print self.days(),period.days()
    def __str__(self):
        return "({0},{1},{2})".format(self.intyg_start,self.start,self.slut)

    def days(self):
        if not self.valid:
            return 0
        return self.slut - self.start + 1
