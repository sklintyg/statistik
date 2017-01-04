/**
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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

import com.google.common.base.Predicate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
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

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static se.inera.statistics.service.warehouse.Fact.aFact;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:icd10.xml", "classpath:query-test.xml"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class DiagnosgruppQueryTest {

    private static final HsaIdVardgivare VARDGIVARE = new HsaIdVardgivare("vardgivare");

    private Warehouse warehouse;

    private int intyg;
    private int patient;

    @Autowired
    private Icd10 icd10;

    @Autowired
    private DiagnosgruppQuery query;

    @Autowired
    private SjukfallUtil sjukfallUtil;

    @Before
    public void setUp() throws Exception {
        warehouse = new Warehouse();
    }

    @Test
    public void one() {
        final String kapitelCode = "A00-B99";
        fact(4010, Icd10.icd10ToInt(kapitelCode, Icd10RangeType.KAPITEL));
        warehouse.complete(LocalDateTime.now());
        final Collection<Sjukfall> sjukfall = calculateSjukfallsHelper(warehouse.get(VARDGIVARE));
        final Map<Integer,Counter<Integer>> count = query.count(sjukfall);
        final Icd10.Kapitel kapitel = icd10.getKapitel(kapitelCode);
        final int kapitelInt = kapitel.toInt();
        final Counter<Integer> kapitelCounter = count.get(kapitelInt);
        final int kapitelCount = kapitelCounter.getCount();
        assertEquals(1, kapitelCount);
    }

    private Collection<Sjukfall> calculateSjukfallsHelper(Aisle aisle) {
        return sjukfallUtil.sjukfallGrupper(LocalDate.of(2000, 1, 1), 1, 1000000, aisle, SjukfallUtil.ALL_ENHETER).iterator().next().getSjukfall();
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
        warehouse.complete(LocalDateTime.now());
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
                withLakarkon(Kon.FEMALE).withLakaralder(32).withLakarbefattning(new int[]{201010}).withLakarid(1).withEnkeltIntyg(false).build();

        warehouse.accept(fact, VARDGIVARE);
    }

    @Test
    public void testGetUnderdiagnosGrupperForKapitel() throws Exception {
        //When
        DiagnosgruppResponse result = query.getUnderdiagnosgrupper(new MutableAisle(new HsaIdVardgivare("vgid")).createAisle(), new FilterPredicates(new Predicate<Fact>() {
            @Override
            public boolean apply(Fact fact) {
                return false;
            }
        }, sjukfall -> true, "hash", false), Instant.ofEpochMilli(1416223845652L).atZone(ZoneId.systemDefault()).toLocalDate(), 1, 1, "A00-B99");

        //Then
        assertEquals(21, result.getIcdTyps().size());
        assertEquals("A00-A09", result.getIcdTyps().get(0).getId());
    }

    @Test
    public void testGetUnderdiagnosGrupperForAvsnitt() throws Exception {
        //When
        DiagnosgruppResponse result = query.getUnderdiagnosgrupper(new MutableAisle(new HsaIdVardgivare("vgid")).createAisle(), new FilterPredicates(new Predicate<Fact>() {
            @Override
            public boolean apply(Fact fact) {
                return false;
            }
        }, sjukfall -> true, "hash", false), Instant.ofEpochMilli(1416223845652L).atZone(ZoneId.systemDefault()).toLocalDate(), 1, 1, "A00-A09");

        //Then
        assertEquals(10, result.getIcdTyps().size());
        assertEquals("A00", result.getIcdTyps().get(0).getId());
    }

}
