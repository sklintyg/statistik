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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import se.inera.statistics.service.report.model.Kon;

public class SjukfallTest {

    @Test
    public void testConstructorNewSjukfall() throws Exception {
        //When
        SjukfallExtended result = new SjukfallExtended(new Fact(1L, 1, 1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,new int[]{1},1,false));

        //Then
        assertEquals(1, result.getAlder());
        assertEquals(1, result.getDiagnosavsnitt());
        assertEquals(1, result.getDiagnoskapitel());
        assertEquals(1, result.getDiagnoskategori());
        assertEquals(1, result.getIntygCount());
        assertEquals(1, result.getRealDays());
        assertEquals(1, result.getSjukskrivningsgrad());
        assertEquals(1, result.getStart());
        assertEquals(Kon.MALE, result.getKon());
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
        SjukfallExtended sjukfall = new SjukfallExtended(new Fact(1L, 1, 1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,new int[]{1},1,false));

        //When
        SjukfallExtended result = new SjukfallExtended(sjukfall, new Fact(2L, 2, 2, 2, 2, 2, 2, 2, 3, 2, 2, 2, 2, 2, 2, 2, 2, 2, new int[]{2}, 2, false));

        //Then
        assertEquals(2, result.getAlder());
        assertEquals(2, result.getDiagnosavsnitt());
        assertEquals(2, result.getDiagnoskapitel());
        assertEquals(2, result.getDiagnoskategori());
        assertEquals(3, result.getEnd());
        assertEquals(2, result.getIntygCount());
        assertEquals(3, result.getRealDays());
        assertEquals(2, result.getSjukskrivningsgrad());
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
        final SjukfallExtended orgSjukfall = new SjukfallExtended(new Fact(1L, 0, 0, 0, 0, 0, 0, orgStart, orgSlut, 0, 0, 0, 0, 0, 0, 0, 0, 0, new int[0], 0, false));
        final Fact fact = new Fact(1L, 0, 0, 0, 0, 0, 0, newStart, newStart, 0, 0, 0, 0, 0, 0, 1, 0, 0, new int[0], 0, false);

        //When
        final SjukfallExtended newSjukfall = orgSjukfall.join(fact);

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
        final SjukfallExtended orgSjukfall = new SjukfallExtended(new Fact(1L, 0, 0, 0, 0, 0, 0, orgStart, orgSlut, 0, 0, 0, 0, 0, 0, 0, 0, 0, new int[0], 0,false));
        final Fact fact = new Fact(1L, 0, 0, 0, 0, 0, 0, newStart, newStart, 0, 0, 0, 0, 0, 0, 1, 0, 0, new int[0], 0,false);

        //When
        final SjukfallExtended newSjukfall = orgSjukfall.join(fact);

        //Then
        assertEquals(false, newSjukfall.isExtended());
    }

    @Test
    public void testGetRealDaysExtendingSjukfall() throws Exception {
        //When
        final SjukfallExtended sjukfall1 = new SjukfallExtended(createFact(2, 4));
        final SjukfallExtended sjukfall2 = sjukfall1.extendSjukfall(new SjukfallExtended(createFact(10, 4)));
        final SjukfallExtended sjukfall3 = sjukfall2.extendSjukfall(new SjukfallExtended(createFact(6, 4)));

        //Then
        assertEquals(12, sjukfall3.getRealDays());
    }

    @Test
    public void testGetRealDaysExtendingSjukfallWithGap() throws Exception {
        //When
        final SjukfallExtended sjukfall1 = new SjukfallExtended(createFact(2, 4));
        final SjukfallExtended sjukfall2 = sjukfall1.extendSjukfall(new SjukfallExtended(createFact(10, 4)));

        //Then
        assertEquals(8, sjukfall2.getRealDays());
    }

    @Test
    public void testGetRealDaysExtendingSjukfallOverlapping() throws Exception {
        //When
        final SjukfallExtended sjukfall1 = new SjukfallExtended(createFact(2, 4));
        final SjukfallExtended sjukfall2 = sjukfall1.extendSjukfall(new SjukfallExtended(createFact(3, 4)));

        //Then
        assertEquals(5, sjukfall2.getRealDays());
    }

    @Test
    public void testGetRealDaysExtendingFact() throws Exception {
        //When
        final SjukfallExtended sjukfall1 = new SjukfallExtended(createFact(2, 4));
        final SjukfallExtended sjukfall2 = sjukfall1.extendSjukfall(createFact(10, 4));
        final SjukfallExtended sjukfall3 = sjukfall2.extendSjukfall(createFact(6, 4));

        //Then
        assertEquals(12, sjukfall3.getRealDays());
    }

    @Test
    public void testGetRealDaysExtendingFactWithGap() throws Exception {
        //When
        final SjukfallExtended sjukfall1 = new SjukfallExtended(createFact(2, 4));
        final SjukfallExtended sjukfall2 = sjukfall1.extendSjukfall(createFact(10, 4));

        //Then
        assertEquals(8, sjukfall2.getRealDays());
    }

