/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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

import se.inera.statistics.service.report.model.DiagnosgruppResponse;
import se.inera.statistics.service.report.model.Icd;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.OverviewChartRowExtended;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.util.DiagnosisGroup;
import se.inera.statistics.service.report.util.Icd10;
import se.inera.statistics.web.model.ChartCategory;
import se.inera.statistics.web.model.ChartData;
import se.inera.statistics.web.model.ChartSeries;
import se.inera.statistics.web.model.DualSexStatisticsData;
import se.inera.statistics.web.model.TableData;
import se.inera.statistics.web.service.Filter;
import se.inera.statistics.web.service.FilterDataResponse;
import se.inera.statistics.web.service.FilterSettings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class DiagnosisGroupsConverter extends MultiDualSexConverter {

    public static final Map<DiagnosisGroup, List<Integer>> DIAGNOSIS_GROUPS_WITH_UNKNOWN = createDiagnosisGroupsMap(true);
    public static final Map<DiagnosisGroup, List<Integer>> DIAGNOSIS_GROUPS_WITHOUT_UNKNOWN = createDiagnosisGroupsMap(false);
    private static final Map<DiagnosisGroup, List<Integer>> DIAGNOSIS_CHART_GROUPS = DIAGNOSIS_GROUPS_WITH_UNKNOWN;
    static final Map<Integer, String> DIAGNOSKAPITEL_TO_DIAGNOSGRUPP = map(DIAGNOSIS_CHART_GROUPS);
    private static final int DISPLAYED_DIAGNOSIS_GROUPS = 5;
    static final String DIAGNOS_REST_NAME = "Andra diagnosgrupper";
    static final String DIAGNOS_REST_COLOR = "#5D5D5D";

    private static Map<Integer, String> map(Map<DiagnosisGroup, List<Integer>> diagnosisChartGroups) {
        Map<Integer, String> result = new HashMap<>();
        for (Entry<DiagnosisGroup, List<Integer>> entry : diagnosisChartGroups.entrySet()) {
            for (Integer kapitel : entry.getValue()) {
                result.put(kapitel, entry.getKey().getName());
            }
        }
        return result;
    }

    private static Map<DiagnosisGroup, List<Integer>> createDiagnosisGroupsMap(boolean includeUnknownGroup) {
        final Map<DiagnosisGroup, List<Integer>> diagnosisGroups = new LinkedHashMap<>();

        for (DiagnosisGroup group: DiagnosisGroup.values()) {
            diagnosisGroups.put(group, Icd10.getKapitelIntIds(group.getChapters()));
        }

        if (!includeUnknownGroup) {
            diagnosisGroups.remove(DiagnosisGroup.NO_GROUP);
        }

        return diagnosisGroups;
    }

    static List<String> getDiagnosisChartGroupsAsList(boolean includeUnknownGroup) {
        return (includeUnknownGroup ? DIAGNOSIS_GROUPS_WITH_UNKNOWN : DIAGNOSIS_GROUPS_WITHOUT_UNKNOWN)
                .keySet()
                .stream()
                .map(DiagnosisGroup::getName)
                .collect(Collectors.toList());
    }

    public DualSexStatisticsData convert(DiagnosgruppResponse diagnosisGroups, FilterSettings filterSettings) {
        TableData tableData = convertTable(diagnosisGroups, "%1$s");
        ChartData maleChart = convertChart(diagnosisGroups, Kon.MALE);
        ChartData femaleChart = convertChart(diagnosisGroups, Kon.FEMALE);
        final Filter filter = filterSettings.getFilter();
        final FilterDataResponse filterResponse = new FilterDataResponse(filter);
        final Range range = filterSettings.getRange();
        return new DualSexStatisticsData(tableData, maleChart, femaleChart, range.toString(), filterResponse,
                Converters.combineMessages(filterSettings.getMessage()));
    }

    public List<OverviewChartRowExtended> convert(List<OverviewChartRowExtended> diagnosisGroups) {
        List<OverviewChartRowExtended> merged = mergeOverviewChartGroups(diagnosisGroups);
        Collections.sort(merged, (o1, o2) -> o2.getQuantity() - o1.getQuantity());

        return Converters.convert(merged, DISPLAYED_DIAGNOSIS_GROUPS, DIAGNOS_REST_NAME, DIAGNOS_REST_COLOR);
    }

    private List<OverviewChartRowExtended> mergeOverviewChartGroups(List<OverviewChartRowExtended> allGroups) {
        Map<String, OverviewChartRowExtended> mergedGroups = new TreeMap<>(new Comparator<String>() {

            @Override
            public int compare(String o1, String o2) {
                return getDiagnosisChartGroupsAsList(true).indexOf(o1) - getDiagnosisChartGroupsAsList(true).indexOf(o2);
            }

        });

        for (DiagnosisGroup group : DIAGNOSIS_GROUPS_WITH_UNKNOWN.keySet()) {
            mergedGroups.put(group.getName(), new OverviewChartRowExtended(group.getName(), 0, 0, group.getColor()));
        }
        for (OverviewChartRowExtended row : allGroups) {
            String grupp = DIAGNOSKAPITEL_TO_DIAGNOSGRUPP.get(Integer.valueOf(row.getName()));
            if (grupp != null) {
                OverviewChartRowExtended mergedRow = mergedGroups.get(grupp);
                int quantity = mergedRow.getQuantity() + row.getQuantity();
                int alternation = mergedRow.getAlternation() + row.getAlternation();

                OverviewChartRowExtended newRow = new OverviewChartRowExtended(mergedRow.getName(),
                        quantity, alternation, mergedRow.getColor());
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

        Map<String, String> colors = DiagnosisGroup.getColors();

        ArrayList<ChartSeries> rows = new ArrayList<>();
        for (Entry<String, List<Integer>> entry : mergedGroups.entrySet()) {
            rows.add(new ChartSeries(entry.getKey(), entry.getValue(), null, colors.get(entry.getKey())));
        }

        final List<ChartCategory> categories = resp.getPeriods().stream().map(ChartCategory::new).collect(Collectors.toList());
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
        for (Entry<DiagnosisGroup, List<Integer>> entry : DIAGNOSIS_CHART_GROUPS.entrySet()) {
            if (entry.getValue().contains(groupId)) {
                return entry.getKey().getName();
            }
        }
        throw new RuntimeException("Unknown groupId: " + groupId);
    }

}
