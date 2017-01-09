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

class Patient:
        def __init__(self, intyg):
            self.intyg = []
            self.kon = intyg.kon
            self.add(intyg)

        def add(self, intyg):
            self.intyg.append(intyg)

        def eval(self, res):
            """Group the intygs for this patient into sjukfall and
               apply the group rule (res).
            """
            from sjukfall import Sjukfall
            tot = 0
            groups = []
        
            intyg = [ i for i in self.intyg ]
            # Split the intygs into sjukfall
            while len(intyg) > 0:
                sjukfall = Sjukfall(intyg.pop())
                groups.append(sjukfall)
                match = True
                # For each intyg added to the sjukfall we need to loop
                # over the remaining intygs, since the sjukfall period
                # has been extended and may match other intygs.
                while match:
                    match = False
                    tmp = []
                    for i in intyg:
                        if sjukfall.match(i):
                            match = True
                        else:
                            tmp.append(i)
                    intyg = tmp

            # Evaluate the group rule (res) to each sjukfall
            for group in groups:
                # Add each sjukfall to the result set
                res.add(group)
