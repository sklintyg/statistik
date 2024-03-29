/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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
import java.util.List;
import java.util.stream.Collectors;
import se.inera.statistics.service.report.model.DiagnosgruppResponse;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.KonDataResponse;
import se.inera.statistics.service.report.model.KonDataRow;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.web.service.dto.MessagesText;
import se.inera.statistics.web.error.Message;
import se.inera.statistics.web.model.ChartCategory;
import se.inera.statistics.web.model.ChartData;
import se.inera.statistics.web.model.ChartSeries;
import se.inera.statistics.web.model.DualSexStatisticsData;
import se.inera.statistics.web.model.TableData;
import se.inera.statistics.web.service.dto.Filter;
import se.inera.statistics.web.service.dto.FilterDataResponse;
import se.inera.statistics.web.service.dto.FilterSettings;
import se.inera.statistics.web.util.Pair;

public class DiagnosisSubGroupsConverter {

    static final int MAX_NUMBER_OF_CHART_SERIES = 7;
    static final int OTHER_GROUP_INDEX = -1;

    private DiagnosisGroupsConverter diagnosisGroupsConverter = new DiagnosisGroupsConverter();

    public DualSexStatisticsData convert(DiagnosgruppResponse diagnosisGroups, FilterSettings filterSettings) {
        return convert(diagnosisGroups, filterSettings, null);
    }

    public DualSexStatisticsData convert(DiagnosgruppResponse diagnosisGroups, FilterSettings filterSettings, Message message) {
        TableData tableData = diagnosisGroupsConverter.convertTable(diagnosisGroups, "%1$s");
        List<Integer> topIndexes = getTopColumnIndexes(diagnosisGroups);
        ChartData maleChart = extractChartData(diagnosisGroups, topIndexes, Kon.MALE);
        ChartData femaleChart = extractChartData(diagnosisGroups, topIndexes, Kon.FEMALE);
        final Filter filter = filterSettings.getFilter();
        final FilterDataResponse filterResponse = new FilterDataResponse(filter);
        final Range range = filterSettings.getRange();
        final List<Message> combinedMessage = Converters.combineMessages(filterSettings.getMessage(), message);
        return new DualSexStatisticsData(tableData, maleChart, femaleChart, range.toString(),
            diagnosisGroups.getAvailableFilters(), filterResponse, combinedMessage);
    }

    private ChartData extractChartData(DiagnosgruppResponse data, List<Integer> topIndexes, Kon sex) {
        List<ChartSeries> topColumns = getTopColumns(data, topIndexes, sex);
        final List<ChartCategory> categories = data.getPeriods().stream().map(ChartCategory::new).collect(Collectors.toList());
        return new ChartData(topColumns, categories);
    }

    private List<ChartSeries> getTopColumns(KonDataResponse data, List<Integer> topIndexes, Kon sex) {
        List<ChartSeries> topColumns = new ArrayList<>();
        if (topIndexes.isEmpty()) {
            topColumns.add(new ChartSeries(MessagesText.REPORT_GROUP_TOTALT, createList(data.getRows().size(), 0)));
            return topColumns;
        }
        for (Integer index : topIndexes) {
            if (index == OTHER_GROUP_INDEX) {
                List<Integer> remainingData = sumRemaining(topIndexes, data, sex);
                topColumns.add(new ChartSeries(MessagesText.REPORT_GROUP_OTHER, remainingData));
            } else {
                List<Integer> indexData = data.getDataFromIndex(index, sex);
                topColumns.add(new ChartSeries(data.getGroups().get(index), indexData));
            }
        }
        return topColumns;
    }

    private static List<Integer> createList(int size, int value) {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            list.add(value);
        }
        return list;
    }

    private static List<Integer> sumRemaining(List<Integer> topIndexes, KonDataResponse data, Kon sex) {
        List<Integer> remaining = new ArrayList<>();
        for (int i = 0; i < data.getRows().size(); i++) {
            remaining.add(0);
        }
        List<KonDataRow> rows = data.getRows();
        for (int r = 0; r < rows.size(); r++) {
            List<Integer> dataForCurrentSex = rows.get(r).getDataForSex(sex);
            for (int i = 0; i < dataForCurrentSex.size(); i++) {
                if (!topIndexes.contains(i)) {
                    remaining.set(r, remaining.get(r) + dataForCurrentSex.get(i));
                }
            }
        }
        return remaining;
    }

    List<Integer> getTopColumnIndexes(KonDataResponse diagnosisGroups) {
        return getTopColumnIndexes(SimpleKonResponse.create(diagnosisGroups));
    }

    static List<Integer> getTopColumnIndexes(SimpleKonResponse simpleKonDataRowSimpleKonResponse) {
        if (simpleKonDataRowSimpleKonResponse.getRows().isEmpty()) {
            return new ArrayList<>();
        }
        final List<Integer> sortedIndexes = getColumnIndexesSortedBySum(simpleKonDataRowSimpleKonResponse);

        if (sortedIndexes.size() == MAX_NUMBER_OF_CHART_SERIES) {
            return sortedIndexes.subList(0, MAX_NUMBER_OF_CHART_SERIES);
        } else if (sortedIndexes.size() > MAX_NUMBER_OF_CHART_SERIES) {
            final ArrayList<Integer> sortedIndexesSubList = new ArrayList<>(sortedIndexes.subList(0, MAX_NUMBER_OF_CHART_SERIES - 1));
            sortedIndexesSubList.add(OTHER_GROUP_INDEX);
            return sortedIndexesSubList;
        } else {
            return sortedIndexes;
        }
    }

    private static List<Integer> getColumnIndexesSortedBySum(SimpleKonResponse simpleKonDataRowSimpleKonResponse) {
        return getIndexedSums(simpleKonDataRowSimpleKonResponse).stream()
            .sorted((o1, o2) -> o2.getValue() - o1.getValue())
            .map(Pair::getKey)
            .collect(Collectors.toList());
    }

    private static List<Pair<Integer, Integer>> getIndexedSums(SimpleKonResponse simpleKonDataRowSimpleKonResponse) {
        final List<Pair<Integer, Integer>> indexedSums = new ArrayList<>();
        final List<Integer> rowSums = simpleKonDataRowSimpleKonResponse.getSummedData();
        for (int i = 0; i < rowSums.size(); i++) {
            final Integer rowSum = rowSums.get(i);
            if (rowSum > 0) {
                indexedSums.add(new Pair<>(i, rowSum));
            }
        }
        return indexedSums;
    }

}
