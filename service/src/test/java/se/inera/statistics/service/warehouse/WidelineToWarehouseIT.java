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

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import se.inera.statistics.integration.hsa.model.HsaIdEnhet;
import se.inera.statistics.integration.hsa.model.HsaIdLakare;
import se.inera.statistics.integration.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.helper.ConversionHelper;
import se.inera.statistics.service.processlog.EventType;
import se.inera.statistics.service.report.util.Icd10;
import se.inera.statistics.service.report.util.Icd10RangeType;
import se.inera.statistics.service.warehouse.model.db.WideLine;

// CHECKSTYLE:OFF MagicNumber
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:process-log-impl-test.xml", "classpath:icd10.xml"})
@DirtiesContext
public class WidelineToWarehouseIT {

    @Autowired
    private WidelineManager widelineManager;

    @Autowired
    private Warehouse warehouse;

    @Test
    public void testPopulateWarehouse() throws Exception {
        WideLine line1 = new WideLine();
        String patientId = "19121212-1212";
        HsaIdEnhet enhet = new HsaIdEnhet("E1");
        line1.setAlder(23);
        line1.setDiagnoskapitel("A00-B99");
        line1.setDiagnosavsnitt("A15-A19");
        line1.setDiagnoskategori("A16");
        line1.setVardenhet(enhet);
        line1.setKon(1);
        line1.setLkf("078002");
        line1.setLakareId(new HsaIdLakare("lid"));
        line1.setLakaralder(33);
        line1.setLakarbefattning("201010");
        line1.setLakarintyg(1L);
        line1.setIntygTyp(EventType.CREATED);
        line1.setLakarkon(2);
        line1.setPatientid(patientId);
        line1.setSjukskrivningsgrad(100);
        line1.setSlutdatum(4999);
        line1.setStartdatum(4997);
        line1.setVardgivareId(new HsaIdVardgivare("vg1"));
        line1.setCorrelationId("{1}");
        line1.setActive(true);
        widelineManager.saveWideline(line1);

        Aisle a = warehouse.get(new HsaIdVardgivare("VG1"));
        Assert.assertEquals(1, a.getSize());
        Fact fact = a.iterator().next();
        Assert.assertEquals(23, fact.getAlder());
        Assert.assertEquals(patientId, ConversionHelper.patientIdToString(fact.getPatient()));
        Assert.assertEquals(enhet, fact.getVardenhet());
        Assert.assertEquals(78002, fact.getForsamling());
        Assert.assertEquals(780, fact.getKommun());
        Assert.assertEquals(7, fact.getLan());
        Assert.assertEquals(Icd10.icd10ToInt("A00-B99", Icd10RangeType.KAPITEL), fact.getDiagnoskapitel());
        Assert.assertEquals(Icd10.icd10ToInt("A15-A19", Icd10RangeType.AVSNITT), fact.getDiagnosavsnitt());
        Assert.assertEquals(Icd10.icd10ToInt("A16", Icd10RangeType.KATEGORI), fact.getDiagnoskategori());
        Assert.assertArrayEquals(new int[]{201010}, fact.getLakarbefattnings());
        Assert.assertEquals(33, fact.getLakaralder());
        Assert.assertEquals(2, fact.getLakarkon());
        Assert.assertEquals(1, fact.getKon());
    }
}
// CHECKSTYLE:ON MagicNumber
