/*
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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

import org.junit.Test;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.report.model.Kon;

import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static se.inera.statistics.service.warehouse.Fact.aFact;

public class AisleTest {

    private final MutableAisle aisle = new MutableAisle(new HsaIdVardgivare("vgid"));

    @Test
    public void outOfOrderFactsGetsSorted() {
        Fact fact1 = aFact().withId(1).withLan(3).withKommun(380).withForsamling(38002).
                withEnhet(1).withLakarintyg(1).
                withPatient(1).withKon(Kon.FEMALE).withAlder(45).
                withDiagnoskapitel(0).withDiagnosavsnitt(14).withDiagnoskategori(16).withDiagnoskod(18).
                withSjukskrivningsgrad(100).withStartdatum(4010).withSlutdatum(4056).
                withLakarkon(Kon.FEMALE).withLakaralder(32).withLakarbefattning(new int[]{201010}).withLakarid(1).build();
        aisle.addLine(fact1);
        Fact fact2 = aFact().withId(2).withLan(3).withKommun(380).withForsamling(38002).
                withEnhet(1).withLakarintyg(2).
                withPatient(1).withKon(Kon.FEMALE).withAlder(45).
                withDiagnoskapitel(0).withDiagnosavsnitt(14).withDiagnoskategori(16).withDiagnoskod(18).
                withSjukskrivningsgrad(100).withStartdatum(4000).withSlutdatum(4046).
                withLakarkon(Kon.FEMALE).withLakaralder(32).withLakarbefattning(new int[]{201010}).withLakarid(1).build();
        aisle.addLine(fact2);
        Iterator<Fact> iterator = aisle.createAisle().iterator();
        assertEquals(2, iterator.next().getLakarintyg());
        assertEquals(1, iterator.next().getLakarintyg());
    }
}
