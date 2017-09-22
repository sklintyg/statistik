/*
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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
import java.util.Map;
import java.util.stream.Collectors;

public enum SickLeaveDegree {

    D_25(25, "25 %", "#E11964"),
    D_50(50, "50 %", "#032C53"),
    D_75(75, "75 %", "#FFBA3E"),
    D_100(100, "100 %", "#799745");

    private final int degree;
    private final String name;
    private final String color;

    SickLeaveDegree(int degree, String name, String color) {
        this.degree = degree;
        this.name = name;
        this.color = color;
    }

    public int getDegree() {
        return degree;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public static List<Integer> getDegrees() {
        return Arrays.stream(values()).map(SickLeaveDegree::getDegree).collect(Collectors.toList());
    }

    public static List<String> getLabels() {
        return Arrays.stream(values()).map(SickLeaveDegree::getName).collect(Collectors.toList());
    }

    public static Map<String, String> getColors() {
        return Arrays.stream(values()).collect(Collectors.toMap(SickLeaveDegree::getName, SickLeaveDegree::getColor));
    }
}
