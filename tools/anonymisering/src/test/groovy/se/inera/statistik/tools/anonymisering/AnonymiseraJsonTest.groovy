package se.inera.statistik.tools.anonymisering

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import org.apache.commons.io.FileUtils
import org.junit.Test
import org.skyscreamer.jsonassert.JSONAssert
import org.springframework.core.io.ClassPathResource
import se.inera.certificate.tools.anonymisering.AnonymiseraDatum
import se.inera.certificate.tools.anonymisering.AnonymiseraHsaId

class AnonymiseraJsonTest {

    def JSON_TEMPLATE_PATH = '/fk7263_L_template.json'
    def JSON_ANONYMIZED_TEMPLATE_PATH = '/fk7263_L_anonymized.json'
    AnonymiseraHsaId anonymiseraHsaId = [anonymisera:{"SE1010"}] as AnonymiseraHsaId
    AnonymiseraDatum anonymiseraDatum = new AnonymiseraDatum()
    AnonymiseraJson anonymiseraJson = new AnonymiseraJson(anonymiseraHsaId, anonymiseraDatum)

    AnonymiseraJsonTest() {
        anonymiseraDatum.random = [nextInt: {(AnonymiseraDatum.DATE_RANGE/2)+1}] as Random
    }

    @Test
    void testaAnonymiseringAvMaximaltIntyg() {
        String json = FileUtils.readFileToString(new ClassPathResource("/fk7263_L_template.json").getFile(), "UTF-8")
        String expected = FileUtils.readFileToString(new ClassPathResource("/fk7263_L_anonymized.json").getFile(), "UTF-8")
        String actual = anonymiseraJson.anonymiseraIntygsJson(json, "10101010-1010")
        JSONAssert.assertEquals(expected, actual, true);
    }

    @Test
    void testa_intyg_when_both_fields_are_enkelt() {

        def jsonUpdateClosure = {result ->
            result.funktionsnedsattning = 'Enkelt'
            result.aktivitetsbegransning = 'Enkelt'
        }

        String json = buildJsonIntyg JSON_TEMPLATE_PATH, jsonUpdateClosure

        String expected = buildJsonIntyg JSON_ANONYMIZED_TEMPLATE_PATH, jsonUpdateClosure

        String actual = anonymiseraJson.anonymiseraIntygsJson(json, "10101010-1010")

        JSONAssert.assertEquals(expected, actual, true);
    }

    @Test
    void test_intyg_when_only_one_field_is_enkelt() {
        String json = buildJsonIntyg JSON_TEMPLATE_PATH, {result ->
            result.funktionsnedsattning = 'Enkelt'
            result.aktivitetsbegransning = 'Svårt'
        }

        String expected = buildJsonIntyg JSON_ANONYMIZED_TEMPLATE_PATH, {result ->
            result.funktionsnedsattning = 'Enkelt'
            result.aktivitetsbegransning = 'xxxxx'
        }
        String actual = anonymiseraJson.anonymiseraIntygsJson(json, "10101010-1010")

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
        String actual = anonymiseraJson.anonymiseraIntygsJson(json, "10101010-1010")

        JSONAssert.assertEquals(expected, actual, true);
    }

    def buildJsonIntyg(def file, def clos = null) {
        def intygString = getClass().getResource( file ).getText( 'UTF-8' )
        def result = new JsonSlurper().parseText intygString

        if(clos) clos.call result

        return new JsonBuilder(result).toString()
    }

}
