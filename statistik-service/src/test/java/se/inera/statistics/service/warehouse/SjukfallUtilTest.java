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
import com.google.common.base.Predicate;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Lists;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.KonDataResponse;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.warehouse.query.CounterFunction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static se.inera.statistics.service.report.model.Kon.Female;
import static se.inera.statistics.service.warehouse.Fact.aFact;

public class SjukfallUtilTest {
    private MutableAisle aisle;

    private SjukfallUtil sjukfallUtil;

    @Before
    public void setup() {
        sjukfallUtil = new SjukfallUtil();
        aisle = new MutableAisle(new HsaIdVardgivare("vgid"));
    }

    @Test
    public void oneIntygIsOneSjukfall() throws Exception {
        aisle.addLine(createFact(1, 1, 4010, 1, 47));
        Collection<Sjukfall> sjukfalls = calculateSjukfallsHelper();
        assertEquals(1, sjukfalls.size());
    }

    @Test
    public void twoCloseIntygForSamePersonIsOneSjukfall() throws Exception {
        aisle.addLine(createFact(1, 1, 4010, 10, 1, 1));
        aisle.addLine(createFact(1, 1, 4025, 10, 1, 2));
        Collection<Sjukfall> sjukfalls = calculateSjukfallsHelper();
        assertEquals(1, sjukfalls.size());

        Sjukfall sjukfall = sjukfalls.iterator().next();
        assertEquals(20, sjukfall.getRealDays());
        final List<Integer> lakare = Lists.transform(new ArrayList<>(sjukfall.getLakare()), new Function<Lakare, Integer>() {
            @Override
            public Integer apply(Lakare lakare) {
                return lakare.getId();
            }
        });
        assertEquals(2, lakare.size());
        assertTrue(lakare.contains(1));
        assertTrue(lakare.contains(2));
    }

    @Test
    public void twoFarSeparatedIntygForSamePersonAreTwoSjukfall() throws Exception {
        aisle.addLine(createFact(1, 1, 4010));
        aisle.addLine(createFact(1, 1, 4026));
        Collection<Sjukfall> sjukfalls = calculateSjukfallsHelper();
        assertEquals(2, sjukfalls.size());
    }

    @Test
    public void twoIntygForTwoPersonsAreTwoSjukfall() throws Exception {
        aisle.addLine(createFact(1, 1, 4010));
        aisle.addLine(createFact(1, 2, 4010));
        Collection<Sjukfall> sjukfalls = calculateSjukfallsHelper();
        assertEquals(2, sjukfalls.size());
    }

    @Test
    public void iterator() throws Exception {
        aisle.addLine(createFact(2, 1, 4010));
        aisle.addLine(createFact(1, 1, 4020));
        aisle.addLine(createFact(2, 1, 4030));

        aisle.addLine(createFact(2, 2, 4010));
        aisle.addLine(createFact(3, 2, 4020));
        aisle.addLine(createFact(2, 2, 4030));

        Iterator<SjukfallGroup> actives = sjukfallUtil.sjukfallGrupper(new LocalDate("2010-11-01"), 3, 1, aisle.createAisle(), createEnhetFilterFromInternalIntValues(2)).iterator();
        assertTrue(actives.next().getSjukfall().isEmpty());

        assertEquals(2, actives.next().getSjukfall().size());
        assertEquals(2, actives.next().getSjukfall().size());
        assertFalse(actives.hasNext());

        actives = sjukfallUtil.sjukfallGrupper(new LocalDate("2010-11-01"), 2, 12, aisle.createAisle(), createEnhetFilterFromInternalIntValues(2)).iterator();

        assertEquals(2, actives.next().getSjukfall().size());
        assertEquals(0, actives.next().getSjukfall().size());
        assertFalse(actives.hasNext());
    }

    private Fact createFact(int enhet, int patient, int startdatum) {
        return createFact(enhet, patient, startdatum, 10, 1, 1);
    }

    private Fact createFact(int enhet, int patient, int startdatum, int lakarintyg) {
        return createFact(enhet, patient, startdatum, 10, lakarintyg, 1);
    }

