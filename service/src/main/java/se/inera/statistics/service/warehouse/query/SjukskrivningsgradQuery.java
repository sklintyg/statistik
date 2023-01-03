/*
 * Copyright (C) 2023 Inera AB (http://www.inera.se)
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
import se.inera.statistics.service.report.model.KonDataResponse;
import se.inera.statistics.service.report.model.OverviewChartRowExtended;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.report.util.SickLeaveDegree;
import se.inera.statistics.service.warehouse.Aisle;
import se.inera.statistics.service.warehouse.FilterPredicates;
import se.inera.statistics.service.warehouse.Sjukfall;
import se.inera.statistics.service.warehouse.SjukfallUtil;

public final class SjukskrivningsgradQuery {

    static final List<String> GRAD_LABEL = Collections.unmodifiableList(SickLeaveDegree.getLabels());
    static final List<Integer> GRAD = Collections.unmodifiableList(SickLeaveDegree.getDegrees());
    private static final int PERCENT = 100;

    private SjukskrivningsgradQuery() {
    }

    static List<OverviewChartRowExtended> getOverviewSjukskrivningsgrad(Collection<Sjukfall> currentSjukfall,
        Collection<Sjukfall> previousSjukfall) {
        Map<Integer, Counter<Integer>> currentCount = count2(currentSjukfall);
        Map<Integer, Counter<Integer>> previousCount = count2(previousSjukfall);

        List<OverviewChartRowExtended> result = new ArrayList<>();

        for (SickLeaveDegree degree : SickLeaveDegree.values()) {
            int current = currentCount.get(degree.getDegree()).getCount();
            int previous = previousCount.get(degree.getDegree()).getCount();
            result.add(new OverviewChartRowExtended(degree.getName(), current, percentChange(current, previous), degree.getColor()));
        }

        return result;
    }

    private static Map<Integer, Counter<Integer>> count2(Collection<Sjukfall> sjukfalls) {
        Map<Integer, Counter<Integer>> counters = Counter.mapFor(GRAD);

        for (Sjukfall sjukfall : sjukfalls) {
            sjukfall.getSjukskrivningsgrader().stream().distinct().forEach(g -> {
                Counter counter = counters.get(g);
                counter.increase(sjukfall);
            });
        }
        return counters;
    }

    public static KonDataResponse getSjukskrivningsgrad(Aisle aisle, FilterPredicates filter, LocalDate start, int periods, int periodSize,
        SjukfallUtil sjukfallUtil) {
        return getSjukskrivningsgrad(aisle, filter, start, periods, periodSize, sjukfallUtil, false);
    }

    public static KonDataResponse getSjukskrivningsgrad(Aisle aisle, FilterPredicates filter, LocalDate start, int periods, int periodSize,
        SjukfallUtil sjukfallUtil, boolean all) {
        CounterFunction<Integer> toCount;
        if (all) {
            toCount = (sjukfall, counter) -> counter.addAll(new HashSet<>(sjukfall.getSjukskrivningsgrader()));
        } else {
            toCount = (sjukfall, counter) -> counter.add(sjukfall.getSjukskrivningsgrad());
        }

        return sjukfallUtil.calculateKonDataResponse(aisle, filter, start, periods, periodSize, GRAD_LABEL, GRAD, toCount);
    }

    private static int percentChange(int current, int previous) {
        if (previous == 0) {
            return 0;
        } else {
            return (current - previous) * PERCENT / previous;
        }
    }

    public static SimpleKonResponse getSjukskrivningsgradTvarsnitt(Aisle aisle, FilterPredicates filter, LocalDate from,
        int periods, int periodLength, SjukfallUtil sjukfallUtil) {
        final CounterFunction<Integer> toCount = (sjukfall, counter) -> counter.addAll(new HashSet<>(sjukfall.getSjukskrivningsgrader()));

        return sjukfallUtil.calculateSimpleKonResponse(aisle, filter, from, periods, periodLength, toCount, GRAD);
    }

}
