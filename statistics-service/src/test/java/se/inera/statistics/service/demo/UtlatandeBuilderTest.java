package se.inera.statistics.service.demo;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.joda.time.LocalDate;
import org.junit.Test;

import se.inera.statistics.service.helper.DocumentHelper;

import com.fasterxml.jackson.databind.JsonNode;

public class UtlatandeBuilderTest {

    @Test
    public void permutateIntyg() {
        UtlatandeBuilder builder = new UtlatandeBuilder();

        final JsonNode result = builder.build("19121212-1212", new LocalDate("2013-01-01"), new LocalDate("2013-01-21"), "vardenhet", "diagnos", 50);

        assertEquals("19121212-1212", result.path("patient").path("id").path("extension").asText());
        assertEquals("diagnos", DocumentHelper.getDiagnos(result));
        assertEquals(Arrays.asList("50"), DocumentHelper.getArbetsformaga(result));
    }

}
