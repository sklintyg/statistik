/*
 * Copyright (C) 2022 Inera AB (http://www.inera.se)
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
package se.inera.statistics.web.service.responseconverter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import se.inera.statistics.service.report.model.AvailableFilters;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.KonDataResponse;
import se.inera.statistics.service.report.model.KonDataRow;
import se.inera.statistics.service.report.model.KonField;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.web.service.dto.MessagesText;
import se.inera.statistics.web.error.Message;
import se.inera.statistics.web.model.ChartCategory;
import se.inera.statistics.web.model.ChartData;
import se.inera.statistics.web.model.ChartSeries;
import se.inera.statistics.web.model.DualSexStatisticsData;
import se.inera.statistics.web.model.NamedData;
import se.inera.statistics.web.model.TableData;
import se.inera.statistics.web.model.TableHeader;
import se.inera.statistics.web.service.dto.Filter;
import se.inera.statistics.web.service.dto.FilterDataResponse;
import se.inera.statistics.web.service.dto.FilterSettings;

abstract class MultiDualSexConverter {

    private static final int NUMBER_OF_COLUMNS = 3;
    private final String tableHeader;
    private final String tableSeriesHeader;

    MultiDualSexConverter() {
        this.tableHeader = MessagesText.REPORT_ANTAL_SJUKFALL_TOTALT;
        this.tableSeriesHeader = MessagesText.REPORT_PERIOD;
    }

    MultiDualSexConverter(String tableHeader) {
        this.tableHeader = tableHeader;
        this.tableSeriesHeader = MessagesText.REPORT_PERIOD;
    }

    MultiDualSexConverter(String tableHeader, String tableSeriesHeader) {
        this.tableHeader = tableHeader;
        this.tableSeriesHeader = tableSeriesHeader;
    }

    DualSexStatisticsData convert(KonDataResponse dataIn, FilterSettings filterSettings, Message message, String seriesNameTemplate) {
        return convert(dataIn, filterSettings, message, seriesNameTemplate, new HashMap<>());
    }

    DualSexStatisticsData convert(KonDataResponse dataIn, FilterSettings filterSettings, Message message, String seriesNameTemplate,
        Map<String, String> colors) {
        TableData tableData = convertTable(dataIn, seriesNameTemplate);
        KonDataResponse data = dataIn.getGroups().isEmpty() ? createEmptyResponse(dataIn.getAvailableFilters()) : dataIn;
        ChartData maleChart = extractChartData(data, Kon.MALE, seriesNameTemplate, colors);
        ChartData femaleChart = extractChartData(data, Kon.FEMALE, seriesNameTemplate, colors);
        final Filter filter = filterSettings.getFilter();
        final FilterDataResponse filterResponse = new FilterDataResponse(filter);
        final Range range = filterSettings.getRange();
        final List<Message> combinedMessage = Converters.combineMessages(filterSettings.getMessage(), message);
        return new DualSexStatisticsData(tableData, maleChart, femaleChart, range.toString(), dataIn.getAvailableFilters(),
            filterResponse, combinedMessage);
    }

    private KonDataResponse createEmptyResponse(AvailableFilters availableFilters) {
        final List<String> groups = Collections.singletonList(MessagesText.REPORT_GROUP_TOTALT);
        final List<KonField> data = Collections.singletonList(new KonField(0, 0));
        final List<KonDataRow> rows = Collections.singletonList(new KonDataRow(MessagesText.REPORT_GROUP_TOTALT, data));
        return new KonDataResponse(availableFilters, groups, rows);
    }

    private ChartData extractChartData(KonDataResponse data, Kon sex, String seriesNameTemplate, Map<String, String> colors) {
        List<ChartSeries> series = getChartSeries(data, sex, seriesNameTemplate, colors);
        final List<ChartCategory> categories = data.getPeriods().stream().map(ChartCategory::new).collect(Collectors.toList());
        return new ChartData(series, categories);
    }

    private List<ChartSeries> getChartSeries(KonDataResponse data, Kon sex, String seriesNameTemplate, Map<String, String> colors) {
        List<ChartSeries> series = new ArrayList<>();
        for (int i = 0; i < data.getGroups().size(); i++) {
            List<Integer> indexData = data.getDataFromIndex(i, sex);
            final String groupName = data.getGroups().get(i);
            final String seriesName = "%1$s".equals(seriesNameTemplate) ? groupName : String.format(seriesNameTemplate, groupName);
            String color = colors.get(groupName);
            series.add(new ChartSeries(seriesName, indexData, null, color));
        }
        return series;
    }

    TableData convertTable(KonDataResponse resp, String seriesNameTemplate) {
        List<NamedData> rows = getTableRows(resp);
        List<List<TableHeader>> headers = getTableHeaders(resp, seriesNameTemplate);
        return new TableData(rows, headers);
    }

    private List<NamedData> getTableRows(KonDataResponse resp) {
        List<NamedData> rows = new ArrayList<>();
        for (KonDataRow row : resp.getRows()) {
            List<Object> mergedSexData = getMergedSexData(row);
            Object sum = getRowSum(row);

            mergedSexData.add(0, sum);
            rows.add(new NamedData(row.getName(), mergedSexData));
        }
        return rows;
    }

    protected Object getRowSum(KonDataRow row) {
        return ServiceUtil.getRowSum(row);
    }

    protected List<Object> getMergedSexData(KonDataRow row) {
        return ServiceUtil.getMergedSexData(row);
    }

    private List<List<TableHeader>> getTableHeaders(KonDataResponse resp, String seriesNameTemplate) {
        List<TableHeader> topHeaderRow = new ArrayList<>();
        topHeaderRow.add(new TableHeader(""));
        topHeaderRow.add(new TableHeader(""));
        List<String> groups = resp.getGroups();
        List<TableHeader> subHeaderRow = new ArrayList<>();
        subHeaderRow.add(new TableHeader(tableSeriesHeader));
        subHeaderRow.add(new TableHeader(tableHeader));
        for (String groupName : groups) {
            final String seriesName = "%1$s".equals(seriesNameTemplate) ? groupName : String.format(seriesNameTemplate, groupName);
            topHeaderRow.add(new TableHeader(seriesName, NUMBER_OF_COLUMNS));
            subHeaderRow.add(new TableHeader(MessagesText.REPORT_GROUP_TOTALT, 1, null, seriesName));
            subHeaderRow.add(new TableHeader(MessagesText.REPORT_GROUP_FEMALE, 1, null, seriesName));
            subHeaderRow.add(new TableHeader(MessagesText.REPORT_GROUP_MALE, 1, null, seriesName));
        }

        List<List<TableHeader>> headers = new ArrayList<>();
        headers.add(topHeaderRow);
        headers.add(subHeaderRow);
        return headers;
    }

}
