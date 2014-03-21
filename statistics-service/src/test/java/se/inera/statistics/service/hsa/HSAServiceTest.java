package se.inera.statistics.service.hsa;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class HSAServiceTest {

    private HSAService hsaService = new HSAServiceMock();

    @Test
    public void documentExists() throws IOException {
        JsonNode hsaInfo = hsaService.getHSAInfo(null);

        assertNotNull(hsaInfo);

        assertEquals("Enhetens namn", hsaInfo.path("enhet").path("namn").textValue());
    }
}
