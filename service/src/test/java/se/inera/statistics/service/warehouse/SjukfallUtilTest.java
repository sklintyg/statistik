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
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static se.inera.statistics.service.report.model.Kon.FEMALE;
import static se.inera.statistics.service.warehouse.FactBuilder.aFact;

import com.google.common.collect.HashMultiset;
import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import se.inera.statistics.integration.hsa.model.HsaIdEnhet;
import se.inera.statistics.integration.hsa.model.HsaIdLakare;
import se.inera.statistics.integration.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.caching.Cache;
import se.inera.statistics.service.caching.NoOpRedisTemplate;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.KonDataResponse;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.warehouse.query.CounterFunction;

public class SjukfallUtilTest {

    private MutableAisle aisle;

    @InjectMocks
    private SjukfallUtil sjukfallUtil;

    @Spy
    private Cache cache = new Cache(new NoOpRedisTemplate());

    private int id = 1;

    @Before
    public void setup() {
        sjukfallUtil = new SjukfallUtil();
        MockitoAnnotations.initMocks(this);
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
        final List<HsaIdLakare> lakare = sjukfall.getLakare().stream().map(lakare1 -> lakare1.getId()).collect(Collectors.toList());
        assertEquals(2, lakare.size());
        assertTrue(lakare.contains(new HsaIdLakare("1")));
        assertTrue(lakare.contains(new HsaIdLakare("2")));
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

        Iterator<SjukfallGroup> actives = sjukfallUtil
            .sjukfallGrupper(LocalDate.parse("2010-11-01"), 3, 1, aisle.createAisle(), createEnhetFilterFromInternalIntValues(2))
            .iterator();
        assertTrue(actives.next().getSjukfall().isEmpty());

        assertEquals(2, actives.next().getSjukfall().size());
        assertEquals(2, actives.next().getSjukfall().size());
        assertFalse(actives.hasNext());

        actives = sjukfallUtil
            .sjukfallGrupper(LocalDate.parse("2010-11-01"), 2, 12, aisle.createAisle(), createEnhetFilterFromInternalIntValues(2))
            .iterator();

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
        return aFact().withId(id++).withLan(3).withKommun(380).withForsamling(38002).withEnhet(enhet).
            withLakarintyg(lakarintyg).withPatient(patient).withStartdatum(startdatum).withKon(FEMALE).withAlder(45).
            withDiagnoskapitel(0).withDiagnosavsnitt(14).withDiagnoskategori(16).withDiagnoskod(18).
            withSjukskrivningsgrad(100).withSlutdatum(startdatum + sjukskrivningslangd - 1).
            withLakarkon(FEMALE).withLakaralder(32).withLakarbefattning(new int[]{201010}).withLakarid(lakarId).build();
    }

    @Test
    public void oneSjukfalFromTwoDifferentEnhetsIsNotAffectedByEnhetFilter() throws Exception {
        int ENHET1 = 1;
        int ENHET2 = 2;
        LocalDate monthStart = LocalDate.parse("2010-11-01");
        aisle.addLine(createFact(ENHET1, 1, WidelineConverter.toDay(monthStart), 1));
        aisle.addLine(createFact(ENHET2, 1, WidelineConverter.toDay(monthStart.plusDays(10)), 2));
        aisle.addLine(createFact(ENHET1, 1, WidelineConverter.toDay(monthStart.plusDays(20)), 3));

        Iterator<SjukfallGroup> actives = sjukfallUtil
            .sjukfallGrupper(monthStart, 1, 1, aisle.createAisle(), createEnhetFilterFromInternalIntValues(ENHET1)).iterator();
        assertEquals(1, actives.next().getSjukfall().size());
    }

    @Test
    public void twoIntygsFarApartAreTwoSjukfalls() throws Exception {
        int ENHET1 = 1;
        LocalDate monthStart = LocalDate.parse("2010-11-01");
        aisle.addLine(createFact(ENHET1, 1, WidelineConverter.toDay(monthStart), 1));
        aisle.addLine(createFact(ENHET1, 1, WidelineConverter.toDay(monthStart.plusDays(21)), 3));

        Iterator<SjukfallGroup> actives = sjukfallUtil
            .sjukfallGrupper(monthStart, 1, 1, aisle.createAisle(), createEnhetFilterFromInternalIntValues(ENHET1)).iterator();
        assertEquals(2, actives.next().getSjukfall().size());
    }

