/*
 * Copyright (C) 2023 Inera AB (http://www.inera.se)
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
package se.inera.testsupport.socialstyrelsenspecial;

import java.util.Arrays;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Special age group list used for a report to socialstyrelsen, see INTYG-6994.
 */
public enum AgeGroupSoc {

    GROUP1_0TO16("<=17", Integer.MIN_VALUE, 17),
    GROUP2_16TO20("18-30", 18, 30),
    GROUP2_21TO25("31-40", 31, 40),
    GROUP5_41TO45("41-50", 41, 50),
    GROUP5_65PLUS("51+", 51, Integer.MAX_VALUE - 1);

    private static final Logger LOG = LoggerFactory.getLogger(AgeGroupSoc.class);

    private final String groupName;
    private final int from;
    private final int to;

    /**
     * @param from Range start, inclusive
     * @param to Range end, inclusive
     */
    AgeGroupSoc(String groupName, int from, int to) {
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

    public static Optional<AgeGroupSoc> getByName(String name) {
        return Arrays.stream(values()).filter(group -> group.groupName.equalsIgnoreCase(name)).findFirst();
    }

    public static Optional<AgeGroupSoc> parse(String name) {
        try {
            final AgeGroupSoc group = valueOf(name);
            return Optional.of(group);
        } catch (IllegalArgumentException e) {
            LOG.debug("Failed to parse name: {}", name, e);
            return Optional.empty();
        }
    }

    public static Optional<AgeGroupSoc> getGroupForAge(int age) {
        for (AgeGroupSoc ageGroup : values()) {
            if (age >= ageGroup.getFrom() && age <= ageGroup.getTo()) {
                return Optional.of(ageGroup);
            }
        }
        return Optional.empty();
    }

}
