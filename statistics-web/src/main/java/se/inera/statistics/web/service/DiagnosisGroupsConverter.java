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
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import se.inera.statistics.service.report.model.*;
import se.inera.statistics.service.report.model.Diagnosgrupp;
import se.inera.statistics.web.model.ChartData;
import se.inera.statistics.web.model.ChartSeries;
import se.inera.statistics.web.model.DualSexStatisticsData;
import se.inera.statistics.web.model.NamedData;
import se.inera.statistics.web.model.TableData;
import se.inera.statistics.web.model.TableHeader;

public class DiagnosisGroupsConverter {

    private static final Map<String, List<String>> DIAGNOSIS_CHART_GROUPS = createDiagnosisGroupsMap();
    private static final String OVRIGT_CHART_GROUP = "P00-P96, Q00-Q99, S00-Y98 Övrigt";

    private static Map<String, List<String>> createDiagnosisGroupsMap() {
        final Map<String, List<String>> diagnosisGroups = new LinkedHashMap<>();
        diagnosisGroups.put("A00-E90, G00-L99, N00-N99 Somatiska sjukdomar", Arrays.asList("A00-B99", "C00-D48", "D50-D89", "E00-E90", "G00-G99", "H00-H59",
                "H00-H59", "H60-H95", "I00-I99", "J00-J99", "K00-K93", "L00-L99", "N00-N99"));
        diagnosisGroups.put("F00-F99 Psykiska sjukdomar", Arrays.asList("F00-F99"));
        diagnosisGroups.put("M00-M99 Muskuloskeletala sjukdomar", Arrays.asList("M00-M99"));
        diagnosisGroups.put("O00-O99 Graviditet och förlossning", Arrays.asList("O00-O99"));
        diagnosisGroups.put(OVRIGT_CHART_GROUP, Arrays.asList("P00-P96", "Q00-Q99", "S00-T98", "U00-U99", "V01-Y98"));
        diagnosisGroups.put("R00-R99 Symtomdiagnoser", Arrays.asList("R00-R99"));
        diagnosisGroups.put("Z00-Z99 Faktorer av betydelse för hälsotillståndet och för kontakter med hälso- och sjukvården", Arrays.asList("Z00-Z99"));
        return diagnosisGroups;
    }

    private static List<String> getDiagnosisChartGroupsAsList() {
        return new ArrayList<>(DIAGNOSIS_CHART_GROUPS.keySet());
    }

    DualSexStatisticsData convert(DiagnosgruppResponse diagnosisGroups, Range range) {
        TableData tableData = convertTable(diagnosisGroups);
        ChartData maleChart = convertChart(diagnosisGroups, Kon.Male);
        ChartData femaleChart = convertChart(diagnosisGroups, Kon.Female);
        return new DualSexStatisticsData(tableData, maleChart, femaleChart, range.toString());
    }

    private ChartData convertChart(DiagnosgruppResponse resp, Kon sex) {
        Map<String, List<Integer>> allGroups = extractAllGroups(resp, sex);
        Map<String, List<Integer>> mergedGroups = mergeChartGroups(allGroups);
        ArrayList<ChartSeries> rows = new ArrayList<>();
        for (Entry<String, List<Integer>> entry : mergedGroups.entrySet()) {
            rows.add(new ChartSeries(entry.getKey(), entry.getValue(), true));
        }

        List<String> headers = resp.getPeriods();
        return new ChartData(rows, headers);
    }

    private Map<String, List<Integer>> mergeChartGroups(Map<String, List<Integer>> allGroups) {
        Map<String, List<Integer>> mergedGroups = new TreeMap<>(new Comparator<String>() {

            @Override
            public int compare(String o1, String o2) {
                return getDiagnosisChartGroupsAsList().indexOf(o1) - getDiagnosisChartGroupsAsList().indexOf(o2);
            }

        });
        List<List<Integer>> values = new ArrayList<List<Integer>>(allGroups.values());
        int listSize = values.isEmpty() ? 0 : values.get(0).toArray().length;
        for (String groupName : getDiagnosisChartGroupsAsList()) {
            mergedGroups.put(groupName, createZeroFilledList(listSize));
        }
        for (Entry<String, List<Integer>> entry : allGroups.entrySet()) {
            addGroupToMergedChartGroups(mergedGroups, entry.getKey(), entry.getValue());
        }
        return mergedGroups;
    }

