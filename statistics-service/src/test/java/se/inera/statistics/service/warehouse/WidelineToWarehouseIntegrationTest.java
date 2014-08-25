/*
 * Copyright (C) 2013 - 2014 Inera AB (http://www.inera.se)
 *
 *     This file is part of Inera Statistics (http://code.google.com/p/inera-statistics/).
 *
 *     Inera Statistics is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Inera Statistics is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU LESSER GENERAL PUBLIC LICENSE for more details.
 *
 *     You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.inera.statistics.service.warehouse;

import org.joda.time.LocalDateTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import se.inera.statistics.service.helper.ConversionHelper;
import se.inera.statistics.service.processlog.EventType;
import se.inera.statistics.service.warehouse.model.db.WideLine;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:process-log-impl-test.xml", "classpath:icd10.xml" })
@DirtiesContext
public class WidelineToWarehouseIntegrationTest {

    @Autowired
    private WidelineLoader widelineLoader;

    @Autowired
    private WidelineManager widelineManager;

    @Autowired
    private Warehouse warehouse;

    @Test
    public void testPopulateWarehouse() throws Exception {
        WideLine line1 = new WideLine();
        String patientId = "19121212-1210";
        String enhet = "e1";
        line1.setAlder(23);
        line1.setDiagnoskapitel("A00-B99");
        line1.setDiagnosavsnitt("A15-A19");
        line1.setDiagnoskategori("A16");
        line1.setEnhet("e1");
        line1.setKon(1);
        line1.setLkf("078002");
        line1.setLakaralder(33);
        line1.setLakarbefattning("201010");
        line1.setLakarintyg(1L);
        line1.setIntygTyp(EventType.CREATED);
        line1.setLakarkon(2);
        line1.setPatientid(patientId);
        line1.setSjukskrivningsgrad(100);
        line1.setSlutdatum(4999);
        line1.setStartdatum(4997);
        line1.setVardgivareId("vg1");
        line1.setCorrelationId("{1}");
        widelineManager.saveWideline(line1);

        widelineLoader.populateWarehouse();
        warehouse.complete(LocalDateTime.now());

        Aisle a = warehouse.get("vg1");
        Assert.assertEquals(1, a.getSize());
        Fact fact = a.iterator().next();
        Assert.assertEquals(23, fact.getAlder());
        Assert.assertEquals(patientId, ConversionHelper.patientIdToString(fact.getPatient()));
        Assert.assertEquals(Warehouse.getEnhetAndRemember(enhet), fact.getEnhet());
        Assert.assertEquals(78002, fact.getForsamling());
        Assert.assertEquals(780, fact.getKommun());
        Assert.assertEquals(7, fact.getLan());
        Assert.assertEquals(0, fact.getDiagnoskapitel());
        Assert.assertEquals(15, fact.getDiagnosavsnitt());
        Assert.assertEquals(16, fact.getDiagnoskategori());
        Assert.assertEquals(201010, fact.getLakarbefattning());
        Assert.assertEquals(33, fact.getLakaralder());
        Assert.assertEquals(2, fact.getLakarkon());
        Assert.assertEquals(1, fact.getKon());
    }
}
