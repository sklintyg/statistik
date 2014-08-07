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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class SjukfallUtil {

    private static final StartFilter ALL_ENHETER = new StartFilter() {
        @Override
        public boolean accept(Fact fact) {
            return true;
        }
    };
    public static final int LONG_LIMIT = 90;

    private SjukfallUtil() {
    }

    public static Collection<Sjukfall> calculateSjukfall(Aisle aisle) {
        return calculateSjukfall(aisle, ALL_ENHETER, Integer.MAX_VALUE);
    }

    private static Collection<Sjukfall> calculateSjukfall(Aisle aisle, StartFilter filter, int cutoff) {
        Collection<Sjukfall> sjukfalls = new ArrayList<>();
        Map<Integer, Sjukfall> active = new HashMap<>();
        for (Fact line : aisle) {
            if (line.getStartdatum() >= cutoff) {
                break;
            }
            int key = line.getPatient();
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
        return calculateSjukfall(aisle, new EnhetFilter(enhetIds), Integer.MAX_VALUE);
    }

    public static Collection<Sjukfall> active(Range range, Aisle aisle, int...enhetIds) {
        return active(calculateSjukfall(aisle, new EnhetFilter(enhetIds), WidelineConverter.toDay(firstDayAfter(range))), range);
    }

    public static Iterable<SjukfallGroup> sjukfallGrupper(final LocalDate from, final int periods, final int periodSize, final Aisle aisle, final int... enhetIds) {
        return new Iterable<SjukfallGroup>() {
            @Override
            public Iterator<SjukfallGroup> iterator() {
                return new SjukfallGroupIterator(from, periods, periodSize, aisle, new EnhetFilter(enhetIds));
            }
        };
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
            if (sjukfall.getRealDays() > LONG_LIMIT) {
                count++;
            }
        }
    return count;
    }

    private interface StartFilter {
        boolean accept(Fact fact);
    }

    private static class EnhetFilter implements StartFilter {
        private final int[] enhetIds;

        public EnhetFilter(int... enhetIds) {
            this.enhetIds = enhetIds;
        }

        @Override
        public boolean accept(Fact fact) {
            return Arrays.binarySearch(enhetIds, fact.getEnhet()) >= 0;
        }
    }

    public static class SjukfallGroupIterator implements Iterator<SjukfallGroup> {

        private final LocalDate current;
        private final LocalDate from;
        private int period = 0;
        private final int periods;
        private final int periodSize;
        private final StartFilter filter;
        private final Map<Integer, Sjukfall> active = new HashMap<>();
        private final Collection<Sjukfall> sjukfalls = new ArrayList<>();
        private final Iterator<Fact> iterator;
        private Fact pendingLine;

        public SjukfallGroupIterator(LocalDate from, int periods, int periodSize, Aisle aisle, StartFilter filter) {
            this.from = from;
            this.current = from;
            this.periods = periods;
            this.periodSize = periodSize;
            this.filter = filter;
            iterator = aisle.iterator();
        }

        @Override
        public boolean hasNext() {
            return period < periods;
        }

        @Override
        public SjukfallGroup next() {
            int cutoff = WidelineConverter.toDay(from.plusMonths((period + 1) * periodSize));
            if (pendingLine != null && pendingLine.getStartdatum() < cutoff) {
                process(pendingLine);
                pendingLine = null;
            }
            if (pendingLine == null) {
                while (iterator.hasNext()) {
                    Fact line = iterator.next();
                    if (line.getStartdatum() >= cutoff) {
                        pendingLine = line;
                        break;
                    }
                    process(line);
                }
            }
            Collection result = new ArrayList<Sjukfall>();
            int firstday = WidelineConverter.toDay(from.plusMonths(period * periodSize));
            for (Sjukfall sjukfall : sjukfalls) {
                if (sjukfall.getEnd() >= firstday) {
                    result.add(sjukfall);
                }
            }

            for (Sjukfall sjukfall : active.values()) {
                if (sjukfall.getEnd() >= firstday) {
                    result.add(sjukfall);
                }
            }
            Range range = new Range(from.plusMonths(period * periodSize), from.plusMonths(period * periodSize + periodSize - 1));
            SjukfallGroup sjukfallGroup = new SjukfallGroup(range, result);
            period++;
            return sjukfallGroup;
        }

        private void process(Fact line) {
            int key = line.getPatient();
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

        @Override
        public void remove() {

        }
    }

    public static class SjukfallGroup {
        private final Range range;
        private final Collection sjukfall;

        public SjukfallGroup(Range range, Collection sjukfall) {
            this.range = range;
            this.sjukfall = sjukfall;
        }

        public Range getRange() {
            return range;
        }

        public Collection getSjukfall() {
            return sjukfall;
        }
    }
}
