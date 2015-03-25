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
package se.inera.statistics.web.service;

import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.KonDataResponse;
import se.inera.statistics.service.report.model.KonDataRow;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.web.model.ChartData;
import se.inera.statistics.web.model.ChartSeries;
import se.inera.statistics.web.model.DualSexStatisticsData;
import se.inera.statistics.web.model.NamedData;
import se.inera.statistics.web.model.TableData;
import se.inera.statistics.web.model.TableHeader;

import java.util.ArrayList;
import java.util.List;

public abstract class DualSexConverter<T extends KonDataResponse> {

    DualSexStatisticsData convert(T data, Range range, Filter filter, String message, String seriesNameTemplate) {
        TableData tableData = convertTable(data, seriesNameTemplate);
        ChartData maleChart = extractChartData(data, Kon.Male, seriesNameTemplate);
        ChartData femaleChart = extractChartData(data, Kon.Female, seriesNameTemplate);
        final FilterDataResponse filterResponse = new FilterDataResponse(filter.getDiagnoser(), filter.getEnheter());
        return new DualSexStatisticsData(tableData, maleChart, femaleChart, range.toString(), filterResponse, message);
    }

    private ChartData extractChartData(T data, Kon sex, String seriesNameTemplate) {
        List<ChartSeries> series = getChartSeries(data, sex, seriesNameTemplate);
        return new ChartData(series, data.getPeriods());
    }

    private List<ChartSeries> getChartSeries(T data, Kon sex, String seriesNameTemplate) {
        List<ChartSeries> series = new ArrayList<>();
        for (int i = 0; i < data.getGroups().size(); i++) {
            List<Integer> indexData = data.getDataFromIndex(i, sex);
            final String seriesName = String.format(seriesNameTemplate, data.getGroups().get(i));
            series.add(new ChartSeries(seriesName, indexData, true));
        }
        return series;
    }

    TableData convertTable(T resp, String seriesNameTemplate) {
        List<NamedData> rows = getTableRows(resp);
        List<List<TableHeader>> headers = getTableHeaders(resp, seriesNameTemplate);
        return new TableData(rows, headers);
    }

    private List<NamedData> getTableRows(T resp) {
        List<NamedData> rows = new ArrayList<>();
        for (KonDataRow row : resp.getRows()) {
            List<Integer> mergedSexData = ServiceUtil.getMergedSexData(row);
            int sum = 0;
            for (Integer dataField : mergedSexData) {
                sum += dataField;
            }
            mergedSexData.add(0, sum);
            rows.add(new NamedData(row.getName(), mergedSexData));
        }
        return rows;
    }

    private List<List<TableHeader>> getTableHeaders(T resp, String seriesNameTemplate) {
        List<TableHeader> topHeaderRow = new ArrayList<>();
        topHeaderRow.add(new TableHeader(""));
        topHeaderRow.add(new TableHeader(""));
        List<String> degreesOfSickLeave = resp.getGroups();
        for (String groupName : degreesOfSickLeave) {
            final String seriesName = String.format(seriesNameTemplate, groupName);
                topHeaderRow.add(new TableHeader(seriesName, 2));
        }

        List<TableHeader> subHeaderRow = new ArrayList<>();
        subHeaderRow.add(new TableHeader("Period"));
        subHeaderRow.add(new TableHeader("Antal sjukfall totalt"));
        for (String s : degreesOfSickLeave) {
            subHeaderRow.add(new TableHeader("Kvinnor"));
            subHeaderRow.add(new TableHeader("Män"));
        }

        List<List<TableHeader>> headers = new ArrayList<>();
        headers.add(topHeaderRow);
        headers.add(subHeaderRow);
        return headers;
    }

}
