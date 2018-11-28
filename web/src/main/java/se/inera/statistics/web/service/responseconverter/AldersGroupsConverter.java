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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import se.inera.statistics.service.report.model.OverviewChartRowExtended;
import se.inera.statistics.service.report.util.OverviewAgeGroup;
import se.inera.statistics.web.MessagesText;

public class AldersGroupsConverter {
    private static final String ALDERSGRUPPER_REST = MessagesText.REPORT_ALDERSGRUPPER_REST;
    private static final String ALDERSGRUPPER_REST_COLOR = "#5D5D5D";
    private static final int DISPLAYED_AGE_GROUPS = 5;
    private static final List<String> ORDERED_GROUPS_FOR_OVERVIEW = Stream.of(
            OverviewAgeGroup.GROUP2_21TO30,
            OverviewAgeGroup.GROUP3_31TO40,
            OverviewAgeGroup.GROUP4_41TO50,
            OverviewAgeGroup.GROUP5_51TO60)
            .map(OverviewAgeGroup::getGroupName).collect(Collectors.toList());

    public List<OverviewChartRowExtended> convert(List<OverviewChartRowExtended> aldersGroups) {
        Collections.sort(aldersGroups, Comparator.comparingInt(this::getGroupOrder));
        return Converters.convert(aldersGroups, DISPLAYED_AGE_GROUPS, ALDERSGRUPPER_REST, ALDERSGRUPPER_REST_COLOR);
    }

    private int getGroupOrder(OverviewChartRowExtended o2) {
        final int i = ORDERED_GROUPS_FOR_OVERVIEW.indexOf(o2.getName());
        return i < 0 ? Integer.MAX_VALUE : i;
    }

}
