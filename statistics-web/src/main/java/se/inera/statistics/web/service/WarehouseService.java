package se.inera.statistics.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import se.inera.statistics.service.helper.ConversionHelper;
import se.inera.statistics.service.report.api.VerksamhetOverview;
import se.inera.statistics.service.report.model.OverviewChartRowExtended;
import se.inera.statistics.service.report.model.OverviewKonsfordelning;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.VerksamhetOverviewResponse;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.warehouse.Aisle;
import se.inera.statistics.service.warehouse.query.AldersgruppQuery;
import se.inera.statistics.service.warehouse.Sjukfall;
import se.inera.statistics.service.warehouse.SjukfallUtil;
import se.inera.statistics.service.warehouse.Warehouse;

import java.util.Collection;
import java.util.List;

public class WarehouseService {

    private static final int DISPLAYED_AGE_GROUPS = 7;

    @Autowired
    private Warehouse warehouse;

    @Autowired
    private VerksamhetOverview datasourceOverview;

    public VerksamhetOverviewResponse getOverview(String enhetId, Range range, String vardgivarId) {
        Aisle aisle = warehouse.get(vardgivarId);
        int numericalEnhetId = ConversionHelper.getEnhetAndRemember(enhetId);

        Collection<Sjukfall> currentSjukfall = SjukfallUtil.active(range, aisle, numericalEnhetId);
        OverviewKonsfordelning currentKonsfordelning = getOverviewKonsfordelning(range, currentSjukfall);

        Range previousRange = ReportUtil.getPreviousPeriod(range);
        Collection<Sjukfall> previousSjukfall = SjukfallUtil.active(previousRange, aisle, numericalEnhetId);
        OverviewKonsfordelning previousKonsfordelning = getOverviewKonsfordelning(previousRange, previousSjukfall);

        VerksamhetOverviewResponse removeOldSource = datasourceOverview.getOverview(enhetId, range);

        int currentLongSjukfall = SjukfallUtil.getLong(currentSjukfall);
        int previousLongSjukfall = SjukfallUtil.getLong(previousSjukfall);
        List<OverviewChartRowExtended> aldersgrupper = AldersgruppQuery.getOverviewAldersgrupper(currentSjukfall, previousSjukfall, DISPLAYED_AGE_GROUPS);
        System.err.println("aldersgrupper "+ aldersgrupper);
        return new VerksamhetOverviewResponse(currentSjukfall.size(), currentKonsfordelning, previousKonsfordelning,
                removeOldSource.getDiagnosisGroups(), aldersgrupper, removeOldSource.getDegreeOfSickLeaveGroups(), removeOldSource.getSickLeaveLengthGroups(),
                currentLongSjukfall, currentLongSjukfall - previousLongSjukfall);
    }

    OverviewKonsfordelning getOverviewKonsfordelning(Range range, Collection<Sjukfall> sjukfalls) {
        int male = countMale(sjukfalls);
        int female = sjukfalls.size() - male;

        return new OverviewKonsfordelning(male, female, range);
    }

    private int countMale(Collection<Sjukfall> sjukfalls) {
        int count = 0;
        for (Sjukfall sjukfall : sjukfalls) {
            if (sjukfall.getKon() == 0) {
                count++;
            }
        }
        return count;
    }
}
