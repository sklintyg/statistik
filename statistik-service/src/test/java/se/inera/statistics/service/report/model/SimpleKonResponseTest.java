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
package se.inera.statistics.service.report.model;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class SimpleKonResponseTest {

    @Test
    public void testCreateWithNullInput() throws Exception {
        //Given
        final KonDataResponse diagnosgruppResponse = null;

        //When
        final SimpleKonResponse result = SimpleKonResponse.create(diagnosgruppResponse);

        //Then
        assertEquals(0, result.getGroups().size());
        assertEquals(0, result.getRows().size());
    }

    @Test
    public void testCreateWithEmptyInput() throws Exception {
        //Given
        final KonDataResponse diagnosgruppResponse = new KonDataResponse(new ArrayList<String>(), new ArrayList<KonDataRow>());

        //When
        final SimpleKonResponse result = SimpleKonResponse.create(diagnosgruppResponse);

        //Then
        assertEquals(0, result.getGroups().size());
        assertEquals(0, result.getRows().size());
    }

    @Test
    public void testCreateGroupDataSummed() throws Exception {
        //Given
        final List<String> groups = Arrays.asList("Group1");
        final List<KonDataRow> rows = Arrays.asList(new KonDataRow("rowname1", Arrays.asList(new KonField(1, 2))), new KonDataRow("rowname2", Arrays.asList(new KonField(3, 4))));
        final KonDataResponse diagnosgruppResponse = new KonDataResponse(groups, rows);

        //When
        final SimpleKonResponse result = SimpleKonResponse.create(diagnosgruppResponse);

        //Then
        assertEquals(1, result.getGroups().size());
        assertEquals("Group1", result.getGroups().get(0));
        assertEquals(1, result.getRows().size());
        assertEquals("Group1", result.getRows().get(0).getName());
        assertEquals(1+3, result.getRows().get(0).getData().getFemale());
        assertEquals(2+4, result.getRows().get(0).getData().getMale());
    }

    @Test
    public void testMerge() throws Exception {
        //Given
        final ArrayList<SimpleKonResponse> responses = new ArrayList<>();
        responses.add(new SimpleKonResponse(Arrays.asList(new SimpleKonDataRow("First", 1, 2))));
        responses.add(new SimpleKonResponse(Arrays.asList(new SimpleKonDataRow("Second", 10, 20))));

        //When
        final SimpleKonResponse mergedResp = SimpleKonResponse.merge(responses, false);

        //Then
        assertEquals(2, mergedResp.getGroups().size());

        assertEquals("First", mergedResp.getGroups().get(0));
        assertEquals("Second", mergedResp.getGroups().get(1));

        assertEquals(1, mergedResp.getRows().get(0).getFemale());
        assertEquals(2, mergedResp.getRows().get(0).getMale());

        assertEquals(10, mergedResp.getRows().get(1).getFemale());
        assertEquals(20, mergedResp.getRows().get(1).getMale());
    }

    @Test
    public void testMergeWithEqualGroupsWithMergeEqualRowsFlag() throws Exception {
        //Given
        final ArrayList<SimpleKonResponse> responses = new ArrayList<>();
        responses.add(new SimpleKonResponse(Arrays.asList(new SimpleKonDataRow("First", 1, 2))));
        responses.add(new SimpleKonResponse(Arrays.asList(new SimpleKonDataRow("Second", 10, 20))));
        responses.add(new SimpleKonResponse(Arrays.asList(new SimpleKonDataRow("First", 1, 2))));
        responses.add(new SimpleKonResponse(Arrays.asList(new SimpleKonDataRow("Second", 10, 20))));

        //When
        final SimpleKonResponse mergedResp = SimpleKonResponse.merge(responses, true);

        //Then
        assertEquals(2, mergedResp.getGroups().size());

        assertEquals("First", mergedResp.getGroups().get(0));
        assertEquals("Second", mergedResp.getGroups().get(1));

        assertEquals(2, mergedResp.getRows().get(0).getFemale());
        assertEquals(4, mergedResp.getRows().get(0).getMale());

        assertEquals(20, mergedResp.getRows().get(1).getFemale());
        assertEquals(40, mergedResp.getRows().get(1).getMale());
    }

    @Test
    public void testMergeWithEqualGroupsWithoutMergeEqualRowsFlag() throws Exception {
        //Given
        final ArrayList<SimpleKonResponse> responses = new ArrayList<>();
        responses.add(new SimpleKonResponse(Arrays.asList(new SimpleKonDataRow("First", 1, 2))));
        responses.add(new SimpleKonResponse(Arrays.asList(new SimpleKonDataRow("Second", 10, 20))));
        responses.add(new SimpleKonResponse(Arrays.asList(new SimpleKonDataRow("First", 1, 2))));
        responses.add(new SimpleKonResponse(Arrays.asList(new SimpleKonDataRow("Second", 10, 20))));

        //When
        final SimpleKonResponse mergedResp = SimpleKonResponse.merge(responses, false);

        //Then
        assertEquals(4, mergedResp.getGroups().size());

        assertEquals("First", mergedResp.getGroups().get(0));
        assertEquals("Second", mergedResp.getGroups().get(1));
        assertEquals("First", mergedResp.getGroups().get(2));
        assertEquals("Second", mergedResp.getGroups().get(3));

        assertEquals(1, mergedResp.getRows().get(0).getFemale());
        assertEquals(2, mergedResp.getRows().get(0).getMale());

        assertEquals(10, mergedResp.getRows().get(1).getFemale());
        assertEquals(20, mergedResp.getRows().get(1).getMale());

        assertEquals(1, mergedResp.getRows().get(2).getFemale());
        assertEquals(2, mergedResp.getRows().get(2).getMale());

        assertEquals(10, mergedResp.getRows().get(3).getFemale());
        assertEquals(20, mergedResp.getRows().get(3).getMale());
    }

}
