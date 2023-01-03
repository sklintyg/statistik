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
package se.inera.statistics.service.helper.certificate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.LocalDate;
import org.junit.Test;
import se.inera.statistics.service.JSONSource;
import se.inera.statistics.service.helper.ConversionHelper;
import se.inera.statistics.service.helper.JSONParser;
import se.inera.statistics.service.helper.Patientdata;
import se.inera.statistics.service.processlog.IntygDTO;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.warehouse.IntygType;

public class JsonDocumentHelperTest {

    private JsonNode document = JSONParser.parse(JSONSource.readTemplateAsString());

    @Test
    public void prepare() {
        final Patientdata patientData = JsonDocumentHelper.getPatientData(document);

        assertEquals(98, patientData.getAlder());
        assertEquals(Kon.MALE, patientData.getKon());
    }

    @Test
    public void getEnhet() {
        assertEquals("VardenhetY", JsonDocumentHelper.getEnhetId(document));
    }

    @Test
    public void getDiagnos() {
        assertEquals("S47", JsonDocumentHelper.getDiagnos(document));
    }

    @Test
    public void getVardgivareId() {
        assertEquals("VardgivarId", JsonDocumentHelper.getVardgivareId(document));
    }

    @Test
    public void getAge() {
        final int age = JsonDocumentHelper.getPatientData(document).getAlder();

        assertEquals(98, age);
    }

    @Test
    public void processorExtractAlderFromIntyg() {
        String personId = "19121212-1212";
        LocalDate date = LocalDate.ofEpochDay(0L); // 1970

        int alder = ConversionHelper.extractAlder(personId, date);

        assertEquals(57, alder);
    }

    @Test
    public void processorExtractAlderFromIntygWithSamordningsnummer() {
        String personId = "19121272-1212";
        LocalDate date = LocalDate.ofEpochDay(0L); // 1970

        int alder = ConversionHelper.extractAlder(personId, date);

        assertEquals(57, alder);
    }

    @Test(expected = IllegalArgumentException.class)
    public void processorExtractAlderFromIntygWithOutOfRangeIdReturnsNoAge() {
        String personId = "19121312-1212";
        LocalDate date = LocalDate.ofEpochDay(0L); // 1970

        ConversionHelper.extractAlder(personId, date);
        fail();
    }

    @Test(expected = IllegalArgumentException.class)
    public void processorExtractAlderFromIntygWithEmptyIdReturnsNoAge() {
        String personId = "";
        LocalDate date = LocalDate.ofEpochDay(0L); // 1970

        ConversionHelper.extractAlder(personId, date);
        fail();
    }

    @Test(expected = IllegalArgumentException.class)
    public void processorExtractAlderFromIntygWithErrenousIdReturnsNoAge() {
        String personId = "xxxxxxxx";
        LocalDate date = LocalDate.ofEpochDay(0L); // 1970

        ConversionHelper.extractAlder(personId, date);
        fail();
    }

    @Test
    public void processorExtractKonManFromIntyg() {
        String personId = "19121212-1212";

        String kon = ConversionHelper.extractKon(personId);

        assertEquals(Kon.MALE.toString(), kon);
    }

    @Test
    public void processorExtractKonKvinnaFromIntyg() {
        String personId = "19790104-9283";

        String kon = ConversionHelper.extractKon(personId);

        assertEquals(Kon.FEMALE.toString(), kon);
    }

    @Test
    public void getForstaDag() {
        String date = JsonDocumentHelper.getForstaNedsattningsdag(document);

        assertEquals("2011-01-26", date);
    }

    @Test
    public void getSistaDag() {
        String date = JsonDocumentHelper.getSistaNedsattningsdag(document);

        assertEquals("2011-05-31", date);
    }

    @Test
    public void getIntygId() {
        assertEquals("80832895-5a9c-450a-bd74-08af43750788", JsonDocumentHelper.getIntygId(document));
    }

    @Test
    public void getPersonId() {
        String id = JsonDocumentHelper.getPersonId(document);

        assertEquals("19121212-1212", id);
    }

    @Test
    public void getLakarId() {
        String id = JsonDocumentHelper.getLakarId(document);

        assertEquals("Personal HSA-ID", id);
    }

    @Test
    public void testConvertToDTONull() {
        IntygDTO dto = JsonDocumentHelper.convertToDTO(null);

        assertNull(dto);
    }

    @Test
    public void testConvertToDTO() {
        IntygDTO dto = JsonDocumentHelper.convertToDTO(document);

        LocalDate signeringsdatum = LocalDate.of(2011, 1, 26);

        assertEquals("19121212-1212", dto.getPatientid());
        assertEquals("VardenhetY", dto.getEnhet());
        assertEquals(IntygType.getByItIntygType("FK7263"), dto.getIntygtyp());
        assertEquals(98, dto.getPatientData().getAlder());
        assertEquals(Kon.MALE, dto.getPatientData().getKon());
        assertEquals(signeringsdatum, dto.getSigneringsdatum());
    }

    // CHECKSTYLE:ON MagicNumber
}
