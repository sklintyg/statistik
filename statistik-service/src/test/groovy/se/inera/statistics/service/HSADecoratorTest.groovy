package se.inera.statistics.service

import org.junit.Before
import org.junit.Test
import se.inera.statistics.service.helper.JSONParser
import se.inera.statistics.service.hsa.HSADecorator
import se.inera.statistics.service.hsa.HSAKey
import se.inera.statistics.service.hsa.HSAService
import se.inera.statistics.service.hsa.HsaInfo
import se.inera.statistics.service.hsa.HsaInfoVg

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

class HSADecoratorTest {
    HSADecorator hsaDecorator = new HSADecorator()
    HSAService hsaService;

    @Before
    void setup() {
        hsaService = [
            getHSAInfo : { HSAKey that ->
                new HsaInfo(null, null, new HsaInfoVg("län för " + that.vardgivareId + "," + that.enhetId + "," + that.lakareId, "", null, null, false), null);
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

        HsaInfo result = hsaService.getHSAInfo(key)

        assertEquals "län för VARDGIVAREID,ENHETID,LAKAREID", result.getVardgivare().getId()
    }

    @Test
    void hsa_decorator_extract_key_from_document() {
        def doc = JSONParser.parse(JSONSource.readTemplateAsString())

        def key = hsaDecorator.extractHSAKey(doc)

        assertEquals "VardenhetY".toUpperCase(), key.enhetId.id
        assertEquals "Personal HSA-ID".toUpperCase().replaceAll(" ", ""), key.lakareId.id
        assertEquals "VardgivarId".toUpperCase(), key.vardgivareId.id
    }

}
