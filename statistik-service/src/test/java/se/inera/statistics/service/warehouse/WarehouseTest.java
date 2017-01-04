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
package se.inera.statistics.service.warehouse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.processlog.Enhet;

public class WarehouseTest {

    private Warehouse warehouse = new Warehouse();

    @Test
    public void testGetEnhetsWithHsaId() throws Exception {
        //Given
        warehouse.accept(new Enhet(new HsaIdVardgivare("vg1"), new HsaIdEnhet("e1"), "1", null, null, null));
        warehouse.accept(new Enhet(new HsaIdVardgivare("vg1"), new HsaIdEnhet("e2"), "2", null, null, null));
        warehouse.accept(new Enhet(new HsaIdVardgivare("vg1"), new HsaIdEnhet("e3"), "3", null, null, null));
        warehouse.accept(new Enhet(new HsaIdVardgivare("vg2"), new HsaIdEnhet("e4"), "4", null, null, null));
        warehouse.accept(new Enhet(new HsaIdVardgivare("vg2"), new HsaIdEnhet("e5"), "5", null, null, null));
        warehouse.completeEnhets();

        //When
        final List<Enhet> enhetsWithHsaId = warehouse.getEnhetsWithHsaId(Arrays.asList(new HsaIdEnhet("e1"), new HsaIdEnhet("e2"), new HsaIdEnhet("e4")));

        //Then
        final List<String> enhetNames = enhetsWithHsaId.stream().map(Enhet::getNamn).collect(Collectors.toList());
        assertEquals(3, enhetNames.size());
        assertTrue(enhetNames.contains("1"));
        assertTrue(enhetNames.contains("2"));
        assertTrue(enhetNames.contains("4"));
    }

    @Test
    public void testGetEnhetsWithHsaIdWithoutCallingCompleteEnhets() throws Exception {
        //Given

        //When
        final List<Enhet> enhetsWithHsaId = warehouse.getEnhetsWithHsaId(Arrays.asList(new HsaIdEnhet("e1"), new HsaIdEnhet("e2"), new HsaIdEnhet("e4")));

        //Then
        assertEquals(0, enhetsWithHsaId.size());
    }

}
