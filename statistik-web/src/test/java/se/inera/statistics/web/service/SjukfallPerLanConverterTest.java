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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Test;

import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.web.model.CasesPerCountyData;
import se.inera.statistics.web.model.ChartData;
import se.inera.statistics.web.model.ChartSeries;
import se.inera.statistics.web.model.NamedData;
import se.inera.statistics.web.model.TableData;

public class SjukfallPerLanConverterTest {

    // CHECKSTYLE:OFF MagicNumber

    @Test
    public void convertCasesPerCountyDataTest() {
        //Given
        ArrayList<SimpleKonDataRow> perCountyRows1 = new ArrayList<>();
        perCountyRows1.add(new SimpleKonDataRow("<20", 13, 14, "01"));
        perCountyRows1.add(new SimpleKonDataRow("20-50", 24, 15, "02"));
        perCountyRows1.add(new SimpleKonDataRow(">50", 3, 9, "03"));
        SimpleKonResponse<SimpleKonDataRow> ageGroupsResponseNew = new SimpleKonResponse<>(perCountyRows1);

//        ArrayList<SimpleKonDataRow> perCountyRowsOld = new ArrayList<>();
//        perCountyRowsOld.add(new SimpleKonDataRow("<20", 3, 4));
//        perCountyRowsOld.add(new SimpleKonDataRow("20-50", 4, 5));
//        perCountyRowsOld.add(new SimpleKonDataRow(">50", 2, 8));
//        SimpleKonResponse<SimpleKonDataRow> ageGroupsResponseOld = new SimpleKonResponse<>(perCountyRowsOld);

//        LocalDate fromOld = new LocalDate(2013, 2, 1);
//        LocalDate toOld = new LocalDate(2013, 4, 1);
        LocalDate fromNew = new LocalDate(2013, 5, 1);
        LocalDate toNew = new LocalDate(2013, 7, 1);

        final HashMap<String, Integer> population = new HashMap<>();
        population.put("01", 1000);
        population.put("02", 1000);
        population.put("03", 1000);

        CasesPerCountyConverter converter = new CasesPerCountyConverter(ageGroupsResponseNew, population, new Range(fromNew, toNew));

        //When
        CasesPerCountyData result = converter.convert();

        //Then
        TableData tableDataResult = result.getTableData();
        assertEquals("[[Län;1, Antal sjukfall;1, Antal invånare;1, Antal sjukfall per 1000 invånare;1]]", tableDataResult.getHeaders().toString());
        List<NamedData> rows = tableDataResult.getRows();
        assertEquals(4, rows.size());
        assertEquals("Samtliga län", rows.get(0).getName());
        assertEquals("<20", rows.get(1).getName());
        assertEquals("20-50", rows.get(2).getName());
        assertEquals(">50", rows.get(3).getName());
        assertEquals("[78, 3000, 26,00]", rows.get(0).getData().toString());
        assertEquals("[27, 1000, 27,00]", rows.get(1).getData().toString());
        assertEquals("[39, 1000, 39,00]", rows.get(2).getData().toString());
        assertEquals("[12, 1000, 12,00]", rows.get(3).getData().toString());

        ChartData chartDataResult = result.getChartData();
        assertEquals("[Samtliga län, <20, 20-50, >50]", chartDataResult.getCategories().toString());
        List<ChartSeries> series = chartDataResult.getSeries();
        assertEquals(1, series.size());
        assertEquals("Antal sjukfall per 1000 invånare", series.get(0).getName());
        assertEquals("[26.0, 27.0, 39.0, 12.0]", series.get(0).getData().toString());

        assertEquals(new Range(fromNew, toNew).toString(), result.getPeriod());
    }

    // CHECKSTYLE:ON MagicNumber
}
