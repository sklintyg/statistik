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
import org.junit.Test;
import org.springframework.util.ReflectionUtils;
import se.inera.statistics.service.report.model.Range;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.Assert.*;

public class SjukfallIteratorTest {

    @Test
    public void testExtendSjukfallIsCorrectlySetWhenUsingAllEnheterFilterConstant() throws Exception {
        final int[] invocations = new int[]{0};
        new SjukfallIterator(new LocalDate(), 1, 1, new Aisle(""), SjukfallUtil.ALL_ENHETER.getFilter(), false) {
            @Override
            SjukfallCalculator createSjukfallCalculator(Aisle aisle, Predicate<Fact> filter, boolean useOriginalSjukfallStart, boolean extendSjukfall, List<Range> ranges) {
                assertEquals(false, extendSjukfall);
                invocations[0] += 1;
                return null;
            }
        };
        assertEquals(1, invocations[0]);
    }

    @Test
    public void testExtendSjukfallIsCorrectlySetWhenUsingCustomFilter() throws Exception {
        final int[] invocations = new int[]{0};
        final Predicate<Fact> filter = new Predicate<Fact>() {
            @Override
            public boolean apply(Fact fact) {
                return true;
            }
        };
        new SjukfallIterator(new LocalDate(), 1, 1, new Aisle(""), filter, false) {
            @Override
            SjukfallCalculator createSjukfallCalculator(Aisle aisle, Predicate<Fact> filter, boolean useOriginalSjukfallStart, boolean extendSjukfall, List<Range> ranges) {
                assertEquals(true, extendSjukfall);
                invocations[0] += 1;
                return null;
            }
        };
        assertEquals(1, invocations[0]);
    }

}
