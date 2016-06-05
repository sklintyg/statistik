/**
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
 *
 * This file is part of statistik (https://github.com/sklintyg/statistik).
 *
 * statistik is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * statistik is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.web.service;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SimpleDualSexConverter {

    private final String tableGroupTitle;
    private final boolean totalSeriesInChart;
    private String seriesNameTemplate;

    public SimpleDualSexConverter(String tableGroupTitle, boolean totalSeriesInChart, String seriesNameTemplate) {
        this.tableGroupTitle = tableGroupTitle;
        this.totalSeriesInChart = totalSeriesInChart;
        this.seriesNameTemplate = seriesNameTemplate;
    }

    public static SimpleDualSexConverter newGenericTvarsnitt() {
        return new SimpleDualSexConverter("", false, "%1$s");
    }

    public SimpleDetailsData convert(SimpleKonResponse<SimpleKonDataRow> casesPerMonth, FilterSettings filterSettings) {
        return convert(casesPerMonth, filterSettings, null);
    }

    public SimpleDetailsData convert(SimpleKonResponse<SimpleKonDataRow> casesPerMonthIn, FilterSettings filterSettings, String message) {
        TableData tableData = convertToTableData(casesPerMonthIn.getRows());
        SimpleKonResponse<SimpleKonDataRow> casesPerMonth = casesPerMonthIn.getRows().isEmpty() ? new SimpleKonResponse<>(Arrays.asList(new SimpleKonDataRow("Totalt", 0, 0))) : casesPerMonthIn;
        ChartData chartData = convertToChartData(casesPerMonth);
        final Filter filter = filterSettings.getFilter();
        final FilterDataResponse filterResponse = new FilterDataResponse(filter);
        final Range range = filterSettings.getRange();
        final String combinedMessage = Converters.combineMessages(filterSettings.getMessage(), message);
        return new SimpleDetailsData(tableData, chartData, range.toString(), filterResponse, combinedMessage);
    }

    protected TableData convertToTableData(List<SimpleKonDataRow> list) {
        List<NamedData> data = new ArrayList<>();
        for (SimpleKonDataRow row : list) {
            final Integer female = row.getFemale();
            final Integer male = row.getMale();
            final String seriesName = String.format(seriesNameTemplate, row.getName());
            data.add(new NamedData(seriesName, Arrays.asList(female + male, female, male), isMarked(row)));
        }

        return TableData.createWithSingleHeadersRow(data, Arrays.asList(tableGroupTitle, "Antal sjukfall totalt", "Antal sjukfall för kvinnor", "Antal sjukfall för män"));
    }

    protected boolean isMarked(SimpleKonDataRow row) {
        return false;
    }

    protected ChartData convertToChartData(SimpleKonResponse<SimpleKonDataRow> casesPerMonth) {
        final ArrayList<ChartCategory> categories = new ArrayList<>();
        for (SimpleKonDataRow casesPerMonthRow : casesPerMonth.getRows()) {
            final String seriesName = String.format(seriesNameTemplate, casesPerMonthRow.getName());
            categories.add(new ChartCategory(seriesName, isMarked(casesPerMonthRow)));
        }

        final ArrayList<ChartSeries> series = new ArrayList<>();
        if (totalSeriesInChart) {
            series.add(new ChartSeries("Totalt", casesPerMonth.getSummedData(), false));
        }
        series.add(new ChartSeries("Kvinnor", casesPerMonth.getDataForSex(Kon.Female), false, Kon.Female));
        series.add(new ChartSeries("Män", casesPerMonth.getDataForSex(Kon.Male), false, Kon.Male));

        return new ChartData(series, categories);
    }

    String getTableGroupTitle() {
        return tableGroupTitle;
    }

    boolean isTotalSeriesInChart() {
        return totalSeriesInChart;
    }

    String getSeriesNameTemplate() {
        return seriesNameTemplate;
    }
}
