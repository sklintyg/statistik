/**
 * Copyright (C) 2015 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.warehouse.query;

import com.google.common.base.Function;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Lists;
import org.joda.time.LocalDate;
import se.inera.statistics.service.report.model.KonDataResponse;
import se.inera.statistics.service.report.model.OverviewChartRowExtended;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.report.util.AldersgroupUtil;
import se.inera.statistics.service.report.util.Ranges;
import se.inera.statistics.service.warehouse.Aisle;
import se.inera.statistics.service.warehouse.Sjukfall;
import se.inera.statistics.service.warehouse.SjukfallFilter;
import se.inera.statistics.service.warehouse.SjukfallGroup;
import se.inera.statistics.service.warehouse.SjukfallUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public final class AldersgruppQuery {
    private static final Ranges RANGES = AldersgroupUtil.RANGES;
    public static final int PERCENT = 100;

    private AldersgruppQuery() {
    }

    public static List<OverviewChartRowExtended> getOverviewAldersgrupper(Collection<Sjukfall> currentSjukfall, Collection<Sjukfall> previousSjukfall, int noOfRows) {
        Map<Ranges.Range, Counter<Ranges.Range>> previousCount = count(previousSjukfall);

        List<Counter<Ranges.Range>> toKeep = count(currentSjukfall, noOfRows);

        List<OverviewChartRowExtended> result = new ArrayList<>();

        for (Counter<Ranges.Range> counter : toKeep) {
            int current = counter.getCount();
            int previous = previousCount.get(counter.getKey()).getCount();
            result.add(new OverviewChartRowExtended(counter.getKey().getName() , current, percentChange(current, previous)));
        }

        return result;
    }

    private static int percentChange(int current, int previous) {
        if (previous == 0) {
            return 0;
        } else {
            return (current - previous) * PERCENT / previous;
        }
    }

    private static Collection<Ranges.Range> rowsToKeep(Map<Ranges.Range, Counter<Ranges.Range>> count, int noOfRows) {
        List<Counter<Ranges.Range>> sorted = new ArrayList<>();
        for (Counter counter : count.values()) {
            sorted.add(counter);
        }
        Collections.sort(sorted);

        Collection<Ranges.Range> result = new HashSet<>();
        for (Counter<Ranges.Range> counter : sorted) {
            result.add(counter.getKey());
            if (result.size() == noOfRows) {
                break;
            }
        }

        return result;
    }

    public static List<Counter<Ranges.Range>> count(Collection<Sjukfall> sjukfalls, int noOfRows) {
        Map<Ranges.Range, Counter<Ranges.Range>> map = count(sjukfalls);
        List<Counter<Ranges.Range>> result = new ArrayList<>();

        Collection<Ranges.Range> rowsToKeep = rowsToKeep(map, noOfRows);
        for (Ranges.Range range : RANGES) {
            if (rowsToKeep.contains(range)) {
                result.add(map.get(range));
            }
        }

        return result;
    }

    public static Map<Ranges.Range, Counter<Ranges.Range>> count(Collection<Sjukfall> sjukfalls) {
        Map<Ranges.Range, Counter<Ranges.Range>> counters = Counter.mapFor(RANGES);
        for (Sjukfall sjukfall : sjukfalls) {
            Counter counter = counters.get(RANGES.rangeFor(sjukfall.getAlder()));
            counter.increase(sjukfall);
        }
        return counters;
    }

    public static SimpleKonResponse<SimpleKonDataRow> getAldersgrupper(Aisle aisle, SjukfallFilter filter, LocalDate from, int periods, int periodLength, SjukfallUtil sjukfallUtil) {
        List<SimpleKonDataRow> rows = new ArrayList<>();
        for (SjukfallGroup sjukfallGroup: sjukfallUtil.sjukfallGrupper(from, periods, periodLength, aisle, filter)) {
            Map<Ranges.Range, Counter<Ranges.Range>> counterMap = AldersgruppQuery.count(sjukfallGroup.getSjukfall());
            for (Ranges.Range i : AldersgroupUtil.RANGES) {
                Counter<Ranges.Range> counter = counterMap.get(i);
                rows.add(new SimpleKonDataRow(i.getName(), counter.getCountFemale(), counter.getCountMale()));
            }
        }
        return new SimpleKonResponse<>(rows, periodLength);
    }

    public static KonDataResponse getAldersgrupperSomTidsserie(Aisle aisle, SjukfallFilter filter, LocalDate start, int periods, int periodLength, SjukfallUtil sjukfallUtil) {
        final ArrayList<Ranges.Range> ranges = Lists.newArrayList(AldersgroupUtil.RANGES);
        final List<String> names = Lists.transform(ranges, new Function<Ranges.Range, String>() {
            @Override
            public String apply(Ranges.Range range) {
                return range.getName();
            }
        });
        final List<Integer> ids = Lists.transform(ranges, new Function<Ranges.Range, Integer>() {
            @Override
            public Integer apply(Ranges.Range range) {
                return range.getCutoff();
            }
        });
        final CounterFunction<Integer> counterFunction = new CounterFunction<Integer>() {
            @Override
            public void addCount(Sjukfall sjukfall, HashMultiset<Integer> counter) {
                final int age = sjukfall.getAlder();
                final int rangeId = getRangeIdForAge(age);
                counter.add(rangeId);
            }

            private int getRangeIdForAge(int age) {
                for (Ranges.Range range: ranges) {
                    final int cutoff = range.getCutoff();
                    if (cutoff > age) {
                        return cutoff;
                    }
                }
                throw new IllegalStateException("Ranges have not been defined correctly. No range includes " + age);
            }

        };

        return sjukfallUtil.calculateKonDataResponse(aisle, filter, start, periods, periodLength, names, ids, counterFunction);
    }

}
