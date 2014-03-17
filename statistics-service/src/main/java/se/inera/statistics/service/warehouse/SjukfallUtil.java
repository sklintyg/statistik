/*
 * Copyright (C) 2013 - 2014 Inera AB (http://www.inera.se)
 *
 *     This file is part of Inera Statistics (http://code.google.com/p/inera-statistics/).
 *
 *     Inera Statistics is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Inera Statistics is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU LESSER GENERAL PUBLIC LICENSE for more details.
 *
 *     You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.service.warehouse;

import org.joda.time.LocalDate;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.util.Ranges;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class SjukfallUtil {

    private static final StartFilter ALL_ENHETER = new StartFilter() {
        @Override
        public boolean accept(Fact fact) {
            return true;
        }
    };

    private SjukfallUtil() {
    }

    public static Collection<Sjukfall> calculateSjukfall(Aisle aisle) {
        return calculateSjukfall(aisle, ALL_ENHETER, Integer.MAX_VALUE);
    }

    private static Collection<Sjukfall> calculateSjukfall(Aisle aisle, StartFilter filter, int cutoff) {
        Collection<Sjukfall> sjukfalls = new ArrayList<>();
        Map<Integer, Sjukfall> active = new HashMap<>();
        int currentDatum = 0;
        for (Fact line : aisle) {
            if (line.startdatum >= cutoff) {
                break;
            }
            int key = line.patient;
            Sjukfall sjukfall = active.get(key);

            if (sjukfall == null) {
                if (filter.accept(line)) {
                    sjukfall = new Sjukfall(line);
                    active.put(key, sjukfall);
                }
            } else {
                Sjukfall nextSjukfall = sjukfall.join(line);
                if (nextSjukfall != sjukfall) {
                    sjukfalls.add(sjukfall);
                    active.put(key, nextSjukfall);
                }
            }
        }
        for (Sjukfall sjukfall : active.values()) {
            sjukfalls.add(sjukfall);
        }
        return sjukfalls;
    }

    public static Collection<Sjukfall> calculateSjukfall(Aisle aisle, int...enhetIds) {
        return calculateSjukfall(aisle, new UnitFilter(enhetIds), Integer.MAX_VALUE);
    }
    public static Collection<Sjukfall> active(Range range, Aisle aisle, int...enhetIds) {
        return active(calculateSjukfall(aisle, new UnitFilter(enhetIds), WidelineConverter.toDay(firstDayAfter(range))), range);

    }
    public static Collection<Sjukfall> active(Collection<Sjukfall> all, Range range) {
        int start = WidelineConverter.toDay(range.getFrom());
        int end = WidelineConverter.toDay(firstDayAfter(range));
        Collection<Sjukfall> active = new ArrayList<>();
        for (Sjukfall sjukfall : all) {
            if (sjukfall.in(start, end)) {
                active.add(sjukfall);
            }
        }
        return active;
    }

    static LocalDate firstDayAfter(Range range) {
        return range.getTo().plusMonths(1);
    }

    public static int getLong(Collection<Sjukfall> sjukfalls) {
        int count = 0;
        for (Sjukfall sjukfall : sjukfalls) {
            if (sjukfall.realDays > 90) {
                count++;
            }
        }
    return count;
    }

    private interface StartFilter {
        boolean accept(Fact fact);
    }

    private static class UnitFilter implements StartFilter {
        private final int[] enhetIds;

        public UnitFilter(int...enhetIds) {
            this.enhetIds = enhetIds;
        }

        @Override
        public boolean accept(Fact fact) {
            return Arrays.binarySearch(enhetIds, fact.getEnhet()) >= 0;
        }
    }
}
