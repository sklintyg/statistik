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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class SimpleKonResponseTest {

    @Test
    public void testCreateWithNullInput() throws Exception {
        //Given
        final KonDataResponse diagnosgruppResponse = null;
        final int numberOfMonthsCalculated = 0;

        //When
        final SimpleKonResponse<SimpleKonDataRow> result = SimpleKonResponse.create(diagnosgruppResponse, numberOfMonthsCalculated);

        //Then
        assertEquals(0, result.getGroups().size());
        assertEquals(0, result.getRows().size());
    }

    @Test
    public void testCreateWithEmptyInput() throws Exception {
        //Given
        final KonDataResponse diagnosgruppResponse = new KonDataResponse(new ArrayList<String>(), new ArrayList<KonDataRow>());
        final int numberOfMonthsCalculated = 0;

        //When
        final SimpleKonResponse<SimpleKonDataRow> result = SimpleKonResponse.create(diagnosgruppResponse, numberOfMonthsCalculated);

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
        final int numberOfMonthsCalculated = 0;

        //When
        final SimpleKonResponse<SimpleKonDataRow> result = SimpleKonResponse.create(diagnosgruppResponse, numberOfMonthsCalculated);

        //Then
        assertEquals(1, result.getGroups().size());
        assertEquals("Group1", result.getGroups().get(0));
        assertEquals(1, result.getRows().size());
        assertEquals("Group1", result.getRows().get(0).getName());
        assertEquals(1+3, result.getRows().get(0).getData().getFemale());
        assertEquals(2+4, result.getRows().get(0).getData().getMale());
    }

}
