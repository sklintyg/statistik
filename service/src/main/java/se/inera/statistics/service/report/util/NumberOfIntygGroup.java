/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.report.util;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.inera.statistics.service.report.common.ReportColor;

public enum NumberOfIntygGroup {

    GROUP1_1_INTYG("1 intyg", 1, 1, ReportColor.ST_COLOR_01.getColor()),
    GROUP2_2_INTYG("2 intyg", 2, 2, ReportColor.ST_COLOR_02.getColor()),
    GROUP3_3_INTYG("3 intyg", 3, 3, ReportColor.ST_COLOR_03.getColor()),
    GROUP4_4_INTYG("4 intyg", 4, 4, ReportColor.ST_COLOR_04.getColor()),
    GROUP5_5_INTYG("5 intyg", 5, 5, ReportColor.ST_COLOR_05.getColor()),
    GROUP6_6_INTYG("6 intyg", 6, 6, ReportColor.ST_COLOR_06.getColor()),
    GROUP7_7_INTYG("7 intyg", 7, 7, ReportColor.ST_COLOR_07.getColor()),
    GROUP8_8_INTYG("8 intyg", 8, 8, ReportColor.ST_COLOR_08.getColor()),
    GROUP9_9_INTYG("9 intyg", 9, 9, ReportColor.ST_COLOR_09.getColor()),
    GROUP10_10_INTYG("10 intyg", 10, 10, ReportColor.ST_COLOR_10.getColor()),
    GROUP11_MORE_THAN_10_INTYG("Över 10 intyg", 11, Integer.MAX_VALUE - 1, ReportColor.ST_COLOR_11.getColor());

    private static final Logger LOG = LoggerFactory.getLogger(NumberOfIntygGroup.class);

    private final String groupName;
    private final int from;
    private final int to;
    private final String color;

    NumberOfIntygGroup(String groupName, int from, int to, String color) {
        this.groupName = groupName;
        this.from = from;
        this.to = to;
        this.color = color;
    }

    public String getGroupName() {
        return groupName;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public String getColor() {
        return color;
    }

    public static Optional<NumberOfIntygGroup> getByName(String name) {
        return Arrays.stream(values()).filter(group -> group.groupName.equalsIgnoreCase(name)).findFirst();
    }

    public static Optional<NumberOfIntygGroup> parse(String name) {
        try {
            final NumberOfIntygGroup group = valueOf(name);
            return Optional.of(group);
        } catch (IllegalArgumentException e) {
            LOG.debug("Failed to parse name: {}", name, e);
            return Optional.empty();
        }
    }

    public static Map<String, String> getColors() {
        return Arrays.stream(values()).collect(Collectors.toMap(NumberOfIntygGroup::getGroupName,
            NumberOfIntygGroup::getColor));
    }

}
