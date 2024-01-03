/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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
import java.util.List;
import org.junit.Test;
import se.inera.statistics.service.report.model.AvailableFilters;
import se.inera.statistics.service.report.model.DiagnosgruppResponse;
import se.inera.statistics.service.report.model.Icd;
import se.inera.statistics.service.report.model.KonDataRow;
import se.inera.statistics.service.report.model.KonField;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.web.model.DualSexStatisticsData;
import se.inera.statistics.web.service.dto.Filter;
import se.inera.statistics.web.service.dto.FilterSettings;

public class DiagnoskapitelConverterTest {

    private final Clock clock = Clock.systemDefaultZone();

    @Test
    public void converterTestEmpty() {
        DiagnosgruppResponse resp = new DiagnosgruppResponse(AvailableFilters.getForSjukfall(), new ArrayList<>(), new ArrayList<>());
        final FilterSettings filterSettings = new FilterSettings(Filter.empty(), new Range(clock));
        DualSexStatisticsData data = new DiagnosisSubGroupsConverter().convert(resp, filterSettings);
        assertEquals("[]", data.getFemaleChart().getCategories().toString());
        assertEquals("[Totalt: []]", data.getFemaleChart().getSeries().toString());
    }

    @Test
    public void converterTest() {
        //Given
        List<Icd> avsnitts = new ArrayList<>();
        avsnitts.add(new Icd("A00-B99", "name1", 1));
        List<KonDataRow> rows = new ArrayList<>();
        List<KonField> diagnosisGroupData = new ArrayList<>();
        // CHECKSTYLE:OFF MagicNumber
        diagnosisGroupData.add(new KonField(3, 2));
        // CHECKSTYLE:ON MagicNumber
        rows.add(new KonDataRow("period1", diagnosisGroupData));
        DiagnosgruppResponse resp = new DiagnosgruppResponse(AvailableFilters.getForSjukfall(), avsnitts, rows);

        //When
        DiagnosisSubGroupsConverter converter = new DiagnosisSubGroupsConverter();
        final FilterSettings filterSettings = new FilterSettings(Filter.empty(), new Range(clock));
        DualSexStatisticsData data = converter.convert(resp, filterSettings);

        //Then
        assertEquals("[period1]", data.getFemaleChart().getCategories().toString());
        assertEquals("[A00-B99 name1: [3]]", data.getFemaleChart().getSeries().toString());

        assertEquals("[period1]", data.getMaleChart().getCategories().toString());
        assertEquals("[A00-B99 name1: [2]]", data.getMaleChart().getSeries().toString());

        assertEquals("[[;1, ;1, A00-B99 name1;3], [Period;1, Antal sjukfall totalt;1, Totalt;1, Kvinnor;1, Män;1]]",
            data.getTableData().getHeaders().toString());
        assertEquals("[period1: [5, 5, 3, 2]]", data.getTableData().getRows().toString());
    }

    @Test
    public void converterTopColumnsTest() {
        //Given
        List<Icd> avsnitts = new ArrayList<>();
        avsnitts.add(new Icd("A00-B90", "name1", -1));
        avsnitts.add(new Icd("A00-B91", "name1", -1));
        avsnitts.add(new Icd("A00-B92", "name1", -1));
        avsnitts.add(new Icd("A00-B93", "name1", -1));
        avsnitts.add(new Icd("A00-B94", "name1", -1));
        avsnitts.add(new Icd("A00-B95", "name1", -1));
        avsnitts.add(new Icd("A00-B96", "name1", -1));
        avsnitts.add(new Icd("A00-B97", "name1", -1));
        List<KonDataRow> rows = new ArrayList<>();
        List<KonField> diagnosisGroupData = new ArrayList<>();
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
        DiagnosgruppResponse resp = new DiagnosgruppResponse(AvailableFilters.getForSjukfall(), avsnitts, rows);

        //When
        DiagnosisSubGroupsConverter converter = new DiagnosisSubGroupsConverter();
        final FilterSettings filterSettings = new FilterSettings(Filter.empty(), new Range(clock));
        DualSexStatisticsData data = converter.convert(resp, filterSettings);

        //Then
        assertEquals(7, data.getFemaleChart().getSeries().size());

        assertEquals("[period1]", data.getFemaleChart().getCategories().toString());
        assertEquals(
            "[A00-B94 name1: [55], A00-B95 name1: [8], A00-B93 name1: [7], A00-B97 name1: [6], A00-B91 name1: [4], A00-B90 name1: [3], Övriga: [3]]",
            data.getFemaleChart().getSeries().toString());

        assertEquals("[period1]", data.getMaleChart().getCategories().toString());
        assertEquals(
            "[A00-B94 name1: [50], A00-B95 name1: [80], A00-B93 name1: [70], A00-B97 name1: [60], A00-B91 name1: [40], A00-B90 name1: [30], Övriga: [30]]",
            data.getMaleChart().getSeries().toString());

        assertEquals(
            "[[;1, ;1, A00-B90 name1;3, A00-B91 name1;3, A00-B92 name1;3, A00-B93 name1;3, A00-B94 name1;3, A00-B95 name1;3, A00-B96 name1;3, A00-B97 name1;3], [Period;1, Antal sjukfall totalt;1, Totalt;1, Kvinnor;1, Män;1, Totalt;1, Kvinnor;1, Män;1, Totalt;1, Kvinnor;1, Män;1, Totalt;1, Kvinnor;1, Män;1, Totalt;1, Kvinnor;1, Män;1, Totalt;1, Kvinnor;1, Män;1, Totalt;1, Kvinnor;1, Män;1, Totalt;1, Kvinnor;1, Män;1]]",
            data.getTableData().getHeaders().toString());
        assertEquals("[period1: [446, 33, 3, 30, 44, 4, 40, 11, 1, 10, 77, 7, 70, 105, 55, 50, 88, 8, 80, 22, 2, 20, 66, 6, 60]]",
            data.getTableData().getRows().toString());
        // CHECKSTYLE:ON MagicNumber
    }

}
