/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;

public class KonDataResponseTest {

    @Test
    public void testCreateNewWithoutEmptyGroupsNullInput() {
        //Given
        final List<String> groups = null;
        final List<KonDataRow> rows = null;

        //When
        final KonDataResponse result = KonDataResponse
            .createNewWithoutEmptyGroups(AvailableFilters.getForSjukfall(), groups, rows, Collections.<String>emptyList());

        //Then
        assertTrue(result.getGroups().isEmpty());
        assertTrue(result.getRows().isEmpty());
    }

    @Test
    public void testCreateNewWithoutEmptyGroupsSingleObjectNoChange() {
        //Given
        final List<String> groups = Arrays.asList("ett");
        final List<KonDataRow> rows = Arrays.asList(new KonDataRow("KDR1", Arrays.asList(new KonField(2, 3))));

        //When
        final KonDataResponse result = KonDataResponse
            .createNewWithoutEmptyGroups(AvailableFilters.getForSjukfall(), groups, rows, Collections.<String>emptyList());

        //Then
        assertEquals(1, result.getGroups().size());
        assertEquals(1, result.getRows().size());
    }

    @Test
    public void testCreateNewWithoutEmptyGroupsEmptyGroupRemoved() {
        //Given
        final List<String> groups = Arrays.asList("ett", "empty", "two");
        final List<KonDataRow> rows = Arrays.asList(
            new KonDataRow("KDR1", Arrays.asList(new KonField(2, 3), new KonField(0, 0), new KonField(4, 5))),
            new KonDataRow("KDR2", Arrays.asList(new KonField(2, 3), new KonField(0, 0), new KonField(4, 5))),
            new KonDataRow("KDR3", Arrays.asList(new KonField(2, 3), new KonField(0, 0), new KonField(4, 5))));

        //When
        final KonDataResponse result = KonDataResponse
            .createNewWithoutEmptyGroups(AvailableFilters.getForSjukfall(), groups, rows, Collections.<String>emptyList());

        //Then
        assertEquals(2, result.getGroups().size());
        assertEquals("ett", result.getGroups().get(0));
        assertEquals("two", result.getGroups().get(1));
        assertEquals(3, result.getRows().size());
        for (KonDataRow konDataRow : result.getRows()) {
            assertEquals(2, konDataRow.getData().size());
        }
    }

    @Test
    public void testCreateNewWithoutEmptyGroupsEmptyGroupRemovedButSpecifiedEmptyGroupKept() {
        //Given
        final List<String> groups = Arrays.asList("ett", "empty", "two", "empty2");
        final List<KonDataRow> rows = Arrays.asList(
            new KonDataRow("KDR1", Arrays.asList(new KonField(2, 3), new KonField(0, 0), new KonField(4, 5), new KonField(0, 0))),
            new KonDataRow("KDR2", Arrays.asList(new KonField(2, 3), new KonField(0, 0), new KonField(4, 5), new KonField(0, 0))),
            new KonDataRow("KDR3", Arrays.asList(new KonField(2, 3), new KonField(0, 0), new KonField(4, 5), new KonField(0, 0))));

        //When
        final KonDataResponse result = KonDataResponse
            .createNewWithoutEmptyGroups(AvailableFilters.getForSjukfall(), groups, rows, Arrays.asList("empty2"));

        //Then
        assertEquals(3, result.getGroups().size());
        assertEquals("ett", result.getGroups().get(0));
        assertEquals("two", result.getGroups().get(1));
        assertEquals("empty2", result.getGroups().get(2));
        assertEquals(3, result.getRows().size());
        for (KonDataRow konDataRow : result.getRows()) {
            assertEquals(3, konDataRow.getData().size());
        }
    }

}
