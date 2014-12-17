/**
 * Copyright (C) 2014 Inera AB (http://www.inera.se)
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
import org.joda.time.LocalDate;
import org.junit.Test;
import se.inera.statistics.service.JSONSource;
import se.inera.statistics.service.report.model.Kon;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class DocumentHelperTest {

    private JsonNode document = JSONParser.parse(JSONSource.readTemplateAsString());
    // CHECKSTYLE:OFF MagicNumber
    @Test
    public void get_enhet() {
        assertEquals("enhetId", DocumentHelper.getEnhetId(document));
    }

    @Test
    public void get_diagnos() {
        assertEquals("H81", DocumentHelper.getDiagnos(document));
    }

    @Test
    public void get_age() {
        assertEquals(35, DocumentHelper.getAge(DocumentHelper.anonymize(document)));
    }

    @Test
    public void processor_extract_alder_from_intyg() {
        String personId = "19121212-1212";
        LocalDate date = new LocalDate(0L); // 1970

        int alder = ConversionHelper.extractAlder(personId, date);

        assertEquals(57, alder);
    }

    @Test
    public void processor_extract_alder_from_intyg_with_samordningsnummer() {
        String personId = "19121272-1212";
        LocalDate date = new LocalDate(0L); // 1970

        int alder = ConversionHelper.extractAlder(personId, date);

        assertEquals(57, alder);
    }

    @Test
    public void processor_extract_alder_from_intyg_with_out_of_range_id_returns_no_age() {
        String personId = "19121312-1212";
        LocalDate date = new LocalDate(0L); // 1970

        try {
            ConversionHelper.extractAlder(personId, date);
            fail();
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void processor_extract_alder_from_intyg_with_empty_id_returns_no_age() {
        String personId = "";
        LocalDate date = new LocalDate(0L); // 1970

        try {
            ConversionHelper.extractAlder(personId, date);
            fail();
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void processor_extract_alder_from_intyg_with_errenous_id_returns_no_age() {
        String personId = "xxxxxxxx";
        LocalDate date = new LocalDate(0L); // 1970

        try {
            ConversionHelper.extractAlder(personId, date);
            fail();
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void processor_extract_kon_man_from_intyg() {
        String personId = "19121212-1212";

        String kon = ConversionHelper.extractKon(personId);

        assertEquals(Kon.Male.toString(), kon);
    }

    @Test
    public void processor_extract_kon_kvinna_from_intyg() {
        String personId = "19121212-0000";

        String kon = ConversionHelper.extractKon(personId);

        assertEquals(Kon.Female.toString(), kon);
    }

    @Test
    public void getForstaDag() {
        String date = DocumentHelper.getForstaNedsattningsdag(document);

        assertEquals("2011-01-24", date);
    }

    @Test
    public void getSistaDag() {
        String date = DocumentHelper.getSistaNedsattningsdag(document);

        assertEquals("2011-02-20", date);
    }

    @Test
    public void getIntygId() {
        assertEquals("80832895-5a9c-450a-bd74-08af43750788", DocumentHelper.getIntygId(document));
    }


    @Test
    public void getArbetsformaga() {
        assertEquals(0, DocumentHelper.getArbetsformaga(document).get(0).intValue());
    }

    // CHECKSTYLE:ON MagicNumber
}
