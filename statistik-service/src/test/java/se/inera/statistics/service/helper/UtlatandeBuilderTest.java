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
package se.inera.statistics.service.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;
import se.inera.statistics.hsa.model.HsaId;

import static org.junit.Assert.assertEquals;

public class UtlatandeBuilderTest {
    @Test
    public void testgetSignerumsDatum() {
        JsonNode result = new UtlatandeBuilder().build("19121212-1212", new LocalDate("2012-12-12"), new LocalDate("2012-12-12"), new HsaId("lakare"), new HsaId("vardenhet1"), new HsaId("vardgivare"), "A01", 50);

        Assert.assertEquals("2012-12-12T07:07:00.000", result.path("grundData").get("signeringsdatum").textValue());
    }

    @Test
    public void getAge() {
        JsonNode result = new UtlatandeBuilder().build("19121212-1212", new LocalDate("2012-12-12"), new LocalDate("2012-12-12"), new HsaId("lakare"), new HsaId("vardenhet1"), new HsaId("vardgivare"), "A01", 50);
        final ObjectNode prepared = DocumentHelper.prepare(result);

        assertEquals(100, DocumentHelper.getAge(prepared));
    }

}
