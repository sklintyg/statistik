/*
 * Copyright (C) 2013 - 2014 Inera AB (http://www.inera.se)
 *
 *     This file is part of Inera Statistics (http://code.google.com/p/inera-statistics/).
 *
 *     Inera Statistics is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Inera Statistics is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU LESSER GENERAL PUBLIC LICENSE for more details.
 *
 *     You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.inera.statistics.service.helper;

import com.fasterxml.jackson.databind.JsonNode;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class UtlatandeBuilderTest {
    @Test
    public void testgetSignerumsDatum() {
        JsonNode result = new UtlatandeBuilder().build("19121212-1212", new LocalDate("2012-12-12"), new LocalDate("2012-12-12"), "lakare", "vardenhet1", "vardgivare", "A01", 50);

        Assert.assertEquals("2012-12-12T07:07:00.000", result.get("signeringsdatum").textValue());
    }

    @Test
    public void get_age() {
        JsonNode result = new UtlatandeBuilder().build("19121212-1212", new LocalDate("2012-12-12"), new LocalDate("2012-12-12"), "lakare", "vardenhet1", "vardgivare", "A01", 50);

        assertEquals(100, DocumentHelper.getAge(DocumentHelper.anonymize(result)));
    }

}
