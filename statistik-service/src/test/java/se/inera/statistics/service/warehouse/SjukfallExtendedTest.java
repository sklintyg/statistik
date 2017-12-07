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

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SjukfallExtendedTest {

    private int lakarintyg = 0;

    @Test
    public void testGetAllDxs() throws Exception {
        //Given
        final SjukfallExtended se1 = new SjukfallExtended(createFact(1, 1, 1, 1));
        final SjukfallExtended se2 = se1.extendSjukfall(createFact(2, 2, 2, 2));
        final SjukfallExtended se3 = se2.extendSjukfall(createFact(3, 3, 3, 3));

        //When
        final List<Diagnos> allDxs = se3.getAllDxs();

        //Then
        assertEquals(3, allDxs.size());
        assertEquals(1, allDxs.get(0).getDiagnosavsnitt());
        assertEquals(1, allDxs.get(0).getDiagnoskapitel());
        assertEquals(1, allDxs.get(0).getDiagnoskategori());
        assertEquals(1, allDxs.get(0).getDiagnoskod());
        assertEquals(2, allDxs.get(1).getDiagnosavsnitt());
        assertEquals(2, allDxs.get(1).getDiagnoskapitel());
        assertEquals(2, allDxs.get(1).getDiagnoskategori());
        assertEquals(2, allDxs.get(1).getDiagnoskod());
        assertEquals(3, allDxs.get(2).getDiagnosavsnitt());
        assertEquals(3, allDxs.get(2).getDiagnoskapitel());
        assertEquals(3, allDxs.get(2).getDiagnoskategori());
        assertEquals(3, allDxs.get(2).getDiagnoskod());
    }

    private Fact createFact(int diagnoskapitel, int diagnosavsnitt, int diagnoskategori, int diagnoskod) {
        return new Fact(1, 1, 1, 1, 1 , lakarintyg++, 1, 1,1,1,1, diagnoskapitel, diagnosavsnitt, diagnoskategori, diagnoskod,1,1,1,null, 1);
    }

}
