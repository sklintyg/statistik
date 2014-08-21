package se.inera.statistics.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import se.inera.statistics.service.report.model.DiagnosgruppResponse;
import se.inera.statistics.service.report.model.OverviewChartRow;
import se.inera.statistics.service.report.model.OverviewChartRowExtended;
import se.inera.statistics.service.report.model.OverviewKonsfordelning;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.report.model.SjukfallslangdResponse;
import se.inera.statistics.service.report.model.SjukskrivningsgradResponse;
import se.inera.statistics.service.report.model.VerksamhetOverviewResponse;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.warehouse.Aisle;
import se.inera.statistics.service.warehouse.query.AldersgruppQuery;
import se.inera.statistics.service.warehouse.Sjukfall;
import se.inera.statistics.service.warehouse.SjukfallUtil;
import se.inera.statistics.service.warehouse.Warehouse;
import se.inera.statistics.service.warehouse.query.DiagnosgruppQuery;
import se.inera.statistics.service.warehouse.query.SjukfallQuery;
import se.inera.statistics.service.warehouse.query.SjukskrivningsgradQuery;
import se.inera.statistics.service.warehouse.query.SjukskrivningslangdQuery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class WarehouseService {

    private static final int DISPLAYED_AGE_GROUPS = 7;
    public static final int PERCENT = 100;

    @Autowired
    private Warehouse warehouse;

    @Autowired
    private DiagnosgruppQuery query;

    public VerksamhetOverviewResponse getOverview(SjukfallUtil.StartFilter filter, Range range, String vardgivarId) {
        Aisle aisle = warehouse.get(vardgivarId);

        Range previousRange = ReportUtil.getPreviousPeriod(range);
        Iterator<SjukfallUtil.SjukfallGroup> groupIterator = SjukfallUtil.sjukfallGrupper(previousRange.getFrom(), 2, previousRange.getMonths(), aisle, filter).iterator();

        SjukfallUtil.SjukfallGroup previousSjukfall = groupIterator.next();
        SjukfallUtil.SjukfallGroup currentSjukfall = groupIterator.next();

        OverviewKonsfordelning previousKonsfordelning = getOverviewKonsfordelning(previousSjukfall.getRange(), previousSjukfall.getSjukfall());
        OverviewKonsfordelning currentKonsfordelning = getOverviewKonsfordelning(currentSjukfall.getRange(), currentSjukfall.getSjukfall());


        int currentLongSjukfall = SjukfallUtil.getLong(currentSjukfall.getSjukfall());
        int previousLongSjukfall = SjukfallUtil.getLong(previousSjukfall.getSjukfall());

        List<OverviewChartRowExtended> aldersgrupper = AldersgruppQuery.getOverviewAldersgrupper(currentSjukfall.getSjukfall(), previousSjukfall.getSjukfall(), DISPLAYED_AGE_GROUPS);
        List<OverviewChartRowExtended> diagnosgrupper = new DiagnosisGroupsConverter().convert(query.getOverviewDiagnosgrupper(currentSjukfall.getSjukfall(), previousSjukfall.getSjukfall(), Integer.MAX_VALUE));
        List<OverviewChartRowExtended> sjukskrivningsgrad = SjukskrivningsgradQuery.getOverviewSjukskrivningsgrad(currentSjukfall.getSjukfall(), previousSjukfall.getSjukfall());
        List<OverviewChartRow> sjukskrivningslangd = SjukskrivningslangdQuery.getOverviewSjukskrivningslangd(currentSjukfall.getSjukfall(), Integer.MAX_VALUE);

        return new VerksamhetOverviewResponse(currentSjukfall.getSjukfall().size(), currentKonsfordelning, previousKonsfordelning,
                diagnosgrupper, aldersgrupper, sjukskrivningsgrad, sjukskrivningslangd,
                currentLongSjukfall, percentChange(currentLongSjukfall, previousLongSjukfall));
    }

    private static int percentChange(int current, int previous) {
        if (previous == 0) {
            return 0;
        } else {
            return (current - previous) * PERCENT / previous;
        }
    }

    OverviewKonsfordelning getOverviewKonsfordelning(Range range, Collection<Sjukfall> sjukfalls) {
        int male = countMale(sjukfalls);
        int female = sjukfalls.size() - male;

        return new OverviewKonsfordelning(male, female, range);
    }

    public static int countMale(Collection<Sjukfall> sjukfalls) {
        int count = 0;
        for (Sjukfall sjukfall : sjukfalls) {
            if (sjukfall.getKon() == 0) {
                count++;
            }
        }
        return count;
    }

    public SimpleKonResponse<SimpleKonDataRow> getCasesPerMonth(SjukfallUtil.StartFilter filter, Range range, String vardgivarId) {
        return SjukfallQuery.getSjukfall(warehouse.get(vardgivarId), filter, range.getFrom(), range.getMonths(), 1);
    }

    public DiagnosgruppResponse getDiagnosgrupperPerMonth(SjukfallUtil.StartFilter filter, Range range, String vardgivarId) {
        return query.getDiagnosgrupper(warehouse.get(vardgivarId), filter, range.getFrom(), range.getMonths(), 1);
    }

    public SjukskrivningsgradResponse getSjukskrivningsgradPerMonth(SjukfallUtil.StartFilter filter, Range range, String vardgivarId) {
        return SjukskrivningsgradQuery.getSjukskrivningsgrad(warehouse.get(vardgivarId), filter, range.getFrom(), range.getMonths(), 1);
    }

    public SjukfallslangdResponse getSjukskrivningslangd(SjukfallUtil.StartFilter filter, Range range, String vardgivarId) {
        return SjukskrivningslangdQuery.getSjuksrivningslangd(warehouse.get(vardgivarId), filter, range.getFrom(), 1, range.getMonths());
    }

    public SimpleKonResponse<SimpleKonDataRow> getLangaSjukskrivningarPerManad(SjukfallUtil.StartFilter filter, Range range, String vardgivarId) {
        return SjukskrivningslangdQuery.getLangaSjukfall(warehouse.get(vardgivarId), filter, range.getFrom(), range.getMonths(), 1);
    }

    public SimpleKonResponse<SimpleKonDataRow> getAldersgrupper(SjukfallUtil.StartFilter filter, Range range, String vardgivarId) {
        return AldersgruppQuery.getAldersgrupper(warehouse.get(vardgivarId), filter, range.getFrom(), 1, range.getMonths());
    }

    public DiagnosgruppResponse getDiagnosavsnitt(SjukfallUtil.StartFilter filter, Range range, String kapitelId, String vardgivarId) {
        return query.getDiagnosavsnitt(warehouse.get(vardgivarId), filter, range.getFrom(), range.getMonths(), 1, kapitelId);
    }
}
