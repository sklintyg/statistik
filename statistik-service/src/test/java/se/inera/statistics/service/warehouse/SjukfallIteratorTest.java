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
package se.inera.statistics.service.warehouse;

import com.google.common.base.Predicate;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import se.inera.statistics.service.report.model.Range;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class SjukfallIteratorTest {

    @Test
    public void testNext() throws Exception {
        //Given
        final LocalDate fromDate = new LocalDate(2015, 1, 1);
        final int periodSize = 1;
        final SjukfallIterator sjukfallIterator = new SjukfallIterator(fromDate, 2, periodSize, new Aisle("", Collections.<Fact>emptyList()), SjukfallUtil.ALL_ENHETER.getFilter(), false){
            @Override
            SjukfallCalculator getSjukfallCalculator(Aisle aisle, Predicate<Fact> filter, boolean useOriginalSjukfallStart, List<Range> ranges) {
                return Mockito.mock(SjukfallCalculator.class);
            }
        };

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
        assertEquals(false, hasNext);
    }
}
