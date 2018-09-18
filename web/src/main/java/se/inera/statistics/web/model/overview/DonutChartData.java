/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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
package se.inera.statistics.web.model.overview;

public class DonutChartData {

    private final String color;
    private final String name;
    private final Number quantity;
    private final Number alternation;

    public DonutChartData(String name, Number quantity, Number alternation, String color) {
        this.color = color;
        this.name = name;
        this.quantity = quantity;
        this.alternation = alternation;
    }

    public String getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public Number getQuantity() {
        return quantity;
    }

    public Number getAlternation() {
        return alternation;
    }

}
