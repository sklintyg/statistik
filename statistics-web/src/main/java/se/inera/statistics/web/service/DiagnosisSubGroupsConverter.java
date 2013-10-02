package se.inera.statistics.web.service;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import se.inera.statistics.service.report.model.DiagnosisGroupResponse;
import se.inera.statistics.service.report.model.Sex;
import se.inera.statistics.web.model.DiagnosisGroupsData;
import se.inera.statistics.web.model.NamedData;
import se.inera.statistics.web.model.TableData;

public class DiagnosisSubGroupsConverter {

    private static final int NUMBER_OF_CHART_SERIES = 6;

    DiagnosisGroupsData convert(DiagnosisGroupResponse diagnosisGroups) {
        TableData maleTable = convertDiagnosisSubGroupsTableData(diagnosisGroups, Sex.Male);
        TableData femaleTable = convertDiagnosisSubGroupsTableData(diagnosisGroups, Sex.Female);
        List<Integer> topIndexes = getTopColumnIndexes(maleTable, femaleTable);
        TableData maleChart = extractChartData(maleTable, topIndexes);
        TableData femaleChart = extractChartData(femaleTable, topIndexes);
        return new DiagnosisGroupsData(maleTable, femaleTable, maleChart, femaleChart);
    }

    private TableData extractChartData(TableData data, List<Integer> topIndexes) {
        List<NamedData> topColumns = getTopColumns(data, topIndexes);
        return new TableData(topColumns, getDataRowNames(data));
    }

    private List<NamedData> getTopColumns(TableData data, List<Integer> topIndexes) {
        List<NamedData> topColumns = new ArrayList<>();
        for (Integer index : topIndexes) {
            List<Integer> indexData = getDataAtIndex(index, data);
            topColumns.add(new NamedData(data.getHeaders().get(index), indexData));
        }
        if (data.getHeaders().size() > NUMBER_OF_CHART_SERIES) {
            List<Integer> remainingData = sumRemaining(topIndexes, data);
            topColumns.add(new NamedData("Övrigt", remainingData));
        }
        return topColumns;
    }

    private ArrayList<String> getDataRowNames(TableData data) {
        List<NamedData> rows = data.getRows();
        ArrayList<String> names = new ArrayList<String>();
        for (NamedData tableRow : rows) {
            names.add(tableRow.getName());
        }
        return names;
    }

    private List<Integer> sumRemaining(List<Integer> topIndexes, TableData data) {
        List<Integer> remaining = new ArrayList<>();
        for (int i = 0; i < data.getRows().size(); i++) {
            remaining.add(0);
        }
        List<NamedData> rows = data.getRows();
        for (int r = 0; r < rows.size(); r++) {
            List<Integer> data2 = rows.get(r).getData();
            for (int i = 0; i < data2.size(); i++) {
                if (!topIndexes.contains(i)) {
                    remaining.set(r, remaining.get(r).intValue() + data2.get(i).intValue());
                }
            }
        }
        return remaining;
    }

    private TableData convertDiagnosisSubGroupsTableData(DiagnosisGroupResponse resp, Sex sex) {
        return DiagnosisGroupsConverter.convertDiagnosisGroupsTableData(resp, sex);
    }

    private List<Integer> getDataAtIndex(Integer index, TableData data) {
        List<Integer> indexData = new ArrayList<>(data.getRows().size());
        List<NamedData> rows = data.getRows();
        for (NamedData tableRow : rows) {
            List<Integer> data2 = tableRow.getData();
            for (int i = 0; i < data2.size(); i++) {
                if (i == index) {
                    indexData.add(data2.get(i));
                }
            }
        }
        return indexData;
    }

    private List<Integer> getTopColumnIndexes(TableData maleData, TableData femaleData) {
        if (maleData.getRows().isEmpty()){
            return new ArrayList<Integer>();
        }
        TreeMap<Integer, Integer> columnSums = new TreeMap<>();
        int dataSize = maleData.getRows().get(0).getData().size();
        for (int i = 0; i < dataSize; i++) {
            columnSums.put(sum(getDataAtIndex(i, maleData)) + sum(getDataAtIndex(i, femaleData)), i);
        }
        ArrayList<Integer> arrayList = new ArrayList<>(columnSums.descendingMap().values());
        return arrayList.subList(0, Math.min(NUMBER_OF_CHART_SERIES, arrayList.size()));
    }

    private int sum(List<Integer> numbers) {
        int sum = 0;
        for (Integer number : numbers) {
            sum += number.intValue();
        }
        return sum;
    }
}
