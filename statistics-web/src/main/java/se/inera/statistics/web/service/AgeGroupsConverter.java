package se.inera.statistics.web.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import se.inera.statistics.service.report.model.AgeGroupsResponse;
import se.inera.statistics.service.report.model.AgeGroupsRow;
import se.inera.statistics.service.report.model.Sex;
import se.inera.statistics.web.model.AgeGroupsData;
import se.inera.statistics.web.model.ChartData;
import se.inera.statistics.web.model.ChartSeries;
import se.inera.statistics.web.model.NamedData;
import se.inera.statistics.web.model.TableData;

public class AgeGroupsConverter {

    private TableData convertToTable(List<AgeGroupsRow> ageGroups) {
        List<NamedData> data = new ArrayList<>();
        int accumulatedSum = 0;
        for (AgeGroupsRow row : ageGroups) {
            int rowSum = row.getFemale() + row.getMale();
            accumulatedSum += rowSum;
            data.add(new NamedData(row.getGroup(), Arrays.asList(rowSum, row.getFemale(), row.getMale(), accumulatedSum)));
        }
        return TableData.createWithSingleHeadersRow(data, Arrays.asList("Åldersgrupper", "Antal sjukfall", "Antal kvinnor", "Antal män", "Summering"));
    }


    private ChartData convertToChart(AgeGroupsResponse resp) {
        List<String> groups = resp.getGroups();
        List<Integer> femaleData = resp.getDataForSex(Sex.Female);
        List<Integer> maleData = resp.getDataForSex(Sex.Male);
        ArrayList<ChartSeries> series = new ArrayList<>();
        series.add(new ChartSeries("Antal sjukskrivningar Män", maleData));
        series.add(new ChartSeries("Antal sjukskrivningar Kvinnor", femaleData));
        return new ChartData(series, groups);
    }

    AgeGroupsData convert(AgeGroupsResponse resp) {
        TableData tableData = convertToTable(resp.getRows());
        ChartData chartData = convertToChart(resp);
        int monthsIncluded = resp.getNumberOfMonthsCalculated();
        return new AgeGroupsData(tableData, chartData, monthsIncluded);
    }
}
