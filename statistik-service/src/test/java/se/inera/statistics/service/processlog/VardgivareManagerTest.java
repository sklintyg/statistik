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
public class VardgivareManagerTest {
    @Autowired
    VardgivareManager vardgivareManager;

    @Autowired
    HSAService hsaService;

    @Test
    public void saveOneEnhet() {
        JsonNode hsaInfo = hsaService.getHSAInfo(new HSAKey("vg", "enhet", "lakare"));

        vardgivareManager.saveEnhet(hsaInfo);

        List<Enhet> allEnhets = vardgivareManager.getAllEnhets();
        assertEquals(1, allEnhets.size());
        assertEquals("enhet", allEnhets.get(0).getEnhetId());
        assertEquals("Enhet enhet", allEnhets.get(0).getNamn());
    }

    @Test
    public void getEnhetsForNonExistingVardgivare() {
        JsonNode hsaInfo = hsaService.getHSAInfo(new HSAKey("vg", "enhet", "lakare"));
        vardgivareManager.saveEnhet(hsaInfo);

        List<Enhet> enhets = vardgivareManager.getEnhets("jag finns inte");

        List<Enhet> allEnhets = vardgivareManager.getAllEnhets();
        assertEquals(1, allEnhets.size());
        assertEquals(0, enhets.size());
    }

    @Test
    public void getEnhetsForExistingVardgivare() {
        JsonNode hsaInfo = hsaService.getHSAInfo(new HSAKey("vg", "enhet", "lakare"));
        vardgivareManager.saveEnhet(hsaInfo);
        hsaInfo = hsaService.getHSAInfo(new HSAKey("other-vg", "other-enhet", "lakare"));
        vardgivareManager.saveEnhet(hsaInfo);

        List<Enhet> enhets = vardgivareManager.getEnhets("vg");

        List<Enhet> allEnhets = vardgivareManager.getAllEnhets();
        assertEquals(2, allEnhets.size());
        assertEquals(1, enhets.size());
        assertEquals("enhet", enhets.get(0).getEnhetId());
        assertEquals("Enhet enhet", enhets.get(0).getNamn());
    }

    @Test
    public void getVardgivareWithOneVardgivareTwoEnhets() {
        JsonNode hsaInfo = hsaService.getHSAInfo(new HSAKey("vg", "enhet1", "lakare"));
        vardgivareManager.saveEnhet(hsaInfo);
        hsaInfo = hsaService.getHSAInfo(new HSAKey("vg", "enhet2", "lakare"));
        vardgivareManager.saveEnhet(hsaInfo);

        List<Vardgivare> allVardgivares = vardgivareManager.getAllVardgivares();

        List<Enhet> allEnhets = vardgivareManager.getAllEnhets();
        assertEquals(2, allEnhets.size());
        assertEquals(1, allVardgivares.size());
        assertEquals("vg", allVardgivares.get(0).getId());
        assertEquals("vardgivarnamn", allVardgivares.get(0).getNamn());
    }
}