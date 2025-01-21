/*
 * Copyright (C) 2025 Inera AB (http://www.inera.se)
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

import static org.junit.Assert.assertEquals;

import java.util.List;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import se.inera.statistics.service.hsa.HSAKey;
import se.inera.statistics.service.hsa.HSAService;
import se.inera.statistics.service.hsa.HsaInfo;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:process-log-impl-test.xml", "classpath:icd10.xml"})
@Transactional
@DirtiesContext
public class VardgivareManagerTest {

    @Autowired
    VardgivareManager vardgivareManager;

    @Autowired
    HSAService hsaService;

    @Test
    @Ignore
    public void saveOneEnhet() {
        HSAKey key = new HSAKey("vg-verksamhet1", "verksamhet1", "lakare_my");
        HsaInfo hsaInfo = hsaService.getHSAInfo(key);

        vardgivareManager.saveEnhet(hsaInfo, null);

        List<Enhet> allEnhets = vardgivareManager.getAllEnhets();
        assertEquals(1, allEnhets.size());
        assertEquals("VERKSAMHET1", allEnhets.get(0).getEnhetId().getId());
        //Enhet name is set by external script (tools/fileservice).
        //Default name until set by script will be the hsa id.
        assertEquals("VERKSAMHET1", allEnhets.get(0).getNamn());
    }

    @Test
    @Ignore
    public void getEnhetsForNonExistingVardgivare() {
        HsaInfo hsaInfo = hsaService.getHSAInfo(new HSAKey("vg-verksamhet1", "verksamhet1", "lakare_my"));
        vardgivareManager.saveEnhet(hsaInfo, null);

        List<Enhet> enhets = vardgivareManager.getEnhets("jag finns inte");

        List<Enhet> allEnhets = vardgivareManager.getAllEnhets();
        assertEquals(1, allEnhets.size());
        assertEquals(0, enhets.size());
    }

    @Test
    @Ignore
    public void getEnhetsForExistingVardgivare() {
        HSAKey key = new HSAKey("vg-verksamhet1", "verksamhet1", "lakare_my");
        HsaInfo hsaInfo = hsaService.getHSAInfo(key);
        vardgivareManager.saveEnhet(hsaInfo, null);
        HSAKey key1 = new HSAKey("vg-verksamhet2", "verksamhet3", "lakare_vieux");
        hsaInfo = hsaService.getHSAInfo(key1);
        vardgivareManager.saveEnhet(hsaInfo, null);

        List<Enhet> enhets = vardgivareManager.getEnhets("VG-VERKSAMHET1");

        List<Enhet> allEnhets = vardgivareManager.getAllEnhets();
        assertEquals(2, allEnhets.size());
        assertEquals(1, enhets.size());
        assertEquals("VERKSAMHET1", allEnhets.get(0).getEnhetId().getId());
        //Enhet name is set by external script (tools/fileservice).
        //Default name until set by script will be the hsa id.
        assertEquals("VERKSAMHET1", allEnhets.get(0).getNamn());
    }

    @Test
    @Ignore
    public void getVardgivareWithOneVardgivareTwoEnhets() {
        HSAKey key = new HSAKey("vg-verksamhet1", "verksamhet1", "lakare_my");
        HsaInfo hsaInfo = hsaService.getHSAInfo(key);
        vardgivareManager.saveEnhet(hsaInfo, null);
        HSAKey key1 = new HSAKey("vg-verksamhet1", "verksamhet2", "lakare_vieux");
        hsaInfo = hsaService.getHSAInfo(key1);
        vardgivareManager.saveEnhet(hsaInfo, null);

        List<Enhet> allEnhets = vardgivareManager.getAllEnhets();
        assertEquals(2, allEnhets.size());
    }

}
