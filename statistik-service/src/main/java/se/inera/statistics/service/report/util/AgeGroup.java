/**
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.report.util;

import java.util.Arrays;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum AgeGroup {

    GROUP1_0TO20("Under 21 år", 0, 20),
    GROUP2_21TO25("21-25 år", 21, 25),
    GROUP3_26TO30("26-30 år", 26, 30),
    GROUP4_31TO35("31-35 år", 31, 35),
    GROUP5_36TO40("36-40 år", 36, 40),
    GROUP5_41TO45("41-45 år", 41, 45),
    GROUP5_46TO50("46-50 år", 46, 50),
    GROUP5_51TO55("51-55 år", 51, 55),
    GROUP5_56TO60("56-60 år", 56, 60),
    GROUP5_61PLUS("Över 60 år", 61, Integer.MAX_VALUE - 1);

    private static final Logger LOG = LoggerFactory.getLogger(AgeGroup.class);

    private final String groupName;
    private final int from;
    private final int to;

    /**
     * @param from Range start, inclusive
     * @param to Range end, inclusive
     */
    AgeGroup(String groupName, int from, int to) {
        this.groupName = groupName;
        this.from = from;
        this.to = to;
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

}