    private Fact createFact(int enhet, int patient, int startdatum, int lakarintyg, int sjukskrivningslangd) {
        return createFact(enhet, patient, startdatum, sjukskrivningslangd, lakarintyg, 1);
    }

    private Fact createFact(int enhet, int patient, int startdatum, int sjukskrivningslangd, int lakarintyg, int lakarId) {
        return aFact().withLan(3).withKommun(380).withForsamling(38002).withEnhet(enhet).
                    withLakarintyg(lakarintyg).withPatient(patient).withStartdatum(startdatum).withKon(Female).withAlder(45).
                    withDiagnoskapitel(0).withDiagnosavsnitt(14).withDiagnoskategori(16).withDiagnoskod(18).
                    withSjukskrivningsgrad(100).withSlutdatum(startdatum + sjukskrivningslangd - 1).
                    withLakarkon(Female).withLakaralder(32).withLakarbefattning(new int[]{201010}).withLakarid(lakarId).withEnkeltIntyg(false).build();
    }

    @Test
    public void oneSjukfalFromTwoDifferentEnhetsIsNotAffectedByEnhetFilter() throws Exception {
        int ENHET1 = 1;
        int ENHET2 = 2;
        LocalDate monthStart = new LocalDate("2010-11-01");
        aisle.addLine(createFact(ENHET1, 1, WidelineConverter.toDay(monthStart), 1));
        aisle.addLine(createFact(ENHET2, 1, WidelineConverter.toDay(monthStart.plusDays(10)), 2));
        aisle.addLine(createFact(ENHET1, 1, WidelineConverter.toDay(monthStart.plusDays(20)), 3));

        Iterator<SjukfallGroup> actives = sjukfallUtil.sjukfallGrupper(monthStart, 1, 1, aisle.createAisle(), createEnhetFilterFromInternalIntValues(ENHET1)).iterator();
        assertEquals(1, actives.next().getSjukfall().size());
    }

    @Test
    public void twoIntygsFarApartAreTwoSjukfalls() throws Exception {
        int ENHET1 = 1;
        LocalDate monthStart = new LocalDate("2010-11-01");
        aisle.addLine(createFact(ENHET1, 1, WidelineConverter.toDay(monthStart), 1));
        aisle.addLine(createFact(ENHET1, 1, WidelineConverter.toDay(monthStart.plusDays(21)), 3));

        Iterator<SjukfallGroup> actives = sjukfallUtil.sjukfallGrupper(monthStart, 1, 1, aisle.createAisle(), createEnhetFilterFromInternalIntValues(ENHET1)).iterator();
        assertEquals(2, actives.next().getSjukfall().size());
    }

    @Test
    public void testSjukfallCacheIsWorking() throws Exception {
        //Given
        LocalDate monthStart = new LocalDate("2015-03-11");

        //When
        final Iterable<SjukfallGroup> sjukfallGroups1 = sjukfallUtil.sjukfallGrupper(monthStart, 1, 1, aisle.createAisle(), SjukfallUtil.ALL_ENHETER);
        final Iterable<SjukfallGroup> sjukfallGroups2 = sjukfallUtil.sjukfallGrupper(monthStart, 1, 1, aisle.createAisle(), SjukfallUtil.ALL_ENHETER);
        final Iterable<SjukfallGroup> sjukfallGroups3 = sjukfallUtil.sjukfallGrupper(monthStart, 2, 1, aisle.createAisle(), SjukfallUtil.ALL_ENHETER);
        final Iterable<SjukfallGroup> sjukfallGroups4 = sjukfallUtil.sjukfallGrupper(monthStart, 1, 1, aisle.createAisle(), SjukfallUtil.ALL_ENHETER);

        //Then
        assertSame(sjukfallGroups1, sjukfallGroups2);
        assertNotSame(sjukfallGroups1, sjukfallGroups3);
        assertSame(sjukfallGroups1, sjukfallGroups4);
    }

