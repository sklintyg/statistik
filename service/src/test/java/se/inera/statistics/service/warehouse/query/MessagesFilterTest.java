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
package se.inera.statistics.service.warehouse.query;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import org.junit.Test;

public class MessagesFilterTest {

    @Test
    public void getNumberOfMonths() {
        assertEquals(2, createMessagesFilter("2018-03-05", "2018-05-06").getNumberOfMonths()); //Simple case
        assertEquals(14, createMessagesFilter("2017-03-05", "2018-05-06").getNumberOfMonths()); //More than one year
        assertEquals(1, createMessagesFilter("2018-03-05", "2018-05-01").getNumberOfMonths()); //Not two full months
    }

    private MessagesFilter createMessagesFilter(String from, String to) {
        return new MessagesFilter(null, LocalDate.parse(from), LocalDate.parse(to), null, null, null, null);
    }

}