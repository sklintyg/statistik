/*
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.web.service;

import se.inera.statistics.service.report.model.OverviewChartRow;
import se.inera.statistics.service.report.model.OverviewChartRowExtended;
import se.inera.statistics.service.report.model.OverviewKonsfordelning;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.VerksamhetOverviewResponse;
import se.inera.statistics.web.error.Message;
import se.inera.statistics.web.model.overview.BarChartData;
import se.inera.statistics.web.model.overview.DonutChartData;
import se.inera.statistics.web.model.overview.SickLeaveLengthOverview;
import se.inera.statistics.web.model.overview.VerksamhetNumberOfCasesPerMonthOverview;
import se.inera.statistics.web.model.overview.VerksamhetOverviewData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class VerksamhetOverviewConverter {

    VerksamhetOverviewData convert(VerksamhetOverviewResponse resp, Range range, Filter filter, Message message) {
        final OverviewKonsfordelning casesPerMonthNew = resp.getCasesPerMonthSexProportionPreviousPeriod();
        final OverviewKonsfordelning casesPerMonthOld = resp.getCasesPerMonthSexProportionBeforePreviousPeriod();

        VerksamhetNumberOfCasesPerMonthOverview casesPerMonth = new VerksamhetNumberOfCasesPerMonthOverview(
                casesPerMonthNew.getMaleAmount(),
                casesPerMonthNew.getFemaleAmount(), casesPerMonthNew.getPeriod().toString(), casesPerMonthOld.getMaleAmount(),
                casesPerMonthOld.getFemaleAmount(), casesPerMonthOld.getPeriod().toString(), resp.getTotalCases());

        List<DonutChartData> diagnosisGroups = resp.getDiagnosisGroups().stream().map(mapOverviewRowData()).collect(Collectors.toList());

        List<DonutChartData> ageGroups = resp.getAgeGroups().stream().map(mapOverviewRowData()).collect(Collectors.toList());

        List<DonutChartData> degreeOfSickLeaveGroups = resp.getDegreeOfSickLeaveGroups().stream().map(mapOverviewRowData()).sorted(comp())
                .collect(Collectors.toList());

        ArrayList<BarChartData> sickLeaveLengthData = new ArrayList<>();
        for (OverviewChartRow row : resp.getSickLeaveLengthGroups()) {
            sickLeaveLengthData.add(new BarChartData(row.getName(), row.getQuantity()));
        }
        SickLeaveLengthOverview sickLeaveLength = new SickLeaveLengthOverview(sickLeaveLengthData, resp.getLongSickLeavesTotal(),
                resp.getLongSickLeavesAlternation());

        final FilterDataResponse filterResponse = new FilterDataResponse(filter);
        List<Message> messages = message == null ? new ArrayList<>() : Arrays.asList(message);

        return new VerksamhetOverviewData(range.toString(), casesPerMonth, diagnosisGroups, ageGroups, degreeOfSickLeaveGroups,
                sickLeaveLength, filterResponse, messages);
    }

    private Comparator<DonutChartData> comp() {
        return (o1, o2) -> o2.getQuantity() - o1.getQuantity();
    }

    private Function<OverviewChartRowExtended, DonutChartData> mapOverviewRowData() {
        return (r) -> new DonutChartData(r.getName(), r.getQuantity(), r.getAlternation(), r.getColor());
    }

}
