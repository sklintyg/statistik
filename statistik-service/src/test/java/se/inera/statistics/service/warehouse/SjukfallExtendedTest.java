/**
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
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

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SjukfallExtendedTest {

    private int lakarintyg = 1;

    @Test
    public void testIsEnkeltIsFalseWhenContainsNoSimpleFacts() throws Exception {
        //Given
        SjukfallExtended sjukfall1 = new SjukfallExtended(createFact(false));
        SjukfallExtended sjukfall2 = new SjukfallExtended(sjukfall1, createFact(false));
        SjukfallExtended sjukfall3 = new SjukfallExtended(sjukfall2, createFact(false));

        //When
        boolean enkelt = sjukfall3.isEnkelt();

        //Then
        assertFalse(enkelt);
    }

    @Test
    public void testIsEnkeltIsTrueWhenContainingAtLeastOneSimpleFact() throws Exception {
        //Given
        SjukfallExtended sjukfall1 = new SjukfallExtended(createFact(false));
        SjukfallExtended sjukfall2 = new SjukfallExtended(sjukfall1, createFact(true));
        SjukfallExtended sjukfall3 = new SjukfallExtended(sjukfall2, createFact(false));

        //When
        boolean enkelt = sjukfall3.isEnkelt();

        //Then
        assertTrue(enkelt);
    }

    private Fact createFact(boolean enkeltIntyg) {
        return new Fact(1,1,1,1, lakarintyg++,1,1,1,1,1,1,1,1,1,1,1,1,new int[]{}, 1, enkeltIntyg);
    }

}
