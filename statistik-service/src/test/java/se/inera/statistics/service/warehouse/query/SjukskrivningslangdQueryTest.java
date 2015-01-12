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

import org.joda.time.LocalDateTime;
import org.junit.Test;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.util.Ranges;
import se.inera.statistics.service.report.util.SjukfallslangdUtil;
import se.inera.statistics.service.warehouse.Fact;
import se.inera.statistics.service.warehouse.Sjukfall;
import se.inera.statistics.service.warehouse.SjukfallUtil;
import se.inera.statistics.service.warehouse.Warehouse;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static se.inera.statistics.service.warehouse.Fact.aFact;

public class SjukskrivningslangdQueryTest {

    public static final String VARDGIVARE = "vardgivare";
    private Warehouse warehouse = new Warehouse();

    private int intyg;
    private int patient;

    @Test
    public void one() {
        fact(4010, 45);
        warehouse.complete(LocalDateTime.now());
        Collection<Sjukfall> sjukfall = SjukfallUtil.calculateSjukfall(warehouse.get(VARDGIVARE));
        Map<Ranges.Range,Counter<Ranges.Range>> count = SjukskrivningslangdQuery.count(sjukfall);
        assertEquals(1, count.get(SjukfallslangdUtil.RANGES.rangeFor(45)).getCount());
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
        warehouse.complete(LocalDateTime.now());
        Collection<Sjukfall> sjukfall = SjukfallUtil.calculateSjukfall(warehouse.get(VARDGIVARE));
        List<Counter<Ranges.Range>> count = SjukskrivningslangdQuery.count(sjukfall,6);

        assertEquals(6, count.size());
        assertEquals(4, count.get(0).getCount());
        assertEquals(2, count.get(1).getCount());
        assertEquals(5, count.get(2).getCount());
        assertEquals(1, count.get(3).getCount());
    }

    private void fact(int startday, int length) {
        Fact fact = aFact().withLan(3).withKommun(380).withForsamling(38002).
                withEnhet(1).withLakarintyg(intyg++).
                withPatient(patient++).withKon(Kon.Female).withAlder(45).
                withDiagnoskapitel(0).withDiagnosavsnitt(14).withDiagnoskategori(16).
                withSjukskrivningsgrad(100).withStartdatum(startday).withSjukskrivningslangd(length).
                withLakarkon(Kon.Female).withLakaralder(32).withLakarbefattning(new int[]{201010}).withLakarid(1).build();
        warehouse.accept(fact, VARDGIVARE);
    }

}
