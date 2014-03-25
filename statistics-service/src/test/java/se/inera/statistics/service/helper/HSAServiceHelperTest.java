package se.inera.statistics.service.helper;

import org.junit.Assert;
import org.junit.Test;

import se.inera.statistics.service.JSONSource;
import se.inera.statistics.service.hsa.HSAKey;
import se.inera.statistics.service.hsa.HSAService;
import se.inera.statistics.service.hsa.HSAServiceMock;

import com.fasterxml.jackson.databind.JsonNode;

public class HSAServiceHelperTest {
    @Test
    public void getLanTest() {
        HSAService hsaService = new HSAServiceMock();
        JsonNode info = hsaService.getHSAInfo(new HSAKey("vardgivarid", "enhetId", "lakareId"));

        String lan = HSAServiceHelper.getLan(info);
        Assert.assertEquals("20", lan);
    }

    @Test
    public void getLanTestForFixedData() {
        JsonNode info = JSONParser.parse(JSONSource.readHSASample());

        String lan = HSAServiceHelper.getLan(info);

        Assert.assertEquals("03", lan);
    }

    @Test
    public void getLanOnHuvudenhet() {
        JsonNode info = JSONParser.parse(JSONSource.readHSASample("hsa_example_huvudenhet"));

        String lan = HSAServiceHelper.getLan(info);

        Assert.assertEquals("05", lan);
    }
}
