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
package se.inera.statistik.tools.anonymisering.base


import org.apache.commons.io.FileUtils
import org.junit.Test
import org.springframework.core.io.ClassPathResource

import static groovy.test.GroovyTestCase.assertEquals

class AnonymiseraXmlTest {

    AnonymiseraDatum anonymiseraDatum = new AnonymiseraDatum()

    AnonymiseraXmlTest() {
        anonymiseraDatum.random = [nextInt: { (AnonymiseraDatum.DATE_RANGE / 2) + 1 }] as Random
    }

    @Test
    void testaAnonymiseringAvFk7263sit() {
        //Given
        AnonymiseraHsaId anonymiseraHsaId = [anonymisera: { "SE833377567" }] as AnonymiseraHsaId
        AnonymiseraDatum anonymiseraDatum = [anonymiseraDatum: { "2015-11-28" }] as AnonymiseraDatum
        AnonymiseraPersonId anonymiseraPersonId = [anonymisera: { "19680418-1749" }] as AnonymiseraPersonId
        AnonymiseraXml anonymiseraXml = new AnonymiseraXml(anonymiseraPersonId, anonymiseraHsaId, anonymiseraDatum)

        String expected = FileUtils.readFileToString(new ClassPathResource("/fk7263sit_anonymized.xml").getFile(), "UTF-8")

        //When
        String document = FileUtils.readFileToString(new ClassPathResource("/fk7263sit_not_anonymized.xml").getFile(), "UTF-8")
        String actual = anonymiseraXml.anonymiseraIntygsXml(document)

        //Then
        assertEquals(expected.replaceAll("\\s", ""), actual.replaceAll("\\s", ""));
    }

    @Test
    void testaAnonymiseringAvLisjp() {
        //Given
        AnonymiseraHsaId anonymiseraHsaId = [anonymisera: { "SE833377567" }] as AnonymiseraHsaId
        AnonymiseraDatum anonymiseraDatum = [anonymiseraDatum: { "2015-11-28" }] as AnonymiseraDatum
        AnonymiseraPersonId anonymiseraPersonId = [anonymisera: { "19670827-2041" }] as AnonymiseraPersonId
        AnonymiseraXml anonymiseraXml = new AnonymiseraXml(anonymiseraPersonId, anonymiseraHsaId, anonymiseraDatum)

        String expected = FileUtils.readFileToString(new ClassPathResource("/lisjp_anonymized.xml").getFile(), "UTF-8")

        //When
        String document = FileUtils.readFileToString(new ClassPathResource("/lisjp_not_anonymized.xml").getFile(), "UTF-8")
        String actual = anonymiseraXml.anonymiseraIntygsXml(document)

        //Then
        assertEquals(expected.replaceAll("\\s", ""), actual.replaceAll("\\s", ""));
    }

    @Test
    void testaAnonymiseringAvLuaeFs() {
        //Given
        AnonymiseraHsaId anonymiseraHsaId = [anonymisera: { "SE833377567" }] as AnonymiseraHsaId
        AnonymiseraDatum anonymiseraDatum = [anonymiseraDatum: { "2015-11-28" }] as AnonymiseraDatum
        AnonymiseraPersonId anonymiseraPersonId = [anonymisera: { "19680803-3000" }] as AnonymiseraPersonId
        AnonymiseraXml anonymiseraXml = new AnonymiseraXml(anonymiseraPersonId, anonymiseraHsaId, anonymiseraDatum)

        String expected = FileUtils.readFileToString(new ClassPathResource("/luae_fs_anonymized.xml").getFile(), "UTF-8")

        //When
        String document = FileUtils.readFileToString(new ClassPathResource("/luae_fs_not_anonymized.xml").getFile(), "UTF-8")
        String actual = anonymiseraXml.anonymiseraIntygsXml(document)

        //Then
        assertEquals(expected.replaceAll("\\s", ""), actual.replaceAll("\\s", ""));
    }

