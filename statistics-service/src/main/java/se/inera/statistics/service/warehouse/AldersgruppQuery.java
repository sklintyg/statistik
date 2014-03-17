package se.inera.statistics.service.warehouse;

import se.inera.statistics.service.report.model.OverviewChartRowExtended;
import se.inera.statistics.service.report.util.AldersgroupUtil;
import se.inera.statistics.service.report.util.Ranges;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class AldersgruppQuery {
        private static Ranges ranges = AldersgroupUtil.RANGES;

    public static List<OverviewChartRowExtended> getOverviewAldersgrupper(Collection<Sjukfall> currentSjukfall, Collection<Sjukfall> previousSjukfall, int noOfRows) {

        Map<Ranges.Range, Counter> currentCount = count(currentSjukfall);
        Map<Ranges.Range, Counter> previousCount = count(previousSjukfall);

        Collection<Ranges.Range> toKeep = rowsToKeep(currentCount, noOfRows);

        List<OverviewChartRowExtended> result = new ArrayList<>();
        for (Ranges.Range range : ranges) {
            if (toKeep.contains(range)) {
                int current = currentCount.get(range).count;
                int previous = previousCount.get(range).count;
                result.add(new OverviewChartRowExtended(range.getName(), current, current - previous ));
            }
        }

        return result;
    }

    private static Collection<Ranges.Range> rowsToKeep(Map<Ranges.Range, Counter> count, int noOfRows) {
        List<Counter> sorted = new ArrayList<>();
        for (Counter counter : count.values()) {
            sorted.add(counter);
        }
        Collections.sort(sorted);

        Collection<Ranges.Range> result = new HashSet<>();
        for (Counter counter : sorted) {
            result.add(counter.range);
            if (--noOfRows == 0) {
                break;
            }
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
            counters.put(range, new Counter(range));
        }
        return counters;
    }

    private static class Counter implements Comparable<Counter> {

        private final Ranges.Range range;
        private int count;

        public Counter(Ranges.Range range) {
            this.range = range;
        }

        public void increase() {
            count++;
        }

        @Override
        public int compareTo(Counter other) {
            if (count < other.count) {
                return -1;
            } else if (count == other.count) {
                return 0;
            } else {
                return 1;
            }
        }
    }
}
