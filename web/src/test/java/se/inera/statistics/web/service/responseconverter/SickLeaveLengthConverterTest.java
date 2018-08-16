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

import org.junit.Test;

import se.inera.statistics.service.report.model.ActiveFilters;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.web.model.ChartData;
import se.inera.statistics.web.model.ChartSeries;
import se.inera.statistics.web.model.NamedData;
import se.inera.statistics.web.model.SimpleDetailsData;
import se.inera.statistics.web.model.TableData;
import se.inera.statistics.web.service.Filter;
import se.inera.statistics.web.service.FilterSettings;
import se.inera.statistics.web.service.responseconverter.SimpleDualSexConverter;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SickLeaveLengthConverterTest {

    // CHECKSTYLE:OFF MagicNumber

    @Test
    public void convertCasesPerMonthDataTest() {
        //Given
        SimpleDualSexConverter converter = SimpleDualSexConverter.newGenericTvarsnitt();
        ArrayList<SimpleKonDataRow> sjukfallslangdRows = new ArrayList<>();
        sjukfallslangdRows.add(new SimpleKonDataRow("< 20 dagar", 13, 14));
        sjukfallslangdRows.add(new SimpleKonDataRow("20-50 dagar", 24, 15));
        sjukfallslangdRows.add(new SimpleKonDataRow("> 50 dagar", 3, 9));
        SimpleKonResponse sjukfallslangdResponse = new SimpleKonResponse(ActiveFilters.getForSjukfall(), sjukfallslangdRows);

        //When
        final Range range = Range.createForLastMonthsExcludingCurrent(7, Clock.systemDefaultZone());
        final FilterSettings filterSettings = new FilterSettings(Filter.empty(), range);
        SimpleDetailsData result = converter.convert(sjukfallslangdResponse, filterSettings);

        //Then
        TableData tableDataResult = result.getTableData();
        assertEquals("[[;1, Antal sjukfall totalt;1, Antal sjukfall för kvinnor;1, Antal sjukfall för män;1]]", tableDataResult.getHeaders().toString());
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
        assertEquals(3, series.size());
        assertEquals("Totalt", series.get(0).getName());
        assertEquals("Kvinnor", series.get(1).getName());
        assertEquals("Män", series.get(2).getName());
        assertEquals("[27, 39, 12]", series.get(0).getData().toString());
        assertEquals("[13, 24, 3]", series.get(1).getData().toString());
        assertEquals("[14, 15, 9]", series.get(2).getData().toString());

        assertEquals(range.toString(), result.getPeriod());
    }

    // CHECKSTYLE:ON MagicNumber
}
