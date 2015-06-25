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
import org.joda.time.LocalDate;
import se.inera.statistics.service.report.model.KonDataResponse;
import se.inera.statistics.service.report.model.OverviewChartRowExtended;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.warehouse.Aisle;
import se.inera.statistics.service.warehouse.Sjukfall;
import se.inera.statistics.service.warehouse.SjukfallFilter;
import se.inera.statistics.service.warehouse.SjukfallUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class SjukskrivningsgradQuery {
    public static final List<String> GRAD_LABEL = Collections.unmodifiableList(Arrays.asList("25", "50", "75", "100"));
    private static final List<Integer> GRAD = Collections.unmodifiableList(Arrays.asList(25, 50, 75, 100));
    public static final int PERCENT = 100;


    private SjukskrivningsgradQuery() {
    }

    public static List<OverviewChartRowExtended> getOverviewSjukskrivningsgrad(Collection<Sjukfall> currentSjukfall, Collection<Sjukfall> previousSjukfall) {
        Map<Integer, Counter<Integer>> currentCount = count2(currentSjukfall);
        Map<Integer, Counter<Integer>> previousCount = count2(previousSjukfall);

        List<OverviewChartRowExtended> result = new ArrayList<>();

        for (Integer range : GRAD) {
            int current = currentCount.get(range).getCount();
            int previous = previousCount.get(range).getCount();
            result.add(new OverviewChartRowExtended(range.toString() + "%", current, percentChange(current, previous)));
        }

        return result;
    }

    private static Map<Integer, Counter<Integer>> count2(Collection<Sjukfall> sjukfalls) {
        Map<Integer, Counter<Integer>> counters = Counter.mapFor(GRAD);
        for (Sjukfall sjukfall : sjukfalls) {
            Counter counter = counters.get(sjukfall.getSjukskrivningsgrad());
            counter.increase(sjukfall);
        }
        return counters;
    }

    public static KonDataResponse getSjukskrivningsgrad(Aisle aisle, SjukfallFilter filter, LocalDate start, int periods, int periodSize, SjukfallUtil sjukfallUtil) {
        return sjukfallUtil.calculateKonDataResponse(aisle, filter, start, periods, periodSize, GRAD_LABEL, GRAD, new CounterFunction<Integer>() {
            @Override
            public void addCount(Sjukfall sjukfall, HashMultiset<Integer> counter) {
                counter.add(sjukfall.getSjukskrivningsgrad());
            }
        });
    }

    private static int percentChange(int current, int previous) {
        if (previous == 0) {
            return 0;
        } else {
            return (current - previous) * PERCENT / previous;
        }
    }

    public static SimpleKonResponse<SimpleKonDataRow> getSjukskrivningsgradTvarsnitt(Aisle aisle, SjukfallFilter filter, LocalDate from, int periods, int periodLength, SjukfallUtil sjukfallUtil) {
        final Function<Sjukfall, Integer> toCount = new Function<Sjukfall, Integer>() {
            @Override
            public Integer apply(Sjukfall sjukfall) {
                return sjukfall.getSjukskrivningsgrad();
            }
        };
        return sjukfallUtil.calculateSimpleKonResponse(aisle, filter, from, periods, periodLength, toCount, GRAD);
    }

}
