/*
 * Copyright (C) 2013 - 2014 Inera AB (http://www.inera.se)
 *
 *     This file is part of Inera Statistics (http://code.google.com/p/inera-statistics/).
 *
 *     Inera Statistics is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Inera Statistics is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU LESSER GENERAL PUBLIC LICENSE for more details.
 *
 *     You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.inera.statistics.web.service;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
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

public class SjukfallPerSexConverter {

    private TableData convertToTableData(List<SimpleDualSexDataRow> list) {
        List<NamedData> data = new ArrayList<>();
        int accumulatedSum = 0;
        int totalSum = 0;
        int femaleSum = 0;
        int maleSum = 0;
        for (SimpleDualSexDataRow row : list) {
            final Integer female = row.getFemale();
            final Integer male = row.getMale();
            int rowSum = female + male;
            accumulatedSum += rowSum;
            data.add(new NamedData(row.getName(), Arrays.asList(new Object[] {rowSum, toTableString(female, rowSum), toTableString(male, rowSum), accumulatedSum})));
            totalSum += rowSum;
            femaleSum += female;
            maleSum += male;
        }
        data.add(new NamedData("Totalt", Arrays.asList(totalSum, femaleSum, maleSum, "")));

        return TableData.createWithSingleHeadersRow(data, Arrays.asList("Län", "Antal sjukfall", "Andel sjukfall för kvinnor", "Andel sjukfall för män", "Summering"));
    }

    private String toTableString(final Integer value, int rowSum) {
        final DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setGroupingSeparator(' ');
        final DecimalFormat formatter = new DecimalFormat("###,###", symbols);
        final float toPercentFactor = 100.0F;
        return Math.round(toPercentFactor * value / rowSum) + "% (" + formatter.format(value) + ")";
    }

    private ChartData convertToChartData(SimpleDualSexResponse<SimpleDualSexDataRow> casesPerMonth) {
        final ArrayList<String> categories = new ArrayList<String>();
        categories.add("Samtliga län");
        for (SimpleDualSexDataRow casesPerMonthRow : casesPerMonth.getRows()) {
            categories.add(casesPerMonthRow.getName());
        }

        final ArrayList<ChartSeries> series = new ArrayList<ChartSeries>();
        final String stacked = "Stacked";
        series.add(new ChartSeries("Andel sjukfall för kvinnor", getSeriesForSexWithTotal(casesPerMonth, Sex.Female), stacked, Sex.Female));
        series.add(new ChartSeries("Andel sjukfall för män", getSeriesForSexWithTotal(casesPerMonth, Sex.Male), stacked, Sex.Male));

        return new ChartData(series, categories);
    }

    private List<Integer> getSeriesForSexWithTotal(SimpleDualSexResponse<SimpleDualSexDataRow> casesPerMonth, final Sex sex) {
        ArrayList<Integer> series = new ArrayList<Integer>();
        series.add(getSumForSex(casesPerMonth, sex));
        series.addAll(casesPerMonth.getDataForSex(sex));
        return series;
    }

    private Integer getSumForSex(SimpleDualSexResponse<SimpleDualSexDataRow> casesPerMonth, Sex sex) {
        int sum = 0;
        List<Integer> data = casesPerMonth.getDataForSex(sex);
        for (Integer value : data) {
            sum += value;
        }
        return sum;
    }

    public SimpleDetailsData convert(SimpleDualSexResponse<SimpleDualSexDataRow> casesPerMonth, Range range) {
        TableData tableData = convertToTableData(casesPerMonth.getRows());
        ChartData chartData = convertToChartData(casesPerMonth);
        return new SimpleDetailsData(tableData, chartData, range.getMonths(), range.toString());
    }
}