    @Test
    void testaAnonymiseringAvLuaeNa() {
        //Given
        AnonymiseraHsaId anonymiseraHsaId = [anonymisera: { "SE833377567" }] as AnonymiseraHsaId
        AnonymiseraDatum anonymiseraDatum = [anonymiseraDatum: { "2015-11-28" }] as AnonymiseraDatum
        AnonymiseraPersonId anonymiseraPersonId = [anonymisera: { "19690316-5565" }] as AnonymiseraPersonId
        AnonymiseraXml anonymiseraXml = new AnonymiseraXml(anonymiseraPersonId, anonymiseraHsaId, anonymiseraDatum)

        String expected = FileUtils.readFileToString(new ClassPathResource("/luae_na_anonymized.xml").getFile(), "UTF-8")

        //When
        String document = FileUtils.readFileToString(new ClassPathResource("/luae_na_not_anonymized.xml").getFile(), "UTF-8")
        String actual = anonymiseraXml.anonymiseraIntygsXml(document)

        //Then
        assertEquals(expected.replaceAll("\\s", ""), actual.replaceAll("\\s", ""));
    }

    @Test
    void testaAnonymiseringAvLuse() {
        //Given
        AnonymiseraHsaId anonymiseraHsaId = [anonymisera: { "SE833377567" }] as AnonymiseraHsaId
        AnonymiseraDatum anonymiseraDatum = [anonymiseraDatum: { "2015-11-28" }] as AnonymiseraDatum
        AnonymiseraPersonId anonymiseraPersonId = [anonymisera: { "19680725-8820" }] as AnonymiseraPersonId
        AnonymiseraXml anonymiseraXml = new AnonymiseraXml(anonymiseraPersonId, anonymiseraHsaId, anonymiseraDatum)

        String expected = FileUtils.readFileToString(new ClassPathResource("/luse_anonymized.xml").getFile(), "UTF-8")

        //When
        String document = FileUtils.readFileToString(new ClassPathResource("/luse_not_anonymized.xml").getFile(), "UTF-8")
        String actual = anonymiseraXml.anonymiseraIntygsXml(document)

        //Then
        assertEquals(expected.replaceAll("\\s", ""), actual.replaceAll("\\s", ""));
    }

    @Test
    void testaAnonymiseringAvDb() {
        //Given
        AnonymiseraHsaId anonymiseraHsaId = [anonymisera: { "SE833377567" }] as AnonymiseraHsaId
        AnonymiseraDatum anonymiseraDatum = [anonymiseraDatum: { "2015-11-28" }] as AnonymiseraDatum
        AnonymiseraPersonId anonymiseraPersonId = [anonymisera: { "19680725-8820" }] as AnonymiseraPersonId
        AnonymiseraXml anonymiseraXml = new AnonymiseraXml(anonymiseraPersonId, anonymiseraHsaId, anonymiseraDatum)

        String expected = FileUtils.readFileToString(new ClassPathResource("/db_anonymized.xml").getFile(), "UTF-8")

        //When
        String document = FileUtils.readFileToString(new ClassPathResource("/db_not_anonymized.xml").getFile(), "UTF-8")
        String actual = anonymiseraXml.anonymiseraIntygsXml(document)

        //Then
        assertEquals(expected.replaceAll("\\s", ""), actual.replaceAll("\\s", ""));
    }

    @Test
    void testaAnonymiseringAvDoi() {
        //Given
        AnonymiseraHsaId anonymiseraHsaId = [anonymisera: { "SE833377567" }] as AnonymiseraHsaId
        AnonymiseraDatum anonymiseraDatum = [anonymiseraDatum: { "2015-11-28" }] as AnonymiseraDatum
        AnonymiseraPersonId anonymiseraPersonId = [anonymisera: { "19680725-8820" }] as AnonymiseraPersonId
        AnonymiseraXml anonymiseraXml = new AnonymiseraXml(anonymiseraPersonId, anonymiseraHsaId, anonymiseraDatum)

        String expected = FileUtils.readFileToString(new ClassPathResource("/doi_anonymized.xml").getFile(), "UTF-8")

        //When
        String document = FileUtils.readFileToString(new ClassPathResource("/doi_not_anonymized.xml").getFile(), "UTF-8")
        String actual = anonymiseraXml.anonymiseraIntygsXml(document)

        //Then
        assertEquals(expected.replaceAll("\\s", ""), actual.replaceAll("\\s", ""));
    }

