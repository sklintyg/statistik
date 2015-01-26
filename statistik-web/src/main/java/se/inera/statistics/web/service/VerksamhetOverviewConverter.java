/**
 * Copyright (C) 2015 Inera AB (http://www.inera.se)
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

import java.util.ArrayList;

import se.inera.statistics.service.report.model.OverviewChartRow;
import se.inera.statistics.service.report.model.OverviewChartRowExtended;
import se.inera.statistics.service.report.model.OverviewKonsfordelning;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.VerksamhetOverviewResponse;
import se.inera.statistics.web.model.overview.BarChartData;
import se.inera.statistics.web.model.overview.DonutChartData;
import se.inera.statistics.web.model.overview.SickLeaveLengthOverview;
import se.inera.statistics.web.model.overview.VerksamhetNumberOfCasesPerMonthOverview;
import se.inera.statistics.web.model.overview.VerksamhetOverviewData;

public class VerksamhetOverviewConverter {

    VerksamhetOverviewData convert(VerksamhetOverviewResponse resp, Range range, Filter filter) {
        final OverviewKonsfordelning casesPerMonthNew = resp.getCasesPerMonthSexProportionPreviousPeriod();
        final OverviewKonsfordelning casesPerMonthOld = resp.getCasesPerMonthSexProportionBeforePreviousPeriod();

        VerksamhetNumberOfCasesPerMonthOverview casesPerMonth = new VerksamhetNumberOfCasesPerMonthOverview(casesPerMonthNew.getMaleAmount(),
                casesPerMonthNew.getFemaleAmount(), casesPerMonthNew.getPeriod().toString(), casesPerMonthOld.getMaleAmount(),
                casesPerMonthOld.getFemaleAmount(), casesPerMonthOld.getPeriod().toString(), resp.getTotalCases());

        ArrayList<DonutChartData> diagnosisGroups = new ArrayList<>();

        for (OverviewChartRowExtended row : resp.getDiagnosisGroups()) {
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

        ArrayList<BarChartData> sickLeaveLengthData = new ArrayList<>();
        for (OverviewChartRow row : resp.getSickLeaveLengthGroups()) {
            sickLeaveLengthData.add(new BarChartData(row.getName(), row.getQuantity()));
        }
        SickLeaveLengthOverview sickLeaveLength = new SickLeaveLengthOverview(sickLeaveLengthData, resp.getLongSickLeavesTotal(), resp.getLongSickLeavesAlternation());

        final FilterDataResponse filterResponse = new FilterDataResponse(filter.getDiagnoser(), filter.getEnheter());

        return new VerksamhetOverviewData(range.toString(), casesPerMonth, diagnosisGroups, ageGroups, degreeOfSickLeaveGroups, sickLeaveLength, filterResponse);
    }

}
