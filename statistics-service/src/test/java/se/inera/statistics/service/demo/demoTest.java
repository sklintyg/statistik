package se.inera.statistics.service.demo;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import se.inera.statistics.service.JSONSource;
import se.inera.statistics.service.helper.JSONParser;

import static org.junit.Assert.assertEquals;

public class demoTest {

    @Test
    public void permutateIntyg() {
        String intyg = (String) JSONSource.readTemplateAsString();
        JsonNode intygTree = JSONParser.parse(intyg);

        JsonNode result = InjectUtlatande.permutate(intygTree, "19121212-1212");

        assertEquals("19121212-1212", result.path("patient").path("id").path("extension").asText());
    }

}
