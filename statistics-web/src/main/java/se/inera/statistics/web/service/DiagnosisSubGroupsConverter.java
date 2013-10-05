package se.inera.statistics.web.service;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import se.inera.statistics.service.report.model.DiagnosisGroupResponse;
import se.inera.statistics.service.report.model.DiagnosisGroupRow;
import se.inera.statistics.service.report.model.Sex;
import se.inera.statistics.web.model.*;

public class DiagnosisSubGroupsConverter {

    private static final int NUMBER_OF_CHART_SERIES = 6;

    DiagnosisGroupsData convert(DiagnosisGroupResponse diagnosisGroups) {
        TableData tableData = DiagnosisGroupsConverter.convertTable(diagnosisGroups);
        List<Integer> topIndexes = getTopColumnIndexes(diagnosisGroups);
        ChartData maleChart = extractChartData(diagnosisGroups, topIndexes, Sex.Male);
        ChartData femaleChart = extractChartData(diagnosisGroups, topIndexes, Sex.Female);
        return new DiagnosisGroupsData(tableData, maleChart, femaleChart);
    }

    private ChartData extractChartData(DiagnosisGroupResponse data, List<Integer> topIndexes, Sex sex) {
        List<ChartSeries> topColumns = getTopColumns(data, topIndexes, sex);
        return new ChartData(topColumns, data.getPeriods());
    }

    private List<ChartSeries> getTopColumns(DiagnosisGroupResponse data, List<Integer> topIndexes, Sex sex) {
        List<ChartSeries> topColumns = new ArrayList<>();
        for (Integer index : topIndexes) {
            List<Integer> indexData = data.getDataFromIndex(index, sex);
            topColumns.add(new ChartSeries(data.getDiagnosisGroupsAsStrings().get(index), indexData));
        }
        if (data.getDiagnosisGroupsAsStrings().size() > NUMBER_OF_CHART_SERIES) {
            List<Integer> remainingData = sumRemaining(topIndexes, data, sex);
            topColumns.add(new ChartSeries("Övrigt", remainingData));
        }
        return topColumns;
    }

    private List<Integer> sumRemaining(List<Integer> topIndexes, DiagnosisGroupResponse data, Sex sex) {
        List<Integer> remaining = new ArrayList<>();
        for (int i = 0; i < data.getRows().size(); i++) {
            remaining.add(0);
        }
        List<DiagnosisGroupRow> rows = data.getRows();
        for (int r = 0; r < rows.size(); r++) {
            List<Integer> dataForCurrentSex = rows.get(r).getDiagnosisGroupData(sex);
            for (int i = 0; i < dataForCurrentSex.size(); i++) {
                if (!topIndexes.contains(i)) {
                    remaining.set(r, remaining.get(r) + dataForCurrentSex.get(i));
                }
            }
        }
        return remaining;
    }

    private List<Integer> getTopColumnIndexes(DiagnosisGroupResponse diagnosisGroups) {
        if (diagnosisGroups.getRows().isEmpty()){
            return new ArrayList<Integer>();
        }
        TreeMap<Integer, Integer> columnSums = new TreeMap<>();
        int dataSize = diagnosisGroups.getRows().get(0).getDiagnosisGroupData().size();
        for (int i = 0; i < dataSize; i++) {
            columnSums.put(sum(diagnosisGroups.getDataFromIndex(i, Sex.Male)) + sum(diagnosisGroups.getDataFromIndex(i, Sex.Male)), i);
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
