/**
 * Copyright (C) 2014 Inera AB (http://www.inera.se)
 *
 * This file is part of statistik (https://github.com/sklintyg/statistik).
 *
 * statistik is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * statistik is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.service.warehouse;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Predicate;
import org.joda.time.LocalDate;
import se.inera.statistics.service.report.model.Range;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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

    public static Collection<Sjukfall> active(Range range, Aisle aisle, Predicate<Fact> filter) {
        return active(calculateSjukfall(aisle, filter, WidelineConverter.toDay(firstDayAfter(range))), range);
    }

    private static Collection<Sjukfall> active(Collection<Sjukfall> all, Range range) {
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

    private static Collection<Sjukfall> calculateSjukfall(Aisle aisle, Predicate<Fact> filter, int cutoff) {
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

    public static EnhetFilter createEnhetFilter(String... enhetIds) {
        int[] numericalIds = new int[enhetIds.length];
        for (int i = 0; i < enhetIds.length; i++) {
            numericalIds[i] = Warehouse.getEnhet(enhetIds[i]);
        }
        Arrays.sort(numericalIds);
        return new EnhetFilter(numericalIds);
    }

    public static Iterable<SjukfallGroup> sjukfallGrupperUsingOriginalSjukfallStart(final LocalDate from, final int periods, final int periodSize, final Aisle aisle, final Predicate<Fact> filter) {
        return new Iterable<SjukfallGroup>() {
            @Override
            public Iterator<SjukfallGroup> iterator() {
                return new SjukfallIterator(from, periods, periodSize, aisle, filter, true);
            }
        };
    }

    public static Iterable<SjukfallGroup> sjukfallGrupper(final LocalDate from, final int periods, final int periodSize, final Aisle aisle, final Predicate<Fact> filter) {
        return new Iterable<SjukfallGroup>() {
            @Override
            public Iterator<SjukfallGroup> iterator() {
                return new SjukfallIterator(from, periods, periodSize, aisle, filter, false);
            }
        };
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

    public static class EnhetFilter implements Predicate<Fact> {
        private final int[] enhetIds;

        public EnhetFilter(int... enhetIds) {
            this.enhetIds = enhetIds;
            Arrays.sort(this.enhetIds);
        }

        @Override
        public boolean apply(Fact fact) {
            return Arrays.binarySearch(enhetIds, fact.getEnhet()) >= 0;
        }

        public int[] getEnhetIds() {
            return enhetIds;
        }

    }

}
