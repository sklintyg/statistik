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

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.junit.Test;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.Range;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class SjukfallTest {

    @Test
    public void testConstructorNewSjukfall() throws Exception {
        //When
        Sjukfall result = new Sjukfall(new Fact(1, 1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,new int[]{1},1));

        //Then
        assertEquals(1, result.getAlder());
        assertEquals(1, result.getDiagnosavsnitt());
        assertEquals(1, result.getDiagnoskapitel());
        assertEquals(1, result.getDiagnoskategori());
        assertEquals(1, result.getEnd());
        assertEquals(1, result.getIntygCount());
        assertEquals(1, result.getRealDays());
        assertEquals(1, result.getSjukskrivningsgrad(null));
        assertEquals(1, result.getStart());
        assertEquals(Kon.Male, result.getKon());
        assertArrayEquals(new Object[]{1}, Lists.transform(new ArrayList<>(result.getLakare()), new Function<Lakare, Integer>() {
            @Override
            public Integer apply(Lakare lakare) {
                return lakare.getId();
            }
        }).toArray());

        assertEquals("01", result.getLanskod());
        assertEquals(false, result.isExtended());
    }

    @Test
    public void testConstructorExtendSjukfall() throws Exception {
        //Given
        Sjukfall sjukfall = new Sjukfall(new Fact(1, 1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,new int[]{1},1));

        //When
        Sjukfall result = new Sjukfall(sjukfall, new Fact(2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, new int[]{2}, 2));

        //Then
        assertEquals(2, result.getAlder());
        assertEquals(2, result.getDiagnosavsnitt());
        assertEquals(2, result.getDiagnoskapitel());
        assertEquals(2, result.getDiagnoskategori());
        assertEquals(3, result.getEnd());
        assertEquals(2, result.getIntygCount());
        assertEquals(3, result.getRealDays());
        assertEquals(2, result.getSjukskrivningsgrad(null));
        assertEquals(1, result.getStart());
        assertEquals(Kon.byNumberRepresentation(2), result.getKon());
        final List<Integer> lakare = Lists.transform(new ArrayList<>(result.getLakare()), new Function<Lakare, Integer>() {
            @Override
            public Integer apply(Lakare lakare) {
                return lakare.getId();
            }
        });
        assertEquals(2, lakare.size());
        assertTrue(lakare.contains(1));
        assertTrue(lakare.contains(2));
        assertEquals("02", result.getLanskod());
        assertEquals(true, result.isExtended());
    }

    @Test
    public void testJoinExtendWith5DaysGap() throws Exception {
        //Given
        final int gap = 5;
        final int orgStart = 1;
        final int orgSlut = 10;
        final int newStart = orgSlut + gap + 1;
        final Sjukfall orgSjukfall = new Sjukfall(new Fact(0, 0, 0, 0, 0, 0, orgStart, 0, 0, 0, 0, 0, 0, orgSlut - orgStart + 1, 0, 0, new int[0], 0));
        final Fact fact = new Fact(0, 0, 0, 0, 0, 0, newStart, 0, 0, 0, 0, 0, 0, 1, 0, 0, new int[0], 0);

        //When
        final Sjukfall newSjukfall = orgSjukfall.join(fact);

        //Then
        assertEquals(true, newSjukfall.isExtended());
    }

    @Test
    public void testJoinDoNotExtendWith6DaysGap() throws Exception {
        //Given
        final int gap = 6;
        final int orgStart = 1;
        final int orgSlut = 10;
        final int newStart = orgSlut + gap + 1;
        final Sjukfall orgSjukfall = new Sjukfall(new Fact(0, 0, 0, 0, 0, 0, orgStart, 0, 0, 0, 0, 0, 0, orgSlut - orgStart + 1, 0, 0, new int[0], 0));
        final Fact fact = new Fact(0, 0, 0, 0, 0, 0, newStart, 0, 0, 0, 0, 0, 0, 1, 0, 0, new int[0], 0);

        //When
        final Sjukfall newSjukfall = orgSjukfall.join(fact);

        //Then
        assertEquals(false, newSjukfall.isExtended());
    }

    @Test
    public void testGetRealDaysExtendingSjukfall() throws Exception {
        //When
        final Sjukfall sjukfall1 = new Sjukfall(createFact(2, 4));
        final Sjukfall sjukfall2 = sjukfall1.extendSjukfall(new Sjukfall(createFact(10, 4)));
        final Sjukfall sjukfall3 = sjukfall2.extendSjukfall(new Sjukfall(createFact(6, 4)));

        //Then
        assertEquals(12, sjukfall3.getRealDays());
    }

    @Test
    public void testGetRealDaysExtendingSjukfallWithGap() throws Exception {
        //When
        final Sjukfall sjukfall1 = new Sjukfall(createFact(2, 4));
        final Sjukfall sjukfall2 = sjukfall1.extendSjukfall(new Sjukfall(createFact(10, 4)));

        //Then
        assertEquals(8, sjukfall2.getRealDays());
    }

    @Test
    public void testGetRealDaysExtendingSjukfallOverlapping() throws Exception {
        //When
        final Sjukfall sjukfall1 = new Sjukfall(createFact(2, 4));
        final Sjukfall sjukfall2 = sjukfall1.extendSjukfall(new Sjukfall(createFact(3, 4)));

        //Then
        assertEquals(5, sjukfall2.getRealDays());
    }

    @Test
    public void testGetRealDaysExtendingFact() throws Exception {
        //When
        final Sjukfall sjukfall1 = new Sjukfall(createFact(2, 4));
        final Sjukfall sjukfall2 = sjukfall1.extendSjukfall(createFact(10, 4));
        final Sjukfall sjukfall3 = sjukfall2.extendSjukfall(createFact(6, 4));

        //Then
        assertEquals(12, sjukfall3.getRealDays());
    }

    @Test
    public void testGetRealDaysExtendingFactWithGap() throws Exception {
        //When
        final Sjukfall sjukfall1 = new Sjukfall(createFact(2, 4));
        final Sjukfall sjukfall2 = sjukfall1.extendSjukfall(createFact(10, 4));

        //Then
        assertEquals(8, sjukfall2.getRealDays());
    }

    @Test
    public void testGetRealDaysExtendingFactOverlapping() throws Exception {
        //When
        final Sjukfall sjukfall1 = new Sjukfall(createFact(2, 4));
        final Sjukfall sjukfall2 = sjukfall1.extendSjukfall(createFact(3, 4));

        //Then
        assertEquals(5, sjukfall2.getRealDays());
    }

    @Test
    public void testGetRealDaysConstructExtendingSjukfall() throws Exception {
        //When
        final Sjukfall sjukfall1 = new Sjukfall(createFact(2, 4));
        final Sjukfall sjukfall2 = new Sjukfall(sjukfall1, new Sjukfall(createFact(10, 4)));
        final Sjukfall sjukfall3 = new Sjukfall(sjukfall2, new Sjukfall(createFact(6, 4)));

        //Then
        assertEquals(12, sjukfall3.getRealDays());
    }

    @Test
    public void testGetRealDaysConstructExtendingSjukfallWithGap() throws Exception {
        //When
        final Sjukfall sjukfall1 = new Sjukfall(createFact(2, 4));
        final Sjukfall sjukfall2 = new Sjukfall(sjukfall1, new Sjukfall(createFact(10, 4)));

        //Then
        assertEquals(8, sjukfall2.getRealDays());
    }

    @Test
    public void testGetRealDaysConstructExtendingSjukfallOverlapping() throws Exception {
        //When
        final Sjukfall sjukfall1 = new Sjukfall(createFact(2, 4));
        final Sjukfall sjukfall2 = new Sjukfall(sjukfall1, new Sjukfall(createFact(3, 4)));

        //Then
        assertEquals(5, sjukfall2.getRealDays());
    }

    @Test
    public void testGetRealDaysConstructExtendingFact() throws Exception {
        //When
        final Sjukfall sjukfall1 = new Sjukfall(createFact(2, 4));
        final Sjukfall sjukfall2 = new Sjukfall(sjukfall1, createFact(10, 4));
        final Sjukfall sjukfall3 = new Sjukfall(sjukfall2, createFact(6, 4));

        //Then
        assertEquals(12, sjukfall3.getRealDays());
    }

    @Test
    public void testGetRealDaysConstructExtendingFactWithGap() throws Exception {
        //When
        final Sjukfall sjukfall1 = new Sjukfall(createFact(2, 4));
        final Sjukfall sjukfall2 = new Sjukfall(sjukfall1, createFact(10, 4));

        //Then
        assertEquals(8, sjukfall2.getRealDays());
    }

    @Test
    public void testGetRealDaysConstructExtendingFactOverlapping() throws Exception {
        //When
        final Sjukfall sjukfall1 = new Sjukfall(createFact(2, 4));
        final Sjukfall sjukfall2 = new Sjukfall(sjukfall1, createFact(3, 4));

        //Then
        assertEquals(5, sjukfall2.getRealDays());
    }

    @Test
    public void testGetRealDaysExtendingWithNewStart() throws Exception {
        //When
        final Sjukfall sjukfall1 = new Sjukfall(createFact(2, 4));
        final Sjukfall sjukfall2 = sjukfall1.extendSjukfallWithNewStart(10, 3);

        //Then
        assertEquals(7, sjukfall2.getRealDays());
    }

    @Test
    public void testGetRealDaysExtendingWithNewStartOverlapping() throws Exception {
        //When
        final Sjukfall sjukfall1 = new Sjukfall(createFact(2, 4));
        final Sjukfall sjukfall2 = sjukfall1.extendSjukfallWithNewStart(4, 3);

        //Then
        assertEquals(5, sjukfall2.getRealDays());
    }

    @Test
    public void testExtendWithRealDaysWithinPeriod() throws Exception {
        //When
        final Sjukfall sjukfall1 = new Sjukfall(createFact(2, 4));
        final Sjukfall sjukfall2 = new Sjukfall(sjukfall1, createFact(20, 4));
        final Sjukfall sjukfall3 = sjukfall2.extendWithRealDaysWithinPeriod(new Sjukfall(createFact(10, 2)));

        //Then
        assertEquals(10, sjukfall3.getRealDays());
    }

    @Test
    public void testGetDiagnoskapitelForOverlappingUnorderedIntygWithinPeriod() throws Exception {
        //Given
        final int diagnoskapitel1 = 2;
        final Sjukfall sjukfall1 = new Sjukfall(createFact(2, 10, diagnoskapitel1));
        final int diagnoskapitel2 = 3;
        final Sjukfall sjukfall2 = new Sjukfall(sjukfall1, createFact(3, 4, diagnoskapitel2));
        final int diagnoskapitel3 = 4;
        final Sjukfall sjukfall3 = new Sjukfall(sjukfall2, createFact(1, 30, diagnoskapitel3));

        //When
        final int diagnoskapitel = sjukfall3.getDiagnoskapitel(new Range(WidelineConverter.toDate(6), WidelineConverter.toDate(20)));

        //Then
        assertEquals(diagnoskapitel2, diagnoskapitel);
    }

    @Test
    public void testGetDiagnoskapitelForOverlappingUnorderedIntygWithOneOutsidePeriod() throws Exception {
        //Given
        final int diagnoskapitel1 = 2;
        final Sjukfall sjukfall1 = new Sjukfall(createFact(2, 10, diagnoskapitel1));
        final int diagnoskapitel2 = 3;
        final Sjukfall sjukfall2 = new Sjukfall(sjukfall1, createFact(3, 4, diagnoskapitel2));
        final Sjukfall sjukfall3 = new Sjukfall(sjukfall2, createFact(1, 30, diagnoskapitel2));

        //When
        final int diagnoskapitel = sjukfall3.getDiagnoskapitel(new Range(WidelineConverter.toDate(10), WidelineConverter.toDate(20)));

        //Then
        assertEquals(diagnoskapitel1, diagnoskapitel);
    }

    private Fact createFact(int startdatum, int sjukskrivningslangd) {
        return new Fact(1,1,1,1,1,1, startdatum,1,1, 1,1,1,1, sjukskrivningslangd,1,1,new int[0], 1);
    }

    private Fact createFact(int startdatum, int sjukskrivningslangd, int diagnoskapitel) {
        return new Fact(1,1,1,1,1,1, startdatum,1,1, diagnoskapitel,1,1,1, sjukskrivningslangd,1,1,new int[0], 1);
    }

}
