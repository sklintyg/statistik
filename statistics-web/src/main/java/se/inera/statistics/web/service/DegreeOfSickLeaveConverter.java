package se.inera.statistics.web.service;

import java.util.ArrayList;
import java.util.List;

import se.inera.statistics.service.report.model.DegreeOfSickLeaveResponse;
import se.inera.statistics.service.report.model.DualSexDataRow;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.Sex;
import se.inera.statistics.web.model.ChartData;
import se.inera.statistics.web.model.ChartSeries;
import se.inera.statistics.web.model.DualSexStatisticsData;
import se.inera.statistics.web.model.NamedData;
import se.inera.statistics.web.model.TableData;
import se.inera.statistics.web.model.TableHeader;

public class DegreeOfSickLeaveConverter {

    DualSexStatisticsData convert(DegreeOfSickLeaveResponse degreeOfSickLeave, Range range) {
        TableData tableData = convertTable(degreeOfSickLeave);
        ChartData maleChart = extractChartData(degreeOfSickLeave, Sex.Male);
        ChartData femaleChart = extractChartData(degreeOfSickLeave, Sex.Female);
        return new DualSexStatisticsData(tableData, maleChart, femaleChart, range.toString());
    }

    private ChartData extractChartData(DegreeOfSickLeaveResponse data, Sex sex) {
        List<ChartSeries> series = getChartSeries(data, sex);
        return new ChartData(series, data.getPeriods());
    }

    private List<ChartSeries> getChartSeries(DegreeOfSickLeaveResponse data, Sex sex) {
        List<ChartSeries> series = new ArrayList<>();
        for (int i = 0; i < data.getDegreesOfSickLeave().size(); i++) {
            List<Integer> indexData = data.getDataFromIndex(i, sex);
            series.add(new ChartSeries("Antal sjukfall med " + data.getDegreesOfSickLeave().get(i) + "% sjukskrivningsgrad", indexData, true));
        }
        return series;
    }

    static TableData convertTable(DegreeOfSickLeaveResponse resp) {
        List<NamedData> rows = getTableRows(resp);
        ServiceUtil.addSumRow(rows, false);
        List<List<TableHeader>> headers = getTableHeaders(resp);
        return new TableData(rows, headers);
    }

    private static List<NamedData> getTableRows(DegreeOfSickLeaveResponse resp) {
        List<NamedData> rows = new ArrayList<>();
        int accumulatedSum = 0;
        for (DualSexDataRow row : resp.getRows()) {
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

    private static List<List<TableHeader>> getTableHeaders(DegreeOfSickLeaveResponse resp) {
        List<TableHeader> topHeaderRow = new ArrayList<>();
        topHeaderRow.add(new TableHeader(""));
        topHeaderRow.add(new TableHeader(""));
        List<String> degreesOfSickLeave = resp.getDegreesOfSickLeave();
        for (String groupName : degreesOfSickLeave) {
                topHeaderRow.add(new TableHeader("Antal sjukfall med " + groupName + "% sjukskrivningsgrad", 2));
        }

        List<TableHeader> subHeaderRow = new ArrayList<>();
        subHeaderRow.add(new TableHeader("Period"));
        subHeaderRow.add(new TableHeader("Antal sjukfall"));
        for (int i = 0; i < degreesOfSickLeave.size(); i++) {
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
