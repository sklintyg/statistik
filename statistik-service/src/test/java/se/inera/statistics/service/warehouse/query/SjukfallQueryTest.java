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
package se.inera.statistics.service.warehouse.query;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import se.inera.statistics.service.processlog.Lakare;
import se.inera.statistics.service.processlog.LakareManager;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.warehouse.Aisle;
import se.inera.statistics.service.warehouse.Fact;
import se.inera.statistics.service.warehouse.SjukfallFilter;
import se.inera.statistics.service.warehouse.SjukfallUtil;
import se.inera.statistics.service.warehouse.Warehouse;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static se.inera.statistics.service.warehouse.Fact.aFact;
import static se.inera.statistics.service.warehouse.WidelineConverter.toDay;

public class SjukfallQueryTest {

    private static final String VG_1 = "vg1";
    private static final int ENHET1_ID = 1;

    private static final String LAKARE1_ID = "lakare1";
    private static final String LAKARE2_ID = "lakare2";
    private static final String LAKARE3_ID = "lakare3";

    private static final int PATIENT1_ID = 1;
    private static final Kon PATIENT1_KON = Kon.Female;
    private static final int PATIENT2_ID = 2;
    private static final Kon PATIENT2_KON = Kon.Male;

    private LocalDate sjukfallDate = new LocalDate(2014, 5, 5);

    private Range range = new Range(sjukfallDate.minusMonths(2).withDayOfMonth(1), sjukfallDate.plusMonths(2).withDayOfMonth(1));

    private int lakarIntygCounter;

    private LakareManager lakareManager;

    private SjukfallQuery sjukfallQuery;

    private Aisle aisle;

    private BiMap<String, Integer> lakarIdMap = HashBiMap.create();

    private SjukfallUtil sjukfallUtil = new SjukfallUtil();

    private SjukfallFilter enhetFilter = sjukfallUtil.createEnhetFilterFromInternalIntValues(ENHET1_ID);

    @Before
    public void setup() {
        populateLakare();
        lakareManager = mockLakareManager();
        sjukfallQuery = new SjukfallQuery();
        sjukfallQuery.setLakareManager(lakareManager);
        ReflectionTestUtils.setField(sjukfallQuery, "sjukfallUtil", sjukfallUtil);
        aisle = new Aisle("vg1");
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldReturnOneSjukfallPerLakare() {
        // Given
        aisle.addLine(fact(PATIENT1_ID, PATIENT1_KON, LAKARE1_ID));
        aisle.addLine(fact(PATIENT2_ID, PATIENT2_KON, LAKARE2_ID));

        // When
        enhetFilter = sjukfallUtil.createEnhetFilterFromInternalIntValues(ENHET1_ID);
        SimpleKonResponse<SimpleKonDataRow> result = sjukfallQuery.getSjukfallPerLakare(aisle, enhetFilter, range.getFrom(), 1, 12);

        // Then
        assertEquals(2, result.getRows().size());
        int checksum = 0;
        for (SimpleKonDataRow lakareRow : result.getRows()) {
            switch (lakareRow.getName()) {
                case "Agata Adamsson":
                    assertEquals(1, lakareRow.getDataForSex(Kon.Female));
                    assertEquals(0, lakareRow.getDataForSex(Kon.Male));
                    checksum +=1;
                    break;
                case "Beata Bertilsson":
                    assertEquals(0, lakareRow.getDataForSex(Kon.Female));
                    assertEquals(1, lakareRow.getDataForSex(Kon.Male));
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
        SimpleKonResponse<SimpleKonDataRow> result = sjukfallQuery.getSjukfallPerLakare(aisle, enhetFilter, range.getFrom(), 1, 12);

        // Then
        assertEquals(2, result.getRows().size());
        int checksum = 0;
        for (SimpleKonDataRow lakareRow : result.getRows()) {
            switch (lakareRow.getName()) {
                case "Agata Adamsson":
                    assertEquals(1, lakareRow.getDataForSex(Kon.Female));
                    assertEquals(0, lakareRow.getDataForSex(Kon.Male));
                    checksum +=1;
                    break;
                case "Beata Bertilsson":
                    assertEquals(1, lakareRow.getDataForSex(Kon.Female));
                    assertEquals(0, lakareRow.getDataForSex(Kon.Male));
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
        SimpleKonResponse<SimpleKonDataRow> result = sjukfallQuery.getSjukfallPerLakare(aisle, sjukfallUtil.createEnhetFilterFromInternalIntValues(ENHET1_ID), range.getFrom(), 1, 12);

        // Then
        assertEquals(2, result.getRows().size());
        int checksum = 0;
        for (SimpleKonDataRow lakareRow : result.getRows()) {
            switch (lakareRow.getName()) {
                case "Beata Bertilsson lakare2":
                    assertEquals(1, lakareRow.getDataForSex(Kon.Female));
                    assertEquals(0, lakareRow.getDataForSex(Kon.Male));
                    checksum +=1;
                    break;
                case "Beata Bertilsson lakare3":
                    assertEquals(1, lakareRow.getDataForSex(Kon.Female));
                    assertEquals(0, lakareRow.getDataForSex(Kon.Male));
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
        Lakare lakare1 = new Lakare(VG_1, LAKARE1_ID, "Agata", "Adamsson");
        Lakare lakare2 = new Lakare(VG_1, LAKARE2_ID, "Beata", "Bertilsson");
        Lakare lakare3 = new Lakare(VG_1, LAKARE3_ID, "Beata", "Bertilsson");
        when(lakareManager.getLakares(VG_1)).thenReturn(asList(lakare1, lakare2, lakare3));
        return lakareManager;
    }

    private void populateLakare() {
        lakarIdMap.put(LAKARE1_ID, Warehouse.getNumLakarIdAndRemember(LAKARE1_ID));
        lakarIdMap.put(LAKARE2_ID, Warehouse.getNumLakarIdAndRemember(LAKARE2_ID));
        lakarIdMap.put(LAKARE3_ID, Warehouse.getNumLakarIdAndRemember(LAKARE3_ID));
    }

    private Fact fact(int patientId, Kon patientKon, String lakareId) {
         return aFact().withLan(3).withKommun(380).withForsamling(38002).
                withEnhet(Integer.valueOf(ENHET1_ID)).withLakarintyg(lakarIntygCounter++).
                withPatient(patientId).withKon(patientKon).withAlder(45).
                withDiagnoskapitel(0).withDiagnosavsnitt(14).withDiagnoskategori(16).
                withSjukskrivningsgrad(100).withStartdatum(toDay(sjukfallDate)).withSjukskrivningslangd(47).
                 withLakarkon(Kon.Female).withLakaralder(32).withLakarbefattning(new int[]{201010}).withLakarid(lakarIdMap.get(lakareId)).build();
    }

}
