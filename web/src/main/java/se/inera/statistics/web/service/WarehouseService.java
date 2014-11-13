package se.inera.statistics.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import se.inera.statistics.service.report.model.DiagnosgruppResponse;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.report.model.SjukfallslangdResponse;
import se.inera.statistics.service.report.model.SjukskrivningsgradResponse;
import se.inera.statistics.service.report.model.VerksamhetOverviewResponse;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.warehouse.query.AldersgruppQuery;
import se.inera.statistics.service.warehouse.SjukfallUtil;
import se.inera.statistics.service.warehouse.Warehouse;
import se.inera.statistics.service.warehouse.query.DiagnosgruppQuery;
import se.inera.statistics.service.warehouse.query.OverviewQuery;
import se.inera.statistics.service.warehouse.query.SjukfallQuery;
import se.inera.statistics.service.warehouse.query.SjukskrivningsgradQuery;
import se.inera.statistics.service.warehouse.query.SjukskrivningslangdQuery;

public class WarehouseService {

    @Autowired
    private Warehouse warehouse;

    @Autowired
    private DiagnosgruppQuery query;

    @Autowired
    private OverviewQuery overviewQuery;

    @Autowired
    private SjukfallQuery sjukfallQuery;

    public VerksamhetOverviewResponse getOverview(SjukfallUtil.FactFilter filter, Range range, String vardgivarId) {
        VerksamhetOverviewResponse overview = overviewQuery.getOverview(warehouse.get(vardgivarId), filter, ReportUtil.getPreviousPeriod(range).getFrom(), range.getMonths());

        return new VerksamhetOverviewResponse(overview.getTotalCases(), overview.getCasesPerMonthSexProportionPreviousPeriod(), overview.getCasesPerMonthSexProportionBeforePreviousPeriod(),
                new DiagnosisGroupsConverter().convert(overview.getDiagnosisGroups()), overview.getAgeGroups(), overview.getDegreeOfSickLeaveGroups(), overview.getSickLeaveLengthGroups(),
                overview.getLongSickLeavesTotal(), overview.getLongSickLeavesAlternation());
    }

    public SimpleKonResponse<SimpleKonDataRow> getCasesPerMonth(SjukfallUtil.FactFilter filter, Range range, String vardgivarId) {
        return sjukfallQuery.getSjukfall(warehouse.get(vardgivarId), filter, range.getFrom(), range.getMonths(), 1);
    }

    public DiagnosgruppResponse getDiagnosgrupperPerMonth(SjukfallUtil.FactFilter filter, Range range, String vardgivarId) {
        return query.getDiagnosgrupper(warehouse.get(vardgivarId), filter, range.getFrom(), range.getMonths(), 1);
    }

    public SjukskrivningsgradResponse getSjukskrivningsgradPerMonth(SjukfallUtil.FactFilter filter, Range range, String vardgivarId) {
        return SjukskrivningsgradQuery.getSjukskrivningsgrad(warehouse.get(vardgivarId), filter, range.getFrom(), range.getMonths(), 1);
    }

    public SjukfallslangdResponse getSjukskrivningslangd(SjukfallUtil.FactFilter filter, Range range, String vardgivarId) {
        return SjukskrivningslangdQuery.getSjuksrivningslangd(warehouse.get(vardgivarId), filter, range.getFrom(), 1, range.getMonths());
    }

    public SimpleKonResponse<SimpleKonDataRow> getLangaSjukskrivningarPerManad(SjukfallUtil.FactFilter filter, Range range, String vardgivarId) {
        return SjukskrivningslangdQuery.getLangaSjukfall(warehouse.get(vardgivarId), filter, range.getFrom(), range.getMonths(), 1);
    }

    public SimpleKonResponse<SimpleKonDataRow> getAldersgrupper(SjukfallUtil.FactFilter filter, Range range, String vardgivarId) {
        return AldersgruppQuery.getAldersgrupper(warehouse.get(vardgivarId), filter, range.getFrom(), 1, range.getMonths());
    }

    public DiagnosgruppResponse getUnderdiagnosgrupper(SjukfallUtil.FactFilter filter, Range range, String kapitelId, String vardgivarId) {
        return query.getUnderdiagnosgrupper(warehouse.get(vardgivarId), filter, range.getFrom(), range.getMonths(), 1, kapitelId);
    }

    public SimpleKonResponse<SimpleKonDataRow> getCasesPerEnhet(SjukfallUtil.EnhetFilter filter, Range range, String vardgivarId) {
        return sjukfallQuery.getSjukfallPerEnhet(warehouse.get(vardgivarId), filter, range, range.getMonths(), 1);
    }

    public SimpleKonResponse<SimpleKonDataRow> getCasesPerLakare(SjukfallUtil.EnhetFilter filter, Range range, String vardgivarId) {
        return sjukfallQuery.getSjukfallPerLakare(vardgivarId, warehouse.get(vardgivarId), filter, range, range.getMonths(), 1);
    }

}
