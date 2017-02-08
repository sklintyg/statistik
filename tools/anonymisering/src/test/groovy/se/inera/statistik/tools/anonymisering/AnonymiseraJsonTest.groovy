package se.inera.statistik.tools.anonymisering

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import org.apache.commons.io.FileUtils
import org.junit.Test
import org.skyscreamer.jsonassert.JSONAssert
import org.springframework.core.io.ClassPathResource
import se.inera.statistik.tools.anonymisering.base.AnonymiseraDatum
import se.inera.statistik.tools.anonymisering.base.AnonymiseraHsaId
import se.inera.statistik.tools.anonymisering.base.AnonymiseraPersonId
import se.inera.statistik.tools.anonymisering.base.AnonymiseraXml

import static groovy.util.GroovyTestCase.assertEquals

class AnonymiseraJsonTest {

    def JSON_TEMPLATE_PATH = '/fk7263_L_template.json'
    def JSON_TEMPLATE_NO_ENKELT_FIELDS_PATH = '/fk7263_L_template_no_fields_for_enkelt.json'
    def JSON_ANONYMIZED_TEMPLATE_PATH = '/fk7263_L_anonymized.json'
    def JSON_ANONYMIZED_TEMPLATE_NO_FIELDS_FOR_ENKELT_PATH = '/fk7263_L_anonymized_no_fields_for_enkelt.json'
    AnonymiseraHsaId anonymiseraHsaId = [anonymisera:{"SE1010"}] as AnonymiseraHsaId
    AnonymiseraDatum anonymiseraDatum = new AnonymiseraDatum()
    AnonymiseraJson anonymiseraJson = new AnonymiseraJson(anonymiseraHsaId, anonymiseraDatum)
    AnonymiseraPersonId anonymiseraPersonId = new AnonymiseraPersonId()
    AnonymiseraXml anonymiseraXml = new AnonymiseraXml(anonymiseraPersonId, anonymiseraHsaId, anonymiseraDatum)

    AnonymiseraJsonTest() {
        anonymiseraDatum.random = [nextInt: {(AnonymiseraDatum.DATE_RANGE/2)+1}] as Random
    }

    @Test
    void testaAnonymiseringAvMaximaltIntyg() {
        String json = FileUtils.readFileToString(new ClassPathResource("/fk7263_L_template.json").getFile(), "UTF-8")
        String expected = FileUtils.readFileToString(new ClassPathResource("/fk7263_L_anonymized.json").getFile(), "UTF-8")
        String actual = anonymiseraJson.anonymiseraIntygsJson(json, "10101010-2010")
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

        String actual = anonymiseraJson.anonymiseraIntygsJson(json, "10101010-2010")

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
        String json = buildJsonIntyg("/fk7263_L_template.json") { result ->
            result.funktionsnedsattning = [funktionsnedsattning: false, beskrivning: 'en liten text', ]
        }

        String expected = buildJsonIntyg("/fk7263_L_anonymized.json") { result ->
            result.funktionsnedsattning = [funktionsnedsattning: false, beskrivning: 'xx xxxxx xxxx', ]
        }

        String actual = anonymiseraJson.anonymiseraIntygsJson(json, "10101010-2010")

        JSONAssert.assertEquals(expected, actual, true);
    }

    @Test
    void testaAnonymiseringAvTSIntyg2() {
        String json = buildJsonIntyg("/fk7263_L_template.json") { result ->
            result.funktionsnedsattning = null
        }

        String expected = buildJsonIntyg("/fk7263_L_anonymized.json") { result ->
            result.funktionsnedsattning = null
        }

        String actual = anonymiseraJson.anonymiseraIntygsJson(json, "10101010-2010")

        JSONAssert.assertEquals(expected, actual, true);
    }

    @Test
    void testaAnonymiseringAvFk7263sit() {
        //Given
        AnonymiseraHsaId anonymiseraHsaId = [anonymisera:{"SE833377567"}] as AnonymiseraHsaId
        AnonymiseraDatum anonymiseraDatum = [anonymiseraDatum:{"2015-11-28"}] as AnonymiseraDatum
        AnonymiseraPersonId anonymiseraPersonId = [anonymisera:{"19680418-1748"}] as AnonymiseraPersonId
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
        AnonymiseraHsaId anonymiseraHsaId = [anonymisera:{"SE833377567"}] as AnonymiseraHsaId
        AnonymiseraDatum anonymiseraDatum = [anonymiseraDatum:{"2015-11-28"}] as AnonymiseraDatum
        AnonymiseraPersonId anonymiseraPersonId = [anonymisera:{"19670827-2049"}] as AnonymiseraPersonId
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
        AnonymiseraHsaId anonymiseraHsaId = [anonymisera:{"SE833377567"}] as AnonymiseraHsaId
        AnonymiseraDatum anonymiseraDatum = [anonymiseraDatum:{"2015-11-28"}] as AnonymiseraDatum
        AnonymiseraPersonId anonymiseraPersonId = [anonymisera:{"19680803-3002"}] as AnonymiseraPersonId
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
        AnonymiseraHsaId anonymiseraHsaId = [anonymisera:{"SE833377567"}] as AnonymiseraHsaId
        AnonymiseraDatum anonymiseraDatum = [anonymiseraDatum:{"2015-11-28"}] as AnonymiseraDatum
        AnonymiseraPersonId anonymiseraPersonId = [anonymisera:{"19690316-5568"}] as AnonymiseraPersonId
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
        AnonymiseraHsaId anonymiseraHsaId = [anonymisera:{"SE833377567"}] as AnonymiseraHsaId
        AnonymiseraDatum anonymiseraDatum = [anonymiseraDatum:{"2015-11-28"}] as AnonymiseraDatum
        AnonymiseraPersonId anonymiseraPersonId = [anonymisera:{"19680725-8824"}] as AnonymiseraPersonId
        AnonymiseraXml anonymiseraXml = new AnonymiseraXml(anonymiseraPersonId, anonymiseraHsaId, anonymiseraDatum)

        String expected = FileUtils.readFileToString(new ClassPathResource("/luse_anonymized.xml").getFile(), "UTF-8")

        //When
        String document = FileUtils.readFileToString(new ClassPathResource("/luse_not_anonymized.xml").getFile(), "UTF-8")
        String actual = anonymiseraXml.anonymiseraIntygsXml(document)

        //Then
        assertEquals(expected.replaceAll("\\s", ""), actual.replaceAll("\\s", ""));
    }

    def buildJsonIntyg(def file, def clos = null) {
        def intygString = getClass().getResource( file ).getText( 'UTF-8' )
        def result = new JsonSlurper().parseText intygString

        if(clos) clos.call result

        return new JsonBuilder(result).toString()
    }

}
