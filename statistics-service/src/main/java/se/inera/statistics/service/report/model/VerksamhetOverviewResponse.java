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

package se.inera.statistics.service.report.model;

import java.util.List;

public class VerksamhetOverviewResponse {

    private final int totalCases;
    private final OverviewSexProportion casesPerMonthSexProportionPreviousPeriod;
    private final OverviewSexProportion casesPerMonthSexProportionBeforePreviousPeriod;

    private final List<OverviewChartRowExtended> diagnosisGroups;

    private final List<OverviewChartRowExtended> ageGroups;

    private final List<OverviewChartRowExtended> degreeOfSickLeaveGroups;

    private final List<OverviewChartRow> sickLeaveLengthGroups;
    private final int longSickLeavesTotal;
    private final int longSickLeavesAlternation;

    // CHECKSTYLE:OFF ParameterNumberCheck
    public VerksamhetOverviewResponse(int totalCases, OverviewSexProportion casesPerMonthSexProportionPreviousPeriod,
            OverviewSexProportion casesPerMonthSexProportionBeforePreviousPeriod, List<OverviewChartRowExtended> diagnosisGroups,
            List<OverviewChartRowExtended> ageGroups, List<OverviewChartRowExtended> degreeOfSickLeaveGroups, List<OverviewChartRow> sickLeaveLengthGroups,
            int longSickLeavesTotal, int longSickLeavesAlternation) {
        this.totalCases = totalCases;
        this.casesPerMonthSexProportionPreviousPeriod = casesPerMonthSexProportionPreviousPeriod;
        this.casesPerMonthSexProportionBeforePreviousPeriod = casesPerMonthSexProportionBeforePreviousPeriod;
        this.diagnosisGroups = diagnosisGroups;
        this.ageGroups = ageGroups;
        this.degreeOfSickLeaveGroups = degreeOfSickLeaveGroups;
        this.sickLeaveLengthGroups = sickLeaveLengthGroups;
        this.longSickLeavesTotal = longSickLeavesTotal;
        this.longSickLeavesAlternation = longSickLeavesAlternation;
    }
    // CHECKSTYLE:ON ParameterNumberCheck

    public int getTotalCases() {
        return totalCases;
    }

    public OverviewSexProportion getCasesPerMonthSexProportionPreviousPeriod() {
        return casesPerMonthSexProportionPreviousPeriod;
    }

    public OverviewSexProportion getCasesPerMonthSexProportionBeforePreviousPeriod() {
        return casesPerMonthSexProportionBeforePreviousPeriod;
    }

    public List<OverviewChartRowExtended> getDiagnosisGroups() {
        return diagnosisGroups;
    }

    public List<OverviewChartRowExtended> getAgeGroups() {
        return ageGroups;
    }

    public List<OverviewChartRowExtended> getDegreeOfSickLeaveGroups() {
        return degreeOfSickLeaveGroups;
    }

    public List<OverviewChartRow> getSickLeaveLengthGroups() {
        return sickLeaveLengthGroups;
    }

    public int getLongSickLeavesTotal() {
        return longSickLeavesTotal;
    }

    public int getLongSickLeavesAlternation() {
        return longSickLeavesAlternation;
    }

    @Override
    public String toString() {
        return "{\"VerksamhetOverviewResponse\":{\"totalCases\":" + totalCases + ", \"casesPerMonthSexProportionPreviousPeriod\":"
                + casesPerMonthSexProportionPreviousPeriod + ", \"casesPerMonthSexProportionBeforePreviousPeriod\":"
                + casesPerMonthSexProportionBeforePreviousPeriod + ", \"diagnosisGroups\":" + diagnosisGroups + ", \"ageGroups\":" + ageGroups
                + ", \"degreeOfSickLeaveGroups\":" + degreeOfSickLeaveGroups + ", \"sickLeaveLengthGroups\":" + sickLeaveLengthGroups + ", \"longSickLeavesTotal\":"
                + longSickLeavesTotal + ", \"longSickLeavesAlternation\":" + longSickLeavesAlternation + "}}";
    }
}
