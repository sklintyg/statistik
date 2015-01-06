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
package se.inera.statistics.service.warehouse;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Test;
import se.inera.statistics.service.report.model.Kon;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class SjukfallTest {

    @Test
    public void testConstructorNewSjukfall() throws Exception {
        //When
        Sjukfall result = new Sjukfall(new Fact(1, 1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,new int[]{1},1));

        //Then
        assertEquals(1, result.getAlder());
        assertEquals(1, result.getDiagnosavsnitt());
        assertEquals(1, result.getDiagnoskapitel());
        assertEquals(1, result.getDiagnoskategori());
        assertEquals(1, result.getEnd());
        assertEquals(1, result.getIntygCount());
        assertEquals(1, result.getRealDays());
        assertEquals(1, result.getSjukskrivningsgrad());
        assertEquals(1, result.getStart());
        assertEquals(Kon.Male, result.getKon());
        assertArrayEquals(new Object[]{1}, Lists.transform(new ArrayList<>(result.getLakare()), new Function<Lakare, Integer>() {
            @Override
            public Integer apply(Lakare lakare) {
                return lakare.getId();
            }
        }).toArray());

        assertEquals("01", result.getLanskod());
        assertEquals(false, result.isExtended());
    }

    @Test
    public void testConstructorExtendSjukfall() throws Exception {
        //Given
        Sjukfall sjukfall = new Sjukfall(new Fact(1, 1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,new int[]{1},1));

        //When
        Sjukfall result = new Sjukfall(sjukfall, new Fact(2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, new int[]{2}, 2));

        //Then
        assertEquals(2, result.getAlder());
        assertEquals(2, result.getDiagnosavsnitt());
        assertEquals(2, result.getDiagnoskapitel());
        assertEquals(2, result.getDiagnoskategori());
        assertEquals(3, result.getEnd());
        assertEquals(2, result.getIntygCount());
        assertEquals(3, result.getRealDays());
        assertEquals(2, result.getSjukskrivningsgrad());
        assertEquals(1, result.getStart());
        assertEquals(Kon.byNumberRepresentation(2), result.getKon());
        final List<Integer> lakare = Lists.transform(new ArrayList<>(result.getLakare()), new Function<Lakare, Integer>() {
            @Override
            public Integer apply(Lakare lakare) {
                return lakare.getId();
            }
        });
        assertEquals(2, lakare.size());
        assertTrue(lakare.contains(1));
        assertTrue(lakare.contains(2));
        assertEquals("02", result.getLanskod());
        assertEquals(true, result.isExtended());
    }

}
