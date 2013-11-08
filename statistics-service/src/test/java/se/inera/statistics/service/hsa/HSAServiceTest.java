package se.inera.statistics.service.hsa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

public class HSAServiceTest {

    private HSAService hsaService = new HSAServiceMock();

    @Test
    public void documentExists() throws IOException {
        JsonNode hsaInfo = hsaService.getHSAInfo(null);
        
        assertNotNull(hsaInfo);
        assertEquals("Enhetens namn", hsaInfo.path("enhetsnamn").textValue());
    }
}
