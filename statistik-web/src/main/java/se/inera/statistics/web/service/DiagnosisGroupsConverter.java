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
import se.inera.statistics.service.report.model.DiagnosgruppResponse;
import se.inera.statistics.service.report.model.Icd;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.OverviewChartRowExtended;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.util.Icd10;
import se.inera.statistics.web.model.ChartCategory;
import se.inera.statistics.web.model.ChartData;
import se.inera.statistics.web.model.ChartSeries;
import se.inera.statistics.web.model.DualSexStatisticsData;
import se.inera.statistics.web.model.TableData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class DiagnosisGroupsConverter extends MultiDualSexConverter<DiagnosgruppResponse> {

    private static final Map<String, List<Integer>> DIAGNOSIS_CHART_GROUPS = createDiagnosisGroupsMap(true);
    static final Map<Integer, String> DIAGNOSKAPITEL_TO_DIAGNOSGRUPP = map(DIAGNOSIS_CHART_GROUPS);
    private static final int DISPLAYED_DIAGNOSIS_GROUPS = 5;
    static final String DIAGNOS_REST_NAME = "Andra diagnosgrupper";
    private static final String OVRIGT_CHART_GROUP = "P00-P96, Q00-Q99, S00-Y98 Övrigt";

    private static Map<Integer, String> map(Map<String, List<Integer>> diagnosisChartGroups) {
        Map<Integer, String> result = new HashMap<>();
        for (Entry<String, List<Integer>> entry : diagnosisChartGroups.entrySet()) {
            for (Integer kapitel : entry.getValue()) {
                result.put(kapitel, entry.getKey());
            }
        }
        return result;
    }

    private static Map<String, List<Integer>> createDiagnosisGroupsMap(boolean includeUnknownGroup) {
        final Map<String, List<Integer>> diagnosisGroups = new LinkedHashMap<>();
        diagnosisGroups.put("A00-E90, G00-L99, N00-N99 Somatiska sjukdomar", Icd10.getKapitelIntIds("A00-B99", "C00-D48", "D50-D89", "E00-E90", "G00-G99", "H00-H59",
                "H00-H59", "H60-H95", "I00-I99", "J00-J99", "K00-K93", "L00-L99", "N00-N99"));
        diagnosisGroups.put("F00-F99 Psykiska sjukdomar", Icd10.getKapitelIntIds("F00-F99"));
        diagnosisGroups.put("M00-M99 Muskuloskeletala sjukdomar", Icd10.getKapitelIntIds("M00-M99"));
        diagnosisGroups.put("O00-O99 Graviditet och förlossning", Icd10.getKapitelIntIds("O00-O99"));
        diagnosisGroups.put(OVRIGT_CHART_GROUP, Icd10.getKapitelIntIds("P00-P96", "Q00-Q99", "S00-T98", "U00-U99", "V01-Y98"));
        diagnosisGroups.put("R00-R99 Symtomdiagnoser", Icd10.getKapitelIntIds("R00-R99"));
        diagnosisGroups.put("Z00-Z99 Faktorer av betydelse för hälsotillståndet och för kontakter med hälso- och sjukvården", Icd10.getKapitelIntIds("Z00-Z99"));
        if (includeUnknownGroup) {
            diagnosisGroups.put(Icd10.UNKNOWN_CODE_NAME, Icd10.getKapitelIntIds(Icd10.OTHER_KAPITEL));
        }
        return diagnosisGroups;
    }

    static List<String> getDiagnosisChartGroupsAsList(boolean includeUnknownGroup) {
        return new ArrayList<>(createDiagnosisGroupsMap(includeUnknownGroup).keySet());
    }

    DualSexStatisticsData convert(DiagnosgruppResponse diagnosisGroups, FilterSettings filterSettings) {
        TableData tableData = convertTable(diagnosisGroups, "%1$s");
        ChartData maleChart = convertChart(diagnosisGroups, Kon.MALE);
        ChartData femaleChart = convertChart(diagnosisGroups, Kon.FEMALE);
        final Filter filter = filterSettings.getFilter();
        final FilterDataResponse filterResponse = new FilterDataResponse(filter);
        final Range range = filterSettings.getRange();
        return new DualSexStatisticsData(tableData, maleChart, femaleChart, range.toString(), filterResponse, Arrays.asList(filterSettings.getMessage()));
    }

    public List<OverviewChartRowExtended> convert(List<OverviewChartRowExtended> diagnosisGroups) {
        List<OverviewChartRowExtended> merged = mergeOverviewChartGroups(diagnosisGroups);
        Collections.sort(merged, (o1,  o2) -> o2.getQuantity() - o1.getQuantity());

        return Converters.convert(merged, DISPLAYED_DIAGNOSIS_GROUPS, DIAGNOS_REST_NAME);
    }

    private List<OverviewChartRowExtended> mergeOverviewChartGroups(List<OverviewChartRowExtended> allGroups) {
        Map<String, OverviewChartRowExtended> mergedGroups = new TreeMap<>(new Comparator<String>() {

            @Override
            public int compare(String o1, String o2) {
                return getDiagnosisChartGroupsAsList(true).indexOf(o1) - getDiagnosisChartGroupsAsList(true).indexOf(o2);
            }

        });

        for (String groupName : getDiagnosisChartGroupsAsList(true)) {
            mergedGroups.put(groupName, new OverviewChartRowExtended(groupName, 0, 0));
        }
        for (OverviewChartRowExtended row : allGroups) {
            String grupp = DIAGNOSKAPITEL_TO_DIAGNOSGRUPP.get(Integer.valueOf(row.getName()));
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

    private ChartData convertChart(DiagnosgruppResponse resp, Kon sex) {
        Map<Integer, List<Integer>> allGroups = extractAllGroups(resp, sex);
        Map<String, List<Integer>> mergedGroups = mergeChartGroups(allGroups);
        ArrayList<ChartSeries> rows = new ArrayList<>();
        for (Entry<String, List<Integer>> entry : mergedGroups.entrySet()) {
            rows.add(new ChartSeries(entry.getKey(), entry.getValue()));
        }

        final List<ChartCategory> categories = Lists.transform(resp.getPeriods(), new Function<String, ChartCategory>() {
            @Override
            public ChartCategory apply(String period) {
                return new ChartCategory(period);
            }
        });
        return new ChartData(rows, categories);
    }

    private Map<String, List<Integer>> mergeChartGroups(Map<Integer, List<Integer>> allGroups) {
        Map<String, List<Integer>> mergedGroups = new TreeMap<>(new Comparator<String>() {

            @Override
            public int compare(String o1, String o2) {
                return getDiagnosisChartGroupsAsList(true).indexOf(o1) - getDiagnosisChartGroupsAsList(true).indexOf(o2);
            }

        });
        List<List<Integer>> values = new ArrayList<>(allGroups.values());
        int listSize = values.isEmpty() ? 0 : values.get(0).toArray().length;
        for (String groupName : getDiagnosisChartGroupsAsList(false)) {
            mergedGroups.put(groupName, createZeroFilledList(listSize));
        }
        for (Entry<Integer, List<Integer>> entry : allGroups.entrySet()) {
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

    private Map<Integer, List<Integer>> extractAllGroups(DiagnosgruppResponse resp, Kon sex) {
        Map<Integer, List<Integer>> allGroups = new HashMap<>();
        for (int i = 0; i < resp.getIcdTyps().size(); i++) {
            Icd groupName = resp.getIcdTyps().get(i);
            allGroups.put(groupName.getNumericalId(), resp.getDataFromIndex(i, sex));
        }
        return allGroups;
    }

    private void addGroupToMergedChartGroups(Map<String, List<Integer>> mergedGroups, Integer groupId, List<Integer> values) {
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

    private String getMergedChartGroupName(Integer groupId) {
        for (Entry<String, List<Integer>> entry : DIAGNOSIS_CHART_GROUPS.entrySet()) {
            if (entry.getValue().contains(groupId)) {
                return entry.getKey();
            }
        }
        throw new RuntimeException("Unknown groupId: " + groupId);
    }

}
