package se.inera.statistics.service.demo;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import se.inera.statistics.service.helper.DocumentHelper;

import com.fasterxml.jackson.databind.JsonNode;

public class DemoTest {

    @Test
    public void permutateIntyg() {
        UtlatandeBuilder builder = new UtlatandeBuilder();

        JsonNode result = InjectUtlatande.permutate(builder, "19121212-1212");

        assertEquals("19121212-1212", result.path("patient").path("id").path("extension").asText());
        assertEquals("Y60", DocumentHelper.getDiagnos(result));
        assertEquals(Arrays.asList("25"), DocumentHelper.getArbetsformaga(result));
    }

}