    @Test
    public void testGetRealDaysExtendingFactOverlapping() throws Exception {
        //When
        final SjukfallExtended sjukfall1 = new SjukfallExtended(createFact(2, 4));
        final SjukfallExtended sjukfall2 = sjukfall1.extendSjukfall(createFact(3, 4));

        //Then
        assertEquals(5, sjukfall2.getRealDays());
    }

    @Test
    public void testGetRealDaysConstructExtendingSjukfall() throws Exception {
        //When
        final SjukfallExtended sjukfall1 = new SjukfallExtended(createFact(2, 4));
        final SjukfallExtended sjukfall2 = new SjukfallExtended(sjukfall1, new SjukfallExtended(createFact(10, 4)));
        final SjukfallExtended sjukfall3 = new SjukfallExtended(sjukfall2, new SjukfallExtended(createFact(6, 4)));

        //Then
        assertEquals(12, sjukfall3.getRealDays());
    }

    @Test
    public void testGetRealDaysConstructExtendingSjukfallWithGap() throws Exception {
        //When
        final SjukfallExtended sjukfall1 = new SjukfallExtended(createFact(2, 4));
        final SjukfallExtended sjukfall2 = new SjukfallExtended(sjukfall1, new SjukfallExtended(createFact(10, 4)));

        //Then
        assertEquals(8, sjukfall2.getRealDays());
    }

    @Test
    public void testGetRealDaysConstructExtendingSjukfallOverlapping() throws Exception {
        //When
        final SjukfallExtended sjukfall1 = new SjukfallExtended(createFact(2, 4));
        final SjukfallExtended sjukfall2 = new SjukfallExtended(sjukfall1, new SjukfallExtended(createFact(3, 4)));

        //Then
        assertEquals(5, sjukfall2.getRealDays());
    }

    @Test
    public void testGetRealDaysConstructExtendingFact() throws Exception {
        //When
        final SjukfallExtended sjukfall1 = new SjukfallExtended(createFact(2, 4));
        final SjukfallExtended sjukfall2 = new SjukfallExtended(sjukfall1, createFact(10, 4));
        final SjukfallExtended sjukfall3 = new SjukfallExtended(sjukfall2, createFact(6, 4));

        //Then
        assertEquals(12, sjukfall3.getRealDays());
    }

    @Test
    public void testGetRealDaysConstructExtendingFactWithGap() throws Exception {
        //When
        final SjukfallExtended sjukfall1 = new SjukfallExtended(createFact(2, 4));
        final SjukfallExtended sjukfall2 = new SjukfallExtended(sjukfall1, createFact(10, 4));

        //Then
        assertEquals(8, sjukfall2.getRealDays());
    }

    @Test
    public void testGetRealDaysConstructExtendingFactOverlapping() throws Exception {
        //When
        final SjukfallExtended sjukfall1 = new SjukfallExtended(createFact(2, 4));
        final SjukfallExtended sjukfall2 = new SjukfallExtended(sjukfall1, createFact(3, 4));

        //Then
        assertEquals(5, sjukfall2.getRealDays());
    }

    @Test
    public void testGetRealDaysExtendingWithNewStart() throws Exception {
        //When
        final SjukfallExtended sjukfall1 = new SjukfallExtended(createFact(2, 4));
        final SjukfallExtended sjukfall2 = sjukfall1.extendSjukfallWithNewStart(createFact(10, 3));

        //Then
        assertEquals(7, sjukfall2.getRealDays());
    }

    @Test
    public void testGetRealDaysExtendingWithNewStartOverlapping() throws Exception {
        //When
        final SjukfallExtended sjukfall1 = new SjukfallExtended(createFact(2, 4));
        final SjukfallExtended sjukfall2 = sjukfall1.extendSjukfallWithNewStart(createFact(4, 3));

        //Then
        assertEquals(5, sjukfall2.getRealDays());
    }

    @Test
    public void testExtendWithRealDaysWithinPeriod() throws Exception {
        //When
        final SjukfallExtended sjukfall1 = new SjukfallExtended(createFact(2, 4));
        final SjukfallExtended sjukfall2 = new SjukfallExtended(sjukfall1, createFact(20, 4));
        final SjukfallExtended sjukfall3 = sjukfall2.extendWithRealDaysWithinPeriod(new SjukfallExtended(createFact(10, 2)));

        //Then
        assertEquals(10, sjukfall3.getRealDays());
    }

    @Test
    public void testGetDiagnoskapitelForOverlappingUnorderedIntygSelectsDxWithLatestStartDate() throws Exception {
        //Given
        final int diagnoskapitel1 = 2;
        final SjukfallExtended sjukfall1 = new SjukfallExtended(createFact(2, 10, diagnoskapitel1));
        final int diagnoskapitel2 = 3;
        final SjukfallExtended sjukfall2 = new SjukfallExtended(sjukfall1, createFact(3, 4, diagnoskapitel2));
        final int diagnoskapitel3 = 4;
        final SjukfallExtended sjukfall3 = new SjukfallExtended(sjukfall2, createFact(1, 30, diagnoskapitel3));

        //When
        final int diagnoskapitel = sjukfall3.getDiagnoskapitel();

        //Then
        assertEquals(diagnoskapitel2, diagnoskapitel);
    }

