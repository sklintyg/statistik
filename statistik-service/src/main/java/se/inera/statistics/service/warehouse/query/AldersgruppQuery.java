package se.inera.statistics.service.warehouse.query;

import com.google.common.base.Predicate;
import org.joda.time.LocalDate;
import se.inera.statistics.service.report.model.OverviewChartRowExtended;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.report.util.AldersgroupUtil;
import se.inera.statistics.service.report.util.Ranges;
import se.inera.statistics.service.warehouse.Aisle;
import se.inera.statistics.service.warehouse.Fact;
import se.inera.statistics.service.warehouse.Sjukfall;
import se.inera.statistics.service.warehouse.SjukfallGroup;
import se.inera.statistics.service.warehouse.SjukfallUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public final class AldersgruppQuery {
    private static final Ranges RANGES = AldersgroupUtil.RANGES;
    public static final int PERCENT = 100;

    private AldersgruppQuery() {
    }

    public static List<OverviewChartRowExtended> getOverviewAldersgrupper(Collection<Sjukfall> currentSjukfall, Collection<Sjukfall> previousSjukfall, int noOfRows) {
        Map<Ranges.Range, Counter<Ranges.Range>> previousCount = count(previousSjukfall);

        List<Counter<Ranges.Range>> toKeep = count(currentSjukfall, noOfRows);

        List<OverviewChartRowExtended> result = new ArrayList<>();

        for (Counter<Ranges.Range> counter : toKeep) {
            int current = counter.getCount();
            int previous = previousCount.get(counter.getKey()).getCount();
            result.add(new OverviewChartRowExtended(counter.getKey().getName() , current, percentChange(current, previous)));
        }

        return result;
    }

    private static int percentChange(int current, int previous) {
        if (previous == 0) {
            return 0;
        } else {
            return (current - previous) * PERCENT / previous;
        }
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
            if (result.size() == noOfRows) {
                break;
            }
        }

        return result;
    }

    public static List<Counter<Ranges.Range>> count(Collection<Sjukfall> sjukfalls, int noOfRows) {
        Map<Ranges.Range, Counter<Ranges.Range>> map = count(sjukfalls);
        List<Counter<Ranges.Range>> result = new ArrayList<>();

        Collection<Ranges.Range> rowsToKeep = rowsToKeep(map, noOfRows);
        for (Ranges.Range range : RANGES) {
            if (rowsToKeep.contains(range)) {
                result.add(map.get(range));
            }
        }

        return result;
    }

    public static Map<Ranges.Range, Counter<Ranges.Range>> count(Collection<Sjukfall> sjukfalls) {
        Map<Ranges.Range, Counter<Ranges.Range>> counters = Counter.mapFor(RANGES);
        for (Sjukfall sjukfall : sjukfalls) {
            Counter counter = counters.get(RANGES.rangeFor(sjukfall.getAlder()));
            counter.increase(sjukfall);
        }
        return counters;
    }

    public static SimpleKonResponse<SimpleKonDataRow> getAldersgrupper(Aisle aisle, Predicate<Fact> filter, LocalDate from, int periods, int periodLength) {
        List<SimpleKonDataRow> rows = new ArrayList<>();
        for (SjukfallGroup sjukfallGroup: SjukfallUtil.sjukfallGrupper(from, periods, periodLength, aisle, filter)) {
            Map<Ranges.Range, Counter<Ranges.Range>> counterMap = AldersgruppQuery.count(sjukfallGroup.getSjukfall());
            for (Ranges.Range i : AldersgroupUtil.RANGES) {
                Counter<Ranges.Range> counter = counterMap.get(i);
                rows.add(new SimpleKonDataRow(i.getName(), counter.getCountFemale(), counter.getCountMale()));
            }
        }

        return new SimpleKonResponse<>(rows, periodLength);

    }
}
