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
from datetime import datetime, timedelta

class VG:
    def __init__(self, name):
        self.name = name
        self.patienter = {}

    def add(self, intyg):
    	from patient import Patient
        # Add intyg to this vårdgivare
        if intyg.id in self.patienter:
            patient = self.patienter[intyg.id]
            patient.add(intyg)
        else:
            self.patienter[intyg.id] = Patient(intyg)

    def eval(self, group):
        """Evaluate the rule to all sjukfall for this VG."""
        for k,patient in self.patienter.items():
            patient.eval(group)
        return group.sum()


def date2days(string, alpha):

    if '-' not in string:
        if not len(string) == 8:
            print "ERROR: Felaktigt datumformat"
            exit(1)
        else:
            date = datetime(int(string[:4]), int(string[4:6]), int(string[6:8])) - alpha
    else:
        data = string.split('-')
        assert(len(data) == 3)
        date = datetime(int(data[0]), int(data[1]), int(data[2])) - alpha

    return date.days

