/*
 * Copyright (C) 2025 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.report.model;

public class OverviewChartRowExtended extends OverviewChartRow {

    private final int alternation;
    private final String color;
    private final Boolean hideInTable;

    public OverviewChartRowExtended(String name, int quantity, int alternation, String color) {
        this(name, quantity, alternation, color, null);
    }

    public OverviewChartRowExtended(String name, int quantity, int alternation, String color, Boolean hideInTable) {
        super(name, quantity);
        this.color = color;
        this.alternation = alternation;
        this.hideInTable = hideInTable;
    }

    public int getAlternation() {
        return alternation;
    }

    public String getColor() {
        return color;
    }

    public Boolean isHideInTable() {
        return hideInTable;
    }

    @Override
    public String toString() {
        return super.toString() + ", {\"OverviewChartRowExtended\":{\"alternation\":" + alternation + "}}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OverviewChartRowExtended)) {
            return false;
        }

        OverviewChartRowExtended that = (OverviewChartRowExtended) o;

        if (!super.equals(o)) {
            return false;
        }

        return alternation == that.alternation;
    }

    @Override
    public int hashCode() {
        return super.hashCode() + alternation;
    }
}
