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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import se.inera.statistics.service.report.model.AgeGroupsResponse;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.db.AgeGroupsRow;
import se.inera.statistics.web.model.AgeGroupsData;
import se.inera.statistics.web.model.ChartData;
import se.inera.statistics.web.model.ChartSeries;
import se.inera.statistics.web.model.NamedData;
import se.inera.statistics.web.model.TableData;

public class AldersgruppConverterTest {

    // CHECKSTYLE:OFF MagicNumber

    @Test
    public void convertCasesPerMonthDataTest() {
        //Given
        AgeGroupsConverter converter = new AgeGroupsConverter();
        ArrayList<AgeGroupsRow> ageGroupsRows = new ArrayList<>();
        ageGroupsRows.add(new AgeGroupsRow(null, "<20", 3, 13, 14));
        ageGroupsRows.add(new AgeGroupsRow(null, "20-50", 3, 24, 15));
        ageGroupsRows.add(new AgeGroupsRow(null, ">50", 3, 3, 9));
        AgeGroupsResponse ageGroupsResponse = new AgeGroupsResponse(ageGroupsRows, 7);

        //When
        AgeGroupsData result = converter.convert(ageGroupsResponse, new Range(7));

        //Then
        TableData tableDataResult = result.getTableData();
        assertEquals("[[Åldersgrupper;1, Antal sjukfall;1, Antal sjukfall för kvinnor;1, Antal sjukfall för män;1, Summering;1]]", tableDataResult.getHeaders().toString());
        List<NamedData> rows = tableDataResult.getRows();
        assertEquals(4, rows.size());
        assertEquals("<20", rows.get(0).getName());
        assertEquals("20-50", rows.get(1).getName());
        assertEquals(">50", rows.get(2).getName());
        assertEquals("Totalt", rows.get(3).getName());
        assertEquals("[27, 13, 14, 27]", rows.get(0).getData().toString());
        assertEquals("[39, 24, 15, 66]", rows.get(1).getData().toString());
        assertEquals("[12, 3, 9, 78]", rows.get(2).getData().toString());
        assertEquals("[78, 40, 38]", rows.get(3).getData().toString());

        ChartData chartDataResult = result.getChartData();
        assertEquals("[<20, 20-50, >50]", chartDataResult.getCategories().toString());
        List<ChartSeries> series = chartDataResult.getSeries();
        assertEquals(2, series.size());
        assertEquals("Antal sjukfall för män", series.get(0).getName());
        assertEquals("Antal sjukfall för kvinnor", series.get(1).getName());
        assertEquals("[14, 15, 9]", series.get(0).getData().toString());
        assertEquals("[13, 24, 3]", series.get(1).getData().toString());

        assertEquals(7, result.getMonthsIncluded());
    }

    // CHECKSTYLE:ON MagicNumber
}
