/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.time.LocalDate;
import java.util.Collections;
import java.util.NoSuchElementException;
import org.junit.Test;
import se.inera.statistics.integration.hsa.model.HsaIdVardgivare;

public class SjukfallIteratorTest {

    @Test
    public void testNext() throws Exception {
        //Given
        final LocalDate fromDate = LocalDate.of(2015, 1, 1);
        final int periodSize = 1;
        final SjukfallIterator sjukfallIterator = new SjukfallIterator(fromDate, 2, periodSize,
            new Aisle(new HsaIdVardgivare(""), Collections.<Fact>emptyList()), SjukfallUtil.ALL_ENHETER);

        //When
        final SjukfallGroup group = sjukfallIterator.next();

        //Then
        assertEquals(fromDate, group.getRange().getFrom());
        assertEquals(fromDate.plusMonths(periodSize).minusDays(1), group.getRange().getTo());

        //When
        final SjukfallGroup group2 = sjukfallIterator.next();

        //Then
        assertEquals(fromDate.plusMonths(periodSize).withDayOfMonth(1), group2.getRange().getFrom());
        assertEquals(fromDate.plusMonths(periodSize * 2).minusDays(1), group2.getRange().getTo());

        //When
        final boolean hasNext = sjukfallIterator.hasNext();

        //Then
        assertFalse(hasNext);
    }

    @Test
    public void testNextThrowsNoSuchElementExceptionIfTheIterationHasNoMoreElementsAccordingToContract() throws Exception {
        //Given
        final LocalDate fromDate = LocalDate.of(2015, 1, 1);
        final int periodSize = 1;
        final SjukfallIterator sjukfallIterator = new SjukfallIterator(fromDate, 2, periodSize,
            new Aisle(new HsaIdVardgivare(""), Collections.<Fact>emptyList()), SjukfallUtil.ALL_ENHETER);

        //When and then
        try {
            sjukfallIterator.next();
        } catch (NoSuchElementException e) {
            fail("Should not fail yet, there are still elements left");
        }
        try {
            sjukfallIterator.next();
        } catch (NoSuchElementException e) {
            fail("Should not fail yet, there are still elements left");
        }
        try {
            sjukfallIterator.next();
        } catch (NoSuchElementException e) {
            return; //Success, there are no more elements left
        }
        fail("Should have failed at last call to next(), there are no elements left");
    }

}
