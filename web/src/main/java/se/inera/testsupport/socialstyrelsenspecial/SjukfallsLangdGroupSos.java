/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum SjukfallsLangdGroupSos {

    GROUP1_0TO14("Under 15 dagar", 0, 14),
    GROUP2_15TO30("15-30 dagar", 15, 30),
    GROUP3_31TO60("31-60 dagar", 31, 60),
    GROUP4_61TO90("61-90 dagar", 61, 90),
    GROUP5_91TO180("91-180 dagar", 91, 180),
    GROUP6_181TO365("181-365 dagar", 181, 365),
    GROUP7_366PLUS("Över 365 dagar", 366, Integer.MAX_VALUE - 1);

    private static final Logger LOG = LoggerFactory.getLogger(SjukfallsLangdGroupSos.class);

    private static final List<SjukfallsLangdGroupSos> LONG_SJUKFALLS = Arrays.asList(GROUP5_91TO180, GROUP6_181TO365, GROUP7_366PLUS);

    private final String groupName;
    private final int from;
    private final int to;

    /**
     * @param from Range start, inclusive
     * @param to Range end, inclusive
     */
    SjukfallsLangdGroupSos(String groupName, int from, int to) {
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

    public static Optional<SjukfallsLangdGroupSos> getByName(String name) {
        return Arrays.stream(values()).filter(group -> group.groupName.equalsIgnoreCase(name)).findFirst();
    }

    public static Optional<SjukfallsLangdGroupSos> parse(String name) {
        try {
            final SjukfallsLangdGroupSos sjukfallsLangdGroup = valueOf(name);
            return Optional.of(sjukfallsLangdGroup);
        } catch (IllegalArgumentException e) {
            LOG.debug("Failed to parse name: {}", name, e);
            return Optional.empty();
        }
    }

    public static SjukfallsLangdGroupSos getByLength(int length) {
        for (SjukfallsLangdGroupSos group : values()) {
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
