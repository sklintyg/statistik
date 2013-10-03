package se.inera.statistics.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import groovy.json.JsonBuilder
import org.junit.Before
import org.junit.Test
import se.inera.statistics.service.helper.JSONParser
import se.inera.statistics.service.hsa.HSADecorator
import se.inera.statistics.service.hsa.HSAInfo
import se.inera.statistics.service.hsa.HSAKey
import se.inera.statistics.service.hsa.HSAService

import static org.junit.Assert.*

class HSADecoratorTest {
    HSADecorator hsaDecorator = new HSADecorator()
    HSAService hsaService;

    @Before
    void setup() {
        hsaService = [
            getHSAInfo : { HSAKey that ->
                def builder = new JsonBuilder()
                def root = builder { lan ("län för " + that.vardgivareId + "," + that.enhetId + "," + that.lakareId) }

                new ObjectMapper().readTree(builder.toString());
            }
        ] as HSAService
    }

    @Test
    void hsa_created_test() {
        assertNotNull(hsaDecorator)
    }

    @Test
    void hsa_get_hsa_data_from_hsa_mock() {
        HSAKey key = new HSAKey("vardgivareId", "enhetId", "lakareId")

        ObjectNode result = hsaService.getHSAInfo(key)

        assertEquals "län för vardgivareId,enhetId,lakareId", result.findPath("lan").textValue()

    }

    @Test
    void hsa_decorator_extract_key_from_document() {
        def doc = JSONParser.parse(JSONSource.readTemplateAsString().toString())
        hsaDecorator.service = { assertEquals "enhetId", it.enhetId; new HSAInfo("Västa Götaland",) } as HSAService

        def key = hsaDecorator.extractHSAKey(doc)

        assertEquals "enhetId", key.enhetId
        assertEquals "Personal HSA-ID", key.lakareId
        assertEquals "VardgivarId", key.vardgivareId
    }

}
