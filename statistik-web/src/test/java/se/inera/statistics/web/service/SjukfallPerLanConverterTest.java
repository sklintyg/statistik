/**
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;

import se.inera.statistics.service.countypopulation.CountyPopulation;
import se.inera.statistics.service.report.model.KonField;
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
        final Clock clock = Clock.systemDefaultZone();
        ArrayList<SimpleKonDataRow> perCountyRows1 = new ArrayList<>();
        perCountyRows1.add(new SimpleKonDataRow("<20", 13, 14, "01"));
        perCountyRows1.add(new SimpleKonDataRow("20-50", 24, 15, "02"));
        perCountyRows1.add(new SimpleKonDataRow(">50", 3, 9, "03"));
        SimpleKonResponse<SimpleKonDataRow> ageGroupsResponseNew = new SimpleKonResponse<>(perCountyRows1);

        LocalDate fromNew = LocalDate.of(2013, 5, 1);
        LocalDate toNew = LocalDate.of(2013, 7, 1);

        final HashMap<String, KonField> population = new HashMap<>();
        population.put("01", new KonField(200, 800));
        population.put("02", new KonField(200, 800));
        population.put("03", new KonField(200, 800));

        final CountyPopulation countyPopulation = new CountyPopulation(population, LocalDate.now(clock));

        CasesPerCountyConverter converter = new CasesPerCountyConverter(ageGroupsResponseNew, countyPopulation, new Range(fromNew, toNew));

        //When
        CasesPerCountyData result = converter.convert();

        //Then
        TableData tableDataResult = result.getTableData();
        assertEquals("[[;1, Antal sjukfall;3, Antal invånare;3, Antal sjukfall per 1000 invånare;3], [Län;1, Totalt;1, Kvinnor;1, Män;1, Totalt;1, Kvinnor;1, Män;1, Totalt;1, Kvinnor;1, Män;1]]", tableDataResult.getHeaders().toString());
        List<NamedData> rows = tableDataResult.getRows();
        assertEquals(4, rows.size());
        assertEquals("Samtliga län", rows.get(0).getName());
        assertEquals("<20", rows.get(1).getName());
        assertEquals("20-50", rows.get(2).getName());
        assertEquals(">50", rows.get(3).getName());
        assertEquals("[78, 40, 38, 3000, 600, 2400, 26,00, 66,67, 15,83]", rows.get(0).getData().toString());
        assertEquals("[27, 13, 14, 1000, 200, 800, 27,00, 65,00, 17,50]", rows.get(1).getData().toString());
        assertEquals("[39, 24, 15, 1000, 200, 800, 39,00, 120,00, 18,75]", rows.get(2).getData().toString());
        assertEquals("[12, 3, 9, 1000, 200, 800, 12,00, 15,00, 11,25]", rows.get(3).getData().toString());

        ChartData chartDataResult = result.getChartData();
        assertEquals("[Samtliga län, <20, 20-50, >50]", chartDataResult.getCategories().toString());
        List<ChartSeries> series = chartDataResult.getSeries();
        assertEquals(2, series.size());
        assertEquals("Kvinnor", series.get(0).getName());
        assertEquals("[66.67, 65.0, 120.0, 15.0]", series.get(0).getData().toString());
        assertEquals("Män", series.get(1).getName());
        assertEquals("[15.83, 17.5, 18.75, 11.25]", series.get(1).getData().toString());

        assertEquals(new Range(fromNew, toNew).toString(), result.getPeriod());
    }

    // CHECKSTYLE:ON MagicNumber
}
