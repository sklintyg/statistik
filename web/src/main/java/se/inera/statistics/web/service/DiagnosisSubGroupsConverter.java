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

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

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
        for (Integer index : topIndexes) {
            List<Integer> indexData = data.getDataFromIndex(index, sex);
            topColumns.add(new ChartSeries(data.getDiagnosisGroupsAsStrings().get(index), indexData, true));
        }
        if (data.getDiagnosisGroupsAsStrings().size() > NUMBER_OF_CHART_SERIES) {
            List<Integer> remainingData = sumRemaining(topIndexes, data, sex);
            topColumns.add(new ChartSeries("Övriga diagnosavsnitt", remainingData, true));
        }
        return topColumns;
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

    private List<Integer> getTopColumnIndexes(DiagnosgruppResponse diagnosisGroups) {
        if (diagnosisGroups.getRows().isEmpty()) {
            return new ArrayList<Integer>();
        }
        TreeMap<Integer, Integer> columnSums = new TreeMap<>();
        int dataSize = diagnosisGroups.getRows().get(0).getData().size();
        for (int i = 0; i < dataSize; i++) {
            columnSums.put(sum(diagnosisGroups.getDataFromIndex(i, Kon.Male)) + sum(diagnosisGroups.getDataFromIndex(i, Kon.Female)), i);
        }
        LOG.debug("Columns: " + diagnosisGroups.getDiagnosisGroupsAsStrings());
        LOG.debug("TopColumnIndexes: " + columnSums);
        ArrayList<Integer> arrayList = new ArrayList<>(columnSums.descendingMap().values());
        return arrayList.subList(0, Math.min(NUMBER_OF_CHART_SERIES, arrayList.size()));
    }

    private int sum(List<Integer> numbers) {
        int sum = 0;
        for (Integer number : numbers) {
            sum += number;
        }
        return sum;
    }
}
