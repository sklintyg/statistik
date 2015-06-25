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
package se.inera.statistics.service.demo;

import com.fasterxml.jackson.databind.JsonNode;
import org.joda.time.LocalDate;
import org.junit.Test;
import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.service.helper.DocumentHelper;
import se.inera.statistics.service.helper.UtlatandeBuilder;

import static org.junit.Assert.assertEquals;

public class UtlatandeBuilderTest {

    @Test
    public void permutateIntyg() {
        UtlatandeBuilder builder = new UtlatandeBuilder();

        final JsonNode result = builder.build("19121212-1212", new LocalDate("2013-01-01"), new LocalDate("2013-01-21"), new HsaIdEnhet("vardenhet"), "diagnos", 50);

        assertEquals("19121212-1212", result.path("grundData").path("patient").path("personId").asText());
        assertEquals("diagnos", DocumentHelper.getDiagnos(result, DocumentHelper.IntygVersion.VERSION2));
    }

}
