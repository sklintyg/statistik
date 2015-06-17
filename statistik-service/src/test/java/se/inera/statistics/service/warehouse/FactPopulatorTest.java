/**
 * Copyright (C) 2015 Inera AB (http://www.inera.se)
 *
 * This file is part of statistik (https://github.com/sklintyg/statistik).
 *
 * statistik is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * statistik is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.service.warehouse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import se.inera.statistics.hsa.model.HsaId;
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
        WideLine wideLine = new WideLine(1L, "correlationId", "088080", new HsaId("enhet"), 2L, EventType.CREATED, "19121212-1210", 1000, 1002, 1, 20, "diagnoskapitel", "diagnosavsnitt", "diagnoskategori", "diagnoskod", 100, 1, 50, "", new HsaId("vardgivareId"), new HsaId("lakareId"));

        Fact fact = factPopulator.toFact(wideLine);

        assertEquals(fact.getAlder(), 20);
    }

    @Test
    public void toFactWithDashBefattning() {
        WideLine wideLine = new WideLine(1L, "correlationId", "088080", new HsaId("enhet"), 2L, EventType.CREATED, "19121212-1210", 1000, 1002, 1, 20, "diagnoskapitel", "diagnosavsnitt", "diagnoskategori", "diagnoskod", 100, 1, 50, "-", new HsaId("vardgivareId"), new HsaId("lakareId"));

        Fact fact = factPopulator.toFact(wideLine);

        assertEquals(0, fact.getLakarbefattnings().length);
    }

    @Test
    public void toFactWithFaultyBefattning() {
        WideLine wideLine = new WideLine(1L, "correlationId", "088080", new HsaId("enhet"), 2L, EventType.CREATED, "19121212-1210", 1000, 1002, 1, 20, "diagnoskapitel", "diagnosavsnitt", "diagnoskategori", "diagnoskod", 100, 1, 50, "xyz123", new HsaId("vardgivareId"), new HsaId("lakareId"));

        Fact fact = factPopulator.toFact(wideLine);

        assertEquals(0, fact.getLakarbefattnings().length);
    }
}
