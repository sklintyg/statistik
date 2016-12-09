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
package se.inera.statistics.service.helper;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import se.inera.statistics.service.JSONSource;
import se.inera.statistics.service.processlog.IntygDTO;
import se.inera.statistics.service.report.model.Kon;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static se.inera.statistics.service.helper.DocumentHelper.IntygVersion.VERSION1;
import static se.inera.statistics.service.helper.DocumentHelper.IntygVersion.VERSION2;

public class DocumentHelperTest {

    private JsonNode documentOldFormat = JSONParser.parse(JSONSource.readTemplateAsString(VERSION1));
    private JsonNode documentVersion2 = JSONParser.parse(JSONSource.readTemplateAsString(VERSION2));

    @Test
    public void prepareOld() {
        final Patientdata patientData = DocumentHelper.getPatientData(documentOldFormat);

        assertEquals(35, patientData.getAlder());
        assertEquals(Kon.MALE, patientData.getKon());
    }

    @Test
    public void prepareVersion2() {
        final Patientdata patientData = DocumentHelper.getPatientData(documentVersion2);

        assertEquals(98, patientData.getAlder());
        assertEquals(Kon.MALE, patientData.getKon());
    }

    @Test
    public void getIntygVersionOld() {
        final DocumentHelper.IntygVersion version = DocumentHelper.getIntygVersion(documentOldFormat);

        assertEquals(DocumentHelper.IntygVersion.VERSION1, version);
    }

    @Test
    public void getIntygVersionVersion2() {
        final DocumentHelper.IntygVersion version = DocumentHelper.getIntygVersion(documentVersion2);

        assertEquals(DocumentHelper.IntygVersion.VERSION2, version);
    }

    @Test
    public void getEnhetOld() {
        assertEquals("enhetId", DocumentHelper.getEnhetId(documentOldFormat, VERSION1));
    }

    @Test
    public void getEnhetVersion2() {
        assertEquals("VardenhetY", DocumentHelper.getEnhetId(documentVersion2, VERSION2));
    }

    @Test
    public void getDiagnosOld() {
        assertEquals("H81", DocumentHelper.getDiagnos(documentOldFormat, VERSION1));
    }

    @Test
    public void getDiagnosVersion2() {
        assertEquals("S47", DocumentHelper.getDiagnos(documentVersion2, VERSION2));
    }

    @Test
    public void getVardgivareIdOld() {
        assertEquals("VardgivarId", DocumentHelper.getVardgivareId(documentOldFormat, VERSION1));
    }

    @Test
    public void getVardgivareIdVersion2() {
        assertEquals("VardgivarId", DocumentHelper.getVardgivareId(documentVersion2, VERSION2));
    }

    @Test
    public void getAgeOld() {
        final int age = DocumentHelper.getPatientData(documentOldFormat).getAlder();

        assertEquals(35, age);
    }

