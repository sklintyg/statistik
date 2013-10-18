package se.inera.statistics.web.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import se.inera.statistics.service.report.model.CasesPerMonthRow;
import se.inera.statistics.service.report.model.Sex;
import se.inera.statistics.web.model.CasesPerMonthData;
import se.inera.statistics.web.model.ChartData;
import se.inera.statistics.web.model.ChartSeries;
import se.inera.statistics.web.model.NamedData;
import se.inera.statistics.web.model.TableData;

public class CasesPerMonthConverter {

    private TableData convertToTableData(List<CasesPerMonthRow> casesPerMonth) {
        List<NamedData> data = new ArrayList<>();
        int accumulatedSum = 0;
        for (CasesPerMonthRow row : casesPerMonth) {
            int rowSum = row.getFemale() + row.getMale();
            accumulatedSum += rowSum;
            data.add(new NamedData(row.getPeriod(), Arrays.asList(new Integer[] {rowSum, row.getFemale(), row.getMale(), accumulatedSum})));
        }

        return TableData.createWithSingleHeadersRow(data, Arrays.asList("Antal sjukfall", "Antal kvinnor", "Antal män", "Summering"));
    }

    private ChartData convertToChartData(List<CasesPerMonthRow> casesPerMonth, TableData tableData) {
        final ArrayList<String> categories = new ArrayList<String>();
        for (CasesPerMonthRow casesPerMonthRow : casesPerMonth) {
            categories.add(casesPerMonthRow.getPeriod());
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

    public CasesPerMonthData convert(List<CasesPerMonthRow> casesPerMonth) {
        TableData tableData = convertToTableData(casesPerMonth);
        ChartData chartData = convertToChartData(casesPerMonth, tableData);
        return new CasesPerMonthData(tableData, chartData, 0);
    }
}
