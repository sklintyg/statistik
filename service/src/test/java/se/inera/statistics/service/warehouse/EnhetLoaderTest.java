/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import se.inera.statistics.integration.hsa.model.HsaIdEnhet;
import se.inera.statistics.integration.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.processlog.Enhet;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:process-log-impl-test.xml", "classpath:icd10.xml"})
@DirtiesContext
public class EnhetLoaderTest {

    @Autowired
    private EnhetLoader enhetLoader;

    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    @Test
    @Transactional
    @Ignore
    public void testGetEnhets() throws Exception {
        //Given
        manager.persist(new Enhet(new HsaIdVardgivare("vg1"), new HsaIdEnhet("e1"), "1", "", "", "", "ve1"));
        manager.persist(new Enhet(new HsaIdVardgivare("vg1"), new HsaIdEnhet("e2"), "2", "", "", "", "ve2"));
        manager.persist(new Enhet(new HsaIdVardgivare("vg1"), new HsaIdEnhet("e3"), "3", "", "", "", "ve3"));
        manager.persist(new Enhet(new HsaIdVardgivare("vg2"), new HsaIdEnhet("e4"), "4", "", "", "", "ve4"));
        manager.persist(new Enhet(new HsaIdVardgivare("vg2"), new HsaIdEnhet("e5"), "5", "", "", "", "ve5"));

        //When
        final List<Enhet> enhetsWithHsaId = enhetLoader
            .getEnhets(Arrays.asList(new HsaIdEnhet("e1"), new HsaIdEnhet("e2"), new HsaIdEnhet("e4")));

        //Then
        final List<String> enhetNames = enhetsWithHsaId.stream().map(Enhet::getNamn).collect(Collectors.toList());
        assertEquals(3, enhetNames.size());
        assertTrue(enhetNames.contains("1"));
        assertTrue(enhetNames.contains("2"));
        assertTrue(enhetNames.contains("4"));
    }

    @Test
    @Transactional
    @Ignore
    public void testGetAllEnhetsForVg() throws Exception {
        //Given
        manager.persist(new Enhet(new HsaIdVardgivare("vg1"), new HsaIdEnhet("e1"), "1", "", "", "", "ve1"));
        manager.persist(new Enhet(new HsaIdVardgivare("vg1"), new HsaIdEnhet("e2"), "2", "", "", "", "ve2"));
        manager.persist(new Enhet(new HsaIdVardgivare("vg1"), new HsaIdEnhet("e3"), "3", "", "", "", "ve3"));
        manager.persist(new Enhet(new HsaIdVardgivare("vg2"), new HsaIdEnhet("e4"), "4", "", "", "", "ve4"));
        manager.persist(new Enhet(new HsaIdVardgivare("vg2"), new HsaIdEnhet("e5"), "5", "", "", "", "ve5"));

        //When
        final List<Enhet> enhetsWithHsaId = enhetLoader.getAllEnhetsForVg(new HsaIdVardgivare("VG2"));

        //Then
        final List<String> enhetNames = enhetsWithHsaId.stream().map(Enhet::getNamn).collect(Collectors.toList());
        assertEquals(2, enhetNames.size());
        assertTrue(enhetNames.contains("4"));
        assertTrue(enhetNames.contains("5"));
    }

    @Test
    @Transactional
    @Ignore
    public void getAllEnhetsForVardenhet() throws Exception {
        //Given
        manager.persist(new Enhet(new HsaIdVardgivare("vg1"), new HsaIdEnhet("e1"), "1", "", "", "", "e1"));
        manager.persist(new Enhet(new HsaIdVardgivare("vg1"), new HsaIdEnhet("e2"), "2", "", "", "", "e1"));
        manager.persist(new Enhet(new HsaIdVardgivare("vg1"), new HsaIdEnhet("e3"), "3", "", "", "", "e3"));
        manager.persist(new Enhet(new HsaIdVardgivare("vg2"), new HsaIdEnhet("e4"), "4", "", "", "", "e4"));
        manager.persist(new Enhet(new HsaIdVardgivare("vg1"), new HsaIdEnhet("e5"), "5", "", "", "", "e1"));

        //When
        final List<Enhet> enhetsWithHsaId = enhetLoader.getAllEnhetsForVardenhet(new HsaIdEnhet("e1"));

        //Then
        final List<String> enhetNames = enhetsWithHsaId.stream().map(Enhet::getNamn).collect(Collectors.toList());
        assertEquals(3, enhetNames.size());
        assertTrue(enhetNames.contains("1"));
        assertTrue(enhetNames.contains("2"));
        assertTrue(enhetNames.contains("5"));
    }
}