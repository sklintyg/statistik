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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import se.inera.statistics.service.report.model.AvailableFilters;
import se.inera.statistics.service.report.model.KonDataResponse;
import se.inera.statistics.service.report.model.OverviewChartRowExtended;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.report.util.AldersgroupUtil;
import se.inera.statistics.service.report.util.Ranges;
import se.inera.statistics.service.warehouse.Aisle;
import se.inera.statistics.service.warehouse.FilterPredicates;
import se.inera.statistics.service.warehouse.Sjukfall;
import se.inera.statistics.service.warehouse.SjukfallGroup;
import se.inera.statistics.service.warehouse.SjukfallUtil;

public final class AldersgruppQuery {
    public static final Ranges RANGES = AldersgroupUtil.RANGES;
    public static final Ranges OVERVIEW_RANGES = AldersgroupUtil.OVERVIEW_RANGES;

    private AldersgruppQuery() {
    }

    public static List<OverviewChartRowExtended> getOverviewAldersgrupper(Collection<Sjukfall> currentSjukfall,
            Collection<Sjukfall> previousSjukfall) {
        Map<Ranges.Range, Counter<Ranges.Range>> previousCount = count(previousSjukfall, OVERVIEW_RANGES);
        Map<Ranges.Range, Counter<Ranges.Range>> map = count(currentSjukfall, OVERVIEW_RANGES);
        List<OverviewChartRowExtended> result = new ArrayList<>();
        Counter<Ranges.Range> counter;
        for (Ranges.Range range : OVERVIEW_RANGES) {
            counter = map.get(range);
            int current = counter.getCount();
            int previous = previousCount.get(counter.getKey()).getCount();
            String color = counter.getKey().getColor();
            result.add(new OverviewChartRowExtended(counter.getKey().getName(), current, current - previous, color));
        }
        return result;
    }

    private static Collection<Ranges.Range> rowsToKeep(Map<Ranges.Range, Counter<Ranges.Range>> count, int noOfRows) {
        List<Counter<Ranges.Range>> sorted = new ArrayList<>();
        sorted.addAll(count.values());
        Collections.sort(sorted, Counter.byTotalCount());

        Collection<Ranges.Range> result = new HashSet<>();
        for (Counter<Ranges.Range> counter : sorted) {
            result.add(counter.getKey());
            if (result.size() == noOfRows) {
                break;
            }
        }

        return result;
    }

    public static Map<Ranges.Range, Counter<Ranges.Range>> count(Collection<Sjukfall> sjukfalls, Ranges ranges) {
        Map<Ranges.Range, Counter<Ranges.Range>> counters = Counter.mapFor(ranges);
        for (Sjukfall sjukfall : sjukfalls) {
            Counter counter = counters.get(ranges.rangeFor(sjukfall.getAlder()));
            counter.increase(sjukfall);
        }
        return counters;
    }

    public static SimpleKonResponse getAldersgrupper(Aisle aisle, FilterPredicates filter, LocalDate from, int periods,
                                                     int periodLength, SjukfallUtil sjukfallUtil, Ranges ranges) {
        List<SimpleKonDataRow> rows = new ArrayList<>();
        for (SjukfallGroup sjukfallGroup : sjukfallUtil.sjukfallGrupper(from, periods, periodLength, aisle, filter)) {
            Map<Ranges.Range, Counter<Ranges.Range>> counterMap = AldersgruppQuery.count(sjukfallGroup.getSjukfall(), ranges);
            for (Ranges.Range i : ranges) {
                Counter<Ranges.Range> counter = counterMap.get(i);
                rows.add(new SimpleKonDataRow(i.getName(), counter.getCountFemale(), counter.getCountMale()));
            }
        }
        return new SimpleKonResponse(AvailableFilters.getForSjukfall(), rows);
    }

    public static KonDataResponse getAldersgrupperSomTidsserie(Aisle aisle, FilterPredicates filter, LocalDate start, int periods,
            int periodLength, SjukfallUtil sjukfallUtil) {
        final Ranges ranges = RANGES;
        final ArrayList<Ranges.Range> rangesList = Lists.newArrayList(ranges);
        final List<String> names = Lists.transform(rangesList, Ranges.Range::getName);
        final List<Integer> ids = Lists.transform(rangesList, Ranges.Range::getCutoff);
        final CounterFunction<Integer> counterFunction = (sjukfall, counter) -> {
            final int age = sjukfall.getAlder();
            final int rangeId = ranges.getRangeCutoffForValue(age);
            counter.add(rangeId);
        };
        return sjukfallUtil.calculateKonDataResponse(aisle, filter, start, periods, periodLength, names, ids, counterFunction);
    }

}
