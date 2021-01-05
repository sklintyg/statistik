/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.processlog;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import se.inera.statistics.integration.hsa.model.HsaIdEnhet;
import se.inera.statistics.integration.hsa.model.HsaIdVardgivare;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;

public class EnhetManagerTest {

    @InjectMocks
    private EnhetManager enhetManager;

    @Mock
    private EntityManager manager;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getAllVardenhetsForVardgivareId() {
        //Given
        final Query query = Mockito.mock(Query.class);
        Mockito.when(manager.createQuery(anyString())).thenReturn(query);
        final Enhet enhet1 = createEnhet("e1", "ve1");
        final Enhet enhet2 = createEnhet("e3", null);
        final Enhet enhet3 = createEnhet("e4", "e4");
        Mockito.when(query.getResultList()).thenReturn(Arrays.asList(enhet1, enhet2, enhet3));
        final String vgId = "VG1";

        //When
        final List<Enhet> enhets = enhetManager.getAllVardenhetsForVardgivareId(new HsaIdVardgivare(vgId));

        //Then
        Mockito.verify(query).setParameter("vgId", vgId);
        assertEquals(2, enhets.size());
        assertTrue(enhets.containsAll(Arrays.asList(enhet2, enhet3)));
    }

    private Enhet createEnhet(String enhetId, String vardenhetId) {
        final Enhet enhet = new Enhet();
        enhet.setEnhetId(new HsaIdEnhet(enhetId));
        enhet.setVardenhetId(vardenhetId);
        return enhet;
    }

}