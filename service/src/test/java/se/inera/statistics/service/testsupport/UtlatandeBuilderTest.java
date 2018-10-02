/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.testsupport;

import com.fasterxml.jackson.databind.JsonNode;

import org.junit.Assert;
import org.junit.Test;
import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.hsa.model.HsaIdLakare;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.helper.certificate.JsonDocumentHelper;
import se.inera.statistics.service.helper.Patientdata;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

public class UtlatandeBuilderTest {
    @Test
    public void testgetSignerumsDatum() {
        JsonNode result = new UtlatandeBuilder().build("19121212-1212", LocalDate.parse("2012-12-12"), LocalDate.parse("2012-12-12"), new HsaIdLakare("lakare"), new HsaIdEnhet("vardenhet1"), new HsaIdVardgivare("vardgivare"), "A01", 50);

        Assert.assertEquals("2012-12-12T07:07:00.000", result.path("grundData").get("signeringsdatum").textValue());
    }

    @Test
    public void getAge() {
        JsonNode result = new UtlatandeBuilder().build("19121212-1212", LocalDate.parse("2012-12-12"), LocalDate.parse("2012-12-12"), new HsaIdLakare("lakare"), new HsaIdEnhet("vardenhet1"), new HsaIdVardgivare("vardgivare"), "A01", 50);
        final Patientdata patientData = JsonDocumentHelper.getPatientData(result);

        assertEquals(100, patientData.getAlder());
    }

}
