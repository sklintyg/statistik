/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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
import se.inera.statistics.service.IntygCreatorHelper;

public class IntygEventTest {

    @Test
    public void testGetIntygFormatXml() {
        //Given
        String data = IntygCreatorHelper.getFk7263Xml();

        //When
        final IntygFormat intygFormat = IntygEvent.getIntygFormat(data);

        //Then
        assertEquals(IntygFormat.REGISTER_CERTIFICATE, intygFormat);
    }

    @Test
    public void testGetIntygFormatJson() {
        //Given
        String data = IntygCreatorHelper.getFk7263Json();

        //When
        final IntygFormat intygFormat = IntygEvent.getIntygFormat(data);

        //Then
        assertEquals(IntygFormat.REGISTER_MEDICAL_CERTIFICATE, intygFormat);
    }

    @Test
    public void testGetIntygFormatIntygNull() {
        //Given
        String data = null;

        //When
        final IntygFormat intygFormat = IntygEvent.getIntygFormat(data);

        //Then
        assertEquals(IntygFormat.REGISTER_MEDICAL_CERTIFICATE, intygFormat);
    }

    @Test
    public void testGetIntygFormatTsDiabetes() {
        //Given
        String data = IntygCreatorHelper.getTsDiabetes();

        //When
        final IntygFormat intygFormat = IntygEvent.getIntygFormat(data);

        //Then
        assertEquals(IntygFormat.REGISTER_TS_DIABETES, intygFormat);
    }

    @Test
    public void testGetIntygFormatTsDiabetesWithExtraNS_INTYGFV12378() {
        //Given
        String data = IntygCreatorHelper.getTsDiabetesWithExtraNS();

        //When
        final IntygFormat intygFormat = IntygEvent.getIntygFormat(data);

        //Then
        assertEquals(IntygFormat.REGISTER_TS_DIABETES, intygFormat);
    }

    @Test
    public void testGetIntygFormatTsBas() {
        //Given
        String data = IntygCreatorHelper.getTsBas();
        //When
        final IntygFormat intygFormat = IntygEvent.getIntygFormat(data);

        //Then
        assertEquals(IntygFormat.REGISTER_TS_BAS, intygFormat);
    }

}
