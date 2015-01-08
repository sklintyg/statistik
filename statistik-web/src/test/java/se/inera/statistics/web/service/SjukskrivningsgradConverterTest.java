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
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import se.inera.statistics.service.report.model.KonDataRow;
import se.inera.statistics.service.report.model.KonField;
import se.inera.statistics.service.report.model.SjukskrivningsgradResponse;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.web.model.DualSexStatisticsData;
import se.inera.statistics.web.model.TableData;

public class SjukskrivningsgradConverterTest {

    @Test
    public void tableConverterTestEmptyInput() {
        final SjukskrivningsgradResponse resp = new SjukskrivningsgradResponse(new ArrayList<String>(), new ArrayList<KonDataRow>());
        TableData tableData = DegreeOfSickLeaveConverter.convertTable(resp);
        assertEquals("[[;1, ;1], [Period;1, Antal sjukfall totalt;1]]", tableData.getHeaders().toString());
        assertEquals("[]", tableData.getRows().toString());
    }

    @Test
    public void tableConverterTest() {
        //Given
        ArrayList<KonDataRow> rows = new ArrayList<>();
        ArrayList<KonField> diagnosisGroupData = new ArrayList<KonField>();
        // CHECKSTYLE:OFF MagicNumber
        diagnosisGroupData.add(new KonField(3, 2));
        // CHECKSTYLE:ON MagicNumber
        rows.add(new KonDataRow("period1", diagnosisGroupData));
        final List<String> degreesOfSickLeave = Arrays.asList("50");
        final SjukskrivningsgradResponse resp = new SjukskrivningsgradResponse(degreesOfSickLeave, rows);

        //When
        TableData tableData = DegreeOfSickLeaveConverter.convertTable(resp);

        //Then
        assertEquals("[[;1, ;1, Antal sjukfall med 50% sjukskrivningsgrad;2], [Period;1, Antal sjukfall totalt;1, Kvinnor;1, Män;1]]", tableData.getHeaders().toString());
        assertEquals("[period1: [5, 3, 2]]", tableData.getRows().toString());
    }

    @Test
    public void converterTestEmpty() {
        SjukskrivningsgradResponse resp = new SjukskrivningsgradResponse(new ArrayList<String>(), new ArrayList<KonDataRow>());
        DualSexStatisticsData data = new DegreeOfSickLeaveConverter().convert(resp, new Range());
        assertEquals("[]", data.getFemaleChart().getCategories().toString());
        assertEquals("[]", data.getFemaleChart().getSeries().toString());
    }

    @Test
    public void converterTest() {
        //Given
        ArrayList<KonDataRow> rows = new ArrayList<>();
        ArrayList<KonField> diagnosisGroupData = new ArrayList<KonField>();
        // CHECKSTYLE:OFF MagicNumber
        diagnosisGroupData.add(new KonField(3, 2));
        // CHECKSTYLE:ON MagicNumber
        rows.add(new KonDataRow("period1", diagnosisGroupData));
        final List<String> degreesOfSickLeave = Arrays.asList("50");
        final SjukskrivningsgradResponse resp = new SjukskrivningsgradResponse(degreesOfSickLeave, rows);

        //When
        DegreeOfSickLeaveConverter converter = new DegreeOfSickLeaveConverter();
        DualSexStatisticsData data = converter.convert(resp, new Range());

        //Then
        assertEquals("[period1]", data.getFemaleChart().getCategories().toString());
        assertTrue(data.getFemaleChart().getSeries().toString(), data.getFemaleChart().getSeries().toString().contains("Antal sjukfall med 50% sjukskrivningsgrad: [3]"));

        assertEquals("[period1]", data.getMaleChart().getCategories().toString());
        assertTrue(data.getMaleChart().getSeries().toString(), data.getMaleChart().getSeries().toString().contains("Antal sjukfall med 50% sjukskrivningsgrad: [2]"));

        assertEquals("[[;1, ;1, Antal sjukfall med 50% sjukskrivningsgrad;2], [Period;1, Antal sjukfall totalt;1, Kvinnor;1, Män;1]]", data.getTableData().getHeaders().toString());
        assertEquals("[period1: [5, 3, 2]]", data.getTableData().getRows().toString());
    }

}
