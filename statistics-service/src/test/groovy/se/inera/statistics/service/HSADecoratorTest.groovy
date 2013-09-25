package se.inera.statistics.service

import org.junit.Before
import org.junit.Test

import static org.junit.Assert.*

class HSADecoratorTest {
    HSADecorator hsaDecorator = new HSADecorator()
    HSAService hsaService;

    @Before
    void setup() {
        hsaService = {
            new HSAInfo("län för " + it.vardgivareId + "," + it.enhetId + "," + it.lakareId)
        } as HSAService
    }

    @Test
    void hsa_created_test() {
        assertNotNull(hsaDecorator)
    }

    @Test
    void hsa_get_hsa_data_from_hsa_mock() {
        HSAKey key = new HSAKey("vardgivareId", "enhetId", "lakareId")

        def result = hsaService.getHSAInfo(key)

        println result
        assertEquals "län för vardgivareId,enhetId,lakareId", result.lan

    }

    @Test
    void hsa_decorator_extract_key_from_document() {
        def doc = new InputStreamReader(this.getClass().getResourceAsStream("/json/fk7263_M_template.json")).text
        hsaDecorator.service = { assertEquals "enhetId", it.enhetId; new HSAInfo("Västa Götaland") } as HSAService

        def key = hsaDecorator.extractHSAKey(doc)

        assertEquals "enhetId", key.enhetId
        assertEquals "Personal HSA-ID", key.lakareId
        assertEquals "VardgivarId", key.vardgivareId
    }

}
