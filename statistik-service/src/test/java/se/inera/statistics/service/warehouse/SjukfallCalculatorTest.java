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
import org.joda.time.LocalDate;
import org.junit.Test;
import org.springframework.util.ReflectionUtils;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.report.model.Range;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SjukfallCalculatorTest {

    @Test
    public void testGetFactsPerPatientAndPeriod() throws Exception {
        //Given
        final long patient = 1;
        final List<Fact> facts = Arrays.asList(createFact(patient, new LocalDate(2015, 2, 20)), createFact(patient, new LocalDate(2015, 3, 20)));
        final List<Range> ranges = SjukfallIterator.getRanges(new LocalDate(2015, 1, 1), 2, 1);

        //When
        final List<ArrayListMultimap<Long, Fact>> factsPerPatientAndPeriod = SjukfallCalculator.getFactsPerPatientAndPeriod(facts, ranges, false);

        //Then
        assertEquals(4, factsPerPatientAndPeriod.size());
        assertEquals(0, factsPerPatientAndPeriod.get(0).get(patient).size());
        assertEquals(0, factsPerPatientAndPeriod.get(1).get(patient).size());
        assertEquals(1, factsPerPatientAndPeriod.get(2).get(patient).size());
    }

    @Test
    public void testGetFactsPerPatientAndPeriod2() throws Exception {
        //Given
        final long patient = 1;
        final List<Fact> facts = Arrays.asList(createFact(patient, new LocalDate(2015, 2, 20)), createFact(patient, new LocalDate(2015, 3, 20)));
        final List<Range> ranges = SjukfallIterator.getRanges(new LocalDate(2015, 2, 1), 2, 1);

        //When
        final List<ArrayListMultimap<Long, Fact>> factsPerPatientAndPeriod = SjukfallCalculator.getFactsPerPatientAndPeriod(facts, ranges, false);

        //Then
        assertEquals(4, factsPerPatientAndPeriod.size());
        assertEquals(0, factsPerPatientAndPeriod.get(0).get(patient).size());
        assertEquals(1, factsPerPatientAndPeriod.get(1).get(patient).size());
        assertEquals(1, factsPerPatientAndPeriod.get(2).get(patient).size());
    }

    @Test
    public void testGetFactsPerPatientAndPeriod3() throws Exception {
        //Given
        final long patient = 1;
        final List<Fact> facts = Arrays.asList(createFact(patient, new LocalDate(2015, 2, 20)), createFact(patient, new LocalDate(2015, 3, 20)));
        final List<Range> ranges = SjukfallIterator.getRanges(new LocalDate(2015, 3, 1), 2, 1);

        //When
        final List<ArrayListMultimap<Long, Fact>> factsPerPatientAndPeriod = SjukfallCalculator.getFactsPerPatientAndPeriod(facts, ranges, false);

        //Then
        assertEquals(4, factsPerPatientAndPeriod.size());
        assertEquals(1, factsPerPatientAndPeriod.get(0).get(patient).size());
        assertEquals(1, factsPerPatientAndPeriod.get(1).get(patient).size());
        assertEquals(0, factsPerPatientAndPeriod.get(2).get(patient).size());
    }

    @Test
    public void testGetFactsPerPatientAndPeriod4() throws Exception {
        //Given
        final long patient = 1;
        final List<Fact> facts = Arrays.asList(createFact(patient, new LocalDate(2015, 2, 20)), createFact(patient, new LocalDate(2015, 3, 20)));
        final List<Range> ranges = SjukfallIterator.getRanges(new LocalDate(2015, 4, 1), 2, 1);

        //When
        final List<ArrayListMultimap<Long, Fact>> factsPerPatientAndPeriod = SjukfallCalculator.getFactsPerPatientAndPeriod(facts, ranges, false);

        //Then
        assertEquals(4, factsPerPatientAndPeriod.size());
        assertEquals(2, factsPerPatientAndPeriod.get(0).get(patient).size());
        assertEquals(0, factsPerPatientAndPeriod.get(1).get(patient).size());
        assertEquals(0, factsPerPatientAndPeriod.get(2).get(patient).size());
    }

    private Fact createFact(long patient, LocalDate startDatum) {
        final int start = WidelineConverter.toDay(startDatum);
        return new Fact(1,1,1,1,1, patient, start,start,1,1,1,1,1,1,1,1,1,new int[0],1,false);
    }

    @Test
    public void testExtendSjukfallIsCorrectlySetWhenUsingAllEnheterFilterConstant() throws Exception {
        //Given
        final List<Range> ranges = SjukfallIterator.getRanges(new LocalDate(2015, 4, 1), 2, 1);

        //When
        final SjukfallCalculator sjukfallCalculator = new SjukfallCalculator(new Aisle(new HsaIdVardgivare(""), Collections.<Fact>emptyList()), SjukfallUtil.ALL_ENHETER.getIntygFilter(), ranges, false);

        //Then
        final Boolean extendSjukfall = (Boolean) getField("extendSjukfall", sjukfallCalculator);
        assertEquals(false, extendSjukfall);
    }

    private Object getField(String name, Object obj) throws NoSuchFieldException {
        final Field declaredField = SjukfallCalculator.class.getDeclaredField(name);
        declaredField.setAccessible(true);
        return ReflectionUtils.getField(declaredField, obj);
    }

    @Test
    public void testExtendSjukfallIsCorrectlySetWhenUsingCustomFilter() throws Exception {
        //Given
        final List<Range> ranges = SjukfallIterator.getRanges(new LocalDate(2015, 4, 1), 2, 1);
        final Predicate<Fact> filter = new Predicate<Fact>() {
            @Override
            public boolean apply(Fact fact) {
                return true;
            }
        };

        //When
        final SjukfallCalculator sjukfallCalculator = new SjukfallCalculator(new Aisle(new HsaIdVardgivare(""), Collections.<Fact>emptyList()), filter, ranges, false);

        //Then
        final Boolean extendSjukfall = (Boolean) getField("extendSjukfall", sjukfallCalculator);
        assertEquals(true, extendSjukfall);
    }

}
