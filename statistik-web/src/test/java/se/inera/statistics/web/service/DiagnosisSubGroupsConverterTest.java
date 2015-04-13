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
import se.inera.statistics.service.report.model.DiagnosgruppResponse;
import se.inera.statistics.service.report.model.Icd;
import se.inera.statistics.service.report.model.KonDataRow;
import se.inera.statistics.service.report.model.KonField;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class DiagnosisSubGroupsConverterTest {

    @Test
    public void testGetTopColumnIndexesAllAreIncluded() throws Exception {
        //Given
        ArrayList<KonDataRow> rows = new ArrayList<>();
        ArrayList<KonField> data = new ArrayList<>();
        data.add(new KonField(1, 2));
        data.add(new KonField(2, 1));
        data.add(new KonField(2, 2));
        rows.add(new KonDataRow("", data));
        DiagnosgruppResponse response = new DiagnosgruppResponse(Arrays.asList(getIcd(1), getIcd(2), getIcd(3)), rows);

        //When
        List<Integer> result = new DiagnosisSubGroupsConverter().getTopColumnIndexes(response);

        //Then
        assertEquals(3, result.size());
    }

    @Test
    public void testGetTopColumnIndexesCorrectOrder() throws Exception {
        //Given
        ArrayList<KonDataRow> rows = new ArrayList<>();
        ArrayList<KonField> data = new ArrayList<>();
        data.add(new KonField(1, 2));
        data.add(new KonField(2, 0));
        data.add(new KonField(2, 2));
        rows.add(new KonDataRow("", data));
        DiagnosgruppResponse response = new DiagnosgruppResponse(Arrays.asList(getIcd(1), getIcd(2), getIcd(3)), rows);

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
    public void testGetTopColumnIndexesRowWithZeroIsExcluded() throws Exception {
        //Given
        ArrayList<KonDataRow> rows = new ArrayList<>();
        ArrayList<KonField> data = new ArrayList<>();
        data.add(new KonField(1, 2));
        data.add(new KonField(0, 0));
        data.add(new KonField(2, 2));
        rows.add(new KonDataRow("", data));
        DiagnosgruppResponse response = new DiagnosgruppResponse(Arrays.asList(getIcd(1), getIcd(2), getIcd(3)), rows);

        //When
        List<Integer> result = new DiagnosisSubGroupsConverter().getTopColumnIndexes(response);

        //Then
        assertEquals(2, result.size());
        assertEquals(2, result.get(0).intValue());
        assertEquals(0, result.get(1).intValue());
    }

}
