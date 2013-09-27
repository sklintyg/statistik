package se.inera.statistics.web.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import se.inera.statistics.service.report.model.DiagnosisGroup;
import se.inera.statistics.service.report.model.DiagnosisGroupResponse;
import se.inera.statistics.service.report.model.DiagnosisGroupRow;
import se.inera.statistics.service.report.model.Sex;
import se.inera.statistics.web.model.DiagnosisGroupsData;
import se.inera.statistics.web.model.NamedData;
import se.inera.statistics.web.model.TableData;

public class DiagnosisGroupsConverter {

    private static final String DIAGNOSIS_CHART_GROUP_1 = "Somatiska sjukdomar (A00-E90, G00-L99, N00-N99)";
    private static final String DIAGNOSIS_CHART_GROUP_2 = "Psykiska sjukdomar (F00-F99)";
    private static final String DIAGNOSIS_CHART_GROUP_3 = "Muskuloskeletala sjukdomar (M00-M99)";
    private static final String DIAGNOSIS_CHART_GROUP_4 = "Graviditet och förlossning (O00-O99)";
    private static final String DIAGNOSIS_CHART_GROUP_5 = "Övrigt (P00-P96, Q00-Q99, S00-Y98)";
    private static final String DIAGNOSIS_CHART_GROUP_6 = "Symtomdiagnoser (R00-R99)";
    private static final String DIAGNOSIS_CHART_GROUP_7 = "Faktorer av betydelse för hälsotillståndet och för kontakter med hälso- och sjukvården (Z00-Z99)";

    DiagnosisGroupsData convert(DiagnosisGroupResponse diagnosisGroups) {
        TableData maleTable = convertDiagnosisGroupsTableData(diagnosisGroups, Sex.Male);
        TableData femaleTable = convertDiagnosisGroupsTableData(diagnosisGroups, Sex.Female);
        TableData maleChart = convertDiagnosisGroupsChartData(diagnosisGroups, Sex.Male);
        TableData femaleChart = convertDiagnosisGroupsChartData(diagnosisGroups, Sex.Female);
        return new DiagnosisGroupsData(maleTable, femaleTable, maleChart, femaleChart);
    }

    private TableData convertDiagnosisGroupsChartData(DiagnosisGroupResponse resp, Sex sex) {
        Map<String, List<Integer>> allGroups = extractAllGroups(resp, sex);
        Map<String, List<Integer>> mergedGroups = mergeChartGroups(allGroups);
        ArrayList<NamedData> rows = new ArrayList<NamedData>();
        for (Entry<String, List<Integer>> entry : mergedGroups.entrySet()) {
            rows.add(new NamedData(entry.getKey(), entry.getValue()));
        }

        List<String> headers = resp.getPeriods();
        return new TableData(rows, headers);
    }

    private Map<String, List<Integer>> mergeChartGroups(Map<String, List<Integer>> allGroups) {
        Map<String, List<Integer>> mergedGroups = new LinkedHashMap<>();
        mergedGroups.put(DIAGNOSIS_CHART_GROUP_1, null);
        mergedGroups.put(DIAGNOSIS_CHART_GROUP_2, null);
        mergedGroups.put(DIAGNOSIS_CHART_GROUP_3, null);
        mergedGroups.put(DIAGNOSIS_CHART_GROUP_4, null);
        mergedGroups.put(DIAGNOSIS_CHART_GROUP_5, null);
        mergedGroups.put(DIAGNOSIS_CHART_GROUP_6, null);
        mergedGroups.put(DIAGNOSIS_CHART_GROUP_7, null);
        for (Entry<String, List<Integer>> entry : allGroups.entrySet()) {
            addGroupToMergedChartGroups(mergedGroups, entry.getKey(), entry.getValue());
        }
        return mergedGroups;
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
        assert list.size() == values.size();
        List<Integer> sum = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            sum.add(list.get(i) + values.get(i));
        }
        return sum;
    }

    private String getMergedChartGroupName(String groupId) {
        List<String> g1 = Arrays.asList(new String[] {"A00-B99", "C00-D48", "D50-D89", "E00-E90", "G00-G99", "H00-H59", "H00-H59", "H60-H95", "I00-I99",
                "J00-J99", "K00-K93", "L00-L99", "N00-N99"});
        List<String> g2 = Arrays.asList(new String[] {"F00-F99"});
        List<String> g3 = Arrays.asList(new String[] {"M00-M99"});
        List<String> g4 = Arrays.asList(new String[] {"O00-O99"});
        List<String> g5 = Arrays.asList(new String[] {"P00-P96", "Q00-Q99", "S00-T98", "U00-U99", "V01-Y98"});
        List<String> g6 = Arrays.asList(new String[] {"R00-R99"});
        List<String> g7 = Arrays.asList(new String[] {"Z00-Z99"});
        if (g1.contains(groupId)) {
            return DIAGNOSIS_CHART_GROUP_1;
        } else if (g2.contains(groupId)) {
            return DIAGNOSIS_CHART_GROUP_2;
        } else if (g3.contains(groupId)) {
            return DIAGNOSIS_CHART_GROUP_3;
        } else if (g4.contains(groupId)) {
            return DIAGNOSIS_CHART_GROUP_4;
        } else if (g5.contains(groupId)) {
            return DIAGNOSIS_CHART_GROUP_5;
        } else if (g6.contains(groupId)) {
            return DIAGNOSIS_CHART_GROUP_6;
        } else if (g7.contains(groupId)) {
            return DIAGNOSIS_CHART_GROUP_7;
        } else {
            // Unknown groups should never occur, but if it do than add it to
            // the Ovrigt-group (or fail in development)
            assert false;
            return DIAGNOSIS_CHART_GROUP_5;
        }
    }


    static TableData convertDiagnosisGroupsTableData(DiagnosisGroupResponse resp, Sex sex) {
        List<String> headers = resp.getDiagnosisGroupsAsStrings();
        ArrayList<NamedData> rows = new ArrayList<NamedData>();
        for (DiagnosisGroupRow row : resp.getRows()) {
            rows.add(new NamedData(row.getPeriod(), row.getDiagnosisGroupData(sex)));
        }
        return new TableData(rows, headers);
    }

}
