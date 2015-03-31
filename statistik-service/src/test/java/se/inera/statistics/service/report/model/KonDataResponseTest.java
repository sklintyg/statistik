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
package se.inera.statistics.service.report.model;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class KonDataResponseTest {

    @Test
    public void testCreateNewWithoutEmptyGroupsNullInput() throws Exception {
        //Given
        final List<String> groups = null;
        final List<KonDataRow> rows = null;

        //When
        final KonDataResponse result = KonDataResponse.createNewWithoutEmptyGroups(groups, rows);

        //Then
        assertTrue(result.getGroups().isEmpty());
        assertTrue(result.getRows().isEmpty());
    }

    @Test
    public void testCreateNewWithoutEmptyGroupsSingleObjectNoChange() throws Exception {
        //Given
        final List<String> groups = Arrays.asList("ett");
        final List<KonDataRow> rows = Arrays.asList(new KonDataRow("KDR1", Arrays.asList(new KonField(2, 3))));

        //When
        final KonDataResponse result = KonDataResponse.createNewWithoutEmptyGroups(groups, rows);

        //Then
        assertEquals(1, result.getGroups().size());
        assertEquals(1, result.getRows().size());
    }

    @Test
    public void testCreateNewWithoutEmptyGroupsEmptyGroupRemoved() throws Exception {
        //Given
        final List<String> groups = Arrays.asList("ett", "empty", "two");
        final List<KonDataRow> rows = Arrays.asList(
                new KonDataRow("KDR1", Arrays.asList(new KonField(2, 3), new KonField(0, 0), new KonField(4, 5))),
                new KonDataRow("KDR2", Arrays.asList(new KonField(2, 3), new KonField(0, 0), new KonField(4, 5))),
                new KonDataRow("KDR3", Arrays.asList(new KonField(2, 3), new KonField(0, 0), new KonField(4, 5))));

        //When
        final KonDataResponse result = KonDataResponse.createNewWithoutEmptyGroups(groups, rows);

        //Then
        assertEquals(2, result.getGroups().size());
        assertEquals("ett", result.getGroups().get(0));
        assertEquals("two", result.getGroups().get(1));
        assertEquals(3, result.getRows().size());
        for (KonDataRow konDataRow : result.getRows()) {
            assertEquals(2, konDataRow.getData().size());
        }
    }


}
