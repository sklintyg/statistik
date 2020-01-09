/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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

import static org.junit.Assert.assertEquals;

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import org.junit.Test;
import se.inera.statistics.service.countypopulation.CountyPopulation;
import se.inera.statistics.service.report.model.AvailableFilters;
import se.inera.statistics.service.report.model.KonField;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.web.model.CasesPerCountyData;

public class CasesPerCountyConverterTest {

    @Test
    public void testConvert() {
        //Given
        final Clock clock = Clock.systemDefaultZone();
        final ArrayList<SimpleKonDataRow> simpleKonDataRows = new ArrayList<>();
        final String name = "Rad1";
        final String lanCode = "01";
        simpleKonDataRows.add(new SimpleKonDataRow(name, 1, 2, lanCode));
        final SimpleKonResponse sjukfallPerLan = new SimpleKonResponse(AvailableFilters.getForSjukfall(), simpleKonDataRows);
        final HashMap<String, KonField> populationPerCounty = new HashMap<>();
        populationPerCounty.put(lanCode, new KonField(300, 200));
        final Range range = Range.year(clock);
        final CountyPopulation countyPopulation = new CountyPopulation(populationPerCounty, LocalDate.now(clock));
        final CasesPerCountyConverter converter = new CasesPerCountyConverter(sjukfallPerLan, countyPopulation, range);

        //When
        final CasesPerCountyData result = converter.convert();

        //Then
        assertEquals("Samtliga län", result.getChartData().getCategories().get(0).getName());
        assertEquals(name, result.getChartData().getCategories().get(1).getName());
        assertEquals("Totalt", result.getChartData().getSeries().get(0).getName());
        assertEquals("Kvinnor", result.getChartData().getSeries().get(1).getName());
        assertEquals("Män", result.getChartData().getSeries().get(2).getName());
        assertEquals(6.0, result.getChartData().getSeries().get(0).getData().get(0));
        assertEquals(3.33, result.getChartData().getSeries().get(1).getData().get(0));
        assertEquals(10.00, result.getChartData().getSeries().get(2).getData().get(0));

        assertEquals("Samtliga län", result.getTableData().getRows().get(0).getName());
        assertEquals(3, result.getTableData().getRows().get(0).getData().get(0));
        assertEquals(500, result.getTableData().getRows().get(0).getData().get(3));
        assertEquals("6,00", result.getTableData().getRows().get(0).getData().get(6));

        assertEquals(name, result.getTableData().getRows().get(1).getName());
        assertEquals(3, result.getTableData().getRows().get(1).getData().get(0));
        assertEquals(500, result.getTableData().getRows().get(1).getData().get(3));
        assertEquals("6,00", result.getTableData().getRows().get(1).getData().get(6));
    }

}
