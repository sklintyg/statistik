package se.inera.statistics.service.warehouse.query;

import se.inera.statistics.service.report.model.OverviewChartRow;
import se.inera.statistics.service.report.util.Ranges;
import se.inera.statistics.service.report.util.SjukfallslangdUtil;
import se.inera.statistics.service.warehouse.Sjukfall;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public final class SjukskrivningslangdQuery {
    private static Ranges ranges = SjukfallslangdUtil.RANGES;

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
        List<Counter<Ranges.Range>> sorted = new ArrayList<>();
        for (Counter counter : count.values()) {
            sorted.add(counter);
        }
        Collections.sort(sorted);

        Collection<Ranges.Range> result = new HashSet<>();
        for (Counter<Ranges.Range> counter : sorted) {
            result.add(counter.getKey());
            if (--noOfRows == 0) {
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
        Map<Ranges.Range, Counter<Ranges.Range>> counters = createCounters();
        for (Sjukfall sjukfall : sjukfalls) {
            Counter counter = counters.get(ranges.rangeFor(sjukfall.getRealDays()));
            counter.increase();
        }
        return counters;
    }

    private static Map<Ranges.Range, Counter<Ranges.Range>> createCounters() {
        Map<Ranges.Range, Counter<Ranges.Range>> counters = new HashMap();
        for (Ranges.Range range : ranges) {
            counters.put(range, new Counter(range));
        }
        return counters;
    }

}
