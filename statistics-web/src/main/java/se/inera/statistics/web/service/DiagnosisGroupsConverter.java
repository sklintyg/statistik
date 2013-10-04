package se.inera.statistics.web.service;

import java.util.*;
import java.util.Map.Entry;

import se.inera.statistics.service.report.model.DiagnosisGroup;
import se.inera.statistics.service.report.model.DiagnosisGroupResponse;
import se.inera.statistics.service.report.model.DiagnosisGroupRow;
import se.inera.statistics.service.report.model.Sex;
import se.inera.statistics.web.model.DiagnosisGroupsData;
import se.inera.statistics.web.model.NamedData;
import se.inera.statistics.web.model.TableData;

public class DiagnosisGroupsConverter {

    private static final List<String> DIAGNOSIS_CHART_GROUPS = Arrays.asList(
            "Somatiska sjukdomar (A00-E90, G00-L99, N00-N99)",
            "Psykiska sjukdomar (F00-F99)",
            "Muskuloskeletala sjukdomar (M00-M99)",
            "Graviditet och förlossning (O00-O99)",
            "Övrigt (P00-P96, Q00-Q99, S00-Y98)",
            "Symtomdiagnoser (R00-R99)",
            "Faktorer av betydelse för hälsotillståndet och för kontakter med hälso- och sjukvården (Z00-Z99)");

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
        ArrayList<NamedData> rows = new ArrayList<>();
        for (Entry<String, List<Integer>> entry : mergedGroups.entrySet()) {
            rows.add(new NamedData(entry.getKey(), entry.getValue()));
        }

        List<String> headers = resp.getPeriods();
        return new TableData(rows, headers);
    }

    private Map<String, List<Integer>> mergeChartGroups(Map<String, List<Integer>> allGroups) {
        Map<String, List<Integer>> mergedGroups = new TreeMap<>(new Comparator<String>(){

            @Override
            public int compare(String o1, String o2) {
                return DIAGNOSIS_CHART_GROUPS.indexOf(o1) - DIAGNOSIS_CHART_GROUPS.indexOf(o2);
            }

        });
        List<List<Integer>> values = new ArrayList<List<Integer>>(allGroups.values());
        int listSize = values.isEmpty() ? 0 : values.get(0).toArray().length;
        for(String groupName : DIAGNOSIS_CHART_GROUPS){
            mergedGroups.put(groupName, createZeroFilledList(listSize));
        }
        for (Entry<String, List<Integer>> entry : allGroups.entrySet()) {
            addGroupToMergedChartGroups(mergedGroups, entry.getKey(), entry.getValue());
        }
        return mergedGroups;
    }

    private List<Integer> createZeroFilledList(int listSize) {
        ArrayList<Integer> listOfZeros = new ArrayList<>();
        for( int i = 0; i < listSize; i++){
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
        List<String> g1 = Arrays.asList("A00-B99", "C00-D48", "D50-D89", "E00-E90", "G00-G99", "H00-H59", "H00-H59", "H60-H95", "I00-I99",
                "J00-J99", "K00-K93", "L00-L99", "N00-N99");
        List<String> g2 = Arrays.asList("F00-F99");
        List<String> g3 = Arrays.asList("M00-M99");
        List<String> g4 = Arrays.asList("O00-O99");
        List<String> g5 = Arrays.asList("P00-P96", "Q00-Q99", "S00-T98", "U00-U99", "V01-Y98");
        List<String> g6 = Arrays.asList("R00-R99");
        List<String> g7 = Arrays.asList("Z00-Z99");
        if (g1.contains(groupId)) {
            return DIAGNOSIS_CHART_GROUPS.get(0);
        } else if (g2.contains(groupId)) {
            return DIAGNOSIS_CHART_GROUPS.get(1);
        } else if (g3.contains(groupId)) {
            return DIAGNOSIS_CHART_GROUPS.get(2);
        } else if (g4.contains(groupId)) {
            return DIAGNOSIS_CHART_GROUPS.get(3);
        } else if (g5.contains(groupId)) {
            return DIAGNOSIS_CHART_GROUPS.get(4);
        } else if (g6.contains(groupId)) {
            return DIAGNOSIS_CHART_GROUPS.get(5);
        } else if (g7.contains(groupId)) {
            return DIAGNOSIS_CHART_GROUPS.get(6);
        } else {
            // Unknown groups should never occur, but if it do than add it to
            // the Ovrigt-group (or fail in development)
            //TODO Add error logging
            assert false;
            return DIAGNOSIS_CHART_GROUPS.get(4);
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
