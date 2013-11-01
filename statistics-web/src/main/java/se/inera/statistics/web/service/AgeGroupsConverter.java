package se.inera.statistics.web.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import se.inera.statistics.service.report.model.AgeGroupsResponse;
import se.inera.statistics.service.report.model.db.AgeGroupsRow;
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
        series.add(new ChartSeries("Antal sjukfall män", maleData, false, Sex.Male));
        series.add(new ChartSeries("Antal sjukfall kvinnor", femaleData, false, Sex.Female));
        return new ChartData(series, groups);
    }

    AgeGroupsData convert(AgeGroupsResponse resp) {
        TableData tableData = convertToTable(resp.getAgeGroupsRows());
        ChartData chartData = convertToChart(resp);
        int monthsIncluded = resp.getMonths();
        return new AgeGroupsData(tableData, chartData, monthsIncluded);
    }
}
