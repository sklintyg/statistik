/**
 * Copyright (C) 2014 Inera AB (http://www.inera.se)
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
package se.inera.statistics.web.model;

public class DualSexStatisticsData {

    private final TableData tableData;
    private final ChartData maleChart;
    private final ChartData femaleChart;
    private final String period;

    public DualSexStatisticsData(TableData tableData, ChartData maleChart, ChartData femaleChart, String period) {
        this.tableData = tableData;
        this.maleChart = maleChart;
        this.femaleChart = femaleChart;
        this.period = period;
    }

    public TableData getTableData() {
        return tableData;
    }

    public ChartData getMaleChart() {
        return maleChart;
    }

    public ChartData getFemaleChart() {
        return femaleChart;
    }

    public String getPeriod() {
        return period;
    }

}
