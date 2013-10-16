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

import se.inera.statistics.service.report.model.DiagnosisGroup;
import se.inera.statistics.service.report.model.DiagnosisGroupResponse;
import se.inera.statistics.service.report.model.DiagnosisGroupRow;
import se.inera.statistics.service.report.model.Sex;
import se.inera.statistics.web.model.ChartData;
import se.inera.statistics.web.model.ChartSeries;
import se.inera.statistics.web.model.DualSexStatisticsData;
import se.inera.statistics.web.model.NamedData;
import se.inera.statistics.web.model.TableData;
import se.inera.statistics.web.model.TableHeader;

public class DiagnosisGroupsConverter {

    private static final LinkedHashMap<String, List<String>> DIAGNOSIS_CHART_GROUPS = createDiagnosisGroupsMap();
    private static final String OVRIGT_CHART_GROUP = "Övrigt (P00-P96, Q00-Q99, S00-Y98)";

    private static LinkedHashMap<String, List<String>> createDiagnosisGroupsMap() {
        final LinkedHashMap<String, List<String>> diagnosisGroups = new LinkedHashMap<>();
        diagnosisGroups.put("Somatiska sjukdomar (A00-E90, G00-L99, N00-N99)", Arrays.asList("A00-B99", "C00-D48", "D50-D89", "E00-E90", "G00-G99", "H00-H59",
                "H00-H59", "H60-H95", "I00-I99", "J00-J99", "K00-K93", "L00-L99", "N00-N99"));
        diagnosisGroups.put("Psykiska sjukdomar (F00-F99)", Arrays.asList("F00-F99"));
        diagnosisGroups.put("Muskuloskeletala sjukdomar (M00-M99)", Arrays.asList("M00-M99"));
        diagnosisGroups.put("Graviditet och förlossning (O00-O99)", Arrays.asList("O00-O99"));
        diagnosisGroups.put(OVRIGT_CHART_GROUP, Arrays.asList("P00-P96", "Q00-Q99", "S00-T98", "U00-U99", "V01-Y98"));
        diagnosisGroups.put("Symtomdiagnoser (R00-R99)", Arrays.asList("R00-R99"));
        diagnosisGroups.put("Faktorer av betydelse för hälsotillståndet och för kontakter med hälso- och sjukvården (Z00-Z99)", Arrays.asList("Z00-Z99"));
        return diagnosisGroups;
    }

    private static List<String> getDiagnosisChartGroupsAsList() {
        return new ArrayList<>(DIAGNOSIS_CHART_GROUPS.keySet());
    }

    DualSexStatisticsData convert(DiagnosisGroupResponse diagnosisGroups) {
        TableData tableData = convertTable(diagnosisGroups);
        ChartData maleChart = convertChart(diagnosisGroups, Sex.Male);
        ChartData femaleChart = convertChart(diagnosisGroups, Sex.Female);
        return new DualSexStatisticsData(tableData, maleChart, femaleChart);
    }

    private ChartData convertChart(DiagnosisGroupResponse resp, Sex sex) {
        Map<String, List<Integer>> allGroups = extractAllGroups(resp, sex);
        Map<String, List<Integer>> mergedGroups = mergeChartGroups(allGroups);
        ArrayList<ChartSeries> rows = new ArrayList<>();
        for (Entry<String, List<Integer>> entry : mergedGroups.entrySet()) {
            rows.add(new ChartSeries(entry.getKey(), entry.getValue()));
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

    private Map<String, List<Integer>> extractAllGroups(DiagnosisGroupResponse resp, Sex sex) {
        Map<String, List<Integer>> allGroups = new HashMap<>();
        for (int i = 0; i < resp.getDiagnosisGroups().size(); i++) {
            DiagnosisGroup groupName = resp.getDiagnosisGroups().get(i);
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

    static TableData convertTable(DiagnosisGroupResponse resp) {
        List<NamedData> rows = getTableRows(resp);
        List<List<TableHeader>> headers = getTableHeaders(resp);
        return new TableData(rows, headers);
    }

    private static List<NamedData> getTableRows(DiagnosisGroupResponse resp) {
        List<NamedData> rows = new ArrayList<>();
        for (DiagnosisGroupRow row : resp.getRows()) {
            List<Integer> mergedSexData = ServiceUtil.getMergedSexData(row);
            List<Integer> mergedAndSummed = ServiceUtil.getAppendedSum(mergedSexData);
            rows.add(new NamedData(row.getName(), mergedAndSummed));
        }
        return rows;
    }

    private static List<List<TableHeader>> getTableHeaders(DiagnosisGroupResponse resp) {
        List<TableHeader> topHeaderRow = new ArrayList<>();
        topHeaderRow.add(new TableHeader(""));
        List<String> diagnosisGroups = resp.getDiagnosisGroupsAsStrings();
        for (String groupName : diagnosisGroups) {
                topHeaderRow.add(new TableHeader(groupName, 2));
        }

        List<TableHeader> subHeaderRow = new ArrayList<>();
        subHeaderRow.add(new TableHeader("Period"));
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
