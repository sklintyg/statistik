/*
 * Copyright (C) 2013 - 2014 Inera AB (http://www.inera.se)
 *
 *     This file is part of Inera Statistics (http://code.google.com/p/inera-statistics/).
 *
 *     Inera Statistics is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Inera Statistics is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU LESSER GENERAL PUBLIC LICENSE for more details.
 *
 *     You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.inera.statistics.web.service;

import java.util.*;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.inera.statistics.service.report.model.DiagnosgruppResponse;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.KonDataRow;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.web.model.ChartData;
import se.inera.statistics.web.model.ChartSeries;
import se.inera.statistics.web.model.DualSexStatisticsData;
import se.inera.statistics.web.model.TableData;
import se.inera.statistics.web.util.Pair;

public class DiagnosisSubGroupsConverter {

    private static final Logger LOG = LoggerFactory.getLogger(DiagnosisGroupsConverter.class);

    private static final int NUMBER_OF_CHART_SERIES = 6;

    DualSexStatisticsData convert(DiagnosgruppResponse diagnosisGroups, Range range) {
        TableData tableData = DiagnosisGroupsConverter.convertTable(diagnosisGroups);
        List<Integer> topIndexes = getTopColumnIndexes(diagnosisGroups);
        ChartData maleChart = extractChartData(diagnosisGroups, topIndexes, Kon.Male);
        ChartData femaleChart = extractChartData(diagnosisGroups, topIndexes, Kon.Female);
        return new DualSexStatisticsData(tableData, maleChart, femaleChart, range.toString());
    }

    private ChartData extractChartData(DiagnosgruppResponse data, List<Integer> topIndexes, Kon sex) {
        List<ChartSeries> topColumns = getTopColumns(data, topIndexes, sex);
        return new ChartData(topColumns, data.getPeriods());
    }

    private List<ChartSeries> getTopColumns(DiagnosgruppResponse data, List<Integer> topIndexes, Kon sex) {
        List<ChartSeries> topColumns = new ArrayList<>();
        if (topIndexes.isEmpty()) {
            topColumns.add(new ChartSeries("Totalt", createList(data.getRows().size(), 0), true));
            return topColumns;
        }
        for (Integer index : topIndexes) {
            List<Integer> indexData = data.getDataFromIndex(index, sex);
            topColumns.add(new ChartSeries(data.getDiagnosisGroupsAsStrings().get(index), indexData, true));
        }
        if (data.getDiagnosisGroupsAsStrings().size() > NUMBER_OF_CHART_SERIES) {
            List<Integer> remainingData = sumRemaining(topIndexes, data, sex);
            topColumns.add(new ChartSeries("Övriga", remainingData, true));
        }
        return topColumns;
    }

    private List<Integer> createList(int size, int value) {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
             list.add(value);
        }
        return list;
    }

    private List<Integer> sumRemaining(List<Integer> topIndexes, DiagnosgruppResponse data, Kon sex) {
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

    List<Integer> getTopColumnIndexes(DiagnosgruppResponse diagnosisGroups) {
        if (diagnosisGroups.getRows().isEmpty()) {
            return new ArrayList<>();
        }
        List<Pair<Integer, Integer>> columnSums = new ArrayList<>();
        int dataSize = diagnosisGroups.getRows().get(0).getData().size();
        for (int i = 0; i < dataSize; i++) {
            int totalSum = sum(diagnosisGroups.getDataFromIndex(i, Kon.Male)) + sum(diagnosisGroups.getDataFromIndex(i, Kon.Female));
            if (totalSum > 0) {
                columnSums.add(new Pair<>(i, totalSum));
            }
        }
        LOG.debug("Columns: " + diagnosisGroups.getDiagnosisGroupsAsStrings());
        LOG.debug("TopColumnIndexes: " + columnSums);
        Collections.sort(columnSums, new Comparator<Pair<Integer, Integer>>() {
            @Override
            public int compare(Pair<Integer, Integer> o1, Pair<Integer, Integer> o2) {
                return o2.getValue() - o1.getValue();
            }
        });
        List<Integer> sortedIndexes = Lists.transform(columnSums, new Function<Pair<Integer, Integer>, Integer>() {
            @Override
            public Integer apply(Pair<Integer, Integer> integerIntegerPair) {
                return integerIntegerPair.getKey();
            }
        });
        return sortedIndexes.subList(0, Math.min(NUMBER_OF_CHART_SERIES, sortedIndexes.size()));
    }

    private int sum(List<Integer> numbers) {
        int sum = 0;
        for (Integer number : numbers) {
            sum += number;
        }
        return sum;
    }
}
