/**
 * Copyright (C) 2015 Inera AB (http://www.inera.se)
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
        perCountyRows1.add(new SimpleKonDataRow("<20", 13, 14));
        perCountyRows1.add(new SimpleKonDataRow("20-50", 24, 15));
        perCountyRows1.add(new SimpleKonDataRow(">50", 3, 9));
        SimpleKonResponse<SimpleKonDataRow> ageGroupsResponseNew = new SimpleKonResponse<>(perCountyRows1, 1);

        ArrayList<SimpleKonDataRow> perCountyRowsOld = new ArrayList<>();
        perCountyRowsOld.add(new SimpleKonDataRow("<20", 3, 4));
        perCountyRowsOld.add(new SimpleKonDataRow("20-50", 4, 5));
        perCountyRowsOld.add(new SimpleKonDataRow(">50", 2, 8));
        SimpleKonResponse<SimpleKonDataRow> ageGroupsResponseOld = new SimpleKonResponse<>(perCountyRowsOld, 2);

        LocalDate fromOld = new LocalDate(2013, 2, 1);
        LocalDate toOld = new LocalDate(2013, 4, 1);
        LocalDate fromNew = new LocalDate(2013, 5, 1);
        LocalDate toNew = new LocalDate(2013, 7, 1);

        CasesPerCountyConverter converter = new CasesPerCountyConverter(ageGroupsResponseNew, ageGroupsResponseOld, new Range(fromNew, toNew), new Range(fromOld, toOld));

        //When
        CasesPerCountyData result = converter.convert();

        //Then
        TableData tableDataResult = result.getTableData();
        assertEquals("[[;1, feb\u2013apr 2013;3, maj\u2013jul 2013;3], [Län;1, Antal sjukfall totalt;1, Antal sjukfall för kvinnor;1, Antal sjukfall för män;1, Antal sjukfall;1, Antal sjukfall för kvinnor;1, Antal sjukfall för män;1]]", tableDataResult.getHeaders().toString());
        List<NamedData> rows = tableDataResult.getRows();
        assertEquals(3, rows.size());
        assertEquals("<20", rows.get(0).getName());
        assertEquals("20-50", rows.get(1).getName());
        assertEquals(">50", rows.get(2).getName());
        assertEquals("[7, 3, 4, 27, 13, 14]", rows.get(0).getData().toString());
        assertEquals("[9, 4, 5, 39, 24, 15]", rows.get(1).getData().toString());
        assertEquals("[10, 2, 8, 12, 3, 9]", rows.get(2).getData().toString());

        ChartData chartDataResult = result.getChartData();
        assertEquals("[<20, 20-50, >50]", chartDataResult.getCategories().toString());
        List<ChartSeries> series = chartDataResult.getSeries();
        assertEquals(4, series.size());
        assertEquals("Sjukfall feb\u2013apr 2013 kvinnor", series.get(0).getName());
        assertEquals("Sjukfall feb\u2013apr 2013 män", series.get(1).getName());
        assertEquals("Sjukfall maj\u2013jul 2013 kvinnor", series.get(2).getName());
        assertEquals("Sjukfall maj\u2013jul 2013 män", series.get(3).getName());
        assertEquals("[3, 4, 2]", series.get(0).getData().toString());
        assertEquals("[4, 5, 8]", series.get(1).getData().toString());
        assertEquals("[13, 24, 3]", series.get(2).getData().toString());
        assertEquals("[14, 15, 9]", series.get(3).getData().toString());

        assertEquals(6, result.getMonthsIncluded());
    }

    // CHECKSTYLE:ON MagicNumber
}
