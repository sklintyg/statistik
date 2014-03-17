package se.inera.statistics.service.warehouse;

import se.inera.statistics.service.report.model.OverviewChartRowExtended;
import se.inera.statistics.service.report.util.AldersgroupUtil;
import se.inera.statistics.service.report.util.Ranges;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AldersgruppQuery {
        private static Ranges ranges = AldersgroupUtil.RANGES;

    public static List<OverviewChartRowExtended> doIt(Collection<Sjukfall> currentSjukfall, Collection<Sjukfall> previousSjukfall) {

        Map<Ranges.Range, Counter> currentCount = count(currentSjukfall);
        Map<Ranges.Range, Counter> previousCount = count(previousSjukfall);

        List<OverviewChartRowExtended> result = new ArrayList<>();
        for (Ranges.Range range : ranges) {
            int current = currentCount.get(range).count;
            int previous = previousCount.get(range).count;
            result.add(new OverviewChartRowExtended(range.getName(), current, current - previous ));
        }

        return result;
    }

    private static Map<Ranges.Range, Counter> count(Collection<Sjukfall> sjukfalls) {
        Map<Ranges.Range, Counter> counters = createCounters();
        for (Sjukfall sjukfall : sjukfalls) {
            Counter counter = counters.get(ranges.rangeFor(sjukfall.alder));
            counter.increase();
        }
        return counters;
    }

    private static Map<Ranges.Range, Counter> createCounters() {
        Map<Ranges.Range, Counter> counters = new HashMap();
        for (Ranges.Range range : ranges) {
            counters.put(range, new Counter());
        }
        return counters;
    }

    private static class Counter {

        private int count;
        public void increase() {
            count++;
        }
    }
}
