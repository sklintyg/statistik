/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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

import org.junit.Test;

import se.inera.statistics.service.report.model.ActiveFilters;
import se.inera.statistics.service.report.model.DiagnosgruppResponse;
import se.inera.statistics.service.report.model.Icd;
import se.inera.statistics.service.report.model.KonDataRow;
import se.inera.statistics.service.report.model.KonField;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.web.model.DualSexStatisticsData;
import se.inera.statistics.web.service.Filter;
import se.inera.statistics.web.service.FilterSettings;
import se.inera.statistics.web.service.responseconverter.DiagnosisSubGroupsConverter;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class DiagnosisSubGroupsConverterTest {

    private final Clock clock = Clock.systemDefaultZone();

    @Test
    public void testGetTopColumnIndexesAllAreIncluded() {
        //Given
        ArrayList<KonDataRow> rows = new ArrayList<>();
        ArrayList<KonField> data = new ArrayList<>();
        data.add(new KonField(1, 2));
        data.add(new KonField(2, 1));
        data.add(new KonField(2, 2));
        rows.add(new KonDataRow("", data));
        DiagnosgruppResponse response = new DiagnosgruppResponse(ActiveFilters.getForSjukfall(), getIcds(data.size()), rows);

        //When
        List<Integer> result = new DiagnosisSubGroupsConverter().getTopColumnIndexes(response);

        //Then
        assertEquals(3, result.size());
    }

    @Test
    public void testGetTopColumnIndexesShowsSevenGroupsEvenWhenThereExistsMoreAndOneOfTheGroupsThenIsOvrigt() {
        //Given
        ArrayList<KonDataRow> rows = new ArrayList<>();
        ArrayList<KonField> data = new ArrayList<>();
        data.add(new KonField(1, 2));
        data.add(new KonField(2, 1));
        data.add(new KonField(2, 2));
        data.add(new KonField(1, 1));
        data.add(new KonField(2, 2));
        data.add(new KonField(2, 2));
        data.add(new KonField(2, 2));
        data.add(new KonField(2, 2));
        data.add(new KonField(2, 2));
        data.add(new KonField(2, 2));
        rows.add(new KonDataRow("", data));
        final List<Icd> icdTyps = getIcds(data.size());
        DiagnosgruppResponse response = new DiagnosgruppResponse(ActiveFilters.getForSjukfall(), icdTyps, rows);

        //When
        List<Integer> result = new DiagnosisSubGroupsConverter().getTopColumnIndexes(response);

        //Then
        assertEquals(7, result.size());
        assertEquals(DiagnosisSubGroupsConverter.OTHER_GROUP_INDEX, result.get(6).intValue());
    }

    private List<Icd> getIcds(int numberOfIcdToCreate) {
        final List<Icd> icds = new ArrayList<>();
        for (int i = 1; i <= numberOfIcdToCreate; i++) {
             icds.add(getIcd(i));

        }
        return icds;
    }

    @Test
    public void testGetTopColumnIndexesAllAreIncludedWhenUpToSevenHasNoneEmptyDataINTYG1877() {
        //Given
        ArrayList<KonDataRow> rows = new ArrayList<>();
        ArrayList<KonField> data = new ArrayList<>();
        data.add(new KonField(1, 2));
        data.add(new KonField(2, 1));
        data.add(new KonField(2, 2));
        data.add(new KonField(0, 0));
        data.add(new KonField(2, 2));
        data.add(new KonField(2, 2));
        data.add(new KonField(2, 2));
        data.add(new KonField(2, 2));
        rows.add(new KonDataRow("", data));
        DiagnosgruppResponse response = new DiagnosgruppResponse(ActiveFilters.getForSjukfall(), getIcds(data.size()), rows);

        //When
        List<Integer> result = new DiagnosisSubGroupsConverter().getTopColumnIndexes(response);

        //Then
        assertEquals(7, result.size());
        assertFalse(result.contains(DiagnosisSubGroupsConverter.OTHER_GROUP_INDEX));
    }

    @Test
    public void testGetTopColumnIndexesCorrectOrder() {
        //Given
        ArrayList<KonDataRow> rows = new ArrayList<>();
        ArrayList<KonField> data = new ArrayList<>();
        data.add(new KonField(1, 2));
        data.add(new KonField(2, 0));
        data.add(new KonField(2, 2));
        rows.add(new KonDataRow("", data));
        DiagnosgruppResponse response = new DiagnosgruppResponse(ActiveFilters.getForSjukfall(), getIcds(data.size()), rows);

        //When
        List<Integer> result = new DiagnosisSubGroupsConverter().getTopColumnIndexes(response);

        //Then
        assertEquals(2, result.get(0).intValue());
        assertEquals(0, result.get(1).intValue());
        assertEquals(1, result.get(2).intValue());
    }

    private Icd getIcd(int id) {
        return new Icd(String.valueOf(id), String.valueOf(id), id);
    }

    @Test
    public void testGetTopColumnIndexesRowWithZeroIsExcluded() {
        //Given
        ArrayList<KonDataRow> rows = new ArrayList<>();
        ArrayList<KonField> data = new ArrayList<>();
        data.add(new KonField(1, 2));
        data.add(new KonField(0, 0));
        data.add(new KonField(2, 2));
        rows.add(new KonDataRow("", data));
        DiagnosgruppResponse response = new DiagnosgruppResponse(ActiveFilters.getForSjukfall(), getIcds(data.size()), rows);

        //When
        List<Integer> result = new DiagnosisSubGroupsConverter().getTopColumnIndexes(response);

        //Then
        assertEquals(2, result.size());
        assertEquals(2, result.get(0).intValue());
        assertEquals(0, result.get(1).intValue());
    }

    @Test
    public void testConvertedResponseDoesNotContainEmptyOvrigtGroupINTYG1821() {
        //Given
        ArrayList<KonDataRow> rows = new ArrayList<>();
        ArrayList<KonField> data = new ArrayList<>();
        data.add(new KonField(1, 0));
        data.add(new KonField(0, 0));
        data.add(new KonField(2, 0));
        data.add(new KonField(1, 2));
        data.add(new KonField(0, 0));
        data.add(new KonField(2, 2));
        data.add(new KonField(5, 0));
        data.add(new KonField(0, 0));
        data.add(new KonField(6, 0));
        data.add(new KonField(0, 0));
        rows.add(new KonDataRow("", data));
        DiagnosgruppResponse response = new DiagnosgruppResponse(ActiveFilters.getForSjukfall(), getIcds(data.size()), rows);

        //When
        final DualSexStatisticsData result = new DiagnosisSubGroupsConverter().convert(response, new FilterSettings(Filter.empty(), Range.quarter(clock)));

        //Then
        assertEquals(6, result.getFemaleChart().getSeries().size());
        assertTrue(result.getFemaleChart().getSeries().stream().noneMatch(chartSeries -> DiagnosisSubGroupsConverter.OTHER_GROUP_NAME.equals(chartSeries.getName())));
    }

    @Test
    public void testConvertedResponseDoesNotContainOvrigtGroupWhenOnly7NoneEmptySeriesExistsINTYG1821() {
        //Given
        ArrayList<KonDataRow> rows = new ArrayList<>();
        ArrayList<KonField> data = new ArrayList<>();
        data.add(new KonField(1, 0));
        data.add(new KonField(0, 0));
        data.add(new KonField(2, 0));
        data.add(new KonField(1, 2));
        data.add(new KonField(0, 0));
        data.add(new KonField(2, 2));
        data.add(new KonField(5, 0));
        data.add(new KonField(0, 0));
        data.add(new KonField(6, 0));
        data.add(new KonField(0, 1));
        rows.add(new KonDataRow("", data));
        DiagnosgruppResponse response = new DiagnosgruppResponse(ActiveFilters.getForSjukfall(), getIcds(data.size()), rows);

        //When
        final DualSexStatisticsData result = new DiagnosisSubGroupsConverter().convert(response, new FilterSettings(Filter.empty(), Range.quarter(clock)));

        //Then
        assertEquals(7, result.getFemaleChart().getSeries().size());
        assertTrue(result.getFemaleChart().getSeries().stream().noneMatch(chartSeries -> DiagnosisSubGroupsConverter.OTHER_GROUP_NAME.equals(chartSeries.getName())));
    }

    @Test
    public void testConvertedResponseDoesContainNoneEmptyOvrigtGroupINTYG1821() {
        //Given
        ArrayList<KonDataRow> rows = new ArrayList<>();
        ArrayList<KonField> data = new ArrayList<>();
        data.add(new KonField(1, 0));
        data.add(new KonField(0, 0));
        data.add(new KonField(2, 0));
        data.add(new KonField(1, 2));
        data.add(new KonField(0, 0));
        data.add(new KonField(2, 2));
        data.add(new KonField(5, 0));
        data.add(new KonField(0, 2));
        data.add(new KonField(6, 0));
        data.add(new KonField(0, 1));
        rows.add(new KonDataRow("", data));
        DiagnosgruppResponse response = new DiagnosgruppResponse(ActiveFilters.getForSjukfall(), getIcds(data.size()), rows);

        //When
        final DualSexStatisticsData result = new DiagnosisSubGroupsConverter().convert(response, new FilterSettings(Filter.empty(), Range.quarter(clock)));

        //Then
        assertEquals(7, result.getFemaleChart().getSeries().size());
        assertTrue(result.getFemaleChart().getSeries().stream().anyMatch(chartSeries -> DiagnosisSubGroupsConverter.OTHER_GROUP_NAME.equals(chartSeries.getName())));
    }

}