    private List<Integer> createZeroFilledList(int listSize) {
        ArrayList<Integer> listOfZeros = new ArrayList<>();
        for (int i = 0; i < listSize; i++) {
            listOfZeros.add(0);
        }
        return listOfZeros;
    }

    private Map<String, List<Integer>> extractAllGroups(DiagnosgruppResponse resp, Kon sex) {
        Map<String, List<Integer>> allGroups = new HashMap<>();
        for (int i = 0; i < resp.getDiagnosgrupps().size(); i++) {
            Diagnosgrupp groupName = resp.getDiagnosgrupps().get(i);
            allGroups.put(groupName.getId(), resp.getDataFromIndex(i, sex));
        }
        return allGroups;
    }

    private void addGroupToMergedChartGroups(Map<String, List<Integer>> mergedGroups, String groupId, List<Integer> values) {
        String mergedName = getMergedChartGroupName(groupId);
        if (mergedGroups.containsKey(mergedName) && mergedGroups.get(mergedName) != null) {
            List<Integer> sumOfLists = sumLists(mergedGroups.get(mergedName), values);
            mergedGroups.put(mergedName, sumOfLists);
        } else {
            mergedGroups.put(mergedName, values);
        }
    }

    private List<Integer> sumLists(List<Integer> list, List<Integer> values) {
        assert list.size() == values.size() : "List size: " + list.size() + " and values size: " + values.size();
        List<Integer> sum = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            sum.add(list.get(i) + values.get(i));
        }
        return sum;
    }

    private String getMergedChartGroupName(String groupId) {
        for (Entry<String, List<String>> entry : DIAGNOSIS_CHART_GROUPS.entrySet()) {
            if (entry.getValue().contains(groupId)) {
                return entry.getKey();
            }
        }
        // Unknown groups should never occur, but if it do than add it to
        // the Ovrigt-group (or fail in development)
        //TODO Add error logging
        assert false;
        return OVRIGT_CHART_GROUP;
    }

    static TableData convertTable(DiagnosgruppResponse resp) {
        List<NamedData> rows = getTableRows(resp);
        ServiceUtil.addSumRow(rows, false);
        List<List<TableHeader>> headers = getTableHeaders(resp);
        return new TableData(rows, headers);
    }

    private static List<NamedData> getTableRows(DiagnosgruppResponse resp) {
        List<NamedData> rows = new ArrayList<>();
        int accumulatedSum = 0;
        for (KonDataRow row : resp.getRows()) {
            List<Integer> mergedSexData = ServiceUtil.getMergedSexData(row);
            int sum = 0;
            for (Integer dataField : mergedSexData) {
                sum += dataField;
            }
            accumulatedSum += sum;
            mergedSexData.add(0, sum);
            mergedSexData.add(accumulatedSum);
            rows.add(new NamedData(row.getName(), mergedSexData));
        }
        return rows;
    }

    private static List<List<TableHeader>> getTableHeaders(DiagnosgruppResponse resp) {
        List<TableHeader> topHeaderRow = new ArrayList<>();
        topHeaderRow.add(new TableHeader(""));
        topHeaderRow.add(new TableHeader(""));
        List<String> diagnosisGroups = resp.getDiagnosisGroupsAsStrings();
        for (String groupName : diagnosisGroups) {
                topHeaderRow.add(new TableHeader(groupName, 2));
        }
        topHeaderRow.add(new TableHeader(""));

        List<TableHeader> subHeaderRow = new ArrayList<>();
        subHeaderRow.add(new TableHeader("Period"));
        subHeaderRow.add(new TableHeader("Antal sjukfall"));
        for (int i = 0; i < diagnosisGroups.size(); i++) {
            subHeaderRow.add(new TableHeader("Kvinnor"));
            subHeaderRow.add(new TableHeader("Män"));
        }
        subHeaderRow.add(new TableHeader("Summering"));

        List<List<TableHeader>> headers = new ArrayList<>();
        headers.add(topHeaderRow);
        headers.add(subHeaderRow);
        return headers;
    }

}
