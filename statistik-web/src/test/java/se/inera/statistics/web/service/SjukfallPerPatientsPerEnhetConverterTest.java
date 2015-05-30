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

import org.junit.Test;
import org.mockito.Mockito;
import se.inera.statistics.service.landsting.persistance.landstingenhet.LandstingEnhet;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.web.model.SimpleDetailsData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class SjukfallPerPatientsPerEnhetConverterTest {

    @Test
    public void testConvertNullLandstingsEnhetsInputGivesEmptyOutput() throws Exception {
        //Given
        final List<LandstingEnhet> landstingEnhets = null;
        final SjukfallPerPatientsPerEnhetConverter sjukfallPerPatientsPerEnhetConverter = new SjukfallPerPatientsPerEnhetConverter(landstingEnhets);

        //When
        final ArrayList<SimpleKonDataRow> simpleKonDataRows = new ArrayList<>();
        simpleKonDataRows.add(new SimpleKonDataRow("ett", 1, 2));
        simpleKonDataRows.add(new SimpleKonDataRow("tva", 10, 20));
        final SimpleKonResponse<SimpleKonDataRow> casesPerMonth = new SimpleKonResponse<>(simpleKonDataRows);
        final SimpleDetailsData result = sjukfallPerPatientsPerEnhetConverter.convert(casesPerMonth, new FilterSettings(Filter.empty(), Range.createForLastMonthsIncludingCurrent(12)), "");

        //Then
        assertEquals(0, result.getTableData().getRows().size());
    }

    @Test
    public void testConvertEmptyLandstingsEnhetsInputGivesEmptyOutput() throws Exception {
        //Given
        final List<LandstingEnhet> landstingEnhets = Collections.emptyList();
        final SjukfallPerPatientsPerEnhetConverter sjukfallPerPatientsPerEnhetConverter = new SjukfallPerPatientsPerEnhetConverter(landstingEnhets);

        //When
        final ArrayList<SimpleKonDataRow> simpleKonDataRows = new ArrayList<>();
        simpleKonDataRows.add(new SimpleKonDataRow("ett", 1, 2));
        simpleKonDataRows.add(new SimpleKonDataRow("tva", 10, 20));
        final SimpleKonResponse<SimpleKonDataRow> casesPerMonth = new SimpleKonResponse<>(simpleKonDataRows);
        final SimpleDetailsData result = sjukfallPerPatientsPerEnhetConverter.convert(casesPerMonth, new FilterSettings(Filter.empty(), Range.createForLastMonthsIncludingCurrent(12)), "");

        //Then
        assertEquals(0, result.getTableData().getRows().size());
    }

    @Test
    public void testConvertNonLandstingsEnhetsInputGivesNonEmptyOutputWhereEnhetsNotInListAreRemoved() throws Exception {
        //Given
        final List<LandstingEnhet> landstingEnhets = Arrays.asList(new LandstingEnhet(2L, "hsa2", 2));
        final SjukfallPerPatientsPerEnhetConverter sjukfallPerPatientsPerEnhetConverter = new SjukfallPerPatientsPerEnhetConverter(landstingEnhets);

        //When
        final ArrayList<SimpleKonDataRow> simpleKonDataRows = new ArrayList<>();
        simpleKonDataRows.add(new SimpleKonDataRow("ett", 1, 2, "hsa1"));
        simpleKonDataRows.add(new SimpleKonDataRow("tva", 10, 20, "hsa2"));
        final SimpleKonResponse<SimpleKonDataRow> casesPerMonth = new SimpleKonResponse<>(simpleKonDataRows);
        final SimpleDetailsData result = sjukfallPerPatientsPerEnhetConverter.convert(casesPerMonth, new FilterSettings(Filter.empty(), Range.createForLastMonthsIncludingCurrent(12)), "");

        //Then
        assertEquals(1, result.getTableData().getRows().size());
        assertEquals("tva", result.getTableData().getRows().get(0).getName());
    }

    @Test
    public void testConvertEnhetsWithZeroPatientsAreNotPartOfResult() throws Exception {
        //Given
        final List<LandstingEnhet> landstingEnhets = Arrays.asList(new LandstingEnhet(1L, "hsa1", 1), new LandstingEnhet(2L, "hsa2", 0));
        final SjukfallPerPatientsPerEnhetConverter sjukfallPerPatientsPerEnhetConverter = new SjukfallPerPatientsPerEnhetConverter(landstingEnhets);

        //When
        final ArrayList<SimpleKonDataRow> simpleKonDataRows = new ArrayList<>();
        simpleKonDataRows.add(new SimpleKonDataRow("ett", 1, 2, "hsa1"));
        simpleKonDataRows.add(new SimpleKonDataRow("tva", 10, 20, "hsa2"));
        final SimpleKonResponse<SimpleKonDataRow> casesPerMonth = new SimpleKonResponse<>(simpleKonDataRows);
        final SimpleDetailsData result = sjukfallPerPatientsPerEnhetConverter.convert(casesPerMonth, new FilterSettings(Filter.empty(), Range.createForLastMonthsIncludingCurrent(12)), "");

        //Then
        assertEquals(1, result.getTableData().getRows().size());
        assertEquals("ett", result.getTableData().getRows().get(0).getName());
    }

    @Test
    public void testConvertEnhetsWithLessThanZeroPatientsAreNotPartOfResult() throws Exception {
        //Given
        final List<LandstingEnhet> landstingEnhets = Arrays.asList(new LandstingEnhet(1L, "hsa1", -1), new LandstingEnhet(2L, "hsa2", 3));
        final SjukfallPerPatientsPerEnhetConverter sjukfallPerPatientsPerEnhetConverter = new SjukfallPerPatientsPerEnhetConverter(landstingEnhets);

        //When
        final ArrayList<SimpleKonDataRow> simpleKonDataRows = new ArrayList<>();
        simpleKonDataRows.add(new SimpleKonDataRow("ett", 1, 2, "hsa1"));
        simpleKonDataRows.add(new SimpleKonDataRow("tva", 10, 20, "hsa2"));
        final SimpleKonResponse<SimpleKonDataRow> casesPerMonth = new SimpleKonResponse<>(simpleKonDataRows);
        final SimpleDetailsData result = sjukfallPerPatientsPerEnhetConverter.convert(casesPerMonth, new FilterSettings(Filter.empty(), Range.createForLastMonthsIncludingCurrent(12)), "");

        //Then
        assertEquals(1, result.getTableData().getRows().size());
        assertEquals("tva", result.getTableData().getRows().get(0).getName());
    }

    @Test
    public void testConvertEnhetsWithUnsetNumberOfPatientsAreNotPartOfResult() throws Exception {
        //Given
        final List<LandstingEnhet> landstingEnhets = Arrays.asList(new LandstingEnhet(1L, "hsa1", null), new LandstingEnhet(2L, "hsa2", 3));
        final SjukfallPerPatientsPerEnhetConverter sjukfallPerPatientsPerEnhetConverter = new SjukfallPerPatientsPerEnhetConverter(landstingEnhets);

        //When
        final ArrayList<SimpleKonDataRow> simpleKonDataRows = new ArrayList<>();
        simpleKonDataRows.add(new SimpleKonDataRow("ett", 1, 2, "hsa1"));
        simpleKonDataRows.add(new SimpleKonDataRow("tva", 10, 20, "hsa2"));
        final SimpleKonResponse<SimpleKonDataRow> casesPerMonth = new SimpleKonResponse<>(simpleKonDataRows);
        final SimpleDetailsData result = sjukfallPerPatientsPerEnhetConverter.convert(casesPerMonth, new FilterSettings(Filter.empty(), Range.createForLastMonthsIncludingCurrent(12)), "");

        //Then
        assertEquals(1, result.getTableData().getRows().size());
        assertEquals("tva", result.getTableData().getRows().get(0).getName());
    }

    @Test
    public void testConvertResultIsUsingTwoDecimalsForBothTableAndChart() throws Exception {
        //Given
        final List<LandstingEnhet> landstingEnhets = Arrays.asList(new LandstingEnhet(1L, "hsa1", 3000));
        final SjukfallPerPatientsPerEnhetConverter sjukfallPerPatientsPerEnhetConverter = new SjukfallPerPatientsPerEnhetConverter(landstingEnhets);

        //When
        final ArrayList<SimpleKonDataRow> simpleKonDataRows = new ArrayList<>();
        simpleKonDataRows.add(new SimpleKonDataRow("ett", 0, 10, "hsa1"));
        final SimpleKonResponse<SimpleKonDataRow> casesPerMonth = new SimpleKonResponse<>(simpleKonDataRows);
        final SimpleDetailsData result = sjukfallPerPatientsPerEnhetConverter.convert(casesPerMonth, new FilterSettings(Filter.empty(), Range.createForLastMonthsIncludingCurrent(12)), "");

        //Then
        assertEquals(1, result.getTableData().getRows().size());
        assertEquals(10, result.getTableData().getRows().get(0).getData().get(0));
        assertEquals(3000, result.getTableData().getRows().get(0).getData().get(1));
        
        assertEquals("3.33", result.getTableData().getRows().get(0).getData().get(2));
        assertEquals(3.33, result.getChartData().getSeries().get(0).getData().get(0));
    }

}
