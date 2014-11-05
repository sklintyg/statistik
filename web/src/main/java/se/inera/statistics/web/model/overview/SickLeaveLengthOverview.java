/*
 * Copyright (C) 2013 - 2014 Inera AB (http://www.inera.se)
 *
 *     This file is part of Inera Statistics (http://code.google.com/p/inera-statistics/).
 *
 *     Inera Statistics is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Inera Statistics is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU LESSER GENERAL PUBLIC LICENSE for more details.
 *
 *     You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.inera.statistics.web.model.overview;

import java.util.List;

public class SickLeaveLengthOverview {

    private final List<BarChartData> chartData;
    private final int longSickLeavesTotal;
    private final int longSickLeavesAlternation;

    public SickLeaveLengthOverview(List<BarChartData> chartData, int longSickLeavesTotal, int longSickLeavesAlternation) {
        this.chartData = chartData;
        this.longSickLeavesTotal = longSickLeavesTotal;
        this.longSickLeavesAlternation = longSickLeavesAlternation;
    }

    public List<BarChartData> getChartData() {
        return chartData;
    }

    public int getLongSickLeavesTotal() {
        return longSickLeavesTotal;
    }

    public int getLongSickLeavesAlternation() {
        return longSickLeavesAlternation;
    }

}
