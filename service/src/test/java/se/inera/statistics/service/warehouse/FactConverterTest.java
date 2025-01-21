/*
 * Copyright (C) 2025 Inera AB (http://www.inera.se)
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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import se.inera.statistics.integration.hsa.model.HsaIdEnhet;
import se.inera.statistics.integration.hsa.model.HsaIdLakare;
import se.inera.statistics.integration.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.processlog.EventType;
import se.inera.statistics.service.report.util.Icd10;
import se.inera.statistics.service.warehouse.model.db.WideLine;
import se.inera.statistics.service.warehouse.query.LakarbefattningQuery;

public class FactConverterTest {

    @InjectMocks
    private FactConverter factConverter;

    @Mock
    private Icd10 icd10;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void toFactWithBasicWideline() {
        WideLine wideLine = new WideLine(1L, "correlationId", "088080", new HsaIdEnhet("enhet"), 2L, EventType.CREATED, "19121212-1212",
            1000, 1002, 1, 20, "diagnoskapitel", "diagnosavsnitt", "diagnoskategori", "diagnoskod", 100, 1, 50, "",
            new HsaIdVardgivare("vardgivareId"), new HsaIdLakare("lakareId"), true, new HsaIdEnhet("vardenhet"));

        Fact fact = factConverter.toFact(wideLine);

        assertEquals(fact.getAlder(), 20);
    }

    @Test
    public void toFactWithDashBefattning() {
        final String lakarbefattning = "-";
        WideLine wideLine = new WideLine(1L, "correlationId", "088080", new HsaIdEnhet("enhet"), 2L, EventType.CREATED, "19121212-1212",
            1000, 1002, 1, 20, "diagnoskapitel", "diagnosavsnitt", "diagnoskategori", "diagnoskod", 100, 1, 50, lakarbefattning,
            new HsaIdVardgivare("vardgivareId"), new HsaIdLakare("lakareId"), true, new HsaIdEnhet("vardenhet"));

        Fact fact = factConverter.toFact(wideLine);

        assertEquals(1, fact.getLakarbefattnings().length);
        assertEquals((long) LakarbefattningQuery.UNKNOWN_BEFATTNING_CODE, fact.getLakarbefattnings()[0]);
    }

    @Test
    public void toFactWithEmptyBefattning() {
        final String lakarbefattning = "";
        WideLine wideLine = new WideLine(1L, "correlationId", "088080", new HsaIdEnhet("enhet"), 2L, EventType.CREATED, "19121212-1212",
            1000, 1002, 1, 20, "diagnoskapitel", "diagnosavsnitt", "diagnoskategori", "diagnoskod", 100, 1, 50, lakarbefattning,
            new HsaIdVardgivare("vardgivareId"), new HsaIdLakare("lakareId"), true, new HsaIdEnhet("vardenhet"));

        Fact fact = factConverter.toFact(wideLine);

        assertEquals(1, fact.getLakarbefattnings().length);
        assertEquals((long) LakarbefattningQuery.UNKNOWN_BEFATTNING_CODE, fact.getLakarbefattnings()[0]);
    }

    @Test
    public void toFactWithFaultyBefattning() {
        final String lakarbefattning = "xyz123";
        WideLine wideLine = new WideLine(1L, "correlationId", "088080", new HsaIdEnhet("enhet"), 2L, EventType.CREATED, "19121212-1212",
            1000, 1002, 1, 20, "diagnoskapitel", "diagnosavsnitt", "diagnoskategori", "diagnoskod", 100, 1, 50, lakarbefattning,
            new HsaIdVardgivare("vardgivareId"), new HsaIdLakare("lakareId"), true, new HsaIdEnhet("vardenhet"));

        Fact fact = factConverter.toFact(wideLine);

        assertEquals(1, fact.getLakarbefattnings().length);
        assertEquals((long) LakarbefattningQuery.UNKNOWN_BEFATTNING_CODE, fact.getLakarbefattnings()[0]);
    }

    @Test
    public void toFactWithFaultyAndCorrectBefattnings() {
        final String lakarbefattning = "123,-,456,abc123";
        WideLine wideLine = new WideLine(1L, "correlationId", "088080", new HsaIdEnhet("enhet"), 2L, EventType.CREATED, "19121212-1212",
            1000, 1002, 1, 20, "diagnoskapitel", "diagnosavsnitt", "diagnoskategori", "diagnoskod", 100, 1, 50, lakarbefattning,
            new HsaIdVardgivare("vardgivareId"), new HsaIdLakare("lakareId"), true, new HsaIdEnhet("vardenhet"));

        Fact fact = factConverter.toFact(wideLine);

        assertEquals(2, fact.getLakarbefattnings().length);
        assertEquals(123, fact.getLakarbefattnings()[0]);
        assertEquals(456, fact.getLakarbefattnings()[1]);
    }

    @Test
    public void testParseBefattning() throws Exception {
        //Given
        final String lakarbefattningString = "1234,4321, 45, 43 ,f5,3t,   6 ,åt, å9, 5";
        final String correlationId = "TestCorrId";

        //When
        final int[] testCorrIds = factConverter.parseBefattning(lakarbefattningString, correlationId);

        //Then
        assertArrayEquals(new int[]{1234, 4321, 45, 43, 6, 5}, testCorrIds);
    }

}
