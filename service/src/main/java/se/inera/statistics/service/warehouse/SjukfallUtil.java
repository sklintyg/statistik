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

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Predicate;
import org.joda.time.LocalDate;
import se.inera.statistics.service.report.model.Range;

import java.util.*;

public final class SjukfallUtil {

    public static final EnhetFilter ALL_ENHETER = new EnhetFilter() {
        @Override
        public boolean apply(Fact fact) {
            return true;
        }
    };

    public static final int LONG_LIMIT = 90;

    private SjukfallUtil() {
    }

    public static Collection<Sjukfall> calculateSjukfall(Aisle aisle, Predicate<Fact> filter, int cutoff) {
        Collection<Sjukfall> sjukfalls = new ArrayList<>();
        Map<Integer, Sjukfall> active = new HashMap<>();
        for (Fact line : aisle) {
            if (line.getStartdatum() >= cutoff) {
                break;
            }
            int key = line.getPatient();
            Sjukfall sjukfall = active.get(key);

            if (sjukfall == null) {
                if (filter.apply(line)) {
                    sjukfall = new Sjukfall(line);
                    active.put(key, sjukfall);
                }
            } else {
                Sjukfall nextSjukfall = sjukfall.join(line);
                active.put(key, nextSjukfall);
                if (!nextSjukfall.isExtended()) {
                    sjukfalls.add(sjukfall);
                }
            }
        }
        for (Sjukfall sjukfall : active.values()) {
            sjukfalls.add(sjukfall);
        }
        return sjukfalls;
    }

    @VisibleForTesting
    public static Collection<Sjukfall> calculateSjukfall(Aisle aisle) {
        return calculateSjukfall(aisle, ALL_ENHETER, Integer.MAX_VALUE);
    }

    @VisibleForTesting
    static Collection<Sjukfall> calculateSjukfall(Aisle aisle, int... enhetIds) {
        return calculateSjukfall(aisle, new EnhetFilter(enhetIds), Integer.MAX_VALUE);
    }

    public static Collection<Sjukfall> active(Range range, Aisle aisle, String... enhetIds) {
        return active(calculateSjukfall(aisle, createEnhetFilter(enhetIds), WidelineConverter.toDay(firstDayAfter(range))), range);
    }

    public static Collection<Sjukfall> active(Range range, Aisle aisle, Predicate<Fact> filter) {
        return active(calculateSjukfall(aisle, filter, WidelineConverter.toDay(firstDayAfter(range))), range);
    }

    public static Iterable<SjukfallGroup> sjukfallGrupper(final LocalDate from, final int periods, final int periodSize, final Aisle aisle, final int... enhetIds) {
        return new Iterable<SjukfallGroup>() {
            @Override
            public Iterator<SjukfallGroup> iterator() {
                return new SjukfallIterator(from, periods, periodSize, aisle, new EnhetFilter(enhetIds));
            }
        };
    }

    public static EnhetFilter createEnhetFilter(Map<String, String> enheter) {
        Map<Integer, String> numericalIds = new HashMap<>();
        for (String id : enheter.keySet()) {
            int numericalId = Warehouse.getEnhet(id);
            numericalIds.put(numericalId, enheter.get(id));
        }
        return new EnhetFilter(numericalIds);
    }

    public static EnhetFilter createEnhetFilter(String... enhetIds) {
        int[] numericalIds = new int[enhetIds.length];
        for (int i = 0; i < enhetIds.length; i++) {
            numericalIds[i] = Warehouse.getEnhet(enhetIds[i]);
        }
        Arrays.sort(numericalIds);
        return new EnhetFilter(numericalIds);
    }

    public static Iterable<SjukfallGroup> sjukfallGrupper(final LocalDate from, final int periods, final int periodSize, final Aisle aisle, final Predicate<Fact> filter) {
        return new Iterable<SjukfallGroup>() {
            @Override
            public Iterator<SjukfallGroup> iterator() {
                return new SjukfallIterator(from, periods, periodSize, aisle, filter);
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

    public static abstract class FactFilter implements Predicate<Fact> {
    }

    public static class EnhetFilter extends FactFilter {
        private final int[] enhetIds;
        private final Map<Integer, String> enhetsNamesById;

        public EnhetFilter(int... enhetIds) {
            this.enhetIds = enhetIds;
            this.enhetsNamesById = null;
        }

        public EnhetFilter(Map<Integer, String> numericalIds) {
            this.enhetIds = new int[numericalIds.size()];
            int counter = 0;
            for (Integer id : numericalIds.keySet()) {
                enhetIds[counter] = id;
                counter++;
            }
            this.enhetsNamesById = numericalIds;
        }

        @Override
        public boolean apply(Fact fact) {
            return Arrays.binarySearch(enhetIds, fact.getEnhet()) >= 0;
        }

        public int[] getEnhetIds() {
            return enhetIds;
        }

        public String getEnhetsName(int id) {
            return enhetsNamesById != null ? enhetsNamesById.get(id): null;
        }
    }

    public static class SjukfallIterator implements Iterator<SjukfallGroup> {

        private final LocalDate current;
        private final LocalDate from;
        private int period = 0;
        private final int periods;
        private final int periodSize;
        private final Predicate<Fact> filter;
        private final Map<Integer, Sjukfall> active = new HashMap<>();
        private Collection<Sjukfall> sjukfalls;
        private final Iterator<Fact> iterator;
        private Fact pendingLine;

        public SjukfallIterator(LocalDate from, int periods, int periodSize, Aisle aisle, Predicate<Fact> filter) {
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
            sjukfalls = new ArrayList<>();
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
                if (filter.apply(line)) {
                    sjukfall = new Sjukfall(line);
                    active.put(key, sjukfall);
                }
            } else {
                Sjukfall nextSjukfall = sjukfall.join(line);
                active.put(key, nextSjukfall);
                if (!nextSjukfall.isExtended()) {
                    sjukfalls.add(sjukfall);
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

        public Collection<Sjukfall> getSjukfall() {
            return sjukfall;
        }
    }
}
