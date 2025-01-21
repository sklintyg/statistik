/*
 * Copyright (C) 2025 Inera AB (http://www.inera.se)
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

import com.google.common.collect.Lists;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.inera.statistics.service.report.model.AvailableFilters;
import se.inera.statistics.service.report.model.KonDataResponse;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.report.util.IntygPerSjukfallGroupUtil;
import se.inera.statistics.service.report.util.Ranges;
import se.inera.statistics.service.warehouse.Aisle;
import se.inera.statistics.service.warehouse.FilterPredicates;
import se.inera.statistics.service.warehouse.ResponseUtil;
import se.inera.statistics.service.warehouse.Sjukfall;
import se.inera.statistics.service.warehouse.SjukfallGroup;
import se.inera.statistics.service.warehouse.SjukfallUtil;

@Component
public class IntygPerSjukfallQuery {

    @Autowired
    private RegionCutoff regionCutoff;

    public static final Ranges RANGES = IntygPerSjukfallGroupUtil.RANGES;

    private static Map<Ranges.Range, Counter<Ranges.Range>> count(Collection<Sjukfall> sjukfalls, Ranges ranges) {
        Map<Ranges.Range, Counter<Ranges.Range>> counters = Counter.mapFor(ranges);
        for (Sjukfall sjukfall : sjukfalls) {
            Counter counter = counters.get(ranges.rangeFor(sjukfall.getIntygCountIncludingBeforeCurrentPeriod()));
            counter.increase(sjukfall);
        }
        return counters;
    }

    private static SimpleKonResponse getIntygPerSjukfallTvarsnitt(Aisle aisle, FilterPredicates filter, LocalDate from, int periods,
        int periodLength, SjukfallUtil sjukfallUtil, int cutoff) {
        List<SimpleKonDataRow> rows = new ArrayList<>();
        for (SjukfallGroup sjukfallGroup : sjukfallUtil.sjukfallGrupper(from, periods, periodLength, aisle, filter)) {
            Map<Ranges.Range, Counter<Ranges.Range>> counterMap = count(sjukfallGroup.getSjukfall(), RANGES);
            for (Ranges.Range i : RANGES) {
                Counter<Ranges.Range> counter = counterMap.get(i);
                rows.add(new SimpleKonDataRow(i.getName(), ResponseUtil.filterCutoff(counter.getCountFemale(), cutoff),
                    ResponseUtil.filterCutoff(counter.getCountMale(), cutoff)));
            }
        }
        return new SimpleKonResponse(AvailableFilters.getForSjukfall(), rows);
    }

    public static SimpleKonResponse getIntygPerSjukfallTvarsnitt(Aisle aisle, FilterPredicates filter, LocalDate from, int periods,
        int periodLength, SjukfallUtil sjukfallUtil) {
        return getIntygPerSjukfallTvarsnitt(aisle, filter, from, periods, periodLength, sjukfallUtil, 0);
    }

    public SimpleKonResponse getIntygPerSjukfallTvarsnittRegion(Aisle aisle, FilterPredicates filter, LocalDate from, int periods,
        int periodLength, SjukfallUtil sjukfallUtil) {
        return getIntygPerSjukfallTvarsnitt(aisle, filter, from, periods, periodLength, sjukfallUtil, regionCutoff.getCutoff());
    }

    public static KonDataResponse getIntygPerSjukfallTidsserie(Aisle aisle, FilterPredicates filter, LocalDate start, int periods,
        int periodLength, SjukfallUtil sjukfallUtil) {
        final Ranges ranges = RANGES;
        final ArrayList<Ranges.Range> rangesList = Lists.newArrayList(ranges);
        final List<String> names = Lists.transform(rangesList, Ranges.Range::getName);
        final List<Integer> ids = Lists.transform(rangesList, Ranges.Range::getCutoff);
        final CounterFunction<Integer> counterFunction = (sjukfall, counter) -> {
            final int certificateCount = sjukfall.getIntygCountIncludingBeforeCurrentPeriod();
            final int rangeId = ranges.getRangeCutoffForValue(certificateCount);
            counter.add(rangeId);
        };
        return sjukfallUtil.calculateKonDataResponse(aisle, filter, start, periods, periodLength, names, ids, counterFunction);
    }

}
