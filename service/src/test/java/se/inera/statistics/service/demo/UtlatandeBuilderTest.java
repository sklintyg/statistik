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
package se.inera.statistics.service.demo;

import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.LocalDate;
import org.junit.Test;
import se.inera.statistics.integration.hsa.model.HsaIdEnhet;
import se.inera.statistics.service.helper.certificate.JsonDocumentHelper;
import se.inera.statistics.service.testsupport.UtlatandeBuilder;

public class UtlatandeBuilderTest {

    @Test
    public void permutateIntyg() {
        UtlatandeBuilder builder = new UtlatandeBuilder();

        final JsonNode result = builder
            .build("19121212-1212", LocalDate.parse("2013-01-01"), LocalDate.parse("2013-01-21"), new HsaIdEnhet("vardenhet"), "diagnos",
                50);

        assertEquals("19121212-1212", result.path("grundData").path("patient").path("personId").asText());
        assertEquals("diagnos", JsonDocumentHelper.getDiagnos(result));
    }

}
