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
package se.inera.statistics.web.model;

import java.util.List;

public class ChartData {

    private final List<ChartSeries> series;
    private final List<ChartCategory> categories;

    public ChartData(List<ChartSeries> series, List<ChartCategory> categories) {
        this.series = series;
        this.categories = categories;
    }

    public List<ChartSeries> getSeries() {
        return series;
    }

    public List<ChartCategory> getCategories() {
        return categories;
    }

}