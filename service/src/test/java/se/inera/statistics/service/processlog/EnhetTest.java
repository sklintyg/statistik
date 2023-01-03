/*
 * Copyright (C) 2023 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.processlog;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import se.inera.statistics.service.report.model.Kommun;

public class EnhetTest {

    @Test
    public void testGetKommunIdIsNull() throws Exception {
        //Given
        final Enhet enhet = new Enhet();
        enhet.setKommunId(null);

        //When
        final String kommunId = enhet.getKommunId();

        //Then
        assertEquals(Kommun.OVRIGT_ID.substring(2), kommunId);
    }

    @Test
    public void testGetKommunIdIsTooShort() throws Exception {
        //Given
        final Enhet enhet = new Enhet();
        enhet.setKommunId("3");

        //When
        final String kommunId = enhet.getKommunId();

        //Then
        assertEquals(Kommun.OVRIGT_ID.substring(2), kommunId);
    }

    @Test
    public void testGetKommunIdIsTwoCharsAsExpected() throws Exception {
        //Given
        final Enhet enhet = new Enhet();
        final String id = "34";
        enhet.setKommunId(id);

        //When
        final String kommunId = enhet.getKommunId();

        //Then
        assertEquals(id, kommunId);
    }

    @Test
    public void testGetKommunIdIsTooLong() throws Exception {
        //Given
        final Enhet enhet = new Enhet();
        enhet.setKommunId("345");

        //When
        final String kommunId = enhet.getKommunId();

        //Then
        assertEquals("45", kommunId);
    }

}
