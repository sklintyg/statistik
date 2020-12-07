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
package se.inera.statistics.service.warehouse.query;

import static org.junit.Assert.assertEquals;
import static se.inera.statistics.service.warehouse.FactBuilder.aFact;
import static se.inera.statistics.service.warehouse.WidelineConverter.toDay;

import java.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import se.inera.statistics.integration.hsa.model.HsaIdLakare;
import se.inera.statistics.integration.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.caching.Cache;
import se.inera.statistics.service.caching.NoOpRedisTemplate;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.KonDataResponse;
import se.inera.statistics.service.report.model.KonField;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.warehouse.Fact;
import se.inera.statistics.service.warehouse.MutableAisle;
import se.inera.statistics.service.warehouse.SjukfallUtil;

public class IntygPerSjukfallQueryTest {

    private static final int ENHET1_ID = 1;

    private static final HsaIdLakare LAKARE1_ID = new HsaIdLakare("LAKARE1");
    private static final HsaIdLakare LAKARE2_ID = new HsaIdLakare("LAKARE2");

    private static final int PATIENT1_ID = 1;
    private static final int PATIENT2_ID = 2;
    private static final int PATIENT3_ID = 3;
    private static final int PATIENT4_ID = 4;
    private static final int PATIENT5_ID = 5;

    private static final int PERIOD_1 = 1;
    private static final int PERIOD_LENGTH_12 = 12;

    private LocalDate sjukfallDate = LocalDate.of(2014, 5, 5);

    private Range range = new Range(sjukfallDate.minusMonths(2).withDayOfMonth(1), sjukfallDate.plusMonths(2).withDayOfMonth(1));

    private int lakarIntygCounter;

    private IntygPerSjukfallQuery intygPerSjukfallQuery;

    private MutableAisle aisle;

    private Cache cache = new Cache(new NoOpRedisTemplate());
    private SjukfallUtil sjukfallUtil = new SjukfallUtil();

    @Before
    public void setup() {
        ReflectionTestUtils.setField(sjukfallUtil, "cache", cache);
        intygPerSjukfallQuery = new IntygPerSjukfallQuery();
        RegionCutoff regionCutoff = new RegionCutoff();
        regionCutoff.setCutoff(5);
        ReflectionTestUtils.setField(intygPerSjukfallQuery, "regionCutoff", regionCutoff);
        aisle = new MutableAisle(new HsaIdVardgivare("vg1"));
        MockitoAnnotations.initMocks(this);
    }

    private Fact fact(int patientId, Kon patientKon, HsaIdLakare lakareId) {
        return aFact().withId(1).withLan(3).withKommun(380).withForsamling(38002).
            withEnhet(ENHET1_ID).withLakarintyg(lakarIntygCounter++).
            withPatient(patientId).withKon(patientKon).withAlder(45).
            withDiagnoskapitel(0).withDiagnosavsnitt(14).withDiagnoskategori(16).withDiagnoskod(18).
            withSjukskrivningsgrad(100).withStartdatum(toDay(sjukfallDate)).withSlutdatum(toDay(sjukfallDate) + 46).
            withLakarkon(Kon.FEMALE).withLakaralder(32).withLakarbefattning(new int[]{201010}).withLakarid(lakareId).build();
    }

    private void verifyThatTheRestIsEmpty(SimpleKonResponse result, int fromIndex) {
        for (int i = fromIndex; i < result.getRows().size(); i++) {
            if (i == result.getRows().size() - 1) {
                assertEquals("Över 10 intyg", result.getRows().get(i).getName());
            } else {
                assertEquals((i + 1) + " intyg", result.getRows().get(i).getName());
            }
            assertEquals(0, result.getRows().get(i).getMale());
            assertEquals(0, result.getRows().get(i).getFemale());
        }
    }

    private void verifyGroupNames(KonDataResponse result) {
        int i = 0;
        for (String groupName : result.getGroups()) {
            i++;
            if (i == result.getGroups().size()) {
                assertEquals("Över 10 intyg", groupName);
            } else {
                assertEquals(i + " intyg", groupName);
            }
        }
    }

    private void verifyEmptyPeriod(KonDataResponse result, String name, int index) {
        assertEquals(name, result.getRows().get(index).getName());
        for (KonField konField : result.getRows().get(index).getData()) {
            assertEquals(0, konField.getMale());
            assertEquals(0, konField.getFemale());
        }
    }

    private void verifyTidsserieWithOneCaseForFemaleWithTwoIntygAndOneCaseForMaleWithOneIntyg(KonDataResponse result, String name,
        int index) {
        assertEquals(name, result.getRows().get(index).getName());
        assertEquals(1, result.getRows().get(index).getData().get(0).getMale());
        assertEquals(0, result.getRows().get(index).getData().get(0).getFemale());
        assertEquals(0, result.getRows().get(index).getData().get(1).getMale());
        assertEquals(1, result.getRows().get(index).getData().get(1).getFemale());
        for (int i = index; i < result.getRows().get(index).getData().size(); i++) {
            assertEquals(0, result.getRows().get(index).getData().get(i).getMale());
            assertEquals(0, result.getRows().get(index).getData().get(i).getFemale());
        }
    }