    @Test
    public void testSjukfallCacheWillOnlyReturnValuesForTheCorrectVg() throws Exception {
        //Given
        LocalDate monthStart = LocalDate.parse("2015-03-11");
        MutableAisle aisle1 = new MutableAisle(new HsaIdVardgivare("vgid1"));
        MutableAisle aisle2 = new MutableAisle(new HsaIdVardgivare("vgid2"));

        //When
        final Iterable<SjukfallGroup> sjukfallGroups1 = sjukfallUtil
            .sjukfallGrupper(monthStart, 1, 1, aisle1.createAisle(), SjukfallUtil.ALL_ENHETER);
        final Iterable<SjukfallGroup> sjukfallGroups2 = sjukfallUtil
            .sjukfallGrupper(monthStart, 2, 1, aisle2.createAisle(), SjukfallUtil.ALL_ENHETER);
        final Iterable<SjukfallGroup> sjukfallGroups3 = sjukfallUtil
            .sjukfallGrupper(monthStart, 1, 1, aisle2.createAisle(), SjukfallUtil.ALL_ENHETER);
        final Iterable<SjukfallGroup> sjukfallGroups4 = sjukfallUtil
            .sjukfallGrupper(monthStart, 2, 1, aisle1.createAisle(), SjukfallUtil.ALL_ENHETER);

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
        final Clock clock = Clock.systemDefaultZone();
        final FilterPredicates filter = SjukfallUtil.ALL_ENHETER;
        final LocalDate start = LocalDate.now(clock);
        final int periods = 1;
        final int periodSize = 2;

        final SjukfallUtil spy = Mockito.spy(sjukfallUtil);
        final ArrayList<SjukfallGroup> sjukfallGrupper = new ArrayList<>();
        sjukfallGrupper.add(new SjukfallGroup(Range.createForLastMonthsExcludingCurrent(1, clock),
            Arrays.asList(createSjukfall(Kon.FEMALE), createSjukfall(Kon.MALE), createSjukfall(Kon.MALE))));
        final Aisle currentAisle = aisle.createAisle();
        Mockito.when(spy.sjukfallGrupper(start, periods, periodSize, currentAisle, filter)).thenReturn(sjukfallGrupper);

        //When
        final KonDataResponse response = spy
            .calculateKonDataResponse(currentAisle, filter, start, periods, periodSize, Arrays.asList("G1"), Arrays.asList(1),
                new CounterFunction<Integer>() {
                    @Override
                    public void addCount(Sjukfall sjukfall, HashMultiset<Integer> counter) {
                        counter.add(1);
                    }
                });

        //Then
        assertEquals(Integer.valueOf(1), response.getDataFromIndex(0, Kon.FEMALE).get(0));
        assertEquals(Integer.valueOf(2), response.getDataFromIndex(0, Kon.MALE).get(0));
    }

    private Sjukfall createSjukfall(Kon kon) {
        return Sjukfall.create(new SjukfallExtended(
            FactBuilder.newFact(1L, 0, 1, 2, 3, 5,  4, 1, 6, 15, kon.getNumberRepresentation(), 30, 0, 0, 0, 0, 100, 1, 30, new int[0], 0)));
    }

    public static FilterPredicates createEnhetFilterFromInternalIntValues(Integer... enhetIds) {
        final Set<HsaIdEnhet> availableEnhets = Arrays.stream(enhetIds)
            .map(integer -> new HsaIdEnhet(String.valueOf(integer))).collect(Collectors.toSet());
        final String hashValue = FilterPredicates.getHashValueForEnhets(availableEnhets);
        return new FilterPredicates(fact -> availableEnhets.contains(fact.getVardenhet()), sjukfall -> true, hashValue, false);
    }

    private Collection<Sjukfall> calculateSjukfallsHelper() {
        return sjukfallUtil.sjukfallGrupper(LocalDate.of(2000, 1, 1), 1, 1000000, aisle.createAisle(), SjukfallUtil.ALL_ENHETER).iterator()
            .next().getSjukfall();
    }

}
