/*
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
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
import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.hsa.model.HsaIdLakare;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.processlog.EventType;
import se.inera.statistics.service.warehouse.model.db.WideLine;
import se.inera.statistics.service.warehouse.query.LakarbefattningQuery;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:process-log-impl-test.xml", "classpath:icd10.xml" })
@DirtiesContext
    public class FactConverterTest {

    @Autowired
    FactConverter factConverter;

    @Test
    public void toFactWithBasicWideline() {
        WideLine wideLine = new WideLine(1L, "correlationId", "088080", new HsaIdEnhet("enhet"), 2L, EventType.CREATED, "19121212-1212", 1000, 1002, 1, 20, "diagnoskapitel", "diagnosavsnitt", "diagnoskategori", "diagnoskod", 100, 1, 50, "", new HsaIdVardgivare("vardgivareId"), new HsaIdLakare("lakareId"));

        Fact fact = factConverter.toFact(wideLine);

        assertEquals(fact.getAlder(), 20);
    }

    @Test
    public void toFactWithDashBefattning() {
        final String lakarbefattning = "-";
        WideLine wideLine = new WideLine(1L, "correlationId", "088080", new HsaIdEnhet("enhet"), 2L, EventType.CREATED, "19121212-1212", 1000, 1002, 1, 20, "diagnoskapitel", "diagnosavsnitt", "diagnoskategori", "diagnoskod", 100, 1, 50, lakarbefattning, new HsaIdVardgivare("vardgivareId"), new HsaIdLakare("lakareId"));

        Fact fact = factConverter.toFact(wideLine);

        assertEquals(1, fact.getLakarbefattnings().length);
        assertEquals((long) LakarbefattningQuery.UNKNOWN_BEFATTNING_CODE, fact.getLakarbefattnings()[0]);
    }

    @Test
    public void toFactWithEmptyBefattning() {
        final String lakarbefattning = "";
        WideLine wideLine = new WideLine(1L, "correlationId", "088080", new HsaIdEnhet("enhet"), 2L, EventType.CREATED, "19121212-1212", 1000, 1002, 1, 20, "diagnoskapitel", "diagnosavsnitt", "diagnoskategori", "diagnoskod", 100, 1, 50, lakarbefattning, new HsaIdVardgivare("vardgivareId"), new HsaIdLakare("lakareId"));

        Fact fact = factConverter.toFact(wideLine);

        assertEquals(1, fact.getLakarbefattnings().length);
        assertEquals((long) LakarbefattningQuery.UNKNOWN_BEFATTNING_CODE, fact.getLakarbefattnings()[0]);
    }

    @Test
    public void toFactWithFaultyBefattning() {
        final String lakarbefattning = "xyz123";
        WideLine wideLine = new WideLine(1L, "correlationId", "088080", new HsaIdEnhet("enhet"), 2L, EventType.CREATED, "19121212-1212", 1000, 1002, 1, 20, "diagnoskapitel", "diagnosavsnitt", "diagnoskategori", "diagnoskod", 100, 1, 50, lakarbefattning, new HsaIdVardgivare("vardgivareId"), new HsaIdLakare("lakareId"));

        Fact fact = factConverter.toFact(wideLine);

        assertEquals(1, fact.getLakarbefattnings().length);
        assertEquals((long) LakarbefattningQuery.UNKNOWN_BEFATTNING_CODE, fact.getLakarbefattnings()[0]);
    }

    @Test
    public void toFactWithFaultyAndCorrectBefattnings() {
        final String lakarbefattning = "123,-,456,abc123";
        WideLine wideLine = new WideLine(1L, "correlationId", "088080", new HsaIdEnhet("enhet"), 2L, EventType.CREATED, "19121212-1212", 1000, 1002, 1, 20, "diagnoskapitel", "diagnosavsnitt", "diagnoskategori", "diagnoskod", 100, 1, 50, lakarbefattning, new HsaIdVardgivare("vardgivareId"), new HsaIdLakare("lakareId"));

        Fact fact = factConverter.toFact(wideLine);

        assertEquals(2, fact.getLakarbefattnings().length);
        assertEquals(123, fact.getLakarbefattnings()[0]);
        assertEquals(456, fact.getLakarbefattnings()[1]);
    }

}
