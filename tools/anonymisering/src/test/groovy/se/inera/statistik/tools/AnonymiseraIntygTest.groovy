/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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
package se.inera.statistik.tools

import org.apache.commons.io.FileUtils
import org.junit.Test
import org.skyscreamer.jsonassert.JSONAssert
import org.springframework.core.io.ClassPathResource
import se.inera.statistik.tools.anonymisering.base.AnonymiseraDatum
import se.inera.statistik.tools.anonymisering.base.AnonymiseraHsaId
import se.inera.statistik.tools.anonymisering.base.AnonymiseraJson
import se.inera.statistik.tools.anonymisering.base.AnonymiseraPersonId
import se.inera.statistik.tools.anonymisering.base.AnonymiseraTsBas
import se.inera.statistik.tools.anonymisering.base.AnonymiseraTsDiabetes
import se.inera.statistik.tools.anonymisering.base.AnonymiseraXml

import static groovy.util.GroovyTestCase.assertEquals

class AnonymiseraIntygTest {

    private AnonymiseraIntyg anonymiseraIntyg;

    AnonymiseraIntygTest() {
        AnonymiseraHsaId anonymiseraHsaId = [anonymisera:{"SE833377567"}] as AnonymiseraHsaId
        AnonymiseraDatum anonymiseraDatum = [anonymiseraDatum:{"2015-11-28"}] as AnonymiseraDatum
        AnonymiseraPersonId anonymiseraPersonId = [anonymisera:{"19680725-8820"}] as AnonymiseraPersonId

        AnonymiseraHsaId anonymiseraHsaIdJson = [anonymisera:{"SE1010"}] as AnonymiseraHsaId
        AnonymiseraDatum anonymiseraDatumJson = new AnonymiseraDatum()
        anonymiseraDatumJson.random = [nextInt: {(AnonymiseraDatum.DATE_RANGE/2)+1}] as Random
        AnonymiseraPersonId anonymiseraPersonIdJson = [anonymisera:{"10101010-2010"}] as AnonymiseraPersonId

        AnonymiseraJson anonymiseraJson = new AnonymiseraJson(anonymiseraHsaIdJson, anonymiseraDatumJson, anonymiseraPersonIdJson)
        AnonymiseraXml anonymiseraXml = new AnonymiseraXml(anonymiseraPersonId, anonymiseraHsaId, anonymiseraDatum)
        AnonymiseraTsBas anonymiseraTsBas = new AnonymiseraTsBas(anonymiseraPersonId, anonymiseraHsaId, anonymiseraDatum)
        AnonymiseraTsDiabetes anonymiseraTsDiabetes = new AnonymiseraTsDiabetes(anonymiseraPersonId, anonymiseraHsaId, anonymiseraDatum)

        anonymiseraIntyg = new AnonymiseraIntyg(null, anonymiseraJson, anonymiseraXml, anonymiseraTsBas, anonymiseraTsDiabetes)
    }

    @Test
    void testaAnonymiseringIntyg() {
        def intygSupported = ['db', 'doi', 'luse', 'af00213', 'tstrk1007', 'ts-bas', 'ts-diabetes']

        intygSupported.forEach({

            String intygTyp = it
            String expected = FileUtils.readFileToString(new ClassPathResource("/" + intygTyp + "_anonymized.xml").getFile(), "UTF-8")

            //When
            String document = FileUtils.readFileToString(new ClassPathResource("/" + intygTyp + "_not_anonymized.xml").getFile(), "UTF-8")
            String actual = anonymiseraIntyg.anonymizeIntyg(document)

            //Then
            assertEquals("Intygstyp: " + intygTyp, expected.replaceAll("\\s", ""), actual.replaceAll("\\s", ""));

        })
    }

    @Test
    void testaAnonymiseringIntygJson() {
        def intygSupported = ['fk7263_L']

        intygSupported.forEach({

            String intygTyp = it
            String expected = FileUtils.readFileToString(new ClassPathResource("/" + intygTyp + "_anonymized.json").getFile(), "UTF-8")

            //When
            String document = FileUtils.readFileToString(new ClassPathResource("/" + intygTyp + "_not_anonymized.json").getFile(), "UTF-8")
            String actual = anonymiseraIntyg.anonymizeIntyg(document)

            //Then
            JSONAssert.assertEquals(expected, actual, true)

        })
    }

}
