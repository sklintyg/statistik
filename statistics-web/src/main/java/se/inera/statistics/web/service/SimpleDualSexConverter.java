package se.inera.statistics.web.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import se.inera.statistics.service.report.model.Sex;
import se.inera.statistics.service.report.model.SimpleDualSexDataRow;
import se.inera.statistics.service.report.model.SimpleDualSexResponse;
import se.inera.statistics.web.model.ChartData;
import se.inera.statistics.web.model.ChartSeries;
import se.inera.statistics.web.model.NamedData;
import se.inera.statistics.web.model.SimpleDetailsData;
import se.inera.statistics.web.model.TableData;

public class SimpleDualSexConverter {

    private TableData convertToTableData(List<SimpleDualSexDataRow> list) {
        List<NamedData> data = new ArrayList<>();
        int accumulatedSum = 0;
        for (SimpleDualSexDataRow row : list) {
            int rowSum = row.getFemale() + row.getMale();
            accumulatedSum += rowSum;
            data.add(new NamedData(row.getName(), Arrays.asList(new Integer[] {rowSum, row.getFemale(), row.getMale(), accumulatedSum})));
        }

        return TableData.createWithSingleHeadersRow(data, Arrays.asList("Antal sjukfall", "Antal kvinnor", "Antal män", "Summering"));
    }

    private ChartData convertToChartData(List<SimpleDualSexDataRow> list, TableData tableData) {
        final ArrayList<String> categories = new ArrayList<String>();
        for (SimpleDualSexDataRow casesPerMonthRow : list) {
            categories.add(casesPerMonthRow.getName());
        }

        final ArrayList<ChartSeries> series = new ArrayList<ChartSeries>();
        series.add(new ChartSeries("Antal sjukfall", getDataFromIndex(0, tableData)));
        series.add(new ChartSeries("Antal kvinnor", getDataFromIndex(1, tableData), Sex.Female));
        series.add(new ChartSeries("Antal män", getDataFromIndex(2, tableData), Sex.Male));

        return new ChartData(series, categories);
    }

    private List<Integer> getDataFromIndex(int i, TableData tableData) {
        ArrayList<Integer> data = new ArrayList<Integer>();
        for (NamedData namedData : tableData.getRows()) {
            data.add(namedData.getData().get(i));
        }
        return data;
    }

    public SimpleDetailsData convert(SimpleDualSexResponse<SimpleDualSexDataRow> casesPerMonth) {
        TableData tableData = convertToTableData(casesPerMonth.getRows());
        ChartData chartData = convertToChartData(casesPerMonth.getRows(), tableData);
        return new SimpleDetailsData(tableData, chartData, 0);
    }
}
