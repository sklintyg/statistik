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
package se.inera.statistics.service.report.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;

public class CaseInsensiviteStringTest {

    @Test
    public void testEquals() throws Exception {
        assertEquals(new CaseInsensiviteString("abc"), new CaseInsensiviteString("abc"));
        assertEquals(new CaseInsensiviteString("abc"), new CaseInsensiviteString("aBC"));
        assertEquals(new CaseInsensiviteString("Abc"), new CaseInsensiviteString("abc"));
        assertNotEquals(new CaseInsensiviteString("ab c"), new CaseInsensiviteString("abc"));
    }

    @Test
    public void testGetString() throws Exception {
        assertEquals("abc", new CaseInsensiviteString("abc").getString());
        assertEquals("aBc", new CaseInsensiviteString("aBc").getString());
        assertNotEquals("abc", new CaseInsensiviteString("aBc").getString());
        assertNotEquals("Abc", new CaseInsensiviteString("abc").getString());
    }

    @Test
    public void testContainsWorksAsExpected() throws Exception {
        final List<CaseInsensiviteString> caseInsensiviteStrings = Arrays
            .asList(new CaseInsensiviteString("Abc1"), new CaseInsensiviteString("aBc2"), new CaseInsensiviteString("abC3"));
        assertTrue(caseInsensiviteStrings.contains(new CaseInsensiviteString("abc1")));
        assertEquals("aBc2", caseInsensiviteStrings.get(caseInsensiviteStrings.indexOf(new CaseInsensiviteString("ABC2"))).getString());
    }

}
