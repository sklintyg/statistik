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

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StopWatch;
import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.hsa.model.HsaIdLakare;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.processlog.EventType;
import se.inera.statistics.service.warehouse.model.db.IntygCommon;
import se.inera.statistics.service.warehouse.model.db.WideLine;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:process-log-impl-test.xml", "classpath:icd10.xml"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class WidelineLoaderTest {

    private static final Logger LOG = LoggerFactory.getLogger(WidelineLoaderTest.class);

    public static final HsaIdVardgivare VG1 = new HsaIdVardgivare("vg1");
    public static final HsaIdVardgivare VG2 = new HsaIdVardgivare("vg2");
    public static final HsaIdVardgivare VG3 = new HsaIdVardgivare("vg3");

    @Autowired
    WidelineLoader widelineLoader;

    @Autowired
    WidelineManager widelineManager;

    @Autowired
    IntygCommonManager intygCommonManager;

    @Autowired
    Warehouse warehouse;

    @Test
    public void loadPopulatesOnlyWithCreatedLines() {
        insertLine(EventType.CREATED, "1", VG1);
        insertLine(EventType.REVOKED, "2", VG1);
        insertLine(EventType.CREATED, "3", VG1);

        final List<Fact> factsForVg = widelineLoader.getAilesForVgs(Collections.singletonList(VG1)).get(0).getLines();

        assertEquals(2, factsForVg.size());
    }

    @Test
    public void doNotLoadCreatedThenRevokedLines() {
        insertLine(EventType.CREATED, "1", VG1);
        insertLine(EventType.REVOKED, "1", VG1);

        final List<Fact> factsForVg = widelineLoader.getAilesForVgs(Collections.singletonList(VG1)).get(0).getLines();

        assertEquals(0, factsForVg.size());
    }

    @Test
    public void doNotLoadRevokedThenCreatedLines() {
        insertLine(EventType.REVOKED, "1", VG1);
        insertLine(EventType.CREATED, "1", VG1);

        final List<Fact> factsForVg = widelineLoader.getAilesForVgs(Collections.singletonList(VG1)).get(0).getLines();

        assertEquals(0, factsForVg.size());
    }

    @Test
    public void testGetAllVgsAreDistinct() {
        insertLine(EventType.CREATED, "1", VG1);
        insertLine(EventType.CREATED, "2", VG1);
        insertLine(EventType.CREATED, "3", VG2);
        insertLine(EventType.CREATED, "4", VG3);
        insertLine(EventType.CREATED, "5", VG1);
        insertLine(EventType.CREATED, "6", VG2);

        final List<VgNumber> allVgs = widelineLoader.getAllVgs();

        allVgs.sort(Comparator.comparing(vgNumber -> vgNumber.getVgid().getId()));
        assertEquals(3, allVgs.size());
        assertEquals(VG1, allVgs.get(0).getVgid());
        assertEquals(VG2, allVgs.get(1).getVgid());
        assertEquals(VG3, allVgs.get(2).getVgid());
    }

    @Test
    public void testGetFactsForVg() {
        for (int i = 1; i < 1000; i++) {
            insertLine(EventType.CREATED, "" + i, VG1);
        }
        for (int i = 1; i < 1000; i += 3) {
            insertLine(EventType.REVOKED, "" + i, VG1);
        }

        StopWatch stopWatch = new StopWatch();
        LOG.error("Starting getFacts");
        stopWatch.start();
        final List<Fact> facts = widelineLoader.getAilesForVgs(Collections.singletonList(VG1)).get(0).getLines();
        stopWatch.stop();
        LOG.error("getFacts done in: " + stopWatch.getTotalTimeMillis());

        assertEquals(666, facts.size());
    }

    private void insertLine(EventType event, String correlationId, HsaIdVardgivare vg) {
        WideLine line1 = new WideLine();
        String patientId = "19121212-1212";
        line1.setAlder(23);
        line1.setDiagnoskapitel("A00-B99");
        line1.setDiagnosavsnitt("A15-A19");
        line1.setDiagnoskategori("A16");
        line1.setEnhet(new HsaIdEnhet("e1"));
        line1.setVardenhet(new HsaIdEnhet("e1"));
        line1.setKon(1);
        line1.setLkf("078002");
        line1.setLakaralder(33);
        line1.setLakarbefattning("201010");
        line1.setLakarintyg(1L);
        line1.setIntygTyp(event);
        line1.setActive(!EventType.REVOKED.equals(event));
        line1.setLakarkon(2);
        line1.setPatientid(patientId);
        line1.setSjukskrivningsgrad(100);
        line1.setSlutdatum(4999);
        line1.setStartdatum(4997);
        line1.setVardgivareId(vg);
        line1.setLakareId(new HsaIdLakare("lakare"));
        line1.setCorrelationId(correlationId);
        widelineManager.saveWideline(line1);
        final IntygCommon line = new IntygCommon(correlationId, patientId, LocalDate.now(),
            IntygType.LISJP, line1.getEnhet().getId(), line1.getVardenhet().getId(), vg.getId(), line1.getKon(), event,
            line1.getDiagnoskod(), true, line1.getLakareId().getId());
        intygCommonManager.persistIfValid(1L, "1", line);
    }
}
