/*
 * Copyright (C) 2025 Inera AB (http://www.inera.se)
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

public enum OverviewAgeGroup {

    GROUP1_0TO20("Under 20 år", 0, 20, "#E11964"),
    GROUP2_21TO30("21-30 år", 21, 30, "#FFBA3E"),
    GROUP3_31TO40("31-40 år", 31, 40, "#3CA3FF"),
    GROUP4_41TO50("41-50 år", 41, 50, "#2A5152"),
    GROUP5_51TO60("51-60 år", 51, 60, "#5CC2BC"),
    GROUP6_60PLUS("Över 60 år", 61, Integer.MAX_VALUE - 1, "#006697");

    private static final Logger LOG = LoggerFactory.getLogger(OverviewAgeGroup.class);

    private final String groupName;
    private final int from;
    private final int to;
    private final String color;

    /**
     * @param from Range start, inclusive
     * @param to Range end, inclusive
     */
    OverviewAgeGroup(String groupName, int from, int to, String color) {
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

    public static Optional<OverviewAgeGroup> getByName(String name) {
        return Arrays.stream(values()).filter(group -> group.groupName.equalsIgnoreCase(name)).findFirst();
    }

    public static Optional<OverviewAgeGroup> parse(String name) {
        try {
            final OverviewAgeGroup group = valueOf(name);
            return Optional.of(group);
        } catch (IllegalArgumentException e) {
            LOG.debug("Failed to parse name: {}", name, e);
            return Optional.empty();
        }
    }

    public static Map<String, String> getColors() {
        return Arrays.stream(values()).collect(Collectors.toMap(OverviewAgeGroup::getGroupName, OverviewAgeGroup::getColor));
    }

    public static Optional<OverviewAgeGroup> getGroupForAge(int age) {
        for (OverviewAgeGroup ageGroup : values()) {
            if (age >= ageGroup.getFrom() && age <= ageGroup.getTo()) {
                return Optional.of(ageGroup);
            }
        }
        return Optional.empty();
    }

}
