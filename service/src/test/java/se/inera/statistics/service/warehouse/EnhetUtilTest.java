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

package se.inera.statistics.service.warehouse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
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
public class EnhetUtilTest {

    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    @Autowired
    private Warehouse warehouse;

    @Test
    @Transactional
    public void testGetAllEnheterInVardenheter() {
        //Given
        manager.persist(new Enhet(new HsaIdVardgivare("vg1"), new HsaIdEnhet("e1"), "1", "", "", "", "e1"));
        manager.persist(new Enhet(new HsaIdVardgivare("vg1"), new HsaIdEnhet("e2"), "2", "", "", "", "e1"));
        manager.persist(new Enhet(new HsaIdVardgivare("vg1"), new HsaIdEnhet("e3"), "3", "", "", "", "e1"));
        manager.persist(new Enhet(new HsaIdVardgivare("vg1"), new HsaIdEnhet("e4"), "4", "", "", "", null));
        manager.persist(new Enhet(new HsaIdVardgivare("vg1"), new HsaIdEnhet("e5"), "5", "", "", "", "e4"));

        Collection<HsaIdEnhet> enheter1 = new HashSet<HsaIdEnhet>();
        enheter1.add(new HsaIdEnhet("e1"));
        enheter1.add(new HsaIdEnhet("e5"));

        Collection<HsaIdEnhet> enheter2 = new HashSet<HsaIdEnhet>();
        enheter2.add(new HsaIdEnhet("e4"));

        //When
        final Collection<HsaIdEnhet> enheterResult1 = EnhetUtil
            .getAllEnheterInVardenheter(enheter1, warehouse);

        final Collection<HsaIdEnhet> enheterResult2 = EnhetUtil
            .getAllEnheterInVardenheter(enheter2, warehouse);

        //Then
        final List<String> enhetIds1 = enheterResult1.stream().map(HsaIdEnhet::getId).collect(Collectors.toList());

        final List<String> enhetIds2 = enheterResult2.stream().map(HsaIdEnhet::getId).collect(Collectors.toList());

        assertEquals(4, enhetIds1.size());
        assertTrue(enhetIds1.contains("E1"));
        assertTrue(enhetIds1.contains("E2"));
        assertTrue(enhetIds1.contains("E3"));
        assertTrue(enhetIds1.contains("E5"));

        assertEquals(2, enhetIds2.size());
        assertTrue(enhetIds2.contains("E4"));
        assertTrue(enhetIds2.contains("E5"));

    }
}