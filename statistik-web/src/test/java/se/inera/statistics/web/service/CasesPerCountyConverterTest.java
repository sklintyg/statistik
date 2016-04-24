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

import org.junit.Test;
import se.inera.statistics.service.report.model.Lan;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.web.model.CasesPerCountyData;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;

public class CasesPerCountyConverterTest {

    @Test
    public void testConvert() throws Exception {
        //Given
        final ArrayList<SimpleKonDataRow> simpleKonDataRows = new ArrayList<>();
        final String name = "Rad1";
        final String lanCode = "01";
        simpleKonDataRows.add(new SimpleKonDataRow(name, 1, 2, lanCode));
        final SimpleKonResponse<SimpleKonDataRow> sjukfallPerLan = new SimpleKonResponse<>(simpleKonDataRows);
        final HashMap<String, Integer> populationPerCounty = new HashMap<>();
        populationPerCounty.put(lanCode, 500);
        final Range range = Range.year();
        final CasesPerCountyConverter converter = new CasesPerCountyConverter(sjukfallPerLan, populationPerCounty, range);

        //When
        final CasesPerCountyData result = converter.convert();

        //Then
        assertEquals("Samtliga län", result.getChartData().getCategories().get(0).getName());
        assertEquals(name, result.getChartData().getCategories().get(1).getName());
        assertEquals("Antal sjukfall per 1000 invånare", result.getChartData().getSeries().get(0).getName());
        assertEquals(6.00, result.getChartData().getSeries().get(0).getData().get(0));

        assertEquals("Samtliga län", result.getTableData().getRows().get(0).getName());
        assertEquals(name, result.getTableData().getRows().get(1).getName());
        assertEquals(3, result.getTableData().getRows().get(0).getData().get(0));
        assertEquals(500, result.getTableData().getRows().get(0).getData().get(1));
        assertEquals("6,00", result.getTableData().getRows().get(0).getData().get(2));
        assertEquals(3, result.getTableData().getRows().get(1).getData().get(0));
        assertEquals(500, result.getTableData().getRows().get(1).getData().get(1));
        assertEquals("6,00", result.getTableData().getRows().get(1).getData().get(2));
    }

}
