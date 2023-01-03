/*
 * Copyright (C) 2023 Inera AB (http://www.inera.se)
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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.Test;
import se.inera.statistics.integration.hsa.model.HsaIdLakare;
import se.inera.statistics.service.report.model.Kon;

public class SjukfallTest {

    @Test
    public void testConstructorNewSjukfall() throws Exception {
        //When
        SjukfallExtended result = new SjukfallExtended(
            FactBuilder.newFact(1L, 1, 1, 1, 1, 1,  1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, new int[]{1}, 1));

        //Then
        assertEquals(1, result.getAlder());
        assertEquals(1, result.getDiagnosavsnitt());
        assertEquals(1, result.getDiagnoskapitel());
        assertEquals(1, result.getDiagnoskategori());
        assertEquals(1, result.getFactCount());
        assertEquals(1, result.getRealDays());
        assertEquals(1, result.getSjukskrivningsgrad());
        assertEquals(1, result.getStart());
        assertEquals(Kon.MALE, Kon.byNumberRepresentation(result.getKonInt()));
        assertArrayEquals(new Object[]{"1"}, result.getLakare().stream().map(lakare -> lakare.getId().getId()).toArray());

        assertEquals("01", result.getLanskod());
        assertEquals(false, result.isExtended());
        assertEquals(1, result.getIntygCountIncludingBeforeCurrentPeriod());
    }

    @Test
    public void testConstructorExtendSjukfall() throws Exception {
        //Given
        SjukfallExtended sjukfall = new SjukfallExtended(
            FactBuilder.newFact(1L, 1, 1, 1, 1, 1,  1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, new int[]{1}, 1));

        //When
        SjukfallExtended result = new SjukfallExtended(sjukfall,
            FactBuilder.newFact(2L, 2, 2, 2, 2, 2,  2, 2, 2, 3, 2, 2, 2, 2, 2, 2, 2, 2, 2, new int[]{2}, 2));

        //Then
        assertEquals(2, result.getAlder());
        assertEquals(2, result.getDiagnosavsnitt());
        assertEquals(2, result.getDiagnoskapitel());
        assertEquals(2, result.getDiagnoskategori());
        assertEquals(3, result.getEnd());
        assertEquals(2, result.getFactCount());
        assertEquals(3, result.getRealDays());
        assertEquals(2, result.getSjukskrivningsgrad());
        assertEquals(1, result.getStart());
        assertEquals(Kon.byNumberRepresentation(2), Kon.byNumberRepresentation(result.getKonInt()));
        final List<HsaIdLakare> lakare = result.getLakare().stream().map(lakare1 -> lakare1.getId()).collect(Collectors.toList());
        assertEquals(2, lakare.size());
        assertTrue(lakare.contains(new HsaIdLakare("1")));
        assertTrue(lakare.contains(new HsaIdLakare("2")));
        assertEquals("02", result.getLanskod());
        assertTrue(result.isExtended());
        assertEquals(2, result.getIntygCountIncludingBeforeCurrentPeriod());
    }

    @Test
    public void testJoinExtendWith5DaysGap() throws Exception {
        //Given
        final int gap = 5;
        final int orgStart = 1;
        final int orgSlut = 10;
        final int newStart = orgSlut + gap + 1;
        final SjukfallExtended orgSjukfall = new SjukfallExtended(
            FactBuilder.newFact(1L, 0, 0, 0, 0, 0,  0, 0, orgStart, orgSlut, 0, 0, 0, 0, 0, 0, 0, 0, 0, new int[0], 0));
        final Fact fact = FactBuilder.newFact(1L, 0, 0, 0, 0, 0,  0, 0, newStart, newStart, 0, 0, 0, 0, 0, 0, 1, 0, 0, new int[0], 0);

        //When
        final SjukfallExtended newSjukfall = orgSjukfall.join(fact);

        //Then
        assertTrue(newSjukfall.isExtended());
    }

    @Test
    public void testJoinDoNotExtendWith6DaysGap() throws Exception {
        //Given
        final int gap = 6;
        final int orgStart = 1;
        final int orgSlut = 10;
        final int newStart = orgSlut + gap + 1;
        final SjukfallExtended orgSjukfall = new SjukfallExtended(
            FactBuilder.newFact(1L, 0, 0, 0, 0, 0,  0, 0, orgStart, orgSlut, 0, 0, 0, 0, 0, 0, 0, 0, 0, new int[0], 0));
        final Fact fact = FactBuilder.newFact(1L, 0, 0, 0, 0, 0,  0, 0, newStart, newStart, 0, 0, 0, 0, 0, 0, 1, 0, 0, new int[0], 0);

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
        final SjukfallExtended sjukfall1 = new SjukfallExtended(createFact(2, 4, 1L));
        final SjukfallExtended sjukfall2 = sjukfall1.extendSjukfallWithNewStart(createFact(10, 3, 2L));

        //Then
        assertEquals(7, sjukfall2.getRealDays());
        assertEquals(2, sjukfall2.getIntygCountIncludingBeforeCurrentPeriod());
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
        final SjukfallExtended sjukfall1 = new SjukfallExtended(createFact(2, 4, 1L));
        final SjukfallExtended sjukfall2 = new SjukfallExtended(sjukfall1, createFact(20, 4, 2L));
        final SjukfallExtended sjukfall3 = sjukfall2.extendWithRealDaysWithinPeriod(new SjukfallExtended(createFact(10, 2, 3L)));

        //Then
        assertEquals(10, sjukfall3.getRealDays());
        assertEquals(3, sjukfall3.getIntygCountIncludingBeforeCurrentPeriod());
    }

    @Test
    public void testExtendWithRealDaysWithinPeriodPeriodAlreadyCovered() throws Exception {
        //When
        final SjukfallExtended sjukfall1 = new SjukfallExtended(createFact(2, 4));
        final SjukfallExtended sjukfall2 = new SjukfallExtended(sjukfall1, createFact(20, 4));
        final SjukfallExtended sjukfall3 = sjukfall2.extendWithRealDaysWithinPeriod(new SjukfallExtended(createFact(10, 12)));

        //Then
        assertEquals(18, sjukfall3.getRealDays());
    }

    @Test
    public void testExtendWithRealDaysAfterPeriod() throws Exception {
        //When
        final SjukfallExtended sjukfall1 = new SjukfallExtended(createFact(2, 4, 1L));
        final SjukfallExtended sjukfall2 = new SjukfallExtended(sjukfall1, createFact(20, 4, 2L));
        final SjukfallExtended sjukfall3 = sjukfall2.extendWithRealDaysWithinPeriod(new SjukfallExtended(createFact(30, 12, 3L)));

        //Then
        assertEquals(8, sjukfall3.getRealDays());
        assertEquals(2, sjukfall3.getIntygCountIncludingBeforeCurrentPeriod());
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
        return FactBuilder
            .newFact(1L, 1, 1, 1, 1, 1,  1, 1, startdatum, startdatum + sjukskrivningslangd - 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, new int[0], 1);
    }

    private Fact createFact(int startdatum, int sjukskrivningslangd, int diagnoskapitel) {
        return FactBuilder
            .newFact(1L, 1, 1, 1, 1, 1,  1, 1, startdatum, startdatum + sjukskrivningslangd - 1, 1, 1, diagnoskapitel, 1, 1, 1, 1, 1, 1,
                new int[0], 1);
    }

    private Fact createFact(int startdatum, int sjukskrivningslangd, long intygsid) {
        return FactBuilder
            .newFact(1L, 1, 1, 1, 1, 1,  intygsid, 1, startdatum, startdatum + sjukskrivningslangd - 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, new int[0],
                1);
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
        final SjukfallExtended sjukfall1 = new SjukfallExtended(
            FactBuilder.newFact(1L, 1, 1, 1, 1, 1,  firstIntygId, 1, 2, 5, 1, 1, 1, 1, 1, 1, 1, 1, 1, new int[0], 1));
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
        Fact factForSecondIntygsId = FactBuilder.newFact(1L, 1, 1, 1, 1, 1,  2, 1, 1, 11, 1, 1, 1, 1, 1, 1, 1, 1, 1, new int[0], 1);
        final SjukfallExtended sjukfall2 = sjukfall1.extendSjukfall(new SjukfallExtended(factForSecondIntygsId));

        //Add another sjukfall which originates from a third intyg - 20 days
        Fact factForThirdIntygsId = FactBuilder.newFact(1L, 1, 1, 1, 1, 1,  3, 1, 2, 21, 1, 1, 1, 1, 1, 1, 1, 1, 1, new int[0], 1);
        final SjukfallExtended sjukfall3 = sjukfall2.extendSjukfall(new SjukfallExtended(factForThirdIntygsId));

        //Then
        assertEquals(factForSecondIntygsId.getLakarintyg(), sjukfall3.getFirstIntygId());
        assertEquals(21, sjukfall3.getRealDays()); // includes length from all
    }

    private Fact createFact(int lakarintyg, int startdatum, int diagnosavsnitt, int sjukskrivningsgrad) {
        return FactBuilder
            .newFact(1L, 1, 1, 1, 1, 1,  lakarintyg, 1, startdatum, 1, 1, 1, 1, diagnosavsnitt, 1, 1, sjukskrivningsgrad, 1, 1, new int[0], 1);
    }

    @Test
    public void testFirstAndLastDx() {
        final SjukfallExtended se = new SjukfallExtended(createFactDxOrder(1, 10, 20, 5));
        final SjukfallExtended se2 = se.extendSjukfall(createFactDxOrder(2, 5, 8, 6));
        final SjukfallExtended se3 = se2.extendSjukfall(createFactDxOrder(3, 9, 9, 7));
        final Sjukfall sjukfall = Sjukfall.create(se3);
        assertEquals(6, sjukfall.getFirstDx().getDiagnosavsnitt());
        assertEquals(5, sjukfall.getLastDx().getDiagnosavsnitt());
    }

    private Fact createFactDxOrder(int lakarintyg, int startdatum, int slutdatum, int diagnosavsnitt) {
        return FactBuilder
            .newFact(1L, 1, 1, 1, 1, 1,  lakarintyg, 1, startdatum, slutdatum, 1, 1, 1, diagnosavsnitt, 1, 1, 1, 1, 1, new int[0], 1);
    }

}
