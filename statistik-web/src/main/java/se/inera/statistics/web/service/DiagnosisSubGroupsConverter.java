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

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.inera.statistics.service.report.model.DiagnosgruppResponse;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.KonDataResponse;
import se.inera.statistics.service.report.model.KonDataRow;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.web.model.ChartCategory;
import se.inera.statistics.web.model.ChartData;
import se.inera.statistics.web.model.ChartSeries;
import se.inera.statistics.web.model.DualSexStatisticsData;
import se.inera.statistics.web.model.TableData;
import se.inera.statistics.web.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DiagnosisSubGroupsConverter {

    private static final Logger LOG = LoggerFactory.getLogger(DiagnosisSubGroupsConverter.class);

    static final int NUMBER_OF_CHART_SERIES = 6;

    private DiagnosisGroupsConverter diagnosisGroupsConverter = new DiagnosisGroupsConverter();

    DualSexStatisticsData convert(DiagnosgruppResponse diagnosisGroups, FilterSettings filterSettings) {
        return convert(diagnosisGroups, filterSettings, null);
    }

    DualSexStatisticsData convert(DiagnosgruppResponse diagnosisGroups, FilterSettings filterSettings, String message) {
        TableData tableData = diagnosisGroupsConverter.convertTable(diagnosisGroups, "%1$s");
        List<Integer> topIndexes = getTopColumnIndexes(diagnosisGroups);
        ChartData maleChart = extractChartData(diagnosisGroups, topIndexes, Kon.Male);
        ChartData femaleChart = extractChartData(diagnosisGroups, topIndexes, Kon.Female);
        final Filter filter = filterSettings.getFilter();
        final FilterDataResponse filterResponse = new FilterDataResponse(filter.getDiagnoser(), filter.getEnheter());
        final Range range = filterSettings.getRange();
        final String combinedMessage = Converters.combineMessages(filterSettings.getMessage(), message);
        return new DualSexStatisticsData(tableData, maleChart, femaleChart, range.toString(), filterResponse, combinedMessage);
    }

    private ChartData extractChartData(DiagnosgruppResponse data, List<Integer> topIndexes, Kon sex) {
        List<ChartSeries> topColumns = getTopColumns(data, topIndexes, sex);
        final List<ChartCategory> categories = Lists.transform(data.getPeriods(), new Function<String, ChartCategory>() {
            @Override
            public ChartCategory apply(String period) {
                return new ChartCategory(period);
            }
        });
        return new ChartData(topColumns, categories);
    }

    private List<ChartSeries> getTopColumns(KonDataResponse data, List<Integer> topIndexes, Kon sex) {
        List<ChartSeries> topColumns = new ArrayList<>();
        if (topIndexes.isEmpty()) {
            topColumns.add(new ChartSeries("Totalt", createList(data.getRows().size(), 0), true));
            return topColumns;
        }
        for (Integer index : topIndexes) {
            List<Integer> indexData = data.getDataFromIndex(index, sex);
            topColumns.add(new ChartSeries(data.getGroups().get(index), indexData, true));
        }
        if (data.getGroups().size() > NUMBER_OF_CHART_SERIES) {
            List<Integer> remainingData = sumRemaining(topIndexes, data, sex);
            topColumns.add(new ChartSeries("Övriga", remainingData, true));
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

    List<Integer> getTopColumnIndexes(KonDataResponse diagnosisGroups2) {
        return getTopColumnIndexes(SimpleKonResponse.create(diagnosisGroups2));
    }

    static List<Integer> getTopColumnIndexes(SimpleKonResponse<SimpleKonDataRow> simpleKonDataRowSimpleKonResponse) {
        if (simpleKonDataRowSimpleKonResponse.getRows().isEmpty()) {
            return new ArrayList<>();
        }
        final List<Pair<Integer, Integer>> indexedSums = getIndexedSums(simpleKonDataRowSimpleKonResponse);
        LOG.debug("Columns: " + simpleKonDataRowSimpleKonResponse.getGroups());
        LOG.debug("TopColumnIndexes: " + indexedSums);
        Collections.sort(indexedSums, new Comparator<Pair<Integer, Integer>>() {
            @Override
            public int compare(Pair<Integer, Integer> o1, Pair<Integer, Integer> o2) {
                return o2.getValue() - o1.getValue();
            }
        });
        final List<Integer> sortedIndexes = Lists.transform(indexedSums, new Function<Pair<Integer, Integer>, Integer>() {
            @Override
            public Integer apply(Pair<Integer, Integer> integerIntegerPair) {
                return integerIntegerPair.getKey();
            }
        });
        return sortedIndexes.subList(0, Math.min(NUMBER_OF_CHART_SERIES, sortedIndexes.size()));
    }

    private static List<Pair<Integer, Integer>> getIndexedSums(SimpleKonResponse<SimpleKonDataRow> simpleKonDataRowSimpleKonResponse) {
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
