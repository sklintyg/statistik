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
package se.inera.statistics.web.model.overview;

import se.inera.statistics.web.service.FilterDataResponse;

import java.util.List;

public class VerksamhetOverviewData {

    private final String periodText;
    private final VerksamhetNumberOfCasesPerMonthOverview casesPerMonth;
    private final List<DonutChartData> diagnosisGroups;
    private final List<DonutChartData> ageGroups;
    private final List<DonutChartData> degreeOfSickLeaveGroups;
    private final SickLeaveLengthOverview sickLeaveLength;
    private final FilterDataResponse filter;

    public VerksamhetOverviewData(String periodText, VerksamhetNumberOfCasesPerMonthOverview casesPerMonth, List<DonutChartData> diagnosisGroups,
            List<DonutChartData> ageGroups, List<DonutChartData> degreeOfSickLeaveGroups,
            SickLeaveLengthOverview sickLeaveLength, FilterDataResponse filter) {
        this.periodText = periodText;
        this.casesPerMonth = casesPerMonth;
        this.diagnosisGroups = diagnosisGroups;
        this.ageGroups = ageGroups;
        this.degreeOfSickLeaveGroups = degreeOfSickLeaveGroups;
        this.sickLeaveLength = sickLeaveLength;
        this.filter = filter;
    }

    public String getPeriodText() {
        return periodText;
    }

    public VerksamhetNumberOfCasesPerMonthOverview getCasesPerMonth() {
        return casesPerMonth;
    }

    public List<DonutChartData> getDiagnosisGroups() {
        return diagnosisGroups;
    }

    public List<DonutChartData> getAgeGroups() {
        return ageGroups;
    }

    public List<DonutChartData> getDegreeOfSickLeaveGroups() {
        return degreeOfSickLeaveGroups;
    }

    public SickLeaveLengthOverview getSickLeaveLength() {
        return sickLeaveLength;
    }

    public FilterDataResponse getFilter() {
        return filter;
    }

}
