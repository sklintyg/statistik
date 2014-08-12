package se.inera.statistics.service.warehouse.query;

import se.inera.statistics.service.report.model.OverviewChartRowExtended;
import se.inera.statistics.service.warehouse.Sjukfall;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class SjukskrivningsgradQuery {
    private static final List<Integer> GRADER = Arrays.asList(25, 50, 75, 100);

    private SjukskrivningsgradQuery() {
    }

    public static List<OverviewChartRowExtended> getOverviewSjukskrivningsgrad(Collection<Sjukfall> currentSjukfall, Collection<Sjukfall> previousSjukfall) {
        Map<Integer, Counter<Integer>> currentCount = count2(currentSjukfall);
        Map<Integer, Counter<Integer>> previousCount = count2(previousSjukfall);

        List<OverviewChartRowExtended> result = new ArrayList<>();

        for (Integer range : GRADER) {
            int current = currentCount.get(range).getCount();
            int previous = previousCount.get(range).getCount();
            result.add(new OverviewChartRowExtended(range.toString(), current, current - previous));
        }

        return result;
    }

    public static List<Counter<Integer>> count(Collection<Sjukfall> sjukfalls) {
        Map<Integer, Counter<Integer>> counters = count2(sjukfalls);

        List<Counter<Integer>> result = new ArrayList<>();
        for (Integer range : GRADER) {
            result.add(counters.get(range));
        }

        return result;
    }

    private static Map<Integer, Counter<Integer>> count2(Collection<Sjukfall> sjukfalls) {
        Map<Integer, Counter<Integer>> counters = Counter.mapFor(GRADER);
        for (Sjukfall sjukfall : sjukfalls) {
            Counter counter = counters.get(sjukfall.getSjukskrivningsgrad());
            counter.increase(sjukfall);
        }
        return counters;
    }
}
