package se.inera.statistics.service.demo;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import se.inera.statistics.service.JSONSource;
import se.inera.statistics.service.helper.DocumentHelper;
import se.inera.statistics.service.helper.JSONParser;

import com.fasterxml.jackson.databind.JsonNode;

public class DemoTest {

    @Test
    public void permutateIntyg() {
        String intyg = (String) JSONSource.readTemplateAsString();
        JsonNode intygTree = JSONParser.parse(intyg);

        JsonNode result = InjectUtlatande.permutate(intygTree, "19121212-1212");

        assertEquals("19121212-1212", result.path("patient").path("id").path("extension").asText());
        assertEquals( "G90", DocumentHelper.getDiagnos(result));
        assertEquals(Arrays.asList("0"), DocumentHelper.getArbetsformaga(result));
    }

}
