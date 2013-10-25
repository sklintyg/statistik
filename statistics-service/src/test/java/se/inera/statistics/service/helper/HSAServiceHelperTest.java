package se.inera.statistics.service.helper;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Assert;
import org.junit.Test;
import se.inera.statistics.service.hsa.HSAKey;
import se.inera.statistics.service.hsa.HSAService;
import se.inera.statistics.service.hsa.HSAServiceImpl;

public class HSAServiceHelperTest {
    @Test
    public void getLanTest() {
        HSAService hsaService = new HSAServiceImpl();
        JsonNode info = hsaService.getHSAInfo(new HSAKey("", "", ""));

        String lan = HSAServiceHelper.getLan(info);

        Assert.assertEquals("14", lan);

    }
}
