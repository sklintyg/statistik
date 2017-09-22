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
package se.inera.statistics.service.report.model;

import java.util.List;

@java.lang.SuppressWarnings("common-java:DuplicatedBlocks") // Code will not be cleaner if extracting a common class for
                                                            // OverviewResponse and VerksamhetOverviewResponse.
public class OverviewResponse {

    private final OverviewKonsfordelning casesPerMonthSexProportion;
    private final int casesPerMonthAlteration;

    private final List<OverviewChartRowExtended> diagnosisGroups;

    private final List<OverviewChartRowExtended> ageGroups;

    private final List<OverviewChartRowExtended> degreeOfSickLeaveGroups;

    private final List<OverviewChartRow> sickLeaveLengthGroups;
    private final int longSickLeavesTotal;
    private final int longSickLeavesAlternation;

    private final List<OverviewChartRowExtended> perCounty;

    // CHECKSTYLE:OFF ParameterNumberCheck
    @java.lang.SuppressWarnings("squid:S00107") // Suppress parameter number warning in Sonar
    public OverviewResponse(OverviewKonsfordelning casesPerMonthSexProportion,
            int casesPerMonthAlteration, List<OverviewChartRowExtended> diagnosisGroups,
            List<OverviewChartRowExtended> ageGroups, List<OverviewChartRowExtended> degreeOfSickLeaveGroups,
            List<OverviewChartRow> sickLeaveLengthGroups, int longSickLeavesTotal, int longSickLeavesAlternation,
            List<OverviewChartRowExtended> perCounty) {
        this.casesPerMonthSexProportion = casesPerMonthSexProportion;
        this.casesPerMonthAlteration = casesPerMonthAlteration;
        this.diagnosisGroups = diagnosisGroups;
        this.ageGroups = ageGroups;
        this.degreeOfSickLeaveGroups = degreeOfSickLeaveGroups;
        this.sickLeaveLengthGroups = sickLeaveLengthGroups;
        this.longSickLeavesTotal = longSickLeavesTotal;
        this.longSickLeavesAlternation = longSickLeavesAlternation;
        this.perCounty = perCounty;
    }
    // CHECKSTYLE:ON ParameterNumberCheck

    public OverviewKonsfordelning getCasesPerMonthSexProportion() {
        return casesPerMonthSexProportion;
    }

    public int getCasesPerMonthAlteration() {
        return casesPerMonthAlteration;
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

    public List<OverviewChartRowExtended> getPerCounty() {
        return perCounty;
    }

    @Override
    public String toString() {
        return "{\"OverviewResponse\":{\"casesPerMonthSexProportion\":" + casesPerMonthSexProportion + ", \"casesPerMonthAlteration\":"
                + casesPerMonthAlteration
                + ", \"diagnosisGroups\":" + diagnosisGroups + ", \"ageGroups\":" + ageGroups + ", \"degreeOfSickLeaveGroups\":"
                + degreeOfSickLeaveGroups
                + ", \"sickLeaveLengthGroups\":" + sickLeaveLengthGroups + ", \"longSickLeavesTotal\":" + longSickLeavesTotal
                + ", \"longSickLeavesAlternation\":"
                + longSickLeavesAlternation + ", \"perCounty\":" + perCounty + "}}";
    }
}
