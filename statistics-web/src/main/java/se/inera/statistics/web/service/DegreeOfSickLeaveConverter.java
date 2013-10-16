package se.inera.statistics.web.service;

import java.util.ArrayList;
import java.util.List;

import se.inera.statistics.service.report.model.DegreeOFSickLeaveRow;
import se.inera.statistics.service.report.model.DegreeOfSickLeaveResponse;
import se.inera.statistics.service.report.model.Sex;
import se.inera.statistics.web.model.ChartData;
import se.inera.statistics.web.model.ChartSeries;
import se.inera.statistics.web.model.DualSexStatisticsData;
import se.inera.statistics.web.model.NamedData;
import se.inera.statistics.web.model.TableData;
import se.inera.statistics.web.model.TableHeader;

public class DegreeOfSickLeaveConverter {

    DualSexStatisticsData convert(DegreeOfSickLeaveResponse degreeOfSickLeave) {
        TableData tableData = convertTable(degreeOfSickLeave);
        ChartData maleChart = extractChartData(degreeOfSickLeave, Sex.Male);
        ChartData femaleChart = extractChartData(degreeOfSickLeave, Sex.Female);
        return new DualSexStatisticsData(tableData, maleChart, femaleChart);
    }

    private ChartData extractChartData(DegreeOfSickLeaveResponse data, Sex sex) {
        List<ChartSeries> series = getChartSeries(data, sex);
        return new ChartData(series, data.getPeriods());
    }

    private List<ChartSeries> getChartSeries(DegreeOfSickLeaveResponse data, Sex sex) {
        List<ChartSeries> series = new ArrayList<>();
        for (int i = 0; i < data.getDegreesOfSickLeave().size(); i++) {
            List<Integer> indexData = data.getDataFromIndex(i, sex);
            series.add(new ChartSeries(data.getDegreesOfSickLeave().get(i), indexData));
        }
        return series;
    }

    static TableData convertTable(DegreeOfSickLeaveResponse resp) {
        List<NamedData> rows = getTableRows(resp);
        List<List<TableHeader>> headers = getTableHeaders(resp);
        return new TableData(rows, headers);
    }

    private static List<NamedData> getTableRows(DegreeOfSickLeaveResponse resp) {
        List<NamedData> rows = new ArrayList<>();
        for (DegreeOFSickLeaveRow row : resp.getRows()) {
            List<Integer> mergedSexData = ServiceUtil.getMergedSexData(row);
            List<Integer> mergedAndSummed = ServiceUtil.getAppendedSum(mergedSexData);
            rows.add(new NamedData(row.getName(), mergedAndSummed));
        }
        return rows;
    }

    private static List<List<TableHeader>> getTableHeaders(DegreeOfSickLeaveResponse resp) {
        List<TableHeader> topHeaderRow = new ArrayList<>();
        topHeaderRow.add(new TableHeader(""));
        List<String> degreesOfSickLeave = resp.getDegreesOfSickLeave();
        for (String groupName : degreesOfSickLeave) {
                topHeaderRow.add(new TableHeader(groupName, 2));
        }

        List<TableHeader> subHeaderRow = new ArrayList<>();
        subHeaderRow.add(new TableHeader("Period"));
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
