/**
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import se.inera.statistics.web.error.Message;
import se.inera.statistics.web.service.FilterDataResponse;

import java.util.List;

public abstract class TableDataReport implements FilteredDataReport {

    public abstract TableData getTableData();
    public abstract String getPeriod();
    @Override public abstract FilterDataResponse getFilter();
    public abstract List<Message> getMessages();

    @JsonIgnore
    public abstract List<ChartData> getChartDatas();

    public boolean isEmpty() {
        final List<ChartData> chartDatas = getChartDatas();
        if (chartDatas == null) {
            return true;
        }
        double sum = 0;
        for (ChartData chartData : chartDatas) {
            if (chartData != null) {
                sum += sum(chartData.getSeries());
            }
        }
        final double maxDiffForEquality = 0.00001D;
        return Math.abs(sum) < maxDiffForEquality;
    }

    private double sum(List<ChartSeries> series) {
        double sum = 0;
        for (ChartSeries serie : series) {
            for (Number number : serie.getData()) {
                sum += number.doubleValue();
            }
        }
        return sum;
    }

}