    private Fact createFact(int startdatum, int sjukskrivningslangd) {
        return new Fact(1L, 1,1,1,1,1,1, startdatum,startdatum + sjukskrivningslangd - 1,1,1,1,1,1,1,1,1,1,new int[0], 1,false);
    }

    private Fact createFact(int startdatum, int sjukskrivningslangd, int diagnoskapitel) {
        return new Fact(1L, 1,1,1,1,1,1, startdatum,startdatum + sjukskrivningslangd - 1,1,1, diagnoskapitel,1,1,1,1,1,1,new int[0], 1,false);
    }

    @Test
    public void testGetLastFactDifferentStartDateSTATISTIK1060() throws Exception {
        //Given
        final SjukfallExtended sjukfallExtended = new SjukfallExtended(createFact(2, 3, 2, 2)).extendSjukfall(createFact(2, 2, 1, 2));

        //When
        final Sjukfall sjukfall = Sjukfall.create(sjukfallExtended);

        //Then
        assertEquals(2, sjukfall.getDiagnosavsnitt());
    }

    @Test
    public void testGetLastFactSameStartDateButDifferentLakarintygSTATISTIK1060() throws Exception {
        //Given
        final SjukfallExtended sjukfallExtended = new SjukfallExtended(createFact(2, 2, 2, 2)).extendSjukfall(createFact(1, 2, 1, 2));

        //When
        final Sjukfall sjukfall = Sjukfall.create(sjukfallExtended);

        //Then
        assertEquals(2, sjukfall.getDiagnosavsnitt());
    }

    @Test
    public void testGetLastFactSameStartDateAndSameDifferentLakarintygButDifferentSjukskrivningsgradSTATISTIK1060() throws Exception {
        //Given
        final SjukfallExtended sjukfallExtended = new SjukfallExtended(createFact(2, 2, 2, 2)).extendSjukfall(createFact(2, 2, 1, 3));

        //When
        final Sjukfall sjukfall = Sjukfall.create(sjukfallExtended);

        //Then
        assertEquals(1, sjukfall.getDiagnosavsnitt());
    }

    @Test
    public void testGetRealDaysFirstIntygConstructExtendingSjukfallOnSameIntyg() throws Exception {
        //When
        final int firstIntygId = 17;
        final SjukfallExtended sjukfall1 = new SjukfallExtended(new Fact(1, 1, 1, 1, firstIntygId, 1, 2, 5, 1, 1, 1, 1, 1, 1, 1, 1, 1, new int[0], 1, false));
        final SjukfallExtended sjukfall2 = new SjukfallExtended(sjukfall1, new SjukfallExtended(createFact(10, 4)));
        final SjukfallExtended sjukfall3 = new SjukfallExtended(sjukfall2, new SjukfallExtended(createFact(6, 4)));

        //Then
        assertEquals(firstIntygId, sjukfall3.getFirstIntygId());
        assertEquals(12, sjukfall3.getRealDays());
    }

    @Test
    public void testGetRealDaysFirstIntygOnlyConsidersFactsFromFirstIntygSameStartDateDifferentIntygsId() throws Exception {
        //When
        final SjukfallExtended sjukfall1 = new SjukfallExtended(createFact(2, 4));

        //Add a sjukfall which originates from a second intyg - 11 days - has the LOWEST startdate
        Fact factForSecondIntygsId = new Fact(1, 1, 1, 1, 2, 1, 1, 11, 1, 1, 1, 1, 1, 1, 1, 1, 1, new int[0], 1, false);
        final SjukfallExtended sjukfall2 = sjukfall1.extendSjukfall(new SjukfallExtended(factForSecondIntygsId));

        //Add another sjukfall which originates from a third intyg - 20 days
        Fact factForThirdIntygsId = new Fact(1, 1, 1, 1, 3, 1, 2, 21, 1, 1, 1, 1, 1, 1, 1, 1, 1, new int[0], 1, false);
        final SjukfallExtended sjukfall3 = sjukfall2.extendSjukfall(new SjukfallExtended(factForThirdIntygsId));

        //Then
        assertEquals(factForSecondIntygsId.getLakarintyg(), sjukfall3.getFirstIntygId());
        assertEquals(21, sjukfall3.getRealDays()); // includes length from all
    }

    private Fact createFact(int lakarintyg, int startdatum, int diagnosavsnitt, int sjukskrivningsgrad) {
        return new Fact(1L, 1,1,1,1, lakarintyg,1, startdatum,1,1,1,1, diagnosavsnitt,1,1, sjukskrivningsgrad,1,1,new int[0],1,false);
    }

}
