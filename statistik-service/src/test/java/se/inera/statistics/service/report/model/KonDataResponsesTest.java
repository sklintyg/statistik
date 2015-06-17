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
import se.inera.statistics.hsa.model.HsaId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class KonDataResponsesTest {

    @Test
    public void testChangeIdGroupsToNamesAndAddIdsToDuplicatesWithEmptyInput() throws Exception {
        //Given
        final ArrayList<String> groups = new ArrayList<>();
        final ArrayList<KonDataRow> rows = new ArrayList<>();
        final KonDataResponse response = new KonDataResponse(groups, rows);
        final HashMap<HsaId, String> idsToNames = new HashMap<>();

        //When
        final KonDataResponse result = KonDataResponses.changeIdGroupsToNamesAndAddIdsToDuplicates(response, idsToNames);

        //Then
        assertEquals(0, result.getGroups().size());
        assertEquals(rows, result.getRows());
    }

    @Test
    public void testChangeIdGroupsToNamesAndAddIdsToDuplicatesWithInputWithDuplicates() throws Exception {
        //Given
        final List<String> groups = Arrays.asList("1", "2", "3");
        final List<KonDataRow> rows = new ArrayList<>();
        final KonDataResponse response = new KonDataResponse(groups, rows);
        final HashMap<HsaId, String> idsToNames = new HashMap<>();
        idsToNames.put(new HsaId("1"), "abc");
        idsToNames.put(new HsaId("2"), "Abc");
        idsToNames.put(new HsaId("3"), "Abcd");

        //When
        final KonDataResponse result = KonDataResponses.changeIdGroupsToNamesAndAddIdsToDuplicates(response, idsToNames);

        //Then
        assertEquals(3, result.getGroups().size());
        assertEquals("abc 1", result.getGroups().get(0));
        assertEquals("Abc 2", result.getGroups().get(1));
        assertEquals("Abcd", result.getGroups().get(2));
    }

}
