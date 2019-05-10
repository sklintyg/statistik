/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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

public enum NumberOfCertificateGroup {

    GROUP1_1_CERTIFICATE("1 intyg", 1, 1, "#E11964"),
    GROUP2_2_CERTIFICATES("2 intyg", 2, 2, "#032C53"),
    GROUP3_3_CERTIFICATES("3 intyg", 3, 3, "#FFBA3E"),
    GROUP4_4_CERTIFICATES("4 intyg", 4, 4, "#799745"),
    GROUP5_5_CERTIFICATES("5 intyg", 5, 5, "#3CA3FF"),
    GROUP6_6_CERTIFICATES("6 intyg", 6, 6, "#C37EB2"),
    GROUP7_7_CERTIFICATES("7 intyg", 7, 7, "#2A5152"),
    GROUP8_8_CERTIFICATES("8 intyg", 8, 8, "#FB7F4D"),
    GROUP9_9_CERTIFICATES("9 intyg", 9, 9, "#5CC2BC"),
    GROUP10_10_CERTIFICATES("10 intyg", 10, 10, "#704F38"),
    GROUP11_MORE_THAN_10_CERTIFICATES("Över 10 intyg", 11, Integer.MAX_VALUE - 1, "#600030");

    private static final Logger LOG = LoggerFactory.getLogger(NumberOfCertificateGroup.class);

    private final String groupName;
    private final int from;
    private final int to;
    private final String color;

    NumberOfCertificateGroup(String groupName, int from, int to, String color) {
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

    public static Optional<NumberOfCertificateGroup> getByName(String name) {
        return Arrays.stream(values()).filter(group -> group.groupName.equalsIgnoreCase(name)).findFirst();
    }

    public static Optional<NumberOfCertificateGroup> parse(String name) {
        try {
            final NumberOfCertificateGroup group = valueOf(name);
            return Optional.of(group);
        } catch (IllegalArgumentException e) {
            LOG.debug("Failed to parse name: {}", name, e);
            return Optional.empty();
        }
    }

    public static Map<String, String> getColors() {
        return Arrays.stream(values()).collect(Collectors.toMap(NumberOfCertificateGroup::getGroupName,
                NumberOfCertificateGroup::getColor));
    }

}
