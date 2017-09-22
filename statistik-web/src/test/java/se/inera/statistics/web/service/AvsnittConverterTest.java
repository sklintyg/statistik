/*
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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
package se.inera.statistics.web.service;

import org.junit.Test;
import se.inera.statistics.service.report.model.DiagnosgruppResponse;
import se.inera.statistics.service.report.model.Icd;
import se.inera.statistics.service.report.model.KonDataRow;
import se.inera.statistics.service.report.model.KonField;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.util.Icd10;
import se.inera.statistics.service.report.util.Icd10RangeType;
import se.inera.statistics.web.model.DualSexStatisticsData;
import se.inera.statistics.web.model.TableData;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AvsnittConverterTest {

    private final Clock clock = Clock.systemDefaultZone();

    @Test
    public void tableConverterTestEmptyInput() {
        DiagnosgruppResponse resp = new DiagnosgruppResponse(new ArrayList<Icd>(), new ArrayList<KonDataRow>());
        TableData tableData = new DiagnosisGroupsConverter().convertTable(resp, "");
        assertEquals("[[;1, ;1], [Period;1, Antal sjukfall totalt;1]]", tableData.getHeaders().toString());
        assertEquals("[]", tableData.getRows().toString());
    }

    @Test
    public void tableConverterTest() {
        //Given
        List<Icd> avsnitts = new ArrayList<>();
        avsnitts.add(new Icd("A01-B99", "name1", -1));
        List<KonDataRow> rows = new ArrayList<>();
        List<KonField> diagnosisGroupData = new ArrayList<>();
        // CHECKSTYLE:OFF MagicNumber
        diagnosisGroupData.add(new KonField(3, 2));
        // CHECKSTYLE:ON MagicNumber
        rows.add(new KonDataRow("period1", diagnosisGroupData));
        DiagnosgruppResponse resp = new DiagnosgruppResponse(avsnitts, rows);

        //When
        TableData tableData = new DiagnosisGroupsConverter().convertTable(resp, "%1$s");

        //Then
        assertEquals("[[;1, ;1, A01-B99 name1;3], [Period;1, Antal sjukfall totalt;1, Totalt;1, Kvinnor;1, Män;1]]", tableData.getHeaders().toString());
        assertEquals("[period1: [5, 5, 3, 2]]", tableData.getRows().toString());
    }

    @Test
    public void converterTestEmpty() {
        DiagnosgruppResponse resp = new DiagnosgruppResponse(new ArrayList<Icd>(), new ArrayList<KonDataRow>());
        final FilterSettings filterSettings = new FilterSettings(Filter.empty(), new Range(clock));
        DualSexStatisticsData data = new DiagnosisGroupsConverter().convert(resp, filterSettings);
        assertEquals("[]", data.getFemaleChart().getCategories().toString());
        assertEquals("[A00-E90, G00-L99, N00-N99 Somatiska sjukdomar: [], F00-F99 Psykiska sjukdomar: [], M00-M99 Muskuloskeletala sjukdomar: [], O00-O99 Graviditet och förlossning: [], P00-P96, Q00-Q99, S00-Y98 Övrigt: [], R00-R99 Symtomdiagnoser: [], Z00-Z99 Faktorer av betydelse för hälsotillståndet och för kontakter med hälso- och sjukvården: []]", data.getFemaleChart().getSeries().toString());
    }

    @Test
    public void converterTest() {
        //Given
        List<Icd> avsnitts = new ArrayList<>();
        avsnitts.add(new Icd("A00-B99", "name1", Icd10.icd10ToInt("A00-B99", Icd10RangeType.KAPITEL)));
        List<KonDataRow> rows = new ArrayList<>();
        List<KonField> diagnosisGroupData = new ArrayList<>();
        // CHECKSTYLE:OFF MagicNumber
        diagnosisGroupData.add(new KonField(3, 2));
        // CHECKSTYLE:ON MagicNumber
        rows.add(new KonDataRow("period1", diagnosisGroupData));
        DiagnosgruppResponse resp = new DiagnosgruppResponse(avsnitts, rows);

        //When
        DiagnosisGroupsConverter converter = new DiagnosisGroupsConverter();
        final FilterSettings filterSettings = new FilterSettings(Filter.empty(), new Range(clock));
        DualSexStatisticsData data = converter.convert(resp, filterSettings);

        //Then
        assertEquals("[period1]", data.getFemaleChart().getCategories().toString());
        assertTrue(data.getFemaleChart().getSeries().toString(), data.getFemaleChart().getSeries().toString().contains("A00-E90, G00-L99, N00-N99 Somatiska sjukdomar: [3]"));

        assertEquals("[period1]", data.getMaleChart().getCategories().toString());
        assertTrue(data.getMaleChart().getSeries().toString(), data.getMaleChart().getSeries().toString().contains("A00-E90, G00-L99, N00-N99 Somatiska sjukdomar: [2]"));

        assertEquals("[[;1, ;1, A00-B99 name1;3], [Period;1, Antal sjukfall totalt;1, Totalt;1, Kvinnor;1, Män;1]]", data.getTableData().getHeaders().toString());
        assertEquals("[period1: [5, 5, 3, 2]]", data.getTableData().getRows().toString());
    }

}
