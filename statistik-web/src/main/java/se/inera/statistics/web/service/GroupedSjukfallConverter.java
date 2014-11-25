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

import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.web.model.ChartData;
import se.inera.statistics.web.model.ChartSeries;
import se.inera.statistics.web.model.NamedData;
import se.inera.statistics.web.model.SimpleDetailsData;
import se.inera.statistics.web.model.TableData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GroupedSjukfallConverter {
    private final String xAxisLabel;

    public GroupedSjukfallConverter(String xAxisLabel) {
        this.xAxisLabel = xAxisLabel;
    }

    public SimpleDetailsData convert(SimpleKonResponse<SimpleKonDataRow> casesPerMonth, Range range) {
        Collections.sort(casesPerMonth.getRows(), new Comparator<SimpleKonDataRow>() {
            @Override
            public int compare(SimpleKonDataRow o1, SimpleKonDataRow o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        TableData tableData = convertToTableData(casesPerMonth.getRows());
        ChartData chartData = convertToChartData(casesPerMonth);
        return new SimpleDetailsData(tableData, chartData, range.getMonths(), range.toString());
    }

    private TableData convertToTableData(List<SimpleKonDataRow> list) {
        List<NamedData> data = new ArrayList<>();
        for (SimpleKonDataRow row : list) {
            final Integer female = row.getFemale();
            final Integer male = row.getMale();
            data.add(new NamedData(row.getName(), Arrays.asList(female + male, female, male)));
        }

        return TableData.createWithSingleHeadersRow(data, Arrays.asList(xAxisLabel, "Antal sjukfall totalt", "Antal sjukfall för kvinnor", "Antal sjukfall för män"));
    }

    private ChartData convertToChartData(SimpleKonResponse<SimpleKonDataRow> casesPerMonth) {
        final ArrayList<String> categories = new ArrayList<>();
        for (SimpleKonDataRow casesPerMonthRow : casesPerMonth.getRows()) {
            categories.add(casesPerMonthRow.getName());
        }

        final ArrayList<ChartSeries> series = new ArrayList<>();
        series.add(new ChartSeries("Antal sjukfall för kvinnor", casesPerMonth.getDataForSex(Kon.Female), false, Kon.Female));
        series.add(new ChartSeries("Antal sjukfall för män", casesPerMonth.getDataForSex(Kon.Male), false, Kon.Male));

        return new ChartData(series, categories);
    }

}
