/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.warehouse.query;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.util.ReflectionTestUtils;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.hsa.model.HsaIdLakare;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.caching.Cache;
import se.inera.statistics.service.caching.NoOpRedisTemplate;
import se.inera.statistics.service.processlog.Lakare;
import se.inera.statistics.service.processlog.LakareManager;
import se.inera.statistics.service.report.model.AvailableFilters;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.KonDataResponse;
import se.inera.statistics.service.report.model.KonDataRow;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.warehouse.Aisle;
import se.inera.statistics.service.warehouse.Fact;
import se.inera.statistics.service.warehouse.FilterPredicates;
import se.inera.statistics.service.warehouse.MutableAisle;
import se.inera.statistics.service.warehouse.SjukfallUtil;
import se.inera.statistics.service.warehouse.SjukfallUtilTest;
import se.inera.statistics.service.warehouse.Warehouse;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static se.inera.statistics.service.warehouse.Fact.aFact;
import static se.inera.statistics.service.warehouse.WidelineConverter.toDay;

public class SjukfallQueryTest {

    private static final HsaIdVardgivare VG_1 = new HsaIdVardgivare("vg1");
    private static final int ENHET1_ID = 1;

    private static final HsaIdLakare LAKARE1_ID = new HsaIdLakare("LAKARE1");
    private static final HsaIdLakare LAKARE2_ID = new HsaIdLakare("LAKARE2");
    private static final HsaIdLakare LAKARE3_ID = new HsaIdLakare("LAKARE3");

    private static final int PATIENT1_ID = 1;
    private static final Kon PATIENT1_KON = Kon.FEMALE;
    private static final int PATIENT2_ID = 2;
    private static final Kon PATIENT2_KON = Kon.MALE;

    private LocalDate sjukfallDate = LocalDate.of(2014, 5, 5);

    private Range range = new Range(sjukfallDate.minusMonths(2).withDayOfMonth(1), sjukfallDate.plusMonths(2).withDayOfMonth(1));

    private int lakarIntygCounter;

    private SjukfallQuery sjukfallQuery;

    private MutableAisle aisle;

    private BiMap<HsaIdLakare, Integer> lakarIdMap = HashBiMap.create();

    private Cache cache = new Cache(new NoOpRedisTemplate(), "1");
    private SjukfallUtil sjukfallUtil = new SjukfallUtil();

    private FilterPredicates enhetFilter = SjukfallUtilTest.createEnhetFilterFromInternalIntValues(ENHET1_ID);

    @Captor
    private ArgumentCaptor<List<Object>> ids;

    @Captor
    private ArgumentCaptor<List<String>> names;

