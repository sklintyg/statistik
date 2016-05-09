/**
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
 *
 * This file is part of statistik (https://github.com/sklintyg/statistik).
 *
 * statistik is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * statistik is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.web.service;

import se.inera.statistics.service.report.model.OverviewChartRow;
import se.inera.statistics.service.report.model.OverviewChartRowExtended;
import se.inera.statistics.service.report.model.OverviewResponse;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.web.model.overview.BarChartData;
import se.inera.statistics.web.model.overview.DonutChartData;
import se.inera.statistics.web.model.overview.OverviewData;
import se.inera.statistics.web.model.overview.SickLeaveLengthOverview;
import se.inera.statistics.web.model.overview.SjukfallPerManadOverview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OverviewConverter {

    OverviewData convert(OverviewResponse resp, Range range) {
        final Range previousPeriod = new Range(range.getFrom().minusMonths(3), range.getTo().minusMonths(3));
        SjukfallPerManadOverview casesPerMonth = new SjukfallPerManadOverview(
                resp.getCasesPerMonthSexProportion().getMaleProportion(), resp.getCasesPerMonthSexProportion().getFemaleProportion(),
                resp.getCasesPerMonthAlteration(), previousPeriod.toString());

        ArrayList<DonutChartData> diagnosisGroups = new ArrayList<>();
        for (OverviewChartRowExtended row : new DiagnosisGroupsConverter().convert(resp.getDiagnosisGroups())) {
            diagnosisGroups.add(new DonutChartData(row.getName(), row.getQuantity(), row.getAlternation()));
        }

        ArrayList<DonutChartData> ageGroups = new ArrayList<>();
        for (OverviewChartRowExtended row : resp.getAgeGroups()) {
            ageGroups.add(new DonutChartData(row.getName(), row.getQuantity(), row.getAlternation()));
        }

        ArrayList<DonutChartData> degreeOfSickLeaveGroups = new ArrayList<>();
        for (OverviewChartRowExtended row : resp.getDegreeOfSickLeaveGroups()) {
            degreeOfSickLeaveGroups.add(new DonutChartData(row.getName(), row.getQuantity(), row.getAlternation()));
        }

        sortByQuantity(degreeOfSickLeaveGroups);

        ArrayList<BarChartData> sickLeaveLengthData = new ArrayList<>();
        for (OverviewChartRow row : resp.getSickLeaveLengthGroups()) {
            sickLeaveLengthData.add(new BarChartData(row.getName(), row.getQuantity()));
        }
        SickLeaveLengthOverview sickLeaveLength = new SickLeaveLengthOverview(sickLeaveLengthData, resp.getLongSickLeavesTotal(), resp.getLongSickLeavesAlternation());

        ArrayList<DonutChartData> perCounty = new ArrayList<>();
        for (OverviewChartRowExtended row : resp.getPerCounty()) {
            perCounty.add(new DonutChartData(row.getName(), row.getQuantity(), row.getAlternation()));
        }

        sortByQuantity(perCounty);

        return new OverviewData(range.toString(), casesPerMonth, diagnosisGroups, ageGroups, degreeOfSickLeaveGroups, sickLeaveLength, perCounty);
    }

    private void sortByQuantity(List<DonutChartData> result) {
        Collections.sort(result, (o1, o2) -> o2.getQuantity() - o1.getQuantity());
    }
}
