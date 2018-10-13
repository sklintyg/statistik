/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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
package se.inera.statistics.web.service.responseconverter;

import se.inera.statistics.service.report.model.OverviewChartRow;
import se.inera.statistics.service.report.model.OverviewChartRowExtended;
import se.inera.statistics.service.report.model.OverviewResponse;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.web.model.overview.BarChartData;
import se.inera.statistics.web.model.overview.DonutChartData;
import se.inera.statistics.web.model.overview.OverviewData;
import se.inera.statistics.web.model.overview.SickLeaveLengthOverview;
import se.inera.statistics.web.model.overview.SjukfallPerManadOverview;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class OverviewConverter {

    public OverviewData convert(OverviewResponse resp, Range range) {
        final Range previousPeriod = ReportUtil.getPreviousOverviewPeriod(range);
        SjukfallPerManadOverview casesPerMonth = new SjukfallPerManadOverview(
                resp.getCasesPerMonthSexProportion().getMaleProportion(), resp.getCasesPerMonthSexProportion().getFemaleProportion(),
                resp.getCasesPerMonthAlteration(), previousPeriod.toString());

        List<DonutChartData> diagnosisGroups = new DiagnosisGroupsConverter().convert(resp.getDiagnosisGroups()).stream()
                .map(mapOverviewRowData()).collect(Collectors.toList());

        List<DonutChartData> ageGroups = new AldersGroupsConverter().convert(resp.getAgeGroups()).stream().map(mapOverviewRowData())
                .collect(Collectors.toList());

        List<DonutChartData> degreeOfSickLeaveGroups = resp.getDegreeOfSickLeaveGroups().stream()
                .sorted((o1, o2) -> getNameAsNumber(o2) - getNameAsNumber(o1))
                .map(mapOverviewRowData()).collect(Collectors.toList());

        List<DonutChartData> perCounty = resp.getPerCounty().stream().map(mapRowDataMilli()).sorted(comp()).collect(Collectors.toList());

        ArrayList<BarChartData> sickLeaveLengthData = new ArrayList<>();
        for (OverviewChartRow row : resp.getSickLeaveLengthGroups()) {
            sickLeaveLengthData.add(new BarChartData(row.getName(), row.getQuantity()));
        }
        SickLeaveLengthOverview sickLeaveLength = new SickLeaveLengthOverview(sickLeaveLengthData, resp.getLongSickLeavesTotal(),
                resp.getLongSickLeavesAlternation());

        return new OverviewData(range.toString(), casesPerMonth, diagnosisGroups, ageGroups, degreeOfSickLeaveGroups, sickLeaveLength,
                perCounty);
    }

    static int getNameAsNumber(OverviewChartRowExtended row) {
        try {
            return Integer.parseInt(row.getName().replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private Comparator<DonutChartData> comp() {
        return (o1, o2) -> Double.compare(o2.getQuantity().doubleValue(), o1.getQuantity().doubleValue());
    }

    private Function<OverviewChartRowExtended, DonutChartData> mapOverviewRowData() {
        return (r) -> new DonutChartData(r.getName(), r.getQuantity(), r.getAlternation(), r.getColor());
    }

    /**
     * The county report is calculated per million citizens but should be
     * presented per thousand citizens. That conversion is performed here.
     */
    private Function<OverviewChartRowExtended, DonutChartData> mapRowDataMilli() {
        return (r) -> {
            final double thousand = 1000d;
            return new DonutChartData(r.getName(), r.getQuantity() / thousand, r.getAlternation(), r.getColor());
        };
    }

}
