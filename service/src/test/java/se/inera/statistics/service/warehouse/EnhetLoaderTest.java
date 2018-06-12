/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.warehouse.EnhetLoader;
import se.inera.statistics.service.processlog.Enhet;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:process-log-impl-test.xml", "classpath:icd10.xml" })
@DirtiesContext
public class EnhetLoaderTest {

    @Autowired
    private EnhetLoader enhetLoader;
    
    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    @Test
    @Transactional
    public void testGetEnhets() throws Exception {
        //Given
        manager.persist(new Enhet(new HsaIdVardgivare("vg1"), new HsaIdEnhet("e1"), "1", "", "", ""));
        manager.persist(new Enhet(new HsaIdVardgivare("vg1"), new HsaIdEnhet("e2"), "2", "", "", ""));
        manager.persist(new Enhet(new HsaIdVardgivare("vg1"), new HsaIdEnhet("e3"), "3", "", "", ""));
        manager.persist(new Enhet(new HsaIdVardgivare("vg2"), new HsaIdEnhet("e4"), "4", "", "", ""));
        manager.persist(new Enhet(new HsaIdVardgivare("vg2"), new HsaIdEnhet("e5"), "5", "", "", ""));

        //When
        final List<Enhet> enhetsWithHsaId = enhetLoader.getEnhets(Arrays.asList(new HsaIdEnhet("e1"), new HsaIdEnhet("e2"), new HsaIdEnhet("e4")));

        //Then
        final List<String> enhetNames = enhetsWithHsaId.stream().map(Enhet::getNamn).collect(Collectors.toList());
        assertEquals(3, enhetNames.size());
        assertTrue(enhetNames.contains("1"));
        assertTrue(enhetNames.contains("2"));
        assertTrue(enhetNames.contains("4"));
    }

    @Test
    @Transactional
    public void testGetAllEnhetsForVg() throws Exception {
        //Given
        manager.persist(new Enhet(new HsaIdVardgivare("vg1"), new HsaIdEnhet("e1"), "1", "", "", ""));
        manager.persist(new Enhet(new HsaIdVardgivare("vg1"), new HsaIdEnhet("e2"), "2", "", "", ""));
        manager.persist(new Enhet(new HsaIdVardgivare("vg1"), new HsaIdEnhet("e3"), "3", "", "", ""));
        manager.persist(new Enhet(new HsaIdVardgivare("vg2"), new HsaIdEnhet("e4"), "4", "", "", ""));
        manager.persist(new Enhet(new HsaIdVardgivare("vg2"), new HsaIdEnhet("e5"), "5", "", "", ""));

        //When
        final List<Enhet> enhetsWithHsaId = enhetLoader.getAllEnhetsForVg(new HsaIdVardgivare("VG2"));

        //Then
        final List<String> enhetNames = enhetsWithHsaId.stream().map(Enhet::getNamn).collect(Collectors.toList());
        assertEquals(2, enhetNames.size());
        assertTrue(enhetNames.contains("4"));
        assertTrue(enhetNames.contains("5"));
    }

}
