/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.caching.Cache;
import se.inera.statistics.service.caching.NoOpRedisTemplate;
import se.inera.statistics.service.report.model.DiagnosgruppResponse;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.util.Icd10;
import se.inera.statistics.service.report.util.Icd10RangeType;
import se.inera.statistics.service.warehouse.Aisle;
import se.inera.statistics.service.warehouse.Fact;
import se.inera.statistics.service.warehouse.MutableAisle;
import se.inera.statistics.service.warehouse.Sjukfall;
import se.inera.statistics.service.warehouse.FilterPredicates;
import se.inera.statistics.service.warehouse.SjukfallUtil;
import se.inera.statistics.service.warehouse.Warehouse;
import se.inera.statistics.service.warehouse.WidelineLoader;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static se.inera.statistics.service.warehouse.Fact.aFact;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:icd10.xml", "classpath:query-test.xml"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class DiagnosgruppQueryTest {

    private static final HsaIdVardgivare VARDGIVARE = new HsaIdVardgivare("vardgivare");

    @Autowired
    private Icd10 icd10;

    @Autowired
    private DiagnosgruppQuery query;

    @Autowired
    private SjukfallUtil sjukfallUtil;

    @InjectMocks
    private Warehouse warehouse = new Warehouse();

    @Mock
    private WidelineLoader widelineLoader;

    @Spy
    private Cache cache = new Cache(new NoOpRedisTemplate(), "1");

    private int intyg;
    private int patient;
    private ArrayList<Fact> facts = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        facts.clear();
    }

    @Test
    public void one() {
        final String kapitelCode = "A00-B99";
        fact(4010, Icd10.icd10ToInt(kapitelCode, Icd10RangeType.KAPITEL));
        Mockito.when(widelineLoader.getAilesForVgs(any())).thenReturn(Collections.singletonList(new Aisle(VARDGIVARE, facts)));
        final Collection<Sjukfall> sjukfall = calculateSjukfallsHelper(warehouse.get(VARDGIVARE));
        final Map<Integer,Counter<Integer>> count = query.count(sjukfall);
        final Icd10.Kapitel kapitel = icd10.getKapitel(kapitelCode);
        final int kapitelInt = kapitel.toInt();
        final Counter<Integer> kapitelCounter = count.get(kapitelInt);
        final int kapitelCount = kapitelCounter.getCount();
        assertEquals(1, kapitelCount);
    }

    private Collection<Sjukfall> calculateSjukfallsHelper(Aisle aisle) {
        final LocalDate fromDate = LocalDate.of(2000, 1, 1);
        return sjukfallUtil.sjukfallGrupper(fromDate, 1, 1000000, aisle, SjukfallUtil.ALL_ENHETER).iterator().next().getSjukfall();
    }

    @Test
    public void useMax() {
        fact(4010, 0);
        fact(4010, 0);
        fact(4010, 0);
        fact(4010, 0);
        fact(4010, 200);
        fact(4010, 200);
        fact(4010, 400);
        fact(4010, 400);
        fact(4010, 500);
        fact(4010, 500);
        fact(4010, 500);
        fact(4010, 600);
        Mockito.when(widelineLoader.getAilesForVgs(any())).thenReturn(Collections.singletonList(new Aisle(VARDGIVARE, facts)));
        Collection<Sjukfall> sjukfall = calculateSjukfallsHelper(warehouse.get(VARDGIVARE));
        List<Counter<Integer>> count = query.count(sjukfall, 4);

        assertEquals(4, count.size());
        assertEquals(4, count.get(0).getCount());
        assertEquals(3, count.get(1).getCount());
        assertEquals(2, count.get(2).getCount());
        assertEquals(2, count.get(3).getCount());
    }

    private void fact(int startday, int diagnoskapitel) {
        Fact fact = aFact().withId(1).withLan(3).withKommun(380).withForsamling(38002).
                withEnhet(1).withLakarintyg(intyg++).
                withPatient(patient++).withKon(Kon.FEMALE).withAlder(45).
                withDiagnoskapitel(diagnoskapitel).withDiagnosavsnitt(14).withDiagnoskategori(16).withDiagnoskod(18).
                withSjukskrivningsgrad(100).withStartdatum(startday).withSlutdatum(startday + 9).
                withLakarkon(Kon.FEMALE).withLakaralder(32).withLakarbefattning(new int[]{201010}).withLakarid(1).build();
        facts.add(fact);
    }

    @Test
    public void testGetUnderdiagnosGrupperForKapitel() throws Exception {
        //When
        final LocalDate start = Instant.ofEpochMilli(1416223845652L).atZone(ZoneId.systemDefault()).toLocalDate();
        DiagnosgruppResponse result = query.getUnderdiagnosgrupper(new MutableAisle(new HsaIdVardgivare("vgid")).createAisle(),
                new FilterPredicates(fact -> false, sjukfall -> true, "hash", false), start, 1, 1, "A00-B99");

        //Then
        assertEquals(21, result.getIcdTyps().size());
        assertEquals("A00-A09", result.getIcdTyps().get(0).getId());
    }

    @Test
    public void testGetUnderdiagnosGrupperForAvsnitt() throws Exception {
        //When
        final Aisle aisle = new MutableAisle(new HsaIdVardgivare("vgid")).createAisle();
        final FilterPredicates filter = new FilterPredicates(fact -> false, sjukfall -> true, "hash", false);
        final LocalDate start = Instant.ofEpochMilli(1416223845652L).atZone(ZoneId.systemDefault()).toLocalDate();
        final int periods = 1;
        final int periodLength = 1;
        final String rangeId = "A00-A09";
        DiagnosgruppResponse result = query.getUnderdiagnosgrupper(aisle, filter, start, periods, periodLength, rangeId);

        //Then
        assertEquals(10, result.getIcdTyps().size());
        assertEquals("A00", result.getIcdTyps().get(0).getId());
    }

}