    @Test
    public void testTvarsnittWithOneCaseForFemaleWithTwoIntygAndOneCaseForMaleWithOneIntyg() {
        aisle.addLine(fact(PATIENT1_ID, Kon.FEMALE, LAKARE1_ID));
        aisle.addLine(fact(PATIENT1_ID, Kon.FEMALE, LAKARE1_ID));
        aisle.addLine(fact(PATIENT2_ID, Kon.MALE, LAKARE2_ID));

        SimpleKonResponse result = IntygPerSjukfallQuery
            .getIntygPerSjukfallTvarsnitt(aisle.createAisle(), SjukfallUtil.ALL_ENHETER, range.getFrom(), PERIOD_1,
                PERIOD_LENGTH_12, sjukfallUtil);

        assertEquals(11, result.getRows().size());
        assertEquals("1 intyg", result.getRows().get(0).getName());
        assertEquals(1, result.getRows().get(0).getMale());
        assertEquals(0, result.getRows().get(0).getFemale());
        assertEquals("2 intyg", result.getRows().get(1).getName());
        assertEquals(0, result.getRows().get(1).getMale());
        assertEquals(1, result.getRows().get(1).getFemale());
        verifyThatTheRestIsEmpty(result, 2);
    }

    @Test
    public void testTidsserieWithOneCaseForFemaleWithTwoIntygAndOneCaseForMaleWithOneIntyg() {
        aisle.addLine(fact(PATIENT1_ID, Kon.FEMALE, LAKARE1_ID));
        aisle.addLine(fact(PATIENT1_ID, Kon.FEMALE, LAKARE1_ID));
        aisle.addLine(fact(PATIENT2_ID, Kon.MALE, LAKARE2_ID));

        KonDataResponse result = IntygPerSjukfallQuery
            .getIntygPerSjukfallTidsserie(aisle.createAisle(), SjukfallUtil.ALL_ENHETER, range.getFrom(), PERIOD_LENGTH_12,
                PERIOD_1, sjukfallUtil);

        assertEquals(11, result.getGroups().size());
        verifyGroupNames(result);
        assertEquals(12, result.getRows().size());
        int index = 0;
        verifyEmptyPeriod(result, "mar 2014", index++);
        verifyEmptyPeriod(result, "apr 2014", index++);
        verifyTidsserieWithOneCaseForFemaleWithTwoIntygAndOneCaseForMaleWithOneIntyg(result, "maj 2014", index++);
        verifyTidsserieWithOneCaseForFemaleWithTwoIntygAndOneCaseForMaleWithOneIntyg(result, "jun 2014", index++);
        verifyEmptyPeriod(result, "jul 2014", index++);
        verifyEmptyPeriod(result, "aug 2014", index++);
        verifyEmptyPeriod(result, "sep 2014", index++);
        verifyEmptyPeriod(result, "okt 2014", index++);
        verifyEmptyPeriod(result, "nov 2014", index++);
        verifyEmptyPeriod(result, "dec 2014", index++);
        verifyEmptyPeriod(result, "jan 2015", index++);
        verifyEmptyPeriod(result, "feb 2015", index);
    }

    @Test
    public void testRegionWithOneCaseForFemaleWithTwoIntygAndOneCaseForMaleWithOneIntyg() {
        aisle.addLine(fact(PATIENT1_ID, Kon.FEMALE, LAKARE1_ID));
        aisle.addLine(fact(PATIENT2_ID, Kon.FEMALE, LAKARE1_ID));
        aisle.addLine(fact(PATIENT3_ID, Kon.FEMALE, LAKARE1_ID));
        aisle.addLine(fact(PATIENT4_ID, Kon.FEMALE, LAKARE1_ID));
        aisle.addLine(fact(PATIENT5_ID, Kon.FEMALE, LAKARE1_ID));

        SimpleKonResponse result = intygPerSjukfallQuery
            .getIntygPerSjukfallTvarsnittRegion(aisle.createAisle(), SjukfallUtil.ALL_ENHETER, range.getFrom(), PERIOD_1,
                PERIOD_LENGTH_12, sjukfallUtil);

        assertEquals(11, result.getGroups().size());
        assertEquals("1 intyg", result.getRows().get(0).getName());
        assertEquals(0, result.getRows().get(0).getMale());
        assertEquals(5, result.getRows().get(0).getFemale());
        verifyThatTheRestIsEmpty(result, 1);

    }

    @Test
    public void testRegionWithOneCaseForFemaleWithTwoIntygAndOneCaseForMaleWithOneIntygNoResultsBecauseOfCutoff() {
        aisle.addLine(fact(PATIENT1_ID, Kon.FEMALE, LAKARE1_ID));
        aisle.addLine(fact(PATIENT1_ID, Kon.FEMALE, LAKARE1_ID));
        aisle.addLine(fact(PATIENT2_ID, Kon.MALE, LAKARE2_ID));

        SimpleKonResponse result = intygPerSjukfallQuery
            .getIntygPerSjukfallTvarsnittRegion(aisle.createAisle(), SjukfallUtil.ALL_ENHETER, range.getFrom(), PERIOD_1,
                PERIOD_LENGTH_12, sjukfallUtil);

        assertEquals(11, result.getGroups().size());
        verifyThatTheRestIsEmpty(result, 0);

    }

}
