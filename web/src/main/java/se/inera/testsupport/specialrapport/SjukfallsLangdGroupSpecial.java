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
package se.inera.testsupport.specialrapport;

import java.util.Arrays;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum SjukfallsLangdGroupSpecial {

    GROUP1_0TO7("Under 8 dagar", 0, 7),
    GROUP2_8TO14("8-14 dagar", 8, 14),
    GROUP3_15TO30("15-30 dagar", 15, 30),
    GROUP4_31TO60("31-60 dagar", 31, 60),
    GROUP5_61TO90("61-90 dagar", 61, 90),
    GROUP6_91TO180("91-180 dagar", 91, 180),
    GROUP7_181TO365("181-365 dagar", 181, 365),
    GROUP8_366PLUS("Över 365 dagar", 366, Integer.MAX_VALUE - 1);

    private static final Logger LOG = LoggerFactory.getLogger(SjukfallsLangdGroupSpecial.class);

    private final String groupName;
    private final int from;
    private final int to;

    /**
     * @param from Range start, inclusive
     * @param to Range end, inclusive
     */
    SjukfallsLangdGroupSpecial(String groupName, int from, int to) {
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

    public static Optional<SjukfallsLangdGroupSpecial> getByName(String name) {
        return Arrays.stream(values()).filter(group -> group.groupName.equalsIgnoreCase(name)).findFirst();
    }

    public static Optional<SjukfallsLangdGroupSpecial> parse(String name) {
        try {
            final SjukfallsLangdGroupSpecial sjukfallsLangdGroup = valueOf(name);
            return Optional.of(sjukfallsLangdGroup);
        } catch (IllegalArgumentException e) {
            LOG.debug("Failed to parse name: {}", name, e);
            return Optional.empty();
        }
    }

    public static SjukfallsLangdGroupSpecial getByLength(int length) {
        for (SjukfallsLangdGroupSpecial group : values()) {
            if (group.from <= length && group.to >= length) {
                return group;
            }
        }
        throw new IllegalArgumentException("Length could not be matched to a group: " + length);
    }

}
