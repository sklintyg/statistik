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

import static org.junit.Assert.assertEquals;

import java.util.List;
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
public class LakareManagerTest {

    @Autowired
    LakareManager lakareManager;

    @Autowired
    HSAService hsaService;

    @Test
    public void saveOneLakare() {
        HsaInfo hsaInfo = hsaService.getHSAInfo(new HSAKey("vg-verksamhet1", "verksamhet1", "lakare_my"));

        lakareManager.saveLakare(hsaInfo);

        List<Lakare> allLakares = lakareManager.getAllLakares();
        assertEquals(1, allLakares.size());
        assertEquals("LAKARE_MY", allLakares.get(0).getLakareId().getId());
        assertEquals("My", allLakares.get(0).getTilltalsNamn());
        assertEquals("Åsgren", allLakares.get(0).getEfterNamn());
    }

    @Test
    public void saveOneLakareWithoutVGFailsWithoutError() {
        HsaInfo hsaInfo = hsaService.getHSAInfo(new HSAKey(null, "verksamhet1", "lakare_my"));
        hsaInfo = new HsaInfo(hsaInfo.getEnhet(), hsaInfo.getHuvudenhet(), null, hsaInfo.getPersonal());

        lakareManager.saveLakare(hsaInfo);

        List<Lakare> allLakares = lakareManager.getAllLakares();
        assertEquals(0, allLakares.size());
    }

    @Test
    public void getAllLakares() {
        HsaInfo hsaInfo = hsaService.getHSAInfo(new HSAKey("vg-verksamhet1", "verksamhet1", "lakare_my"));
        lakareManager.saveLakare(hsaInfo);
        hsaInfo = hsaService.getHSAInfo(new HSAKey("vg-verksamhet2", "verksamhet3", "lakare_vieux"));
        lakareManager.saveLakare(hsaInfo);

        List<Lakare> allLakares = lakareManager.getAllLakares();
        assertEquals(2, allLakares.size());
        assertEquals("LAKARE_MY", allLakares.get(0).getLakareId().getId());
        assertEquals("My", allLakares.get(0).getTilltalsNamn());
        assertEquals("Åsgren", allLakares.get(0).getEfterNamn());
        assertEquals("LAKARE_VIEUX", allLakares.get(1).getLakareId().getId());
        assertEquals("Vieux", allLakares.get(1).getTilltalsNamn());
        assertEquals("En", allLakares.get(1).getEfterNamn());
    }

}
