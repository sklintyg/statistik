/**
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
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
import com.google.common.collect.ArrayListMultimap;
import org.junit.Test;
import org.springframework.util.ReflectionUtils;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.warehouse.sjukfallcalc.FactsPerPatientAndPeriodGrouper;
import se.inera.statistics.service.warehouse.sjukfallcalc.SjukfallPerPeriodCalculator;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SjukfallCalculatorTest {

    @Test
    public void testExtendSjukfallIsCorrectlySetWhenUsingAllEnheterFilterConstant() throws Exception {
        //Given
        final List<Range> ranges = SjukfallIterator.getRanges(LocalDate.of(2015, 4, 1), 2, 1);

        //When
        final SjukfallCalculator sjukfallCalculator = new SjukfallCalculator(new Aisle(new HsaIdVardgivare(""), Collections.<Fact>emptyList()), SjukfallUtil.ALL_ENHETER.getIntygFilter(), ranges, false);

        //Then
        final SjukfallPerPeriodCalculator sjukfallPerPeriodCalculator = (SjukfallPerPeriodCalculator) getField("sjukfallPerPeriodCalculator", SjukfallCalculator.class, sjukfallCalculator);
        final Boolean extendSjukfall = (Boolean) getField("extendSjukfall", SjukfallPerPeriodCalculator.class, sjukfallPerPeriodCalculator);
        assertEquals(false, extendSjukfall);
    }

    private Object getField(String name, Class clazz, Object obj) throws NoSuchFieldException {
        final Field declaredField = clazz.getDeclaredField(name);
        declaredField.setAccessible(true);
        return ReflectionUtils.getField(declaredField, obj);
    }

    @Test
    public void testExtendSjukfallIsCorrectlySetWhenUsingCustomFilter() throws Exception {
        //Given
        final List<Range> ranges = SjukfallIterator.getRanges(LocalDate.of(2015, 4, 1), 2, 1);
        final Predicate<Fact> filter = fact -> true;

        //When
        final SjukfallCalculator sjukfallCalculator = new SjukfallCalculator(new Aisle(new HsaIdVardgivare(""), Collections.<Fact>emptyList()), filter, ranges, false);

        //Then
        final SjukfallPerPeriodCalculator sjukfallPerPeriodCalculator = (SjukfallPerPeriodCalculator) getField("sjukfallPerPeriodCalculator", SjukfallCalculator.class, sjukfallCalculator);
        final Boolean extendSjukfall = (Boolean) getField("extendSjukfall", SjukfallPerPeriodCalculator.class, sjukfallPerPeriodCalculator);
        assertEquals(true, extendSjukfall);
    }

}
