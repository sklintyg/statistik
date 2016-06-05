package se.inera.testsupport;

import org.junit.Test;

import static org.junit.Assert.*;

public class RestTemplateStubTest {

    @Test
    public void testCountyPopulationIsPopulatedCorrectlyAtCreation() throws Exception {
        final RestTemplateStub restTemplateStub = new RestTemplateStub();
        assertEquals(11, restTemplateStub.getCountyPopulationPerYear().size());
    }
}
