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
package se.inera.statistics.service.region;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.hsa.model.HsaIdUser;
import se.inera.statistics.hsa.model.HsaIdVardgivare;

public class RegionEnhetFileDataTest {

    @Test
    public void testNullInputCreatesEmptyObject() throws Exception {
        //Given
        final HsaIdVardgivare vgId = null;
        final List<RegionEnhetFileDataRow> rows = null;

        //When
        final RegionEnhetFileData data = new RegionEnhetFileData(vgId, rows, "", new HsaIdUser(""), "");

        //Then
        assertEquals("", data.getVgId().getId());
        assertNotNull(data.getRows());
    }

    @Test
    public void testObjectIsCreatedWithCorrectValues() throws Exception {
        //Given
        final HsaIdVardgivare vgId = new HsaIdVardgivare("TestVgId");
        final HsaIdEnhet testHsaId = new HsaIdEnhet("TestHsaId");
        final int listadePatienter = 2;
        final List<RegionEnhetFileDataRow> rows = Arrays.asList(new RegionEnhetFileDataRow(testHsaId, listadePatienter));

        //When
        final RegionEnhetFileData data = new RegionEnhetFileData(vgId, rows, "", new HsaIdUser(""), "");

        //Then
        assertEquals(vgId, data.getVgId());
        assertEquals(1, data.getRows().size());
        final RegionEnhetFileDataRow row = data.getRows().get(0);
        assertEquals(testHsaId, row.getEnhetensHsaId());
        assertEquals(Integer.valueOf(listadePatienter), row.getListadePatienter());
    }

}
