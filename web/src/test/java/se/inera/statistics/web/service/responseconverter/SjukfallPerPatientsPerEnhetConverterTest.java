/*
 * Copyright (C) 2023 Inera AB (http://www.inera.se)
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import se.inera.statistics.integration.hsa.model.HsaIdEnhet;
import se.inera.statistics.service.region.persistance.regionenhet.RegionEnhet;
import se.inera.statistics.service.report.model.AvailableFilters;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.web.error.ErrorSeverity;
import se.inera.statistics.web.error.ErrorType;
import se.inera.statistics.web.error.Message;
import se.inera.statistics.web.model.ChartCategory;
import se.inera.statistics.web.model.ChartData;
import se.inera.statistics.web.model.NamedData;
import se.inera.statistics.web.model.SimpleDetailsData;
import se.inera.statistics.web.model.TableData;
import se.inera.statistics.web.service.dto.Filter;
import se.inera.statistics.web.service.dto.FilterSettings;

public class SjukfallPerPatientsPerEnhetConverterTest {

    private final Clock clock = Clock.systemDefaultZone();

    @Test
    public void testConvertNullRegionsEnhetsInputGivesEmptyOutput() {
        //Given
        final List<RegionEnhet> regionEnhets = null;
        final SjukfallPerPatientsPerEnhetConverter sjukfallPerPatientsPerEnhetConverter = new SjukfallPerPatientsPerEnhetConverter(
            regionEnhets, Collections.<HsaIdEnhet>emptyList());

        //When
        final ArrayList<SimpleKonDataRow> simpleKonDataRows = new ArrayList<>();
        simpleKonDataRows.add(new SimpleKonDataRow("ett", 1, 2));
        simpleKonDataRows.add(new SimpleKonDataRow("tva", 10, 20));
        final SimpleKonResponse casesPerMonth = new SimpleKonResponse(AvailableFilters.getForSjukfall(), simpleKonDataRows);
        final SimpleDetailsData result = sjukfallPerPatientsPerEnhetConverter
            .convert(casesPerMonth, new FilterSettings(Filter.empty(), Range.createForLastMonthsIncludingCurrent(12, clock)),
                createMessage());

        //Then
        assertEquals(0, result.getTableData().getRows().size());
    }

    @Test
    public void testConvertEmptyRegionsEnhetsInputGivesEmptyOutput() {
        //Given
        final List<RegionEnhet> regionEnhets = Collections.emptyList();
        final SjukfallPerPatientsPerEnhetConverter sjukfallPerPatientsPerEnhetConverter = new SjukfallPerPatientsPerEnhetConverter(
            regionEnhets, Collections.<HsaIdEnhet>emptyList());

        //When
        final ArrayList<SimpleKonDataRow> simpleKonDataRows = new ArrayList<>();
        simpleKonDataRows.add(new SimpleKonDataRow("ett", 1, 2));
        simpleKonDataRows.add(new SimpleKonDataRow("tva", 10, 20));
        final SimpleKonResponse casesPerMonth = new SimpleKonResponse(AvailableFilters.getForSjukfall(), simpleKonDataRows);
        final SimpleDetailsData result = sjukfallPerPatientsPerEnhetConverter
            .convert(casesPerMonth, new FilterSettings(Filter.empty(), Range.createForLastMonthsIncludingCurrent(12, clock)),
                createMessage());

        //Then
        assertEquals(0, result.getTableData().getRows().size());
    }

    @Test
    public void testConvertNonRegionsEnhetsInputGivesNonEmptyOutputWhereEnhetsNotInListAreRemoved() {
        //Given
        final List<RegionEnhet> regionEnhets = Arrays.asList(new RegionEnhet(2L, new HsaIdEnhet("HSA2"), 2));
        final SjukfallPerPatientsPerEnhetConverter sjukfallPerPatientsPerEnhetConverter = new SjukfallPerPatientsPerEnhetConverter(
            regionEnhets, Collections.<HsaIdEnhet>emptyList());

        //When
        final ArrayList<SimpleKonDataRow> simpleKonDataRows = new ArrayList<>();
        simpleKonDataRows.add(new SimpleKonDataRow("ett", 1, 2, new HsaIdEnhet("HSA1")));
        simpleKonDataRows.add(new SimpleKonDataRow("tva", 10, 20, new HsaIdEnhet("HSA2")));
        final SimpleKonResponse casesPerMonth = new SimpleKonResponse(AvailableFilters.getForSjukfall(), simpleKonDataRows);
        final SimpleDetailsData result = sjukfallPerPatientsPerEnhetConverter
            .convert(casesPerMonth, new FilterSettings(Filter.empty(), Range.createForLastMonthsIncludingCurrent(12, clock)),
                createMessage());

        //Then
        assertEquals(1, result.getTableData().getRows().size());
        assertEquals("tva", result.getTableData().getRows().get(0).getName());
    }

    @Test
    public void testConvertEnhetsWithZeroPatientsAreNotPartOfResult() {
        //Given
        final List<RegionEnhet> regionEnhets = Arrays
            .asList(new RegionEnhet(1L, new HsaIdEnhet("HSA1"), 1), new RegionEnhet(2L, new HsaIdEnhet("HSA2"), 0));
        final SjukfallPerPatientsPerEnhetConverter sjukfallPerPatientsPerEnhetConverter = new SjukfallPerPatientsPerEnhetConverter(
            regionEnhets, Collections.<HsaIdEnhet>emptyList());

        //When
        final ArrayList<SimpleKonDataRow> simpleKonDataRows = new ArrayList<>();
        simpleKonDataRows.add(new SimpleKonDataRow("ett", 1, 2, new HsaIdEnhet("HSA1")));
        simpleKonDataRows.add(new SimpleKonDataRow("tva", 10, 20, new HsaIdEnhet("HSA2")));
        final SimpleKonResponse casesPerMonth = new SimpleKonResponse(AvailableFilters.getForSjukfall(), simpleKonDataRows);
        final SimpleDetailsData result = sjukfallPerPatientsPerEnhetConverter
            .convert(casesPerMonth, new FilterSettings(Filter.empty(), Range.createForLastMonthsIncludingCurrent(12, clock)),
                createMessage());

        //Then
        assertEquals(1, result.getTableData().getRows().size());
        assertEquals("ett", result.getTableData().getRows().get(0).getName());
    }

    @Test
    public void testConvertEnhetsWithLessThanZeroPatientsAreNotPartOfResult() {
        //Given
        final List<RegionEnhet> regionEnhets = Arrays
            .asList(new RegionEnhet(1L, new HsaIdEnhet("HSA1"), -1), new RegionEnhet(2L, new HsaIdEnhet("HSA2"), 3));
        final SjukfallPerPatientsPerEnhetConverter sjukfallPerPatientsPerEnhetConverter = new SjukfallPerPatientsPerEnhetConverter(
            regionEnhets, Collections.<HsaIdEnhet>emptyList());

        //When
        final ArrayList<SimpleKonDataRow> simpleKonDataRows = new ArrayList<>();
        simpleKonDataRows.add(new SimpleKonDataRow("ett", 1, 2, new HsaIdEnhet("HSA1")));
        simpleKonDataRows.add(new SimpleKonDataRow("tva", 10, 20, new HsaIdEnhet("HSA2")));
        final SimpleKonResponse casesPerMonth = new SimpleKonResponse(AvailableFilters.getForSjukfall(), simpleKonDataRows);
        final SimpleDetailsData result = sjukfallPerPatientsPerEnhetConverter
            .convert(casesPerMonth, new FilterSettings(Filter.empty(), Range.createForLastMonthsIncludingCurrent(12, clock)),
                createMessage());

        //Then
        assertEquals(1, result.getTableData().getRows().size());
        assertEquals("tva", result.getTableData().getRows().get(0).getName());
    }

    @Test
    public void testConvertEnhetsWithUnsetNumberOfPatientsAreNotPartOfResult() {
        //Given
        final List<RegionEnhet> regionEnhets = Arrays
            .asList(new RegionEnhet(1L, new HsaIdEnhet("HSA1"), null), new RegionEnhet(2L, new HsaIdEnhet("HSA2"), 3));
        final SjukfallPerPatientsPerEnhetConverter sjukfallPerPatientsPerEnhetConverter = new SjukfallPerPatientsPerEnhetConverter(
            regionEnhets, Collections.<HsaIdEnhet>emptyList());

        //When
        final ArrayList<SimpleKonDataRow> simpleKonDataRows = new ArrayList<>();
        simpleKonDataRows.add(new SimpleKonDataRow("ett", 1, 2, new HsaIdEnhet("HSA1")));
        simpleKonDataRows.add(new SimpleKonDataRow("tva", 10, 20, new HsaIdEnhet("HSA2")));
        final SimpleKonResponse casesPerMonth = new SimpleKonResponse(AvailableFilters.getForSjukfall(), simpleKonDataRows);
        final SimpleDetailsData result = sjukfallPerPatientsPerEnhetConverter
            .convert(casesPerMonth, new FilterSettings(Filter.empty(), Range.createForLastMonthsIncludingCurrent(12, clock)),
                createMessage());

        //Then
        assertEquals(1, result.getTableData().getRows().size());
        assertEquals("tva", result.getTableData().getRows().get(0).getName());
    }

    @Test
    public void testConvertResultIsUsingTwoDecimalsForBothTableAndChart() {
        //Given
        final List<RegionEnhet> regionEnhets = Arrays.asList(new RegionEnhet(1L, new HsaIdEnhet("HSA1"), 3000));
        final SjukfallPerPatientsPerEnhetConverter sjukfallPerPatientsPerEnhetConverter = new SjukfallPerPatientsPerEnhetConverter(
            regionEnhets, Collections.<HsaIdEnhet>emptyList());

        //When
        final ArrayList<SimpleKonDataRow> simpleKonDataRows = new ArrayList<>();
        simpleKonDataRows.add(new SimpleKonDataRow("ett", 0, 10, new HsaIdEnhet("HSA1")));
        final SimpleKonResponse casesPerMonth = new SimpleKonResponse(AvailableFilters.getForSjukfall(), simpleKonDataRows);
        final SimpleDetailsData result = sjukfallPerPatientsPerEnhetConverter
            .convert(casesPerMonth, new FilterSettings(Filter.empty(), Range.createForLastMonthsIncludingCurrent(12, clock)),
                createMessage());

        //Then
        assertEquals(1, result.getTableData().getRows().size());
        assertEquals(10, result.getTableData().getRows().get(0).getData().get(0));
        assertEquals(3000, result.getTableData().getRows().get(0).getData().get(1));

        assertEquals("3,33", result.getTableData().getRows().get(0).getData().get(2));
        assertEquals(3.33, result.getChartData().getSeries().get(0).getData().get(0));
    }

    @Test
    public void testConvertEnhetsHasCorrectSortingSTATISTIK1034() {
        //Given
        final List<RegionEnhet> regionEnhets = Arrays
            .asList(new RegionEnhet(1L, new HsaIdEnhet("HSA1"), 1000), new RegionEnhet(2L, new HsaIdEnhet("HSA2"), 1000),
                new RegionEnhet(3L, new HsaIdEnhet("HSA3"), 1000));
        final SjukfallPerPatientsPerEnhetConverter sjukfallPerPatientsPerEnhetConverter = new SjukfallPerPatientsPerEnhetConverter(
            regionEnhets, Collections.<HsaIdEnhet>emptyList());

        //When
        final ArrayList<SimpleKonDataRow> simpleKonDataRows = new ArrayList<>();
        simpleKonDataRows.add(new SimpleKonDataRow("ett", 5, 5, new HsaIdEnhet("HSA1")));
        simpleKonDataRows.add(new SimpleKonDataRow("tva", 5, 1, new HsaIdEnhet("HSA2")));
        simpleKonDataRows.add(new SimpleKonDataRow("tre", 10, 5, new HsaIdEnhet("HSA3")));
        final SimpleKonResponse casesPerMonth = new SimpleKonResponse(AvailableFilters.getForSjukfall(), simpleKonDataRows);
        final SimpleDetailsData result = sjukfallPerPatientsPerEnhetConverter
            .convert(casesPerMonth, new FilterSettings(Filter.empty(), Range.createForLastMonthsIncludingCurrent(12, clock)),
                createMessage());

        //Then
        //STATISTIK-1034: Table sorted by name
        TableData tableData = result.getTableData();
        List<NamedData> tableRows = tableData.getRows();
        assertEquals(3, tableRows.size());
        assertEquals("ett", tableRows.get(0).getName());
        assertEquals("tre", tableRows.get(1).getName());
        assertEquals("tva", tableRows.get(2).getName());

        //STATISTIK-1034: Chart sorted by highest bar
        ChartData chartData = result.getChartData();
        final List<ChartCategory> categories = chartData.getCategories();
        assertEquals(3, categories.size());
        assertEquals("tre", categories.get(0).getName());
        assertEquals("ett", categories.get(1).getName());
        assertEquals("tva", categories.get(2).getName());
    }

    @Test
    public void testRoundToTwoDecimalsAndFormatToString() {
        final String result = SjukfallPerPatientsPerEnhetConverter.roundToTwoDecimalsAndFormatToString(1.2345F);
        assertEquals("1,23", result);
    }

    @Test
    public void testRoundToTwoDecimalsAndFormatToStringAddDecimal() {
        final String result = SjukfallPerPatientsPerEnhetConverter.roundToTwoDecimalsAndFormatToString(1.2F);
        assertEquals("1,20", result);
    }

    @Test
    public void testRoundToTwoDecimalsAndFormatToStringRountUpCorrectly() {
        final String result = SjukfallPerPatientsPerEnhetConverter.roundToTwoDecimalsAndFormatToString(1.235F);
        assertEquals("1,24", result);
    }

    @Test
    public void testRoundToTwoDecimals() {
        final double result = SjukfallPerPatientsPerEnhetConverter.roundToTwoDecimals(1.2345F);
        assertEquals(1.23, result, 0.00);
    }

    @Test
    public void testRoundToTwoDecimalsAddDecimal() {
        final double result = SjukfallPerPatientsPerEnhetConverter.roundToTwoDecimals(1.2F);
        assertEquals(1.20, result, 0.00);
    }

    @Test
    public void testRoundToTwoDecimalsRountUpCorrectly() {
        final double result = SjukfallPerPatientsPerEnhetConverter.roundToTwoDecimals(1.235F);
        assertEquals(1.24, result, 0.00);
    }

    private Message createMessage() {
        return Message.create(ErrorType.FILTER, ErrorSeverity.INFO, "");
    }
}
