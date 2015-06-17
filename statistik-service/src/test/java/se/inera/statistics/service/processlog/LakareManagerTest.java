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

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:process-log-impl-test.xml", "classpath:icd10.xml" })
@Transactional
@DirtiesContext
public class LakareManagerTest {
    @Autowired
    LakareManager lakareManager;

    @Autowired
    HSAService hsaService;

    @Test
    public void saveOneLakare() {
        JsonNode hsaInfo = hsaService.getHSAInfo(new HSAKey("vg", "enhet", "lakare"));

        lakareManager.saveLakare(hsaInfo);

        List<Lakare> allLakares = lakareManager.getAllLakares();
        assertEquals(1, allLakares.size());
        assertEquals("LAKARE", allLakares.get(0).getLakareId().getId());
        assertEquals("Sirkka", allLakares.get(0).getTilltalsNamn());
        assertEquals("Uddhammar", allLakares.get(0).getEfterNamn());
    }

    @Test
    public void saveOneLakareWithoutVGFailsWithoutError() {
        JsonNode hsaInfo = hsaService.getHSAInfo(new HSAKey(null, "enhet", "lakare"));

        lakareManager.saveLakare(hsaInfo);

        List<Lakare> allLakares = lakareManager.getAllLakares();
        assertEquals(0, allLakares.size());
    }

    @Test
    public void getAllLakares() {
        JsonNode hsaInfo = hsaService.getHSAInfo(new HSAKey("vg", "enhet", "lakare1"));
        lakareManager.saveLakare(hsaInfo);
        hsaInfo = hsaService.getHSAInfo(new HSAKey("other-vg", "other-enhet", "lakare2"));
        lakareManager.saveLakare(hsaInfo);

        List<Lakare> allLakares = lakareManager.getAllLakares();
        assertEquals(2, allLakares.size());
        assertEquals("LAKARE1", allLakares.get(0).getLakareId().getId());
        assertEquals("Ibrahim", allLakares.get(0).getTilltalsNamn());
        assertEquals("Lazar", allLakares.get(0).getEfterNamn());
        assertEquals("LAKARE2", allLakares.get(1).getLakareId().getId());
        assertEquals("David", allLakares.get(1).getTilltalsNamn());
        assertEquals("Andersson", allLakares.get(1).getEfterNamn());
    }

}
