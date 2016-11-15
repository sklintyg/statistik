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
package se.inera.statistics.web.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.KonDataResponse;
import se.inera.statistics.service.report.model.KonDataRow;
import se.inera.statistics.service.report.model.KonField;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.web.error.Message;
import se.inera.statistics.web.model.*;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

public abstract class MultiDualSexConverter<T extends KonDataResponse> {

    private static final String TOTAL = "Totalt";
    private static final int NUMBER_OF_COLUMNS = 3;

    DualSexStatisticsData convert(T dataIn, FilterSettings filterSettings, Message message, String seriesNameTemplate) {
        TableData tableData = convertTable(dataIn, seriesNameTemplate);
        T data = dataIn.getGroups().isEmpty() ? createEmptyResponse() : dataIn;
        ChartData maleChart = extractChartData(data, Kon.MALE, seriesNameTemplate);
        ChartData femaleChart = extractChartData(data, Kon.FEMALE, seriesNameTemplate);
        final Filter filter = filterSettings.getFilter();
        final FilterDataResponse filterResponse = new FilterDataResponse(filter);
        final Range range = filterSettings.getRange();
        final List<Message> combinedMessage = Converters.combineMessages(filterSettings.getMessage(), message);
        return new DualSexStatisticsData(tableData, maleChart, femaleChart, range.toString(), filterResponse, combinedMessage);
    }

    private T createEmptyResponse() {
        final List<String> groups = Arrays.asList(TOTAL);
        final List<KonField> data = Arrays.asList(new KonField(0, 0));
        final List<KonDataRow> rows = Arrays.asList(new KonDataRow(TOTAL, data));
        return (T) (new KonDataResponse(groups, rows));
    }

    private ChartData extractChartData(T data, Kon sex, String seriesNameTemplate) {
        List<ChartSeries> series = getChartSeries(data, sex, seriesNameTemplate);
        final List<ChartCategory> categories = Lists.transform(data.getPeriods(), new Function<String, ChartCategory>() {
            @Override
            public ChartCategory apply(String period) {
                return new ChartCategory(period);
            }
        });
        return new ChartData(series, categories);
    }

    private List<ChartSeries> getChartSeries(T data, Kon sex, String seriesNameTemplate) {
        List<ChartSeries> series = new ArrayList<>();
        for (int i = 0; i < data.getGroups().size(); i++) {
            List<Integer> indexData = data.getDataFromIndex(i, sex);
            final String groupName = data.getGroups().get(i);
            final String seriesName = "%1$s".equals(seriesNameTemplate) ? groupName : String.format(seriesNameTemplate, groupName);
            series.add(new ChartSeries(seriesName, indexData));
        }
        return series;
    }

    TableData convertTable(T resp, String seriesNameTemplate) {
        List<NamedData> rows = getTableRows(resp);
        List<List<TableHeader>> headers = getTableHeaders(resp, seriesNameTemplate);
        return new TableData(rows, headers);
    }

    List<NamedData> getTableRows(T resp) {
        List<NamedData> rows = new ArrayList<>();
        for (KonDataRow row : resp.getRows()) {
            List<Integer> mergedSexData = ServiceUtil.getMergedSexData(row);
            int sum = ServiceUtil.getRowSum(row);

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
            final String seriesName = "%1$s".equals(seriesNameTemplate) ? groupName : String.format(seriesNameTemplate, groupName);
            topHeaderRow.add(new TableHeader(seriesName, NUMBER_OF_COLUMNS));
        }

        List<TableHeader> subHeaderRow = new ArrayList<>();
        subHeaderRow.add(new TableHeader("Period"));
        subHeaderRow.add(new TableHeader("Antal sjukfall totalt"));
        for (String s : degreesOfSickLeave) {
            subHeaderRow.add(new TableHeader(TOTAL));
            subHeaderRow.add(new TableHeader("Kvinnor"));
            subHeaderRow.add(new TableHeader("Män"));
        }

        List<List<TableHeader>> headers = new ArrayList<>();
        headers.add(topHeaderRow);
        headers.add(subHeaderRow);
        return headers;
    }

}
