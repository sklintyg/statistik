/**
 * Copyright (C) 2014 Inera AB (http://www.inera.se)
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
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import se.inera.statistics.service.report.model.DiagnosgruppResponse;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.util.Icd10;
import se.inera.statistics.service.warehouse.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static se.inera.statistics.service.warehouse.Fact.aFact;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:icd10.xml", "classpath:query-test.xml"})
public class DiagnosgruppQueryTest {

    public static final String VARDGIVARE = "vardgivare";
    private Warehouse warehouse = new Warehouse();

    private int intyg;
    private int patient;

    @Autowired
    private Icd10 icd10;

    @Autowired
    private DiagnosgruppQuery query;

    @Test
    public void one() {
        fact(4010, 0);
        warehouse.complete(LocalDateTime.now());
        Collection<Sjukfall> sjukfall = SjukfallUtil.calculateSjukfall(warehouse.get(VARDGIVARE));
        Map<String,Counter<String>> count = query.count(sjukfall);
        assertEquals(1, count.get(icd10.getKapitel("A00-B99").getId()).getCount());
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
        Collection<Sjukfall> sjukfall = SjukfallUtil.calculateSjukfall(warehouse.get(VARDGIVARE));
        List<Counter<String>> count = query.count(sjukfall, 4);

        assertEquals(4, count.size());
        assertEquals(4, count.get(0).getCount());
        assertEquals(2, count.get(1).getCount());
        assertEquals(3, count.get(3).getCount());
    }

    private void fact(int startday, int diagnoskapitel) {
        Fact fact = aFact().withLan(3).withKommun(380).withForsamling(38002).
                withEnhet(1).withLakarintyg(intyg++).
                withPatient(patient++).withKon(Kon.Female).withAlder(45).
                withDiagnoskapitel(diagnoskapitel).withDiagnosavsnitt(14).withDiagnoskategori(16).
                withSjukskrivningsgrad(100).withStartdatum(startday).withSjukskrivningslangd(10).
                withLakarkon(Kon.Female).withLakaralder(32).withLakarbefattning(new int[]{201010}).withLakarid(1).build();

        warehouse.accept(fact, VARDGIVARE);
    }

    @Test
    public void testGetUnderdiagnosGrupperForKapitel() throws Exception {
        //When
        DiagnosgruppResponse result = query.getUnderdiagnosgrupper(new Aisle(), new Predicate<Fact>() {
            @Override
            public boolean apply(Fact fact) {
                return false;
            }
        }, new LocalDate(1416223845652L), 1, 1, "A00-B99");

        //Then
        assertEquals(21, result.getIcdTyps().size());
        assertEquals("A00-A09", result.getIcdTyps().get(0).getId());
    }

    @Test
    public void testGetUnderdiagnosGrupperForAvsnitt() throws Exception {
        //When
        DiagnosgruppResponse result = query.getUnderdiagnosgrupper(new Aisle(), new Predicate<Fact>() {
            @Override
            public boolean apply(Fact fact) {
                return false;
            }
        }, new LocalDate(1416223845652L), 1, 1, "A00-A09");

        //Then
        assertEquals(10, result.getIcdTyps().size());
        assertEquals("A00", result.getIcdTyps().get(0).getId());
    }

}
