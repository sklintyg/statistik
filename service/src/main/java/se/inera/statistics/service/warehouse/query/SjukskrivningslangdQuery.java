/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.service.warehouse.query;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import se.inera.statistics.service.report.model.AvailableFilters;
import se.inera.statistics.service.report.model.KonDataResponse;
import se.inera.statistics.service.report.model.OverviewChartRow;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.report.util.Ranges;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.report.util.SjukfallslangdUtil;
import se.inera.statistics.service.warehouse.Aisle;
import se.inera.statistics.service.warehouse.FilterPredicates;
import se.inera.statistics.service.warehouse.Sjukfall;
import se.inera.statistics.service.warehouse.SjukfallGroup;
import se.inera.statistics.service.warehouse.SjukfallUtil;

public final class SjukskrivningslangdQuery {
    private static Ranges ranges = SjukfallslangdUtil.RANGES;
    private static final int LONG_SJUKFALL = 90;

    private SjukskrivningslangdQuery() {
    }

    public static List<OverviewChartRow> getOverviewSjukskrivningslangd(Collection<Sjukfall> currentSjukfall, int noOfRows) {
        List<Counter<Ranges.Range>> toKeep = count(currentSjukfall, noOfRows);

        List<OverviewChartRow> result = new ArrayList<>();

        for (Counter<Ranges.Range> counter : toKeep) {
            int current = counter.getCount();
            result.add(new OverviewChartRow(counter.getKey().getName(), current));
        }

        return result;
    }

    private static Collection<Ranges.Range> rowsToKeep(Map<Ranges.Range, Counter<Ranges.Range>> count, int noOfRows) {
        List<Counter<Ranges.Range>> sorted = new ArrayList<>(count.values());
        sorted.sort(Counter.byTotalCount());

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
        for (Ranges.Range range : ranges) {
            if (rowsToKeep.contains(range)) {
                result.add(map.get(range));
            }
        }

        return result;
    }

    public static Map<Ranges.Range, Counter<Ranges.Range>> count(Collection<Sjukfall> sjukfalls) {
        Map<Ranges.Range, Counter<Ranges.Range>> counters = Counter.mapFor(SjukfallslangdUtil.RANGES);
        for (Sjukfall sjukfall : sjukfalls) {
            Counter counter = counters.get(ranges.rangeFor(sjukfall.getRealDays()));
            counter.increase(sjukfall);
        }
        return counters;
    }

    public static SimpleKonResponse getLangaSjukfall(Aisle aisle, FilterPredicates filter, LocalDate from, int periods,
            int periodLength, SjukfallUtil sjukfallUtil) {
        final Function<SjukfallGroup, String> rowNameFunction = sjukfallGroup -> ReportUtil.toPeriod(sjukfallGroup.getRange().getFrom());
        return getLangaSjukfall(aisle, filter, from, periods, periodLength, sjukfallUtil, rowNameFunction);
    }

    public static SimpleKonResponse getLangaSjukfallTvarsnitt(Aisle aisle, FilterPredicates filter, LocalDate from,
            int periods, int periodLength, SjukfallUtil sjukfallUtil) {
        final Function<SjukfallGroup, String> rowNameFunction = sjukfallGroup -> "Mer än 90 dagar";
        return getLangaSjukfall(aisle, filter, from, periods, periodLength, sjukfallUtil, rowNameFunction);
    }

    private static SimpleKonResponse getLangaSjukfall(Aisle aisle, FilterPredicates filter, LocalDate from, int periods,
            int periodLength, SjukfallUtil sjukfallUtil, Function<SjukfallGroup, String> rowNameFunction) {
        List<SimpleKonDataRow> rows = new ArrayList<>();
        for (SjukfallGroup sjukfallGroup : sjukfallUtil.sjukfallGrupper(from, periods, periodLength, aisle,
                filter)) {
            Counter counter = new Counter<>("");
            for (Sjukfall sjukfall : sjukfallGroup.getSjukfall()) {
                if (sjukfall.getRealDays() > LONG_SJUKFALL) {
                    counter.increase(sjukfall);
                }
            }
            rows.add(new SimpleKonDataRow(rowNameFunction.apply(sjukfallGroup), counter.getCountFemale(), counter.getCountMale()));
        }

        return new SimpleKonResponse(AvailableFilters.getForSjukfall(), rows);
    }

    public static SimpleKonResponse getSjuksrivningslangd(Aisle aisle, FilterPredicates filter, LocalDate from,
            int periods, int periodLength, SjukfallUtil sjukfallUtil) {
        List<SimpleKonDataRow> rows = new ArrayList<>();
        for (SjukfallGroup sjukfallGroup : sjukfallUtil.sjukfallGrupper(from, periods, periodLength, aisle,
                filter)) {
            Map<Ranges.Range, Counter<Ranges.Range>> counterMap = SjukskrivningslangdQuery.count(sjukfallGroup.getSjukfall());
            for (Ranges.Range i : SjukfallslangdUtil.RANGES) {
                Counter<Ranges.Range> counter = counterMap.get(i);
                rows.add(new SimpleKonDataRow(i.getName(), counter.getCountFemale(), counter.getCountMale()));
            }
        }
        return new SimpleKonResponse(AvailableFilters.getForSjukfall(), rows);

    }

    public static KonDataResponse getSjuksrivningslangdomTidsserie(Aisle aisle, FilterPredicates filter, LocalDate start, int periods,
            int periodLength, SjukfallUtil sjukfallUtil) {
        final Ranges ranges = SjukfallslangdUtil.RANGES;
        final ArrayList<Ranges.Range> rangesList = Lists.newArrayList(ranges);
        final List<String> names = rangesList.stream().map(Ranges.Range::getName).collect(Collectors.toList());
        final List<Integer> ids = rangesList.stream().map(Ranges.Range::getCutoff).collect(Collectors.toList());
        final CounterFunction<Integer> counterFunction = (sjukfall, counter) -> {
            final int length = sjukfall.getRealDays();
            final int rangeId = ranges.getRangeCutoffForValue(length);
            counter.add(rangeId);
        };
        return sjukfallUtil.calculateKonDataResponse(aisle, filter, start, periods, periodLength, names, ids, counterFunction);
    }

    static int getLong(Collection<Sjukfall> sjukfalls) {
        int count = 0;
        for (Sjukfall sjukfall : sjukfalls) {
            if (sjukfall.getRealDays() > LONG_SJUKFALL) {
                count++;
            }
        }
        return count;
    }

}
