/*
 * Copyright (C) 2025 Inera AB (http://www.inera.se)
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

import java.util.ArrayList;
import org.junit.Test;

public class SimpleKonResponsesTest {

    @Test
    public void testAddExtrasToNameDuplicatesEmptyInput() {
        //Given
        final ArrayList<SimpleKonDataRow> skdr = new ArrayList<>();
        final SimpleKonResponse skr = new SimpleKonResponse(AvailableFilters.getForSjukfall(), skdr);

        //When
        final SimpleKonResponse result = SimpleKonResponses.addExtrasToNameDuplicates(skr);

        //Then
        assertEquals(0, result.getGroups().size());
    }

    @Test
    public void testAddExtrasToNameDuplicates() {
        //Given
        final ArrayList<SimpleKonDataRow> skdr = new ArrayList<>();
        skdr.add(new SimpleKonDataRow("ABC", 0, 0, 1));
        skdr.add(new SimpleKonDataRow("abc", 0, 0, 2));
        skdr.add(new SimpleKonDataRow("CBA", 0, 0, 3));
        final SimpleKonResponse skr = new SimpleKonResponse(AvailableFilters.getForSjukfall(), skdr);

        //When
        final SimpleKonResponse result = SimpleKonResponses.addExtrasToNameDuplicates(skr);

        //Then
        assertEquals(3, result.getGroups().size());
        assertEquals("ABC 1", result.getGroups().get(0));
        assertEquals("abc 2", result.getGroups().get(1));
        assertEquals("CBA", result.getGroups().get(2));
    }

}