    @Before
    public void setup() {
        ReflectionTestUtils.setField(sjukfallUtil, "cache", cache);
        populateLakare();
        LakareManager lakareManager = mockLakareManager();
        sjukfallQuery = new SjukfallQuery();
        sjukfallQuery.setLakareManager(lakareManager);
        ReflectionTestUtils.setField(sjukfallQuery, "sjukfallUtil", sjukfallUtil);
        aisle = new MutableAisle(new HsaIdVardgivare("vg1"));
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldReturnOneSjukfallPerLakare() {
        // Given
        aisle.addLine(fact(PATIENT1_ID, PATIENT1_KON, LAKARE1_ID));
        aisle.addLine(fact(PATIENT2_ID, PATIENT2_KON, LAKARE2_ID));

        // When
        enhetFilter = SjukfallUtilTest.createEnhetFilterFromInternalIntValues(ENHET1_ID);
        SimpleKonResponse result = sjukfallQuery.getSjukfallPerLakare(aisle.createAisle(), enhetFilter, range.getFrom(), 1, 12);

        // Then
        assertEquals(2, result.getRows().size());
        int checksum = 0;
        for (SimpleKonDataRow lakareRow : result.getRows()) {
            switch (lakareRow.getName()) {
                case "Agata Adamsson":
                    assertEquals(1, lakareRow.getDataForSex(Kon.FEMALE));
                    assertEquals(0, lakareRow.getDataForSex(Kon.MALE));
                    checksum +=1;
                    break;
                case "Beata Bertilsson":
                    assertEquals(0, lakareRow.getDataForSex(Kon.FEMALE));
                    assertEquals(1, lakareRow.getDataForSex(Kon.MALE));
                    checksum +=2;
                    break;
                default:
                    throw new RuntimeException("Unexpected doctor name: " + lakareRow.getName());
            }
        }
        // Just a check that we haven't counted the same match twice
        assertEquals(3, checksum);
    }

    @Test
    public void shouldLetLakareShareSjukfall() {
        // Given
        // Two intygs (w/ different doctors) for the same patient results in one sjukfall "belonging" to both doctors
        aisle.addLine(fact(PATIENT1_ID, PATIENT1_KON, LAKARE1_ID));
        aisle.addLine(fact(PATIENT1_ID, PATIENT1_KON, LAKARE2_ID));

        // When
        SimpleKonResponse result = sjukfallQuery.getSjukfallPerLakare(aisle.createAisle(), enhetFilter, range.getFrom(), 1, 12);

        // Then
        assertEquals(2, result.getRows().size());
        int checksum = 0;
        for (SimpleKonDataRow lakareRow : result.getRows()) {
            switch (lakareRow.getName()) {
                case "Agata Adamsson":
                    assertEquals(1, lakareRow.getDataForSex(Kon.FEMALE));
                    assertEquals(0, lakareRow.getDataForSex(Kon.MALE));
                    checksum +=1;
                    break;
                case "Beata Bertilsson":
                    assertEquals(1, lakareRow.getDataForSex(Kon.FEMALE));
                    assertEquals(0, lakareRow.getDataForSex(Kon.MALE));
                    checksum +=2;
                    break;
                default:
                    throw new RuntimeException("Unexpected doctor name: " + lakareRow.getName());
            }
        }
        // Just a check that we haven't counted the same match twice
        assertEquals(3, checksum);
    }

    @Test
    public void twoDoctorsWithSameNameShouldBeDistinguishedByHSAId() {
        // Given
        aisle.addLine(fact(PATIENT1_ID, PATIENT1_KON, LAKARE2_ID));
        aisle.addLine(fact(PATIENT1_ID, PATIENT1_KON, LAKARE3_ID));

        // When
        SimpleKonResponse result = sjukfallQuery.getSjukfallPerLakare(aisle.createAisle(), SjukfallUtilTest.createEnhetFilterFromInternalIntValues(ENHET1_ID), range.getFrom(), 1, 12);

        // Then
        assertEquals(2, result.getRows().size());
        int checksum = 0;
        for (SimpleKonDataRow lakareRow : result.getRows()) {
            switch (lakareRow.getName()) {
                case "Beata Bertilsson LAKARE2":
                    assertEquals(1, lakareRow.getDataForSex(Kon.FEMALE));
                    assertEquals(0, lakareRow.getDataForSex(Kon.MALE));
                    checksum +=1;
                    break;
                case "Beata Bertilsson LAKARE3":
                    assertEquals(1, lakareRow.getDataForSex(Kon.FEMALE));
                    assertEquals(0, lakareRow.getDataForSex(Kon.MALE));
                    checksum +=2;
                    break;
                default:
                    throw new RuntimeException("Unexpected doctor name: " + lakareRow.getName());
            }
        }
        // Just a check that we haven't counted the same match twice
        assertEquals(3, checksum);
    }

    private LakareManager mockLakareManager() {
        LakareManager lakareManager = mock(LakareManager.class);
        final Lakare lakare1 = new Lakare(VG_1, LAKARE1_ID, "Agata", "Adamsson");
        final Lakare lakare2 = new Lakare(VG_1, LAKARE2_ID, "Beata", "Bertilsson");
        final Lakare lakare3 = new Lakare(VG_1, LAKARE3_ID, "Beata", "Bertilsson");
        when(lakareManager.getLakares(VG_1)).thenReturn(asList(lakare1, lakare2, lakare3));
        when(lakareManager.getAllLakares()).thenReturn(asList(lakare1, lakare2, lakare3));
        when(lakareManager.getAllSpecifiedLakares(any(Collection.class))).thenAnswer(new Answer<Object>() {
            @Override
            public List<Lakare> answer(InvocationOnMock invocationOnMock) throws Throwable {
                final Object[] arguments = invocationOnMock.getArguments();
                final List<HsaIdLakare> hsaIds = (List<HsaIdLakare>) arguments[0];
                return hsaIds.stream().map(hsaId -> {
                    if (LAKARE1_ID.equals(hsaId)) {
                        return lakare1;
                    } else if (LAKARE2_ID.equals(hsaId)) {
                        return lakare2;
                    } else if (LAKARE3_ID.equals(hsaId)) {
                        return lakare3;
                    }
                    throw new RuntimeException("Lakare does not exist: " + hsaId);
                }).collect(Collectors.toList());
            }
        });
        return lakareManager;
    }

    private void populateLakare() {
        lakarIdMap.put(LAKARE1_ID, Warehouse.getNumLakarIdAndRemember(LAKARE1_ID));
        lakarIdMap.put(LAKARE2_ID, Warehouse.getNumLakarIdAndRemember(LAKARE2_ID));
        lakarIdMap.put(LAKARE3_ID, Warehouse.getNumLakarIdAndRemember(LAKARE3_ID));
    }

    private Fact fact(int patientId, Kon patientKon, HsaIdLakare lakareId) {
         return aFact().withId(1).withLan(3).withKommun(380).withForsamling(38002).
                withEnhet(ENHET1_ID).withLakarintyg(lakarIntygCounter++).
                withPatient(patientId).withKon(patientKon).withAlder(45).
                withDiagnoskapitel(0).withDiagnosavsnitt(14).withDiagnoskategori(16).withDiagnoskod(18).
                withSjukskrivningsgrad(100).withStartdatum(toDay(sjukfallDate)).withSlutdatum(toDay(sjukfallDate) + 46).
                 withLakarkon(Kon.FEMALE).withLakaralder(32).withLakarbefattning(new int[]{201010}).withLakarid(lakarIdMap.get(lakareId)).build();
    }

    @Test
    public void testGetSjukfallPerEnhetSeries() {
        //Given
        final Clock clock = Clock.systemDefaultZone();
        final SjukfallUtil sjukfallUtilMock = Mockito.mock(SjukfallUtil.class);
        Mockito.when(sjukfallUtilMock.calculateKonDataResponse(any(Aisle.class), any(FilterPredicates.class), any(LocalDate.class), anyInt(), anyInt(), anyListOf(String.class), anyListOf(Object.class), any(CounterFunction.class))).thenReturn(new KonDataResponse(AvailableFilters.getForSjukfall(), Collections.<String>emptyList(), Collections.<KonDataRow>emptyList()));
        ReflectionTestUtils.setField(sjukfallQuery, "sjukfallUtil", sjukfallUtilMock);

        final FilterPredicates filter = SjukfallUtilTest.createEnhetFilterFromInternalIntValues(ENHET1_ID);
        final LocalDate start = LocalDate.now(clock);
        final int periods = 1;
        final int periodSize = 2;
        final HashMap<HsaIdEnhet, String> idsToNames = new HashMap<>();
        idsToNames.put(new HsaIdEnhet("-1"), "name1");

        //When
        final Aisle currentAisle = aisle.createAisle();
        sjukfallQuery.getSjukfallPerEnhetSeries(currentAisle, filter, start, periods, periodSize, idsToNames);

        //Then
        Mockito.verify(sjukfallUtilMock).calculateKonDataResponse(eq(currentAisle), eq(filter), eq(start), eq(periods), eq(periodSize), names.capture(), ids.capture(), Matchers.<CounterFunction<Object>>any());
        assertEquals("-1", names.getValue().get(0));
        assertEquals(-1, ids.getValue().get(0));
    }

    @Test
    public void testGetSjukfallPerEnhetSeriesTwoEnhetsWthSameNameWillGetIdAsSuffixSTATISTIK1121() {
        //Given
        final Clock clock = Clock.systemDefaultZone();
        final SjukfallUtil sjukfallUtilMock = Mockito.mock(SjukfallUtil.class);
        final KonDataResponse konDataResponse = new KonDataResponse(AvailableFilters.getForSjukfall(), Arrays.asList("1", "2", "3"), Collections.<KonDataRow>emptyList());
        Mockito.when(sjukfallUtilMock.calculateKonDataResponse(any(Aisle.class), any(FilterPredicates.class), any(LocalDate.class), anyInt(), anyInt(), anyListOf(String.class), anyListOf(Object.class), any(CounterFunction.class))).thenReturn(konDataResponse);
        ReflectionTestUtils.setField(sjukfallQuery, "sjukfallUtil", sjukfallUtilMock);
        final FilterPredicates enhetFilterFromInternalIntValues = SjukfallUtilTest.createEnhetFilterFromInternalIntValues(ENHET1_ID);
        final HashMap<HsaIdEnhet, String> idsToNames = new HashMap<>();
        idsToNames.put(new HsaIdEnhet("1"), "ABC");
        idsToNames.put(new HsaIdEnhet("2"), "CBA");
        idsToNames.put(new HsaIdEnhet("3"), "ABC");

        //When
        final KonDataResponse result = sjukfallQuery.getSjukfallPerEnhetSeries(aisle.createAisle(), enhetFilterFromInternalIntValues, LocalDate.now(clock), 1, 2, idsToNames);

        //Then
        assertEquals(3, result.getGroups().size());
        assertEquals("ABC 1", result.getGroups().get(0));
        assertEquals("CBA", result.getGroups().get(1));
        assertEquals("ABC 3", result.getGroups().get(2));
    }

}
