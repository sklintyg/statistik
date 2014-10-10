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

import se.inera.statistics.service.report.model.Avsnitt;
import se.inera.statistics.service.report.model.DiagnosgruppResponse;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.KonDataRow;
import se.inera.statistics.service.report.model.KonField;
import se.inera.statistics.service.report.model.OverviewChartRowExtended;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.web.model.ChartData;
import se.inera.statistics.web.model.ChartSeries;
import se.inera.statistics.web.model.DualSexStatisticsData;
import se.inera.statistics.web.model.NamedData;
import se.inera.statistics.web.model.TableData;
import se.inera.statistics.web.model.TableHeader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class DiagnosisGroupsConverter {

    private static final Map<String, List<String>> DIAGNOSIS_CHART_GROUPS = createDiagnosisGroupsMap();
    private static final Map<String, String> DIAGNOSKAPITEL_TO_DIAGNOSGRUPP = map(DIAGNOSIS_CHART_GROUPS);
    private static final int DISPLAYED_DIAGNOSIS_GROUPS = 5;
    public static final int PERCENT = 100;

    private static Map<String, String> map(Map<String, List<String>> diagnosisChartGroups) {
        Map<String, String> result = new HashMap<>();
        for (Entry<String, List<String>> entry : diagnosisChartGroups.entrySet()) {
            for (String kapitel : entry.getValue()) {
                result.put(kapitel, entry.getKey());
            }
        }
        return result;
    }

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
        diagnosisGroups.put("Utan giltig ICD-10 kod", Arrays.asList("Ö00-Ö00"));
        return diagnosisGroups;
    }

    private static List<String> getDiagnosisChartGroupsAsList() {
        return new ArrayList<>(DIAGNOSIS_CHART_GROUPS.keySet());
    }

    DualSexStatisticsData convert(DiagnosgruppResponse diagnosisGroups, Range range) {
        boolean empty = isUtanGiltigEmpty(diagnosisGroups);
        diagnosisGroups = removeUtanGiltigWhenEmpty(diagnosisGroups, empty);
        TableData tableData = convertTable(diagnosisGroups);
        ChartData maleChart = convertChart(diagnosisGroups, Kon.Male, empty);
        ChartData femaleChart = convertChart(diagnosisGroups, Kon.Female, empty);
        return new DualSexStatisticsData(tableData, maleChart, femaleChart, range.toString());
    }

    private boolean isUtanGiltigEmpty(DiagnosgruppResponse diagnosisGroups) {
        List<KonDataRow> rows = diagnosisGroups.getRows();
        boolean empty = true;
        for (KonDataRow row : rows) {
            List<Integer> f = row.getDataForSex(Kon.Female);
            List<Integer> m = row.getDataForSex(Kon.Male);
            if (f.get(f.size() - 1) > 0 || m.get(m.size() - 1) > 0) {
                empty = false;
                break;
            }
        }
        return empty;
    }

    private DiagnosgruppResponse removeUtanGiltigWhenEmpty(DiagnosgruppResponse diagnosisGroups, boolean remove) {
        List<KonDataRow> rows = diagnosisGroups.getRows();
        if (remove && rows.size() > 0) {
            List<KonDataRow> newRows = new ArrayList<>();
            List<Avsnitt> newAvsnitts = diagnosisGroups.getAvsnitts();
            for (KonDataRow row : rows) {
                List<KonField> col = row.getData();
                col.remove(col.size() - 1);
                newRows.add(new KonDataRow(row.getName(), col));
            }
            newAvsnitts.remove(newAvsnitts.size() - 1);
            return new DiagnosgruppResponse(newAvsnitts, newRows);
        } else {
            return diagnosisGroups;
        }
    }

    public List<OverviewChartRowExtended> convert(List<OverviewChartRowExtended> diagnosisGroups) {
        List<OverviewChartRowExtended> merged = mergeOverviewChartGroups(diagnosisGroups);

        List<OverviewChartRowExtended> result = new ArrayList<>();
        for (OverviewChartRowExtended row : merged.subList(0, DISPLAYED_DIAGNOSIS_GROUPS)) {
            int previous = row.getQuantity() - row.getAlternation();
            int percentChange = previous != 0 ? row.getAlternation() * PERCENT / previous : 0;
            result.add(new OverviewChartRowExtended(row.getName(), row.getQuantity(), percentChange));
        }
        return result;
    }

    private List<OverviewChartRowExtended> mergeOverviewChartGroups(List<OverviewChartRowExtended> allGroups) {
        Map<String, OverviewChartRowExtended> mergedGroups = new TreeMap<>(new Comparator<String>() {

            @Override
            public int compare(String o1, String o2) {
                return getDiagnosisChartGroupsAsList().indexOf(o1) - getDiagnosisChartGroupsAsList().indexOf(o2);
            }

        });

        for (String groupName : getDiagnosisChartGroupsAsList()) {
            mergedGroups.put(groupName, new OverviewChartRowExtended(groupName, 0, 0));
        }
        for (OverviewChartRowExtended row : allGroups) {
            String grupp = DIAGNOSKAPITEL_TO_DIAGNOSGRUPP.get(row.getName());
            if (grupp != null) {
                OverviewChartRowExtended mergedRow = mergedGroups.get(grupp);
                OverviewChartRowExtended newRow = new OverviewChartRowExtended(mergedRow.getName(), mergedRow.getQuantity() + row.getQuantity(), mergedRow.getAlternation() + row.getAlternation());
                mergedGroups.put(newRow.getName(), newRow);
            }
        }

        List<OverviewChartRowExtended> result = new ArrayList<>();
        result.addAll(mergedGroups.values());
        return result;
    }

    private ChartData convertChart(DiagnosgruppResponse resp, Kon sex, boolean empty) {
        Map<String, List<Integer>> allGroups = extractAllGroups(resp, sex);
        Map<String, List<Integer>> mergedGroups = mergeChartGroups(allGroups, empty);
        ArrayList<ChartSeries> rows = new ArrayList<>();
        for (Entry<String, List<Integer>> entry : mergedGroups.entrySet()) {
            rows.add(new ChartSeries(entry.getKey(), entry.getValue(), true));
        }

        List<String> headers = resp.getPeriods();
        return new ChartData(rows, headers);
    }

    private Map<String, List<Integer>> mergeChartGroups(Map<String, List<Integer>> allGroups, boolean empty) {
        Map<String, List<Integer>> mergedGroups = new TreeMap<>(new Comparator<String>() {

            @Override
            public int compare(String o1, String o2) {
                return getDiagnosisChartGroupsAsList().indexOf(o1) - getDiagnosisChartGroupsAsList().indexOf(o2);
            }

        });
        List<List<Integer>> values = new ArrayList<List<Integer>>(allGroups.values());
        int listSize = values.isEmpty() ? 0 : values.get(0).toArray().length;
        if (empty) {
            for (int i = 0; i < getDiagnosisChartGroupsAsList().size() - 1; i++) {
                mergedGroups.put(getDiagnosisChartGroupsAsList().get(i), createZeroFilledList(listSize));
            }
        } else {
            for (String groupName : getDiagnosisChartGroupsAsList()) {
                mergedGroups.put(groupName, createZeroFilledList(listSize));
            }
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
        for (int i = 0; i < resp.getAvsnitts().size(); i++) {
            Avsnitt groupName = resp.getAvsnitts().get(i);
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
        List<List<TableHeader>> headers = getTableHeaders(resp);
        return new TableData(rows, headers);
    }

    private static List<NamedData> getTableRows(DiagnosgruppResponse resp) {
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
        subHeaderRow.add(new TableHeader("Antal sjukfall totalt"));
        for (String s : diagnosisGroups) {
            subHeaderRow.add(new TableHeader("Kvinnor"));
            subHeaderRow.add(new TableHeader("Män"));
        }

        List<List<TableHeader>> headers = new ArrayList<>();
        headers.add(topHeaderRow);
        headers.add(subHeaderRow);
        return headers;
    }
}