    @Test
    public void getAgeVersion2() {
        final int age = DocumentHelper.getPatientData(documentVersion2).getAlder();

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
    public void getForstaDagOld() {
        String date = DocumentHelper.getForstaNedsattningsdag(documentOldFormat, VERSION1);

        assertEquals("2011-01-24", date);
    }

    @Test
    public void getSistaDagOld() {
        String date = DocumentHelper.getSistaNedsattningsdag(documentOldFormat, VERSION1);

        assertEquals("2011-02-20", date);
    }

    @Test
    public void getForstaDagVersion2() {
        String date = DocumentHelper.getForstaNedsattningsdag(documentVersion2, VERSION2);

        assertEquals("2011-01-26", date);
    }

    @Test
    public void getSistaDagVersion2() {
        String date = DocumentHelper.getSistaNedsattningsdag(documentVersion2, VERSION2);

        assertEquals("2011-05-31", date);
    }

    @Test
    public void getIntygIdOld() {
        assertEquals("80832895-5a9c-450a-bd74-08af43750788", DocumentHelper.getIntygId(documentOldFormat, VERSION1));
    }

    @Test
    public void getIntygIdVersion2() {
        assertEquals("80832895-5a9c-450a-bd74-08af43750788", DocumentHelper.getIntygId(documentVersion2, VERSION2));
    }

    @Test
    public void getPersonIdOldVersion() {
        String id = DocumentHelper.getPersonId(documentOldFormat, VERSION1);

        assertEquals("19750424-9215", id);
    }

    @Test
    public void getPersonIdVersion2() {
        String id = DocumentHelper.getPersonId(documentVersion2, VERSION2);

        assertEquals("19121212-1212", id);
    }

    @Test
    public void getLakarIdOld() {
        String id = DocumentHelper.getLakarId(documentOldFormat, VERSION1);

        assertEquals("Personal HSA-ID", id);
    }

    @Test
    public void getLakarIdVersion2() {
        String id = DocumentHelper.getLakarId(documentVersion2, VERSION2);

        assertEquals("Personal HSA-ID", id);
    }

    @Test
    public void getArbetsnedsattning() {
        assertEquals(50, DocumentHelper.getArbetsnedsattning(documentOldFormat, VERSION1).get(0).getNedsattning());
    }

    @Test
    public void testIsAnyFieldIndicatingEnkeltIntyg() throws Exception {
        assertTrue(DocumentHelper.isAnyFieldIndicatingEnkeltIntyg("e"));
        assertTrue(DocumentHelper.isAnyFieldIndicatingEnkeltIntyg("E"));
        assertTrue(DocumentHelper.isAnyFieldIndicatingEnkeltIntyg("Enkel"));
        assertTrue(DocumentHelper.isAnyFieldIndicatingEnkeltIntyg("ENKEL"));
        assertTrue(DocumentHelper.isAnyFieldIndicatingEnkeltIntyg("enkel"));
        assertTrue(DocumentHelper.isAnyFieldIndicatingEnkeltIntyg("Enkelt"));
        assertTrue(DocumentHelper.isAnyFieldIndicatingEnkeltIntyg("ENKELT"));
        assertTrue(DocumentHelper.isAnyFieldIndicatingEnkeltIntyg("enkelt"));

        assertTrue(DocumentHelper.isAnyFieldIndicatingEnkeltIntyg("enkøelt"));
        assertFalse(DocumentHelper.isAnyFieldIndicatingEnkeltIntyg("enköelt"));
        assertTrue(DocumentHelper.isAnyFieldIndicatingEnkeltIntyg("Ée"));
        assertFalse(DocumentHelper.isAnyFieldIndicatingEnkeltIntyg("enkla"));
        assertTrue(DocumentHelper.isAnyFieldIndicatingEnkeltIntyg("en-kel"));

        assertTrue(DocumentHelper.isAnyFieldIndicatingEnkeltIntyg("svår", "enkel"));
        assertFalse(DocumentHelper.isAnyFieldIndicatingEnkeltIntyg("svår", "medium"));
    }

    @Test
    public void testConvertToDTONull() {
        IntygDTO dto = DocumentHelper.convertToDTO(null);

        assertNull(dto);
    }

    @Test
    public void testConvertToDTOVersion2() {
        IntygDTO dto = DocumentHelper.convertToDTO(documentVersion2);

        LocalDate signeringsdatum = LocalDate.of(2011, 1, 26);

        assertEquals("19121212-1212", dto.getPatientid());
        assertEquals("VardenhetY", dto.getEnhet());
        assertEquals("FK7263", dto.getIntygtyp());
        assertEquals(98, dto.getPatientData().getAlder());
        assertEquals(Kon.MALE, dto.getPatientData().getKon());
        assertEquals(signeringsdatum, dto.getSigneringsdatum());
    }

    @Test
    public void testConvertToDTOOld() {
        IntygDTO dto = DocumentHelper.convertToDTO(documentOldFormat);

        LocalDate signeringsdatum = LocalDate.of(2011, 1, 26);

        assertEquals("19750424-9215", dto.getPatientid());
        assertEquals("enhetId", dto.getEnhet());
        assertEquals("FK7263", dto.getIntygtyp());
        assertEquals(35, dto.getPatientData().getAlder());
        assertEquals(Kon.MALE, dto.getPatientData().getKon());
        assertEquals(signeringsdatum, dto.getSigneringsdatum());
    }

    // CHECKSTYLE:ON MagicNumber
}
