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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.web.model.AgeGroupsData;
import se.inera.statistics.web.model.ChartData;
import se.inera.statistics.web.model.ChartSeries;
import se.inera.statistics.web.model.NamedData;
import se.inera.statistics.web.model.TableData;

public class AgeGroupsConverter {

    private TableData convertToTable(List<SimpleKonDataRow> list) {
        List<NamedData> data = new ArrayList<>();
        for (SimpleKonDataRow row : list) {
            int rowSum = row.getFemale() + row.getMale();
            data.add(new NamedData(row.getName(), Arrays.asList(rowSum, row.getFemale(), row.getMale())));
        }
        return TableData.createWithSingleHeadersRow(data, Arrays.asList("Åldersgrupper", "Antal sjukfall totalt", "Antal sjukfall för kvinnor", "Antal sjukfall för män"));
    }


    private ChartData convertToChart(SimpleKonResponse<SimpleKonDataRow> resp) {
        List<String> groups = resp.getGroups();
        List<Integer> femaleData = resp.getDataForSex(Kon.Female);
        List<Integer> maleData = resp.getDataForSex(Kon.Male);
        ArrayList<ChartSeries> series = new ArrayList<>();
        series.add(new ChartSeries("Antal sjukfall för män", maleData, false, Kon.Male));
        series.add(new ChartSeries("Antal sjukfall för kvinnor", femaleData, false, Kon.Female));
        return new ChartData(series, groups);
    }

    AgeGroupsData convert(SimpleKonResponse<SimpleKonDataRow> resp, Range range) {
        TableData tableData = convertToTable(resp.getRows());
        ChartData chartData = convertToChart(resp);
        return new AgeGroupsData(tableData, chartData, range.getMonths(), range.toString());
    }
}
