package se.inera.statistics.web.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import se.inera.statistics.service.report.model.Range;
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

        return TableData.createWithSingleHeadersRow(data, Arrays.asList("Period", "Antal sjukfall", "Antal kvinnor", "Antal män", "Summering"));
    }

    private ChartData convertToChartData(SimpleDualSexResponse<SimpleDualSexDataRow> casesPerMonth) {
        final ArrayList<String> categories = new ArrayList<String>();
        for (SimpleDualSexDataRow casesPerMonthRow : casesPerMonth.getRows()) {
            categories.add(casesPerMonthRow.getName());
        }

        final ArrayList<ChartSeries> series = new ArrayList<ChartSeries>();
        series.add(new ChartSeries("Antal sjukfall", casesPerMonth.getSummedData(), false));
        series.add(new ChartSeries("Antal kvinnor", casesPerMonth.getDataForSex(Sex.Female), false, Sex.Female));
        series.add(new ChartSeries("Antal män", casesPerMonth.getDataForSex(Sex.Male), false, Sex.Male));

        return new ChartData(series, categories);
    }

    public SimpleDetailsData convert(SimpleDualSexResponse<SimpleDualSexDataRow> casesPerMonth, Range range) {
        TableData tableData = convertToTableData(casesPerMonth.getRows());
        ChartData chartData = convertToChartData(casesPerMonth);
        return new SimpleDetailsData(tableData, chartData, range.getMonths(), range.toString());
    }
}
