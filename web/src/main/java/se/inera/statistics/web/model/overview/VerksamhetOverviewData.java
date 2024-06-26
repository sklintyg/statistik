/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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
package se.inera.statistics.web.model.overview;

import java.util.List;
import se.inera.statistics.service.report.model.AvailableFilters;
import se.inera.statistics.web.error.Message;
import se.inera.statistics.web.model.FilteredDataReport;
import se.inera.statistics.web.service.dto.FilterDataResponse;

public class VerksamhetOverviewData implements FilteredDataReport {

    private final String periodText;
    private final VerksamhetNumberOfCasesPerMonthOverview casesPerMonth;
    private final List<DonutChartData> diagnosisGroups;
    private final List<DonutChartData> ageGroups;
    private final List<DonutChartData> degreeOfSickLeaveGroups;
    private final SickLeaveLengthOverview sickLeaveLength;
    private final AvailableFilters availableFilters;
    private final FilterDataResponse filter;
    private final List<Message> messages;
    private final List<DonutChartData> kompletteringar;

    // CHECKSTYLE:OFF ParameterNumber
    @java.lang.SuppressWarnings("squid:S00107") // Suppress parameter number warning in Sonar
    public VerksamhetOverviewData(String periodText, VerksamhetNumberOfCasesPerMonthOverview casesPerMonth,
        List<DonutChartData> diagnosisGroups,
        List<DonutChartData> ageGroups, List<DonutChartData> degreeOfSickLeaveGroups,
        SickLeaveLengthOverview sickLeaveLength, List<DonutChartData> kompletteringar,
        AvailableFilters availableFilters, FilterDataResponse filter, List<Message> messages) {
        this.periodText = periodText;
        this.casesPerMonth = casesPerMonth;
        this.diagnosisGroups = diagnosisGroups;
        this.ageGroups = ageGroups;
        this.degreeOfSickLeaveGroups = degreeOfSickLeaveGroups;
        this.sickLeaveLength = sickLeaveLength;
        this.kompletteringar = kompletteringar;
        this.availableFilters = availableFilters;
        this.filter = filter;
        this.messages = messages;
    }
    // CHECKSTYLE:ON ParameterNumber

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

    public List<DonutChartData> getKompletteringar() {
        return kompletteringar;
    }

    @Override
    public FilterDataResponse getFilter() {
        return filter;
    }

    @Override
    public List<Message> getMessages() {
        return messages;
    }

    @Override
    public boolean isEmpty() {
        return casesPerMonth.getTotalCases() == 0;
    }

    @Override
    public AvailableFilters getAvailableFilters() {
        return availableFilters;
    }
}
