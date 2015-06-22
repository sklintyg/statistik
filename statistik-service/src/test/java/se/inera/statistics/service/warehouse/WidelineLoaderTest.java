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
package se.inera.statistics.service.warehouse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.hsa.model.HsaIdLakare;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.processlog.EventType;
import se.inera.statistics.service.warehouse.model.db.WideLine;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:process-log-impl-test.xml", "classpath:icd10.xml" })
@DirtiesContext(classMode= DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class WidelineLoaderTest {
    @Autowired
    WidelineLoader widelineLoader;

    @Autowired
    WidelineManager widelineManager;

    @Autowired
    Warehouse warehouse;

    @Test
    public void loadPopulatesOnlyWithCreatedLines() {
        insertLine(EventType.CREATED, "1");
        insertLine(EventType.REVOKED, "2");
        insertLine(EventType.CREATED, "3");

        int result = widelineLoader.populateWarehouse();

        assertEquals(2, result);
    }

    @Test
    public void doNotLoadCreatedThenRevokedLines() {
        insertLine(EventType.CREATED, "1");
        insertLine(EventType.REVOKED, "1");

        int result = widelineLoader.populateWarehouse();

        assertEquals(0, result);
    }

    @Test
    public void doNotLoadRevokedThenCreatedLines() {
        insertLine(EventType.REVOKED, "1");
        insertLine(EventType.CREATED, "1");

        int result = widelineLoader.populateWarehouse();

        assertEquals(0, result);
    }

    private void insertLine(EventType event, String correlationId) {
        WideLine line1 = new WideLine();
        String patientId = "19121212-1210";
        line1.setAlder(23);
        line1.setDiagnoskapitel("A00-B99");
        line1.setDiagnosavsnitt("A15-A19");
        line1.setDiagnoskategori("A16");
        line1.setEnhet(new HsaIdEnhet("e1"));
        line1.setKon(1);
        line1.setLkf("078002");
        line1.setLakaralder(33);
        line1.setLakarbefattning("201010");
        line1.setLakarintyg(1L);
        line1.setIntygTyp(event);
        line1.setLakarkon(2);
        line1.setPatientid(patientId);
        line1.setSjukskrivningsgrad(100);
        line1.setSlutdatum(4999);
        line1.setStartdatum(4997);
        line1.setVardgivareId(new HsaIdVardgivare("vg1"));
        line1.setLakareId(new HsaIdLakare("lakare"));
        line1.setCorrelationId(correlationId);
        widelineManager.saveWideline(line1);
    }
}
