/**
 * Copyright (C) 2015 Inera AB (http://www.inera.se)
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

import se.inera.statistics.web.service.FilterDataResponse;

import java.util.Arrays;
import java.util.List;

public class SimpleDetailsData extends TableDataReport {

    private final TableData tableData;
    private final ChartData chartData;
    private final String period;
    private final FilterDataResponse filter;
    private final String message;

    public SimpleDetailsData(TableData tableData, ChartData chartData, String period, FilterDataResponse filter, String message) {
        this.tableData = tableData;
        this.chartData = chartData;
        this.period = period;
        this.filter = filter;
        this.message = message;
    }

    public SimpleDetailsData(TableData tableData, ChartData chartData, String period, FilterDataResponse filter) {
        this(tableData, chartData, period, filter, null);
    }

    public TableData getTableData() {
        return tableData;
    }

    public ChartData getChartData() {
        return chartData;
    }

    public String getPeriod() {
        return period;
    }

    public FilterDataResponse getFilter() {
        return filter;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public List<ChartData> getChartDatas() {
        return Arrays.asList(chartData);
    }

}
