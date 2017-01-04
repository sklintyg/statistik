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

import org.junit.Test;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.util.Ranges;
import se.inera.statistics.service.report.util.SjukfallslangdUtil;
import se.inera.statistics.service.warehouse.Aisle;
import se.inera.statistics.service.warehouse.Fact;
import se.inera.statistics.service.warehouse.Sjukfall;
import se.inera.statistics.service.warehouse.SjukfallUtil;
import se.inera.statistics.service.warehouse.Warehouse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static se.inera.statistics.service.warehouse.Fact.aFact;

public class SjukskrivningslangdQueryTest {

    public static final HsaIdVardgivare VARDGIVARE = new HsaIdVardgivare("vardgivare");
    private Warehouse warehouse = new Warehouse();

    private int intyg;
    private int patient;

    @Test
    public void one() {
        fact(4010, 45);
        warehouse.complete(LocalDateTime.now());
        Collection<Sjukfall> sjukfall = calculateSjukfallsHelper(warehouse.get(VARDGIVARE));
        Map<Ranges.Range,Counter<Ranges.Range>> count = SjukskrivningslangdQuery.count(sjukfall);
        assertEquals(1, count.get(SjukfallslangdUtil.RANGES.rangeFor(45)).getCount());
    }

    private Collection<Sjukfall> calculateSjukfallsHelper(Aisle aisle) {
        return new SjukfallUtil().sjukfallGrupper(LocalDate.of(2000, 1, 1), 1, 1000000, aisle, SjukfallUtil.ALL_ENHETER).iterator().next().getSjukfall();
    }

    @Test
    public void useMax() {
        fact(4010, 1);
        fact(4010, 1);
        fact(4010, 1);
        fact(4010, 1);
        fact(4010, 30);
        fact(4010, 30);
        fact(4010, 45);
        fact(4010, 45);
        fact(4010, 50);
        fact(4010, 50);
        fact(4010, 50);
        fact(4010, 100);
        fact(4010, 200);
        fact(4010, 400);
        warehouse.complete(LocalDateTime.now());
        Collection<Sjukfall> sjukfall = calculateSjukfallsHelper(warehouse.get(VARDGIVARE));
        List<Counter<Ranges.Range>> count = SjukskrivningslangdQuery.count(sjukfall,6);

        assertEquals(6, count.size());
        assertAmountAndName(count.get(0), 4, "Under 15");
        assertAmountAndName(count.get(1), 2, "15-30");
        assertAmountAndName(count.get(2), 5, "31-60");
        assertAmountAndName(count.get(3), 1, "91-180");
        assertAmountAndName(count.get(4), 1, "181-365");
        assertAmountAndName(count.get(5), 1, "Över 365");
    }

    private void assertAmountAndName(Counter<Ranges.Range> rangeCounter, int expectedAmount, String nameContains) {
        assertEquals(expectedAmount, rangeCounter.getCount());
        assertTrue(rangeCounter.getKey().getName().contains(nameContains));
    }

    private void fact(int startday, int length) {
        Fact fact = aFact().withLan(3).withKommun(380).withForsamling(38002).
                withEnhet(1).withLakarintyg(intyg++).
                withPatient(patient++).withKon(Kon.FEMALE).withAlder(45).
                withDiagnoskapitel(0).withDiagnosavsnitt(14).withDiagnoskategori(16).withDiagnoskod(18).
                withSjukskrivningsgrad(100).withStartdatum(startday).withSlutdatum(startday + length - 1).
                withLakarkon(Kon.FEMALE).withLakaralder(32).withLakarbefattning(new int[]{201010}).withLakarid(1).withEnkeltIntyg(false).build();
        warehouse.accept(fact, VARDGIVARE);
    }

}
