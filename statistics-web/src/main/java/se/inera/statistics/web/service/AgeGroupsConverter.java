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

import se.inera.statistics.service.report.model.AgeGroupsResponse;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.Sex;
import se.inera.statistics.service.report.model.db.AgeGroupsRow;
import se.inera.statistics.web.model.AgeGroupsData;
import se.inera.statistics.web.model.ChartData;
import se.inera.statistics.web.model.ChartSeries;
import se.inera.statistics.web.model.NamedData;
import se.inera.statistics.web.model.TableData;

public class AgeGroupsConverter {

    private TableData convertToTable(List<AgeGroupsRow> ageGroups) {
        List<NamedData> data = new ArrayList<>();
        int accumulatedSum = 0;
        for (AgeGroupsRow row : ageGroups) {
            int rowSum = row.getFemale() + row.getMale();
            accumulatedSum += rowSum;
            data.add(new NamedData(row.getGroup(), Arrays.asList(rowSum, row.getFemale(), row.getMale(), accumulatedSum)));
        }
        ServiceUtil.addSumRow(data, false);
        return TableData.createWithSingleHeadersRow(data, Arrays.asList("Åldersgrupper", "Antal sjukfall", "Antal sjukfall för kvinnor", "Antal sjukfall för män", "Summering"));
    }


    private ChartData convertToChart(AgeGroupsResponse resp) {
        List<String> groups = resp.getGroups();
        List<Integer> femaleData = resp.getDataForSex(Sex.Female);
        List<Integer> maleData = resp.getDataForSex(Sex.Male);
        ArrayList<ChartSeries> series = new ArrayList<>();
        series.add(new ChartSeries("Antal sjukfall för män", maleData, false, Sex.Male));
        series.add(new ChartSeries("Antal sjukfall för kvinnor", femaleData, false, Sex.Female));
        return new ChartData(series, groups);
    }

    AgeGroupsData convert(AgeGroupsResponse resp, Range range) {
        TableData tableData = convertToTable(resp.getAgeGroupsRows());
        ChartData chartData = convertToChart(resp);
        return new AgeGroupsData(tableData, chartData, range.getMonths(), range.toString());
    }
}
