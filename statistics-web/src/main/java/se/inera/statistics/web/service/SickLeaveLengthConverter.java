package se.inera.statistics.web.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import se.inera.statistics.service.report.model.Sex;
import se.inera.statistics.service.report.model.SickLeaveLengthResponse;
import se.inera.statistics.service.report.model.SickLeaveLengthRow;
import se.inera.statistics.web.model.ChartData;
import se.inera.statistics.web.model.ChartSeries;
import se.inera.statistics.web.model.NamedData;
import se.inera.statistics.web.model.SickLeaveLengthData;
import se.inera.statistics.web.model.TableData;

public class SickLeaveLengthConverter {

    private TableData convertToTable(List<SickLeaveLengthRow> sickLeaveLengths) {
        List<NamedData> data = new ArrayList<>();
        int accumulatedSum = 0;
        for (SickLeaveLengthRow row : sickLeaveLengths) {
            int rowSum = row.getFemale() + row.getMale();
            accumulatedSum += rowSum;
            data.add(new NamedData(row.getName(), Arrays.asList(rowSum, row.getFemale(), row.getMale(), accumulatedSum)));
        }
        return TableData.createWithSingleHeadersRow(data, Arrays.asList("Sjukskrivningslängd", "Antal sjukfall", "Antal sjukfall Kvinnor", "Antal sjukfall Män", "Summering"));
    }


    private ChartData convertToChart(SickLeaveLengthResponse resp) {
        List<String> groups = resp.getGroups();
        List<Integer> femaleData = resp.getDataForSex(Sex.Female);
        List<Integer> maleData = resp.getDataForSex(Sex.Male);
        ArrayList<ChartSeries> series = new ArrayList<>();
        series.add(new ChartSeries("Antal sjukfall Män", maleData));
        series.add(new ChartSeries("Antal sjukfall Kvinnor", femaleData));
        return new ChartData(series, groups);
    }

    SickLeaveLengthData convert(SickLeaveLengthResponse resp) {
        TableData tableData = convertToTable(resp.getRows());
        ChartData chartData = convertToChart(resp);
        int monthsIncluded = resp.getNumberOfMonthsCalculated();
        return new SickLeaveLengthData(tableData, chartData, monthsIncluded);
    }
}
