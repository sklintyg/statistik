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
import calendar
from datetime import datetime, timedelta

alpha = datetime(2000, 1, 1)
month_names=["Januari", "Februari", "Mars", "April", "Maj", "Juni", "Juli",
             "Augusti", "September", "Oktober", "November", "December"]

class Ranges:
    def __init__(self, daterange=None):
        self.ranges = []
        self.start = None
        self.slut = None
        if daterange:
            self.add(daterange)

    def put(self, start, slut, name):
        self.add(DateRange(start, slut, name))

    def add(self, dr):
        """ Add a date range to the list """
        if self.start is None or (dr.start < self.start):
            self.start = dr.start
        if self.slut is None or (dr.slut > self.slut):
            self.slut = dr.slut
        self.ranges.append(dr)

    def items(self):
        """ Return all ranges """
        return self.ranges

class DateRange:
    def __init__(self, start, slut, name):
        self.start = start
        self.slut = slut
        self.name = name

def month_range(ranges):
        """ Parse the from:to string into month ranges """
        months = Ranges()
        # YYYY-MM:YYYY-MM

        # split the from:to string
        (f,t) = ranges.split(':')
        items = f.split('-')
        assert(len(items) >= 2)
        year = int(items[0])
        month = int(items[1])

        items = t.split('-')
        assert(len(items) >= 2)
        toyear = int(items[0])
        tomonth = int(items[1])

        while year < toyear or (year == toyear and month <= tomonth):
            (first, last) = calendar.monthrange(year, month)
            start_day = datetime(year, month, 1) - alpha
            end_day = datetime(year, month, last) - alpha
            months.put(start_day.days, end_day.days,
                  "{0} ({1})".format(month_names[month-1], year))

            if month == 12:
                month = 1
                year += 1
            else:
                month += 1

        return months

def date2days(string):
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

def interval_range(string):
    interval = string.split(':')
    assert(len(interval) == 2)
    start = date2days(interval[0])
    slut  = date2days(interval[1])
    return Ranges(DateRange(start, slut, string))
