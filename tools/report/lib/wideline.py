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

class Wideline:
    def __init__(self, columns):
        self.columns = {}
        i = 0
        for col in columns.split():
            self.columns[col.strip()] = i
            i += 1

    def get(self, values, key, mandatory=False):
        i = self.index(key)
        if i is None:
            if mandatory:
                raise Exception(key + " not found")
            return None

        if len(values) < i:
            raise Exception('Bad index ' + str(i) + " length " + str(len(values)))

        return values[i].strip()

    def index(self, key):
        if key in self.columns:
            return self.columns[key]
        return None
