/*
 * Copyright (C) 2013 - 2014 Inera AB (http://www.inera.se)
 *
 *     This file is part of Inera Statistics (http://code.google.com/p/inera-statistics/).
 *
 *     Inera Statistics is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Inera Statistics is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU LESSER GENERAL PUBLIC LICENSE for more details.
 *
 *     You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.inera.statistics.web.service;

import org.junit.Test;
import se.inera.statistics.service.report.model.Avsnitt;
import se.inera.statistics.service.report.model.DiagnosgruppResponse;
import se.inera.statistics.service.report.model.KonDataRow;
import se.inera.statistics.service.report.model.KonField;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.web.model.DualSexStatisticsData;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class DiagnoskapitelConverterTest {

    @Test
    public void converterTestEmpty() {
        DiagnosgruppResponse resp = new DiagnosgruppResponse(new ArrayList<Avsnitt>(), new ArrayList<KonDataRow>());
        DualSexStatisticsData data = new DiagnosisSubGroupsConverter().convert(resp, new Range());
        assertEquals("[]", data.getFemaleChart().getCategories().toString());
        assertEquals("[]", data.getFemaleChart().getSeries().toString());
    }

    @Test
    public void converterTest() {
        //Given
        ArrayList<Avsnitt> avsnitts = new ArrayList<Avsnitt>();
        avsnitts.add(new Avsnitt("A00-B99", "name1"));
        ArrayList<KonDataRow> rows = new ArrayList<KonDataRow>();
        ArrayList<KonField> diagnosisGroupData = new ArrayList<KonField>();
        // CHECKSTYLE:OFF MagicNumber
        diagnosisGroupData.add(new KonField(3, 2));
        // CHECKSTYLE:ON MagicNumber
        rows.add(new KonDataRow("period1", diagnosisGroupData));
        DiagnosgruppResponse resp = new DiagnosgruppResponse(avsnitts, rows);

        //When
        DiagnosisSubGroupsConverter converter = new DiagnosisSubGroupsConverter();
        DualSexStatisticsData data = converter.convert(resp, new Range());

        //Then
        assertEquals("[period1]", data.getFemaleChart().getCategories().toString());
        assertEquals("[A00-B99 name1: [3]]", data.getFemaleChart().getSeries().toString());

        assertEquals("[period1]", data.getMaleChart().getCategories().toString());
        assertEquals("[A00-B99 name1: [2]]", data.getMaleChart().getSeries().toString());

        assertEquals("[[;1, ;1, A00-B99 name1;2, ;1], [Period;1, Antal sjukfall totalt;1, Kvinnor;1, Män;1, Summering;1]]", data.getTableData().getHeaders().toString());
        assertEquals("[period1: [5, 3, 2, 5], Totalt: [5, 3, 2]]", data.getTableData().getRows().toString());
    }

    @Test
    public void converterTopColumnsTest() {
        //Given
        ArrayList<Avsnitt> avsnitts = new ArrayList<Avsnitt>();
        avsnitts.add(new Avsnitt("A00-B90", "name1"));
        avsnitts.add(new Avsnitt("A00-B91", "name1"));
        avsnitts.add(new Avsnitt("A00-B92", "name1"));
        avsnitts.add(new Avsnitt("A00-B93", "name1"));
        avsnitts.add(new Avsnitt("A00-B94", "name1"));
        avsnitts.add(new Avsnitt("A00-B95", "name1"));
        avsnitts.add(new Avsnitt("A00-B96", "name1"));
        avsnitts.add(new Avsnitt("A00-B97", "name1"));
        ArrayList<KonDataRow> rows = new ArrayList<KonDataRow>();
        ArrayList<KonField> diagnosisGroupData = new ArrayList<KonField>();
        // CHECKSTYLE:OFF MagicNumber
        diagnosisGroupData.add(new KonField(3, 30));
        diagnosisGroupData.add(new KonField(4, 40));
        diagnosisGroupData.add(new KonField(1, 10));
        diagnosisGroupData.add(new KonField(7, 70));
        diagnosisGroupData.add(new KonField(55, 50));
        diagnosisGroupData.add(new KonField(8, 80));
        diagnosisGroupData.add(new KonField(2, 20));
        diagnosisGroupData.add(new KonField(6, 60));
        rows.add(new KonDataRow("period1", diagnosisGroupData));
        DiagnosgruppResponse resp = new DiagnosgruppResponse(avsnitts, rows);

        //When
        DiagnosisSubGroupsConverter converter = new DiagnosisSubGroupsConverter();
        DualSexStatisticsData data = converter.convert(resp, new Range());

        //Then
        assertEquals(7, data.getFemaleChart().getSeries().size());
        // CHECKSTYLE:ON MagicNumber

        assertEquals("[period1]", data.getFemaleChart().getCategories().toString());
        assertEquals("[A00-B94 name1: [55], A00-B95 name1: [8], A00-B93 name1: [7], A00-B97 name1: [6], A00-B91 name1: [4], A00-B90 name1: [3], Övriga diagnosavsnitt: [3]]", data.getFemaleChart().getSeries().toString());

        assertEquals("[period1]", data.getMaleChart().getCategories().toString());
        assertEquals("[A00-B94 name1: [50], A00-B95 name1: [80], A00-B93 name1: [70], A00-B97 name1: [60], A00-B91 name1: [40], A00-B90 name1: [30], Övriga diagnosavsnitt: [30]]", data.getMaleChart().getSeries().toString());

        assertEquals("[[;1, ;1, A00-B90 name1;2, A00-B91 name1;2, A00-B92 name1;2, A00-B93 name1;2, A00-B94 name1;2, A00-B95 name1;2, A00-B96 name1;2, A00-B97 name1;2, ;1], [Period;1, Antal sjukfall totalt;1, Kvinnor;1, Män;1, Kvinnor;1, Män;1, Kvinnor;1, Män;1, Kvinnor;1, Män;1, Kvinnor;1, Män;1, Kvinnor;1, Män;1, Kvinnor;1, Män;1, Kvinnor;1, Män;1, Summering;1]]", data.getTableData().getHeaders().toString());
        assertEquals("[period1: [446, 3, 30, 4, 40, 1, 10, 7, 70, 55, 50, 8, 80, 2, 20, 6, 60, 446], Totalt: [446, 3, 30, 4, 40, 1, 10, 7, 70, 55, 50, 8, 80, 2, 20, 6, 60]]", data.getTableData().getRows().toString());
    }

}
