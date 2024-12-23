/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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
package se.inera.statistics.web.service.responseconverter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;
import se.inera.statistics.web.error.ErrorSeverity;
import se.inera.statistics.web.error.ErrorType;
import se.inera.statistics.web.error.Message;

public class ConvertersTest {

    @Test
    public void testCombineMessagesOnlyNullsReturnsEmptyArray() throws Exception {
        //When
        final List<Message> result = Converters.combineMessages(null, null);

        //Then
        assertEquals(0, result.size());
    }

    @Test
    public void testCombineMessagesEmptyInputReturnsEmptyArray() throws Exception {
        //When
        final List<Message> result = Converters.combineMessages();

        //Then
        assertEquals(0, result.size());
    }

    @Test
    public void testCombineMessagesEmptyInputStringIsIgnored() throws Exception {
        Message m1 = Message.create(ErrorType.FILTER, ErrorSeverity.INFO, "m1");
        Message m2 = Message.create(ErrorType.FILTER, ErrorSeverity.INFO, "");
        Message m3 = Message.create(ErrorType.FILTER, ErrorSeverity.INFO, "m3");

        //When
        final List<Message> result = Converters.combineMessages(m1, m2, m3);

        //Then
        assertEquals(2, result.size());
    }

    @Test
    public void testCombineMessagesNullInputIsIgnored() throws Exception {
        Message m1 = Message.create(ErrorType.FILTER, ErrorSeverity.INFO, "m1");
        Message m2 = Message.create(ErrorType.FILTER, ErrorSeverity.INFO, "m2");

        //When
        final List<Message> result = Converters.combineMessages(m1, null, m2);

        //Then
        assertEquals(2, result.size());
    }

}