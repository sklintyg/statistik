/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum SjukfallsLangdGroup {

    GROUP1_0TO14("Under 15 dagar", 0, 14),
    GROUP2_15TO30("15-30 dagar", 15, 30),
    GROUP3_31TO60("31-60 dagar", 31, 60),
    GROUP4_61TO90("61-90 dagar", 61, 90),
    GROUP5_91TO180("91-180 dagar", 91, 180),
    GROUP6_181TO364("181-364 dagar", 181, 364),
    GROUP7_365TO730("1-2 år", 365, 730),
    GROUP8_731PLUS("2- år", 731, Integer.MAX_VALUE - 1);

    private static final Logger LOG = LoggerFactory.getLogger(SjukfallsLangdGroup.class);

    private static final List<SjukfallsLangdGroup> LONG_SJUKFALLS =
        Arrays.asList(GROUP5_91TO180, GROUP6_181TO364, GROUP7_365TO730, GROUP8_731PLUS);

    private final String groupName;
    private final int from;
    private final int to;

    /**
     * @param from Range start, inclusive
     * @param to Range end, inclusive
     */
    SjukfallsLangdGroup(String groupName, int from, int to) {
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

    public static Optional<SjukfallsLangdGroup> getByName(String name) {
        return Arrays.stream(values()).filter(group -> group.groupName.equalsIgnoreCase(name)).findFirst();
    }

    public static Optional<SjukfallsLangdGroup> parse(String name) {
        try {
            final SjukfallsLangdGroup sjukfallsLangdGroup = valueOf(name);
            return Optional.of(sjukfallsLangdGroup);
        } catch (IllegalArgumentException e) {
            LOG.debug("Failed to parse name: {}", name, e);
            return Optional.empty();
        }
    }

    public static SjukfallsLangdGroup getByLength(int length) {
        for (SjukfallsLangdGroup group : values()) {
            if (group.from <= length && group.to >= length) {
                return group;
            }
        }
        throw new IllegalArgumentException("Length could not be matched to a group: " + length);
    }

    public boolean isLongSjukfallInOverview() {
        return LONG_SJUKFALLS.contains(this);
    }

}
