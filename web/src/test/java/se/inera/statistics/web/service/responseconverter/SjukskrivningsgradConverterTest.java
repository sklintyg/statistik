/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
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
import static org.junit.Assert.assertTrue;

import java.time.Clock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import se.inera.statistics.service.report.model.AvailableFilters;
import se.inera.statistics.service.report.model.KonDataResponse;
import se.inera.statistics.service.report.model.KonDataRow;
import se.inera.statistics.service.report.model.KonField;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.web.model.DualSexStatisticsData;
import se.inera.statistics.web.model.TableData;
import se.inera.statistics.web.service.Filter;
import se.inera.statistics.web.service.FilterSettings;

public class SjukskrivningsgradConverterTest {

    private final Clock clock = Clock.systemDefaultZone();

    @Test
    public void tableConverterTestEmptyInput() {
        final KonDataResponse resp = new KonDataResponse(AvailableFilters.getForSjukfall(), new ArrayList<>(), new ArrayList<>());
        TableData tableData = new DegreeOfSickLeaveConverter().convertTable(resp, "");
        assertEquals("[[;1, ;1], [Period;1, Antal sjukfall totalt;1]]", tableData.getHeaders().toString());
        assertEquals("[]", tableData.getRows().toString());
    }

    @Test
    public void tableConverterTest() {
        //Given
        List<KonDataRow> rows = new ArrayList<>();
        List<KonField> diagnosisGroupData = new ArrayList<>();
        // CHECKSTYLE:OFF MagicNumber
        diagnosisGroupData.add(new KonField(3, 2));
        // CHECKSTYLE:ON MagicNumber
        rows.add(new KonDataRow("period1", diagnosisGroupData));
        final List<String> degreesOfSickLeave = Arrays.asList("50");
        final KonDataResponse resp = new KonDataResponse(AvailableFilters.getForSjukfall(), degreesOfSickLeave, rows);

        //When
        TableData tableData = new DegreeOfSickLeaveConverter().convertTable(resp, "Antal sjukfall med %1$s%% sjukskrivningsgrad");

        //Then
        assertEquals(
            "[[;1, ;1, Antal sjukfall med 50% sjukskrivningsgrad;3], [Period;1, Antal sjukfall totalt;1, Totalt;1, Kvinnor;1, Män;1]]",
            tableData.getHeaders().toString());
        assertEquals("[period1: [5, 5, 3, 2]]", tableData.getRows().toString());
    }

    @Test
    public void converterTestEmpty() {
        KonDataResponse resp = new KonDataResponse(AvailableFilters.getForSjukfall(), new ArrayList<>(), new ArrayList<>());
        final FilterSettings filterSettings = new FilterSettings(Filter.empty(), new Range(clock));
        DualSexStatisticsData data = new DegreeOfSickLeaveConverter().convert(resp, filterSettings);
        assertEquals("[Totalt]", data.getFemaleChart().getCategories().toString());
        assertEquals("[Totalt sjukskrivningsgrad: [0]]", data.getFemaleChart().getSeries().toString());
    }

    @Test
    public void converterTest() {
        //Given
        List<KonDataRow> rows = new ArrayList<>();
        List<KonField> diagnosisGroupData = new ArrayList<>();
        // CHECKSTYLE:OFF MagicNumber
        diagnosisGroupData.add(new KonField(3, 2));
        // CHECKSTYLE:ON MagicNumber
        rows.add(new KonDataRow("period1", diagnosisGroupData));
        final List<String> degreesOfSickLeave = Arrays.asList("50 %");
        final KonDataResponse resp = new KonDataResponse(AvailableFilters.getForSjukfall(), degreesOfSickLeave, rows);

        //When
        DegreeOfSickLeaveConverter converter = new DegreeOfSickLeaveConverter();
        final FilterSettings filterSettings = new FilterSettings(Filter.empty(), new Range(clock));
        DualSexStatisticsData data = converter.convert(resp, filterSettings);

        //Then
        assertEquals("[period1]", data.getFemaleChart().getCategories().toString());
        assertTrue(data.getFemaleChart().getSeries().toString(),
            data.getFemaleChart().getSeries().toString().contains("50 % sjukskrivningsgrad: [3]"));

        assertEquals("[period1]", data.getMaleChart().getCategories().toString());
        assertTrue(data.getMaleChart().getSeries().toString(),
            data.getMaleChart().getSeries().toString().contains("50 % sjukskrivningsgrad: [2]"));

        assertEquals("[[;1, ;1, 50 % sjukskrivningsgrad;3], [Period;1, Antal sjukfall totalt;1, Totalt;1, Kvinnor;1, Män;1]]",
            data.getTableData().getHeaders().toString());
        assertEquals("[period1: [5, 5, 3, 2]]", data.getTableData().getRows().toString());
    }

}
