/*
 * Copyright (C) 2025 Inera AB (http://www.inera.se)
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
import static org.mockito.ArgumentMatchers.any;
import static se.inera.statistics.service.warehouse.FactBuilder.aFact;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.test.util.ReflectionTestUtils;
import se.inera.statistics.integration.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.caching.Cache;
import se.inera.statistics.service.caching.NoOpRedisTemplate;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.OverviewChartRowExtended;
import se.inera.statistics.service.report.util.AldersgroupUtil;
import se.inera.statistics.service.report.util.OverviewAgeGroup;
import se.inera.statistics.service.report.util.Ranges;
import se.inera.statistics.service.warehouse.Aisle;
import se.inera.statistics.service.warehouse.Fact;
import se.inera.statistics.service.warehouse.Sjukfall;
import se.inera.statistics.service.warehouse.SjukfallUtil;
import se.inera.statistics.service.warehouse.Warehouse;
import se.inera.statistics.service.warehouse.WidelineLoader;

public class AldersgruppQueryTest {

    private static final HsaIdVardgivare VARDGIVARE = new HsaIdVardgivare("vardgivare");

    private final SjukfallUtil sjukfallUtil = new SjukfallUtil();

    @InjectMocks
    private Warehouse warehouse = new Warehouse();

    @Mock
    private WidelineLoader widelineLoader;

    @Spy
    private Cache cache = new Cache(new NoOpRedisTemplate());

    private int intyg;
    private int patient;
    private ArrayList<Fact> facts = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        ReflectionTestUtils.setField(sjukfallUtil, "cache", cache);
        MockitoAnnotations.initMocks(this);
        facts.clear();
    }

    @Test
    public void one() {
        fact(4010, 10, 45);
        Mockito.when(widelineLoader.getAilesForVgs(any())).thenReturn(Collections.singletonList(new Aisle(VARDGIVARE, facts)));
        Collection<Sjukfall> sjukfall = calculateSjukfallsHelper(warehouse.get(VARDGIVARE));
        Map<Ranges.Range, Counter<Ranges.Range>> count = AldersgruppQuery.count(sjukfall, AldersgruppQuery.RANGES);
        assertEquals(1, count.get(AldersgroupUtil.RANGES.rangeFor("41-45 år")).getCount());
    }

    private Collection<Sjukfall> calculateSjukfallsHelper(Aisle aisle) {
        return sjukfallUtil.sjukfallGrupper(LocalDate.of(2000, 1, 1), 1, 1000000, aisle, SjukfallUtil.ALL_ENHETER).iterator().next()
            .getSjukfall();
    }

    @Test
    public void useMax() {
        fact(4010, 10, 1);
        fact(4010, 10, 1);
        fact(4010, 10, 1);
        fact(4010, 10, 1);
        fact(4010, 10, 30);
        fact(4010, 10, 30);
        fact(4010, 10, 45);
        fact(4010, 10, 45);
        fact(4010, 10, 50);
        fact(4010, 10, 50);
        fact(4010, 10, 50);
        fact(4010, 10, 100);
        Mockito.when(widelineLoader.getAilesForVgs(any())).thenReturn(Collections.singletonList(new Aisle(VARDGIVARE, facts)));
        Collection<Sjukfall> sjukfall = calculateSjukfallsHelper(warehouse.get(VARDGIVARE));
        List<OverviewChartRowExtended> count = AldersgruppQuery.getOverviewAldersgrupper(sjukfall, sjukfall);

        assertEquals(6, count.size());
        assertEquals(4, count.get(0).getQuantity());
        assertEquals(2, count.get(1).getQuantity());
        assertEquals(0, count.get(2).getQuantity());
        assertEquals(5, count.get(3).getQuantity());
        assertEquals(0, count.get(4).getQuantity());
        assertEquals(1, count.get(5).getQuantity());
    }

    private void fact(int startday, int length, int alder) {
        Fact fact = aFact().withId(1).withLan(3).withKommun(380).withForsamling(38002).
            withEnhet(1).withLakarintyg(intyg++).
            withPatient(patient++).withKon(Kon.FEMALE).withAlder(alder).
            withDiagnoskapitel(0).withDiagnosavsnitt(14).withDiagnoskategori(16).withDiagnoskod(18).
            withSjukskrivningsgrad(100).withStartdatum(startday).withSlutdatum(startday + length - 1).
            withLakarkon(Kon.FEMALE).withLakaralder(32).withLakarbefattning(new int[]{201010}).withLakarid(1).build();
        facts.add(fact);
    }


    @Test
    public void testGetOverviewAldersgrupperReturnsAllOverviewGroups() {
        //Given
        facts(20, 45, 47);
        Mockito.when(widelineLoader.getAilesForVgs(any())).thenReturn(Collections.singletonList(new Aisle(VARDGIVARE, facts)));
        Collection<Sjukfall> sjukfall = calculateSjukfallsHelper(warehouse.get(VARDGIVARE));

        //When
        List<OverviewChartRowExtended> count = AldersgruppQuery.getOverviewAldersgrupper(sjukfall, sjukfall);

        //Then
        final OverviewAgeGroup[] ageGroups = OverviewAgeGroup.values();
        assertEquals(ageGroups.length, count.size());
        for (int i = 0; i < ageGroups.length; i++) {
            OverviewAgeGroup ageGroup = ageGroups[i];
            assertEquals(ageGroup.getGroupName(), count.get(i).getName());
            switch (ageGroup) {
                case GROUP1_0TO20:
                    assertEquals(1, count.get(i).getQuantity());
                    break;
                case GROUP4_41TO50:
                    assertEquals(2, count.get(i).getQuantity());
                    break;
                default:
                    assertEquals(0, count.get(i).getQuantity());
                    break;
            }
        }
    }

    private void facts(int... ages) {
        for (int age : ages) {
            fact(4010, 10, age);
        }
    }

}
