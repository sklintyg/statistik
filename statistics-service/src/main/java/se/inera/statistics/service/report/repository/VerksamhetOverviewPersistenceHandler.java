/*
 * Copyright (C) 2013 - 2014 Inera AB (http://www.inera.se)
 *
 *     This file is part of Inera Statistics (http://code.google.com/p/inera-statistics/).
 *
 *     Inera Statistics is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Inera Statistics is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU LESSER GENERAL PUBLIC LICENSE for more details.
 *
 *     You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.inera.statistics.service.report.repository;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import se.inera.statistics.service.report.api.VerksamhetOverview;
import se.inera.statistics.service.report.model.OverviewChartRow;
import se.inera.statistics.service.report.model.OverviewChartRowExtended;
import se.inera.statistics.service.report.model.OverviewKonsfordelning;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.VerksamhetOverviewResponse;
import se.inera.statistics.service.report.util.ReportUtil;

public class VerksamhetOverviewPersistenceHandler extends OverviewBasePersistenceHandler implements VerksamhetOverview {

    private static final int DISPLAYED_DIAGNOSIS_GROUPS = 5;
    private static final int DISPLAYED_AGE_GROUPS = 7;
    private static final int DISPLAYED_SJUKFALLSLANGD_GROUPS = 5;
    private static final int LONG_SICKLEAVE_CUTOFF = 90;

    @Transactional
    @Override
    public VerksamhetOverviewResponse getOverview(String verksamhetId, Range range) {
        OverviewKonsfordelning sexProportion = getSexProportion(verksamhetId, range);
        OverviewKonsfordelning prevSexProportion = getSexProportion(verksamhetId, ReportUtil.getPreviousPeriod(range));
        List<OverviewChartRowExtended> diagnosisGroups = getDiagnosisGroups(verksamhetId, range, DISPLAYED_DIAGNOSIS_GROUPS);
        List<OverviewChartRowExtended> ageGroups = getAgeGroups(verksamhetId, range, DISPLAYED_AGE_GROUPS);
        List<OverviewChartRowExtended> degreeOfSickLeaveGroups = getDegreeOfSickLeaveGroups(verksamhetId, range);
        List<OverviewChartRow> sickLeaveLengthGroups = getSickLeaveLengthGroups(verksamhetId, range, DISPLAYED_SJUKFALLSLANGD_GROUPS);
        int longSickLeaves = getLongSickLeaves(verksamhetId, range, LONG_SICKLEAVE_CUTOFF);
        int longSickLeavesPrevious = getLongSickLeaves(verksamhetId, ReportUtil.getPreviousPeriod(range), LONG_SICKLEAVE_CUTOFF);
        int longSickLeavesChange = changeInPercent(longSickLeaves, longSickLeavesPrevious);
        int casesPerMonth = getCasesPerMonth(verksamhetId, range);

        return new VerksamhetOverviewResponse(casesPerMonth, sexProportion, prevSexProportion, diagnosisGroups, ageGroups, degreeOfSickLeaveGroups,
                sickLeaveLengthGroups, longSickLeaves, longSickLeavesChange);
    }

}