    @Test
    void testaAnonymiseringAvAf00213() {
        //Given
        AnonymiseraHsaId anonymiseraHsaId = [anonymisera: { "SE833377567" }] as AnonymiseraHsaId
        AnonymiseraDatum anonymiseraDatum = [anonymiseraDatum: { "2015-11-28" }] as AnonymiseraDatum
        AnonymiseraPersonId anonymiseraPersonId = [anonymisera: { "19680725-8820" }] as AnonymiseraPersonId
        AnonymiseraXml anonymiseraXml = new AnonymiseraXml(anonymiseraPersonId, anonymiseraHsaId, anonymiseraDatum)

        String expected = FileUtils.readFileToString(new ClassPathResource("/af00213_anonymized.xml").getFile(), "UTF-8")

        //When
        String document = FileUtils.readFileToString(new ClassPathResource("/af00213_not_anonymized.xml").getFile(), "UTF-8")
        String actual = anonymiseraXml.anonymiseraIntygsXml(document)

        //Then
        assertEquals(expected.replaceAll("\\s", ""), actual.replaceAll("\\s", ""));
    }

    @Test
    void testaAnonymiseringAvTstrk1007() {
        //Given
        AnonymiseraHsaId anonymiseraHsaId = [anonymisera: { "SE833377567" }] as AnonymiseraHsaId
        AnonymiseraDatum anonymiseraDatum = [anonymiseraDatum: { "2015-11-28" }] as AnonymiseraDatum
        AnonymiseraPersonId anonymiseraPersonId = [anonymisera: { "19680725-8820" }] as AnonymiseraPersonId
        AnonymiseraXml anonymiseraXml = new AnonymiseraXml(anonymiseraPersonId, anonymiseraHsaId, anonymiseraDatum)

        String expected = FileUtils.readFileToString(new ClassPathResource("/tstrk1007_anonymized.xml").getFile(), "UTF-8")

        //When
        String document = FileUtils.readFileToString(new ClassPathResource("/tstrk1007_not_anonymized.xml").getFile(), "UTF-8")
        String actual = anonymiseraXml.anonymiseraIntygsXml(document)

        //Then
        assertEquals(expected.replaceAll("\\s", ""), actual.replaceAll("\\s", ""));
    }

    @Test
    void testaAnonymiseringAvSendMessageToCare() {
        //Given
        AnonymiseraPersonId anonymiseraPersonId = [anonymisera: { "19810803-3022" }] as AnonymiseraPersonId
        AnonymiseraXml anonymiseraXml = new AnonymiseraXml(anonymiseraPersonId, null, null)

        String expected = FileUtils.readFileToString(new ClassPathResource("/sendmessagetocare_anonymized.xml").getFile(), "UTF-8")

        //When
        String document = FileUtils.readFileToString(new ClassPathResource("/sendmessagetocare_not_anonymized.xml").getFile(), "UTF-8")
        String actual = anonymiseraXml.anonymiseraMeddelandeXml(document, "SENT")

        //Then
        assertEquals(expected.replaceAll("\\s", ""), actual.replaceAll("\\s", ""));
    }

    @Test
    void testaAnonymiseringAvSendMessageToCare2() {
        //Given
        AnonymiseraPersonId anonymiseraPersonId = [anonymisera: { "19121212-1212" }] as AnonymiseraPersonId
        AnonymiseraXml anonymiseraXml = new AnonymiseraXml(anonymiseraPersonId, null, null)

        String expected = FileUtils.readFileToString(new ClassPathResource("/sendmessagetocare_anonymized2.xml").getFile(), "UTF-8")

        //When
        String document = FileUtils.readFileToString(new ClassPathResource("/sendmessagetocare_not_anonymized2.xml").getFile(), "UTF-8")
        String actual = anonymiseraXml.anonymiseraMeddelandeXml(document, "SENT")

        //Then
        assertEquals(expected.replaceAll("\\s", ""), actual.replaceAll("\\s", ""));
    }
}