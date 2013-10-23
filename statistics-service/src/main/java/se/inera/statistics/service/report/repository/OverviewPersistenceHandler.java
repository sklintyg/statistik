package se.inera.statistics.service.report.repository;

import org.springframework.transaction.annotation.Transactional;
import se.inera.statistics.service.report.api.Overview;
import se.inera.statistics.service.report.model.*;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.report.util.Verksamhet;

import java.util.Collections;
import java.util.List;

public class OverviewPersistenceHandler extends OverviewBasePersistenceHandler implements Overview {
    private static final String NATIONELL = Verksamhet.NATIONELL.name();
    private static final int DISPLAYED_DIAGNOSIS_GROUPS = 5;
    private static final int DISPLAYED_AGE_GROUPS = 7;
    private static final int DISPLAYED_SJUKFALLSLANGD_GROUPS = 5;
    private static final int LONG_SICKLEAVE_CUTOFF = 90;

    @Transactional
    @Override
    public OverviewResponse getOverview(Range range) {
        OverviewSexProportion sexProportion = getSexProportion(NATIONELL, range);
        List<OverviewChartRowExtended> diagnosisGroups = getDiagnosisGroups(NATIONELL, range, DISPLAYED_DIAGNOSIS_GROUPS);
        List<OverviewChartRowExtended> ageGroups = getAgeGroups(NATIONELL, range, DISPLAYED_AGE_GROUPS);
        List<OverviewChartRowExtended> degreeOfSickLeaveGroups = getDegreeOfSickLeaveGroups(NATIONELL, range);
        List<OverviewChartRow> sickLeaveLengthGroups = getSickLeaveLengthGroups(NATIONELL, range, DISPLAYED_SJUKFALLSLANGD_GROUPS);
        int longSickLeaves = getLongSickLeaves(NATIONELL, range, LONG_SICKLEAVE_CUTOFF);
        int longSickLeavesPrevious = getLongSickLeaves(NATIONELL, ReportUtil.getPreviousPeriod(range), LONG_SICKLEAVE_CUTOFF);
        int longSickLeavesChange = changeInPercent(longSickLeaves, longSickLeavesPrevious);
        int casesPerMonthChange = getCasesPerMonth(NATIONELL, range);
        List<OverviewChartRowExtended> perCounty = getCasesPerCounty(range);

        return new OverviewResponse(sexProportion, casesPerMonthChange, diagnosisGroups, ageGroups, degreeOfSickLeaveGroups, sickLeaveLengthGroups, longSickLeaves, longSickLeavesChange, perCounty);
    }

    private List<OverviewChartRowExtended> getCasesPerCounty(Range range) {
        return Collections.emptyList();
    }


}
