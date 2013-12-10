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
            final Integer female = row.getFemale();
            final Integer male = row.getMale();
            int rowSum = female + male;
            accumulatedSum += rowSum;
            data.add(new NamedData(row.getName(), Arrays.asList(new Integer[] {rowSum, female, male, accumulatedSum})));
        }
        ServiceUtil.addSumRow(data, false);

        return TableData.createWithSingleHeadersRow(data, Arrays.asList("Period", "Antal sjukfall", "Antal sjukfall för kvinnor", "Antal sjukfall för män", "Summering"));
    }

    private ChartData convertToChartData(SimpleDualSexResponse<SimpleDualSexDataRow> casesPerMonth) {
        final ArrayList<String> categories = new ArrayList<String>();
        for (SimpleDualSexDataRow casesPerMonthRow : casesPerMonth.getRows()) {
            categories.add(casesPerMonthRow.getName());
        }

        final ArrayList<ChartSeries> series = new ArrayList<ChartSeries>();
        series.add(new ChartSeries("Antal sjukfall", casesPerMonth.getSummedData(), false));
        series.add(new ChartSeries("Antal sjukfall för kvinnor", casesPerMonth.getDataForSex(Sex.Female), false, Sex.Female));
        series.add(new ChartSeries("Antal sjukfall för män", casesPerMonth.getDataForSex(Sex.Male), false, Sex.Male));

        return new ChartData(series, categories);
    }

    public SimpleDetailsData convert(SimpleDualSexResponse<SimpleDualSexDataRow> casesPerMonth, Range range) {
        TableData tableData = convertToTableData(casesPerMonth.getRows());
        ChartData chartData = convertToChartData(casesPerMonth);
        return new SimpleDetailsData(tableData, chartData, range.getMonths(), range.toString());
    }
}
