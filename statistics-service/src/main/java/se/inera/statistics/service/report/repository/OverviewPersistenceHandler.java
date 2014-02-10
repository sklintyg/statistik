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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import se.inera.statistics.service.report.api.Overview;
import se.inera.statistics.service.report.model.Lan;
import se.inera.statistics.service.report.model.OverviewChartRow;
import se.inera.statistics.service.report.model.OverviewChartRowExtended;
import se.inera.statistics.service.report.model.OverviewResponse;
import se.inera.statistics.service.report.model.OverviewSexProportion;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.report.util.Verksamhet;

import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OverviewPersistenceHandler extends OverviewBasePersistenceHandler implements Overview {
    private static final String NATIONELL = Verksamhet.NATIONELL.name();
    private static final int DISPLAYED_DIAGNOSIS_GROUPS = 5;
    private static final int DISPLAYED_LANS = 5;
    private static final int DISPLAYED_AGE_GROUPS = 7;
    private static final int DISPLAYED_SJUKFALLSLANGD_GROUPS = 5;
    private static final int LONG_SICKLEAVE_CUTOFF = 91;

    @Autowired
    private Lan lan;

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
        int casesPerMonth = getCasesPerMonth(NATIONELL, range);
        int casesPerMonthPrevious = getCasesPerMonth(NATIONELL, ReportUtil.getPreviousPeriod(range));
        int casesPerMonthChange = changeInPercent(casesPerMonth, casesPerMonthPrevious);
        List<OverviewChartRowExtended> perCounty = getCasesPerCounty(range);

        return new OverviewResponse(sexProportion, casesPerMonthChange, diagnosisGroups, ageGroups, degreeOfSickLeaveGroups, sickLeaveLengthGroups, longSickLeaves, longSickLeavesChange, perCounty);
    }

    private List<OverviewChartRowExtended> getCasesPerCounty(Range range) {
        List<OverviewChartRow> rows = getCasesPerCountyFromDb(range);
        List<OverviewChartRow> rowsForPreviousPeriod = getCasesPerCountyFromDb(ReportUtil.getPreviousPeriod(range));

        Collections.sort(rows, CHART_ROW_COMPARATOR);

        List<OverviewChartRowExtended> result = new ArrayList<>();

        for (int i = 0; i < rows.size() && i < DISPLAYED_LANS; i++) {
            OverviewChartRow row = rows.get(i);
            String name = lan.getNamn(row.getName());
            int change = lookupChange(row, rowsForPreviousPeriod);
            result.add(new OverviewChartRowExtended(name, row.getQuantity(), change));
        }

        sortWithCollation(result);

        return result;
    }

    private List<OverviewChartRow> getCasesPerCountyFromDb(Range range) {
        TypedQuery<OverviewChartRow> query = getManager().createQuery("SELECT new se.inera.statistics.service.report.model.OverviewChartRow(c.key.lanId, SUM (c.female) + SUM (c.male)) FROM SjukfallPerLanRow c WHERE c.key.period = :to GROUP BY c.key.lanId", OverviewChartRow.class);
        query.setParameter("to", ReportUtil.toPeriod(range.getTo()));

        return query.getResultList();
    }
}