    @Test
    public void testSjukfallCacheWillOnlyReturnValuesForTheCorrectVg() throws Exception {
        //Given
        LocalDate monthStart = new LocalDate("2015-03-11");
        MutableAisle aisle1 = new MutableAisle(new HsaIdVardgivare("vgid1"));
        MutableAisle aisle2 = new MutableAisle(new HsaIdVardgivare("vgid2"));

        //When
        final Iterable<SjukfallGroup> sjukfallGroups1 = sjukfallUtil.sjukfallGrupper(monthStart, 1, 1, aisle1.createAisle(), SjukfallUtil.ALL_ENHETER);
        final Iterable<SjukfallGroup> sjukfallGroups2 = sjukfallUtil.sjukfallGrupper(monthStart, 2, 1, aisle2.createAisle(), SjukfallUtil.ALL_ENHETER);
        final Iterable<SjukfallGroup> sjukfallGroups3 = sjukfallUtil.sjukfallGrupper(monthStart, 1, 1, aisle2.createAisle(), SjukfallUtil.ALL_ENHETER);
        final Iterable<SjukfallGroup> sjukfallGroups4 = sjukfallUtil.sjukfallGrupper(monthStart, 2, 1, aisle1.createAisle(), SjukfallUtil.ALL_ENHETER);

        //Then
        assertNotSame(sjukfallGroups1, sjukfallGroups2);
        assertNotSame(sjukfallGroups1, sjukfallGroups3);
        assertNotSame(sjukfallGroups1, sjukfallGroups4);
        assertNotSame(sjukfallGroups2, sjukfallGroups3);
        assertNotSame(sjukfallGroups2, sjukfallGroups4);
        assertNotSame(sjukfallGroups3, sjukfallGroups4);
    }

    @Test
    public void testCalculateKonDataResponse() throws Exception {
        //Given
        final SjukfallFilter filter = SjukfallUtil.ALL_ENHETER;
        final LocalDate start = new LocalDate();
        final int periods = 1;
        final int periodSize = 2;

        final SjukfallUtil spy = Mockito.spy(sjukfallUtil);
        final ArrayList<SjukfallGroup> sjukfallGrupper = new ArrayList<>();
        sjukfallGrupper.add(new SjukfallGroup(Range.createForLastMonthsExcludingCurrent(1), Arrays.asList(createSjukfall(Kon.Female), createSjukfall(Kon.Male), createSjukfall(Kon.Male))));
        final Aisle currentAisle = aisle.createAisle();
        Mockito.when(spy.sjukfallGrupper(start, periods, periodSize, currentAisle, filter, false)).thenReturn(sjukfallGrupper);

        //When
        final KonDataResponse response = spy.calculateKonDataResponse(currentAisle, filter, start, periods, periodSize, Arrays.asList("G1"), Arrays.asList(1), new CounterFunction<Integer>() {
            @Override
            public void addCount(Sjukfall sjukfall, HashMultiset<Integer> counter) {
                counter.add(1);
            }
        });

        //Then
        assertEquals(new Integer(1), response.getDataFromIndex(0, Kon.Female).get(0));
        assertEquals(new Integer(2), response.getDataFromIndex(0, Kon.Male).get(0));
    }

    private Sjukfall createSjukfall(Kon kon) {
        return Sjukfall.create(new SjukfallExtended(new Fact(0, 1, 2, 3, 4, 1, 6, 15, kon.getNumberRepresentation(), 30, 0, 0, 0, 0, 100, 1, 30, new int[0], 0, false)));
    }

    public static SjukfallFilter createEnhetFilterFromInternalIntValues(Integer... enhetIds) {
        final HashSet<Integer> availableEnhets = new HashSet<>(Arrays.asList(enhetIds));
        return new SjukfallFilter(new Predicate<Fact>() {
            @Override
            public boolean apply(Fact fact) {
                return availableEnhets.contains(fact.getEnhet());
            }
        }, SjukfallFilter.getHashValueForEnhets(availableEnhets.toArray()));
    }

    private Collection<Sjukfall> calculateSjukfallsHelper() {
        return sjukfallUtil.sjukfallGrupper(new LocalDate(2000, 1, 1), 1, 1000000, aisle.createAisle(), SjukfallUtil.ALL_ENHETER).iterator().next().getSjukfall();
    }

}
