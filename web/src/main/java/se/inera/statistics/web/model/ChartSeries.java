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
package se.inera.statistics.web.model;

import java.io.Serializable;
import java.util.List;
import se.inera.statistics.service.report.model.Kon;

public class ChartSeries implements Serializable {

    private final String name;
    private final List<? extends Number> data;
    private final Kon sex;
    private final String color;

    public ChartSeries(String name, List<? extends Number> data, Kon sex, String color) {
        this.name = name;
        this.data = data;
        this.sex = sex;
        this.color = color;
    }

    public ChartSeries(String name, List<? extends Number> data, Kon sex) {
        this(name, data, sex, null);
    }

    public ChartSeries(String name, List<? extends Number> data) {
        this(name, data, null);
    }

    public String getName() {
        return name;
    }

    public List<? extends Number> getData() {
        return data;
    }

    public Kon getSex() {
        return sex;
    }

    public String getColor() {
        return color;
    }

    @Override
    public String toString() {
        return name + ": " + data.toString();
    }

}
