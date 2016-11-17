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

class Group:
    def __init__(self, rule=None, threshold=False, agg=None, start=None, end=None):
        self.group = {}
        # The aggregated result
        self.agg   = agg
        self.threshold = threshold
        self.rule = rule
        self.start = start
        self.end = end
        self.notvalid = 0

    def __add(self, key, num, kon):
        """Internal add for the aggregated result"""
        tup = (0,0)
        if key in self.group:
            tup = self.group[key]

        # lägg till num på nuvarande värde
        if kon == 1: # MAN
            self.group[key] = (tup[0] + num, tup[1])
        elif kon == 2: # KVINNA
            self.group[key] = (tup[0], tup[1] + num)
        else:
            raise Exception('Bad kon number: {} '.format(kon))

    def add(self, sjukfall):
        """Evaluate the sjukfall for the group rule"""
        if self.start and self.end:
            if not sjukfall.valid(self.start, self.end):
                self.notvalid += 1
                return

        # Evaluate the rule, a list of values may be returned.
        key = self.rule.key(sjukfall, self.start, self.end)
        if key:
            if isinstance(key, list):
                for k in key:
                    self.__add(k, 1, sjukfall.kon())
            else:
                self.__add(key, 1, sjukfall.kon())

    def sum(self):
        """Summarize all the values and possible apply a threshold."""
        tot = 0
        for k, v in self.group.items():
            if self.threshold is False or v[0] >= THRESHOLD:
                tot += v[0]
                if self.agg:
                    self.agg.__add(k,v[0], 1)
            if self.threshold is False or v[1] >= THRESHOLD:
                tot += v[1]
                if self.agg:
                    self.agg.__add(k,v[1], 2)
        return tot

    def log(self):
        for key in sorted(self.group):
            v = self.group[key]
            print "{0} (Kvinnor: {1}, Män: {2}) => {3}".format(key,v[1],v[0], v[0] + v[1])
