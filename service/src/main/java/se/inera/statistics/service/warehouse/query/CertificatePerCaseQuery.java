/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import se.inera.statistics.service.report.model.AvailableFilters;
import se.inera.statistics.service.report.model.KonDataResponse;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.report.util.CertificatePerCaseGroupUtil;
import se.inera.statistics.service.report.util.Ranges;
import se.inera.statistics.service.warehouse.*;

public final class CertificatePerCaseQuery {

    public static final Ranges RANGES = CertificatePerCaseGroupUtil.RANGES;

    private CertificatePerCaseQuery() {

    }

    private static Map<Ranges.Range, Counter<Ranges.Range>> count(Collection<Sjukfall> sjukfalls, Ranges ranges) {
        Map<Ranges.Range, Counter<Ranges.Range>> counters = Counter.mapFor(ranges);
        for (Sjukfall sjukfall : sjukfalls) {
            Counter counter = counters.get(ranges.rangeFor(sjukfall.getIntygCount()));
            counter.increase(sjukfall);
        }
        return counters;
    }

    public static SimpleKonResponse getCertificatePerCaseTvarsnitt(Aisle aisle, FilterPredicates filter, LocalDate from, int periods,
                                                                   int periodLength, SjukfallUtil sjukfallUtil, Ranges ranges) {
        List<SimpleKonDataRow> rows = new ArrayList<>();
        for (SjukfallGroup sjukfallGroup : sjukfallUtil.sjukfallGrupper(from, periods, periodLength, aisle, filter)) {
            Map<Ranges.Range, Counter<Ranges.Range>> counterMap = count(sjukfallGroup.getSjukfall(), ranges);
            for (Ranges.Range i : ranges) {
                Counter<Ranges.Range> counter = counterMap.get(i);
                rows.add(new SimpleKonDataRow(i.getName(), counter.getCountFemale(), counter.getCountMale()));
            }
        }
        return new SimpleKonResponse(AvailableFilters.getForSjukfall(), rows);
    }

    public static KonDataResponse getCertificatePerCaseTidsserie(Aisle aisle, FilterPredicates filter, LocalDate start, int periods,
                                                                 int periodLength, SjukfallUtil sjukfallUtil) {
        final Ranges ranges = RANGES;
        final ArrayList<Ranges.Range> rangesList = Lists.newArrayList(ranges);
        final List<String> names = Lists.transform(rangesList, Ranges.Range::getName);
        final List<Integer> ids = Lists.transform(rangesList, Ranges.Range::getCutoff);
        final CounterFunction<Integer> counterFunction = (sjukfall, counter) -> {
            final int age = sjukfall.getIntygCount();
            final int rangeId = ranges.getRangeCutoffForValue(age);
            counter.add(rangeId);
        };
        return sjukfallUtil.calculateKonDataResponse(aisle, filter, start, periods, periodLength, names, ids, counterFunction);
    }

}
