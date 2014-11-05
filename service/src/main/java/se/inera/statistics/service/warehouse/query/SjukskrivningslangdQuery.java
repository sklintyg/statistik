package se.inera.statistics.service.warehouse.query;

import com.google.common.base.Predicate;
import org.joda.time.LocalDate;
import se.inera.statistics.service.report.model.OverviewChartRow;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.report.model.SjukfallslangdResponse;
import se.inera.statistics.service.report.model.SjukfallslangdRow;
import se.inera.statistics.service.report.util.Ranges;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.report.util.SjukfallslangdUtil;
import se.inera.statistics.service.warehouse.Aisle;
import se.inera.statistics.service.warehouse.Fact;
import se.inera.statistics.service.warehouse.Sjukfall;
import se.inera.statistics.service.warehouse.SjukfallUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public final class SjukskrivningslangdQuery {
    private static Ranges ranges = SjukfallslangdUtil.RANGES;
    private static final int LONG_SJUKFALL = 90;

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
        for (Ranges.Range range : ranges) {
            if (rowsToKeep.contains(range)) {
                result.add(map.get(range));
            }
        }

        return result;
    }

    public static Map<Ranges.Range, Counter<Ranges.Range>> count(Collection<Sjukfall> sjukfalls) {
        Map<Ranges.Range, Counter<Ranges.Range>> counters = Counter.mapFor(SjukfallslangdUtil.RANGES);
        for (Sjukfall sjukfall : sjukfalls) {
            Counter counter = counters.get(ranges.rangeFor(sjukfall.getRealDays()));
            counter.increase(sjukfall);
        }
        return counters;
    }

    public static SimpleKonResponse<SimpleKonDataRow> getLangaSjukfall(Aisle aisle, Predicate<Fact> filter, LocalDate from, int periods, int periodLength) {
        List<SimpleKonDataRow> rows = new ArrayList<>();
        for (SjukfallUtil.SjukfallGroup sjukfallGroup: SjukfallUtil.sjukfallGrupper(from, periods, periodLength, aisle, filter)) {
            Counter counter = new Counter("");
            for (Sjukfall sjukfall: sjukfallGroup.getSjukfall()) {
                if (sjukfall.getRealDays() > LONG_SJUKFALL) {
                    counter.increase(sjukfall);
                }
            }
            rows.add(new SimpleKonDataRow(ReportUtil.toPeriod(sjukfallGroup.getRange().getFrom()), counter.getCountFemale(), counter.getCountMale()));
        }

        return new SimpleKonResponse<>(rows, periods);
    }

    public static SjukfallslangdResponse getSjuksrivningslangd(Aisle aisle, Predicate<Fact> filter, LocalDate from, int periods, int periodLength) {
        List<SjukfallslangdRow> rows = new ArrayList<>();
        for (SjukfallUtil.SjukfallGroup sjukfallGroup: SjukfallUtil.sjukfallGrupper(from, periods, periodLength, aisle, filter)) {
            Map<Ranges.Range, Counter<Ranges.Range>> counterMap = SjukskrivningslangdQuery.count(sjukfallGroup.getSjukfall());
            for (Ranges.Range i : SjukfallslangdUtil.RANGES) {
                Counter<Ranges.Range> counter = counterMap.get(i);
                rows.add(new SjukfallslangdRow(i.getName(), counter.getCountFemale(), counter.getCountMale()));
            }
        }
        return new SjukfallslangdResponse(rows, periodLength);

    }
}
