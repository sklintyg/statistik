package se.inera.statistics.service.warehouse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import se.inera.statistics.service.processlog.EventType;
import se.inera.statistics.service.warehouse.model.db.WideLine;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:process-log-impl-test.xml", "classpath:icd10.xml" })
@DirtiesContext
    public class FactPopulatorTest {

    @Autowired
    FactPopulator factPopulator;

    @Test
    public void toFactWithBasicWideline() {
        WideLine wideLine = new WideLine(1L, "correlationId", "088080", "enhet", 2L, EventType.CREATED, "19121212-1210", 1000, 1002, 1, 20, "diagnoskapitel", "diagnosavsnitt", "diagnoskategori", 100, 1, 50, "", "vardgivareId", "lakareId");

        Fact fact = factPopulator.toFact(wideLine);

        assertEquals(fact.getAlder(), 20);
    }

    @Test
    public void toFactWithDashBefattning() {
        WideLine wideLine = new WideLine(1L, "correlationId", "088080", "enhet", 2L, EventType.CREATED, "19121212-1210", 1000, 1002, 1, 20, "diagnoskapitel", "diagnosavsnitt", "diagnoskategori", 100, 1, 50, "-", "vardgivareId", "lakareId");

        Fact fact = factPopulator.toFact(wideLine);

        assertEquals(0, fact.getLakarbefattnings().length);
    }

    @Test
    public void toFactWithFaultyBefattning() {
        WideLine wideLine = new WideLine(1L, "correlationId", "088080", "enhet", 2L, EventType.CREATED, "19121212-1210", 1000, 1002, 1, 20, "diagnoskapitel", "diagnosavsnitt", "diagnoskategori", 100, 1, 50, "xyz123", "vardgivareId", "lakareId");

        Fact fact = factPopulator.toFact(wideLine);

        assertEquals(0, fact.getLakarbefattnings().length);
    }
}