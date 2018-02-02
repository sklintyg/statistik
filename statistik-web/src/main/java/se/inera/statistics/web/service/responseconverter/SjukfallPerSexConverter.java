/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.web.service.responseconverter;

import org.jetbrains.annotations.NotNull;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.web.model.ChartCategory;
import se.inera.statistics.web.model.ChartData;
import se.inera.statistics.web.model.ChartSeries;
import se.inera.statistics.web.model.NamedData;
import se.inera.statistics.web.model.SimpleDetailsData;
import se.inera.statistics.web.model.TableData;
import se.inera.statistics.web.service.FilterDataResponse;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SjukfallPerSexConverter {

    static final String SAMTLIGA_LAN = "Samtliga län";

    private TableData convertToTableData(SimpleKonResponse list) {
        List<NamedData> data = new ArrayList<>();
        data.add(getDataForAllCounties(list));
        for (SimpleKonDataRow row : list.getRows()) {
            int female = row.getFemale();
            int male = row.getMale();
            int rowSum = female + male;
            data.add(new NamedData(row.getName(), Arrays.asList(rowSum, toTableString(female, rowSum), toTableString(male, rowSum))));
        }

        return TableData.createWithSingleHeadersRow(data,
                Arrays.asList("Län", "Antal sjukfall totalt", "Andel sjukfall för kvinnor", "Andel sjukfall för män"));
    }

    @NotNull
    private NamedData getDataForAllCounties(SimpleKonResponse list) {
        final Integer sumForFemale = getSumForSex(list, Kon.FEMALE);
        final Integer sumForMale = getSumForSex(list, Kon.MALE);
        final int sum = sumForFemale + sumForMale;
        return new NamedData(SAMTLIGA_LAN, Arrays.asList(sum, toTableString(sumForFemale, sum), toTableString(sumForMale, sum)));
    }

    private String toTableString(int value, int rowSum) {
        final DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setGroupingSeparator(' ');
        final DecimalFormat formatter = new DecimalFormat("###,###", symbols);
        final float toPercentFactor = 100.0F;
        return Math.round(toPercentFactor * value / rowSum) + " % (" + formatter.format(value) + ")";
    }

    private ChartData convertToChartData(SimpleKonResponse casesPerMonth) {
        final ArrayList<ChartCategory> categories = new ArrayList<>();
        categories.add(new ChartCategory(SAMTLIGA_LAN));
        for (SimpleKonDataRow casesPerMonthRow : casesPerMonth.getRows()) {
            categories.add(new ChartCategory(casesPerMonthRow.getName()));
        }

        final ArrayList<ChartSeries> series = new ArrayList<>();
        series.add(new ChartSeries("Kvinnor", getSeriesForSexWithTotal(casesPerMonth, Kon.FEMALE), Kon.FEMALE));
        series.add(new ChartSeries("Män", getSeriesForSexWithTotal(casesPerMonth, Kon.MALE), Kon.MALE));

        return new ChartData(series, categories);
    }

    private List<Integer> getSeriesForSexWithTotal(SimpleKonResponse casesPerMonth, final Kon kon) {
        ArrayList<Integer> series = new ArrayList<>();
        series.add(getSumForSex(casesPerMonth, kon));
        series.addAll(casesPerMonth.getDataForSex(kon));
        return series;
    }

    private Integer getSumForSex(SimpleKonResponse casesPerMonth, Kon kon) {
        int sum = 0;
        List<Integer> data = casesPerMonth.getDataForSex(kon);
        for (Integer value : data) {
            sum += value;
        }
        return sum;
    }

    public SimpleDetailsData convert(SimpleKonResponse casesPerMonth, Range range) {
        TableData tableData = convertToTableData(casesPerMonth);
        ChartData chartData = convertToChartData(casesPerMonth);
        return new SimpleDetailsData(tableData, chartData, range.toString(), FilterDataResponse.empty());
    }
}
