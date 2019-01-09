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
package se.inera.statistik.tools.anonymisering.base


import org.apache.commons.io.FileUtils
import org.junit.Test
import org.springframework.core.io.ClassPathResource

import static groovy.util.GroovyTestCase.assertEquals

class AnonymiseraTsDiabetesTest {

    AnonymiseraDatum anonymiseraDatum = new AnonymiseraDatum()

    AnonymiseraTsDiabetesTest() {
        anonymiseraDatum.random = [nextInt: {(AnonymiseraDatum.DATE_RANGE/2)+1}] as Random
    }

    @Test
    void testaAnonymiseringAvTsDiabetes() {
        //Given
        AnonymiseraHsaId anonymiseraHsaId = [anonymisera:{"SE833377567"}] as AnonymiseraHsaId
        AnonymiseraDatum anonymiseraDatum = [anonymiseraDatum:{"2015-11-28"}] as AnonymiseraDatum
        AnonymiseraPersonId anonymiseraPersonId = [anonymisera:{"19680725-8820"}] as AnonymiseraPersonId
        AnonymiseraTsDiabetes anonymiseraTsDiabetes = new AnonymiseraTsDiabetes(anonymiseraPersonId, anonymiseraHsaId, anonymiseraDatum)

        String expected = FileUtils.readFileToString(new ClassPathResource("/ts-diabetes_anonymized.xml").getFile(), "UTF-8")

        //When
        String document = FileUtils.readFileToString(new ClassPathResource("/ts-diabetes_not_anonymized.xml").getFile(), "UTF-8")
        String actual = anonymiseraTsDiabetes.anonymisera(document)

        //Then
        assertEquals(expected.replaceAll("\\s", ""), actual.replaceAll("\\s", ""));
    }
}
