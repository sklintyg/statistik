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

import org.junit.Test;

import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SjukfallslangdResponse;
import se.inera.statistics.service.report.model.SjukfallslangdRow;
import se.inera.statistics.web.model.ChartData;
import se.inera.statistics.web.model.ChartSeries;
import se.inera.statistics.web.model.NamedData;
import se.inera.statistics.web.model.SickLeaveLengthData;
import se.inera.statistics.web.model.TableData;

public class SickLeaveLengthConverterTest {

    // CHECKSTYLE:OFF MagicNumber

    @Test
    public void convertCasesPerMonthDataTest() {
        //Given
        SickLeaveLengthConverter converter = new SickLeaveLengthConverter();
        ArrayList<SjukfallslangdRow> sjukfallslangdRows = new ArrayList<>();
        sjukfallslangdRows.add(new SjukfallslangdRow("< 20 dagar", 13, 14));
        sjukfallslangdRows.add(new SjukfallslangdRow("20-50 dagar", 24, 15));
        sjukfallslangdRows.add(new SjukfallslangdRow("> 50 dagar", 3, 9));
        SjukfallslangdResponse sjukfallslangdResponse = new SjukfallslangdResponse(sjukfallslangdRows, 7);

        //When
        SickLeaveLengthData result = converter.convert(sjukfallslangdResponse, new Range(7));

        //Then
        TableData tableDataResult = result.getTableData();
        assertEquals("[[Sjukskrivningslängd;1, Antal sjukfall totalt;1, Antal sjukfall för kvinnor;1, Antal sjukfall för män;1]]", tableDataResult.getHeaders().toString());
        List<NamedData> rows = tableDataResult.getRows();
        assertEquals(3, rows.size());
        assertEquals("< 20 dagar", rows.get(0).getName());
        assertEquals("20-50 dagar", rows.get(1).getName());
        assertEquals("> 50 dagar", rows.get(2).getName());
        assertEquals("[27, 13, 14]", rows.get(0).getData().toString());
        assertEquals("[39, 24, 15]", rows.get(1).getData().toString());
        assertEquals("[12, 3, 9]", rows.get(2).getData().toString());

        ChartData chartDataResult = result.getChartData();
        assertEquals("[< 20 dagar, 20-50 dagar, > 50 dagar]", chartDataResult.getCategories().toString());
        List<ChartSeries> series = chartDataResult.getSeries();
        assertEquals(2, series.size());
        assertEquals("Antal sjukfall för män", series.get(1).getName());
        assertEquals("Antal sjukfall för kvinnor", series.get(0).getName());
        assertEquals("[14, 15, 9]", series.get(1).getData().toString());
        assertEquals("[13, 24, 3]", series.get(0).getData().toString());

        assertEquals(7, result.getMonthsIncluded());
    }

    // CHECKSTYLE:ON MagicNumber
}
