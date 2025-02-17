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

public enum AgeGroup {

    GROUP1_0TO16("Under 16 år", 0, 15, "#E11964"),
    GROUP2_16TO20("16-20 år", 16, 20, "#032C53"),
    GROUP2_21TO25("21-25 år", 21, 25, "#FFBA3E"),
    GROUP3_26TO30("26-30 år", 26, 30, "#799745"),
    GROUP4_31TO35("31-35 år", 31, 35, "#3CA3FF"),
    GROUP5_36TO40("36-40 år", 36, 40, "#C37EB2"),
    GROUP5_41TO45("41-45 år", 41, 45, "#2A5152"),
    GROUP5_46TO50("46-50 år", 46, 50, "#FB7F4D"),
    GROUP5_51TO55("51-55 år", 51, 55, "#5CC2BC"),
    GROUP5_56TO60("56-60 år", 56, 60, "#704F38"),
    GROUP5_61TO65("61-65 år", 61, 65, "#600030"),
    GROUP5_65PLUS("Över 65 år", 66, Integer.MAX_VALUE - 1, "#006697");

    private static final Logger LOG = LoggerFactory.getLogger(AgeGroup.class);

    private final String groupName;
    private final int from;
    private final int to;
    private final String color;

    /**
     * @param from Range start, inclusive
     * @param to Range end, inclusive
     */
    AgeGroup(String groupName, int from, int to, String color) {
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

    public static Optional<AgeGroup> getByName(String name) {
        return Arrays.stream(values()).filter(group -> group.groupName.equalsIgnoreCase(name)).findFirst();
    }

    public static Optional<AgeGroup> parse(String name) {
        try {
            final AgeGroup group = valueOf(name);
            return Optional.of(group);
        } catch (IllegalArgumentException e) {
            LOG.debug("Failed to parse name: {}", name, e);
            return Optional.empty();
        }
    }

    public static Map<String, String> getColors() {
        return Arrays.stream(values()).collect(Collectors.toMap(AgeGroup::getGroupName, AgeGroup::getColor));
    }

    public static Optional<AgeGroup> getGroupForAge(int age) {
        for (AgeGroup ageGroup : values()) {
            if (age >= ageGroup.getFrom() && age <= ageGroup.getTo()) {
                return Optional.of(ageGroup);
            }
        }
        return Optional.empty();
    }

}
