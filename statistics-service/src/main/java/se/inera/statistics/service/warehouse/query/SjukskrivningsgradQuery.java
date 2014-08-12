package se.inera.statistics.service.warehouse.query;

import org.joda.time.LocalDate;
import se.inera.statistics.service.report.model.KonDataRow;
import se.inera.statistics.service.report.model.KonField;
import se.inera.statistics.service.report.model.OverviewChartRowExtended;
import se.inera.statistics.service.report.model.SjukskrivningsgradResponse;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.warehouse.Aisle;
import se.inera.statistics.service.warehouse.Sjukfall;
import se.inera.statistics.service.warehouse.SjukfallUtil;
import se.inera.statistics.service.warehouse.Warehouse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class SjukskrivningsgradQuery {
    private static final List<String> GRAD_LABEL = Collections.unmodifiableList(Arrays.asList("25", "50", "75", "100"));
    private static final List<Integer> GRAD = Collections.unmodifiableList(Arrays.asList(25, 50, 75, 100));


    private SjukskrivningsgradQuery() {
    }

    public static List<OverviewChartRowExtended> getOverviewSjukskrivningsgrad(Collection<Sjukfall> currentSjukfall, Collection<Sjukfall> previousSjukfall) {
        Map<Integer, Counter<Integer>> currentCount = count2(currentSjukfall);
        Map<Integer, Counter<Integer>> previousCount = count2(previousSjukfall);

        List<OverviewChartRowExtended> result = new ArrayList<>();

        for (Integer range : GRAD) {
            int current = currentCount.get(range).getCount();
            int previous = previousCount.get(range).getCount();
            result.add(new OverviewChartRowExtended(range.toString(), current, current - previous));
        }

        return result;
    }

    public static List<Counter<Integer>> count(Collection<Sjukfall> sjukfalls) {
        Map<Integer, Counter<Integer>> counters = count2(sjukfalls);

        List<Counter<Integer>> result = new ArrayList<>();
        for (Integer range : GRAD) {
            result.add(counters.get(range));
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

    public static SjukskrivningsgradResponse getSjukskrivningsgrad(Warehouse warehouse, SjukfallUtil.StartFilter filter, LocalDate start, int periods, int periodSize, String vardgivarId) {
        Aisle aisle = warehouse.get(vardgivarId);

        List<KonDataRow> rows = new ArrayList<>();
        for (SjukfallUtil.SjukfallGroup sjukfallGroup: SjukfallUtil.sjukfallGrupper(start, periods, periodSize, aisle, filter)) {
            Map<Integer, Counter<Integer>> counters = Counter.mapFor(GRAD);
            for (Sjukfall sjukfall : sjukfallGroup.getSjukfall()) {
                counters.get(sjukfall.getSjukskrivningsgrad()).increase(sjukfall);
            }
            List<KonField> list = new ArrayList<>(GRAD.size());
            for (int i: GRAD) {
                list.add(new KonField(counters.get(i).getCountFemale(), counters.get(i).getCountMale()));
            }
            rows.add(new KonDataRow(ReportUtil.toPeriod(sjukfallGroup.getRange().getFrom()), list));
        }

        return new SjukskrivningsgradResponse(GRAD_LABEL, rows);

    }
}
