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
        List<Counter<Integer>> currentCount = count(currentSjukfall);
        Map<Integer, Counter<Integer>> previousCount = count2(previousSjukfall);

        List<OverviewChartRowExtended> result = new ArrayList<>();

        for (Counter<Integer> count : currentCount) {
            int current = count.getCount();
            int previous = previousCount.get(count.getKey()).getCount();
            result.add(new OverviewChartRowExtended(count.getKey().toString(), current, current - previous));
        }

        return result;
    }

    public static List<Counter<Integer>> count(Collection<Sjukfall> sjukfalls) {
        Map<Integer, Counter<Integer>> map = count2(sjukfalls);
        List<Counter<Integer>> result = new ArrayList<>();

        for (Integer range : GRADER) {
            result.add(map.get(range));
        }

        return result;
    }

    private static Map<Integer, Counter<Integer>> count2(Collection<Sjukfall> sjukfalls) {
        Map<Integer, Counter<Integer>> counters = createCounters();
        for (Sjukfall sjukfall : sjukfalls) {
            Counter counter = counters.get(sjukfall.getSjukskrivningsgrad());
            counter.increase();
        }
        return counters;
    }

    private static Map<Integer, Counter<Integer>> createCounters() {
        Map<Integer, Counter<Integer>> counters = new HashMap<>();
        for (Integer range : GRADER) {
            counters.put(range, new Counter<>(range));
        }
        return counters;
    }

}
