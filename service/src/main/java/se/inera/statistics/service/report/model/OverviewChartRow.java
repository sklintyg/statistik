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
package se.inera.statistics.service.report.model;

public class OverviewChartRow {

    private static final int HASH_CODE = 31;
    private final String name;
    private final int quantity;

    public OverviewChartRow(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    public OverviewChartRow(String name, long quantity) {
        this(name, (int) quantity);
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public String toString() {
        return "{\"OverviewChartRow\":{\"name\":\"" + name + '"' + ", \"quantity\":" + quantity + "}}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof OverviewChartRow)) {
            return false;
        }

        OverviewChartRow that = (OverviewChartRow) o;

        if (quantity != that.quantity) {
            return false;
        }

        return name != null ? name.equals(that.name) : that.name == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = HASH_CODE * result + quantity;
        return result;
    }
}
