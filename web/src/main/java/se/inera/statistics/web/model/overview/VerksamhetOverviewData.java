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

package se.inera.statistics.web.model.overview;

import java.util.List;

public class VerksamhetOverviewData {

    private final String periodText;
    private final VerksamhetNumberOfCasesPerMonthOverview casesPerMonth;
    private final List<DonutChartData> diagnosisGroups;
    private final List<DonutChartData> ageGroups;
    private final List<DonutChartData> degreeOfSickLeaveGroups;
    private final SickLeaveLengthOverview sickLeaveLength;

    public VerksamhetOverviewData(String periodText, VerksamhetNumberOfCasesPerMonthOverview casesPerMonth, List<DonutChartData> diagnosisGroups,
            List<DonutChartData> ageGroups, List<DonutChartData> degreeOfSickLeaveGroups,
            SickLeaveLengthOverview sickLeaveLength) {
        this.periodText = periodText;
        this.casesPerMonth = casesPerMonth;
        this.diagnosisGroups = diagnosisGroups;
        this.ageGroups = ageGroups;
        this.degreeOfSickLeaveGroups = degreeOfSickLeaveGroups;
        this.sickLeaveLength = sickLeaveLength;
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

}
