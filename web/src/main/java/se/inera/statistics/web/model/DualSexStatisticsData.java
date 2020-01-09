/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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

import java.util.Arrays;
import java.util.List;
import se.inera.statistics.service.report.model.AvailableFilters;
import se.inera.statistics.web.error.Message;
import se.inera.statistics.web.service.FilterDataResponse;

public class DualSexStatisticsData extends TableDataReport {

    private final TableData tableData;
    private final ChartData maleChart;
    private final ChartData femaleChart;
    private final String period;
    private final AvailableFilters availableFilters;
    private final FilterDataResponse filter;
    private final List<Message> messages;

    public DualSexStatisticsData(TableData tableData, ChartData maleChart, ChartData femaleChart, String period,
        AvailableFilters availableFilters, FilterDataResponse filter, List<Message> messages) {
        this.tableData = tableData;
        this.maleChart = maleChart;
        this.femaleChart = femaleChart;
        this.period = period;
        this.availableFilters = availableFilters;
        this.filter = filter;
        this.messages = messages;
    }

    @Override
    public TableData getTableData() {
        return tableData;
    }

    public ChartData getMaleChart() {
        return maleChart;
    }

    public ChartData getFemaleChart() {
        return femaleChart;
    }

    @Override
    public String getPeriod() {
        return period;
    }

    @Override
    public FilterDataResponse getFilter() {
        return filter;
    }

    @Override
    public List<Message> getMessages() {
        return messages;
    }

    @Override
    public List<ChartData> getChartDatas() {
        return Arrays.asList(maleChart, femaleChart);
    }

    @Override
    public AvailableFilters getAvailableFilters() {
        return availableFilters;
    }
}
