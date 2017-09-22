/*
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import se.inera.statistics.web.service.FilterDataResponse;

import java.util.List;

public abstract class TableDataReport implements FilteredDataReport {

    private static final double MAX_DIFF_FOR_EQUALITY = 0.00001D;

    public abstract TableData getTableData();
    public abstract String getPeriod();
    @Override public abstract FilterDataResponse getFilter();

    @JsonIgnore
    public abstract List<ChartData> getChartDatas();

    @Override
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
        return Math.abs(sum) < MAX_DIFF_FOR_EQUALITY;
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
