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
package se.inera.statistics.web.service;

import org.junit.Test;

import static org.junit.Assert.*;

public class ConvertersTest {

    @Test
    public void testCombineMessagesOneNullIsIgnored() throws Exception {
        //When
        final String result = Converters.combineMessages("test", null, "testar 2");

        //Then
        assertEquals("test : testar 2", result);
    }

    @Test
    public void testCombineMessagesOnlyNullsReturnsNull() throws Exception {
        //When
        final String result = Converters.combineMessages(null, null);

        //Then
        assertEquals(null, result);
    }

    @Test
    public void testCombineMessagesEmptyInputReturnsNull() throws Exception {
        //When
        final String result = Converters.combineMessages();

        //Then
        assertEquals(null, result);
    }

    @Test
    public void testCombineMessagesEmptyInputStringIsIgnored() throws Exception {
        //When
        final String result = Converters.combineMessages("abc", "", "def");

        //Then
        assertEquals("abc : def", result);
    }

}
