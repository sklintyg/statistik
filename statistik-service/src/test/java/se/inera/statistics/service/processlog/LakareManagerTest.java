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
        assertEquals("lakare", allLakares.get(0).getLakareId());
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
        assertEquals("lakare1", allLakares.get(0).getLakareId());
        assertEquals("Ibrahim", allLakares.get(0).getTilltalsNamn());
        assertEquals("Lazar", allLakares.get(0).getEfterNamn());
        assertEquals("lakare2", allLakares.get(1).getLakareId());
        assertEquals("David", allLakares.get(1).getTilltalsNamn());
        assertEquals("Andersson", allLakares.get(1).getEfterNamn());
    }

}
