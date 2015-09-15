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
package se.inera.statistics.service.processlog;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import se.inera.statistics.service.hsa.HSAKey;
import se.inera.statistics.service.hsa.HSAService;
import se.inera.statistics.service.hsa.HsaDataInjectable;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:process-log-impl-test.xml", "classpath:icd10.xml" })
@Transactional
@DirtiesContext
public class VardgivareManagerTest {
    @Autowired
    VardgivareManager vardgivareManager;

    @Autowired
    HSAService hsaService;

    @Autowired
    private HsaDataInjectable hsaDataInjectable;

    @Test
    public void saveOneEnhet() {
        HSAKey key = new HSAKey("vg", "enhet", "lakare");
        hsaDataInjectable.setHsaKey(key);
        JsonNode hsaInfo = hsaService.getHSAInfo(key);

        vardgivareManager.saveEnhet(hsaInfo, null);

        List<Enhet> allEnhets = vardgivareManager.getAllEnhets();
        assertEquals(1, allEnhets.size());
        assertEquals("ENHET", allEnhets.get(0).getEnhetId().getId());
        //Enhet name is set by external script (tools/fileservice).
        //Default name until set by script will be the hsa id.
        assertEquals("enhet", allEnhets.get(0).getNamn());
    }

    @Test
    public void getEnhetsForNonExistingVardgivare() {
        JsonNode hsaInfo = hsaService.getHSAInfo(new HSAKey("vg", "enhet", "lakare"));
        vardgivareManager.saveEnhet(hsaInfo, null);

        List<Enhet> enhets = vardgivareManager.getEnhets("jag finns inte");

        List<Enhet> allEnhets = vardgivareManager.getAllEnhets();
        assertEquals(1, allEnhets.size());
        assertEquals(0, enhets.size());
    }

    @Test
    public void getEnhetsForExistingVardgivare() {
        HSAKey key = new HSAKey("vg", "enhet", "lakare");
        hsaDataInjectable.setHsaKey(key);
        JsonNode hsaInfo = hsaService.getHSAInfo(key);
        vardgivareManager.saveEnhet(hsaInfo, null);
        HSAKey key1 = new HSAKey("other-vg", "other-enhet", "lakare");
        hsaDataInjectable.setHsaKey(key1);
        hsaInfo = hsaService.getHSAInfo(key1);
        vardgivareManager.saveEnhet(hsaInfo, null);

        List<Enhet> enhets = vardgivareManager.getEnhets("VG");

        List<Enhet> allEnhets = vardgivareManager.getAllEnhets();
        assertEquals(2, allEnhets.size());
        assertEquals(1, enhets.size());
        assertEquals("ENHET", enhets.get(0).getEnhetId().getId());
        //Enhet name is set by external script (tools/fileservice).
        //Default name until set by script will be the hsa id.
        assertEquals("enhet", enhets.get(0).getNamn());
    }

    @Test
    public void getVardgivareWithOneVardgivareTwoEnhets() {
        HSAKey key = new HSAKey("vg", "enhet1", "lakare");
        hsaDataInjectable.setHsaKey(key);
        JsonNode hsaInfo = hsaService.getHSAInfo(key);
        vardgivareManager.saveEnhet(hsaInfo, null);
        HSAKey key1 = new HSAKey("vg", "enhet2", "lakare");
        hsaDataInjectable.setHsaKey(key1);
        hsaInfo = hsaService.getHSAInfo(key1);
        vardgivareManager.saveEnhet(hsaInfo, null);

        List<Vardgivare> allVardgivares = vardgivareManager.getAllVardgivares();

        List<Enhet> allEnhets = vardgivareManager.getAllEnhets();
        assertEquals(2, allEnhets.size());
        assertEquals(1, allVardgivares.size());
        assertEquals("VG", allVardgivares.get(0).getId());
        //Enhet name is set by external script (tools/fileservice).
        //Default name until set by script will be the hsa id.
        assertEquals("vg", allVardgivares.get(0).getNamn());
    }
}
