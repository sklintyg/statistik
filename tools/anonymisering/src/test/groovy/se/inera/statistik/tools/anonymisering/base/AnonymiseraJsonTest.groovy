/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import org.apache.commons.io.FileUtils
import org.junit.Test
import org.skyscreamer.jsonassert.JSONAssert
import org.springframework.core.io.ClassPathResource
import se.inera.statistik.tools.anonymisering.base.AnonymiseraDatum
import se.inera.statistik.tools.anonymisering.base.AnonymiseraHsaId

class AnonymiseraJsonTest {

    def JSON_TEMPLATE_PATH = '/fk7263_L_not_anonymized.json'
    def JSON_TEMPLATE_NO_ENKELT_FIELDS_PATH = '/fk7263_L_template_no_fields_for_enkelt.json'
    def JSON_ANONYMIZED_TEMPLATE_PATH = '/fk7263_L_anonymized.json'
    def JSON_ANONYMIZED_TEMPLATE_NO_FIELDS_FOR_ENKELT_PATH = '/fk7263_L_anonymized_no_fields_for_enkelt.json'
    AnonymiseraHsaId anonymiseraHsaId = [anonymisera:{"SE1010"}] as AnonymiseraHsaId
    AnonymiseraDatum anonymiseraDatum = new AnonymiseraDatum()
    se.inera.statistik.tools.anonymisering.AnonymiseraJson anonymiseraJson = new se.inera.statistik.tools.anonymisering.AnonymiseraJson(anonymiseraHsaId, anonymiseraDatum)

    AnonymiseraJsonTest() {
        anonymiseraDatum.random = [nextInt: {(AnonymiseraDatum.DATE_RANGE/2)+1}] as Random
    }

    @Test
    void testaAnonymiseringAvMaximaltIntyg() {
        String json = FileUtils.readFileToString(new ClassPathResource(JSON_TEMPLATE_PATH).getFile(), "UTF-8")
        String expected = FileUtils.readFileToString(new ClassPathResource(JSON_ANONYMIZED_TEMPLATE_PATH).getFile(), "UTF-8")
        String actual = anonymiseraJson.anonymiseraIntygsJson(json, "10101010-2010")
        JSONAssert.assertEquals(expected, actual, true);
    }

    @Test
    void test_intyg_when_no_fields_are_enkelt() {
        String json = buildJsonIntyg JSON_TEMPLATE_PATH, {result ->
            result.funktionsnedsattning = 'En text som råkar ha enkelt i sig'
            result.aktivitetsbegransning = 'En annan text'
        }

        String expected = buildJsonIntyg JSON_ANONYMIZED_TEMPLATE_PATH, {result ->
            result.funktionsnedsattning = 'xx xxxx xxx xxxxx xx xxxxxx x xxx'
            result.aktivitetsbegransning = 'xx xxxxx xxxx'
        }
        String actual = anonymiseraJson.anonymiseraIntygsJson(json, "10101010-2010")

        JSONAssert.assertEquals(expected, actual, true);
    }

    @Test
    void test_intyg_when_fields_do_not_exist() {
        String json = buildJsonIntyg JSON_TEMPLATE_NO_ENKELT_FIELDS_PATH

        String expected = buildJsonIntyg JSON_ANONYMIZED_TEMPLATE_NO_FIELDS_FOR_ENKELT_PATH

        String actual = anonymiseraJson.anonymiseraIntygsJson(json, "10101010-2010")

        JSONAssert.assertEquals(expected, actual, true);
    }

    @Test
    void testaAnonymiseringAvTSIntyg() {
        String json = buildJsonIntyg(JSON_TEMPLATE_PATH) { result ->
            result.funktionsnedsattning = [funktionsnedsattning: false, beskrivning: 'en liten text', ]
        }

        String expected = buildJsonIntyg(JSON_ANONYMIZED_TEMPLATE_PATH) { result ->
            result.funktionsnedsattning = [funktionsnedsattning: false, beskrivning: 'xx xxxxx xxxx', ]
        }

        String actual = anonymiseraJson.anonymiseraIntygsJson(json, "10101010-2010")

        JSONAssert.assertEquals(expected, actual, true);
    }

    @Test
    void testaAnonymiseringAvTSIntyg2() {
        String json = buildJsonIntyg(JSON_TEMPLATE_PATH) { result ->
            result.funktionsnedsattning = null
        }

        String expected = buildJsonIntyg(JSON_ANONYMIZED_TEMPLATE_PATH) { result ->
            result.funktionsnedsattning = null
        }

        String actual = anonymiseraJson.anonymiseraIntygsJson(json, "10101010-2010")

        JSONAssert.assertEquals(expected, actual, true);
    }

    def buildJsonIntyg(def file, def clos = null) {
        def intygString = getClass().getResource( file ).getText( 'UTF-8' )
        def result = new JsonSlurper().parseText intygString

        if(clos) clos.call result

        return new JsonBuilder(result).toString()
    }

}
