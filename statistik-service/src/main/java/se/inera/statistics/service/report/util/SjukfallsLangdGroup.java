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

public enum SjukfallsLangdGroup {

    GROUP0TO14("Under 15 dagar", 0, 14),
    GROUP15TO30("15-30 dagar", 16, 30),
    GROUP31TO60("31-60 dagar", 31, 60),
    GROUP61TO90("61-90 dagar", 61, 90),
    GROUP91TO180("91-180 dagar", 91, 180),
    GROUP181TO365("181-365 dagar", 181, 365),
    GROUP366PLUS("Över 365 dagar", 366, Integer.MAX_VALUE - 1);

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

}
