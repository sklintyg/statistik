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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import se.inera.statistics.service.warehouse.model.db.WideLine;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:process-log-impl-test.xml" })
@DirtiesContext
public class FactPopulatorTest {

    @Autowired
    private FactPopulator factPopulator;

    @Autowired
    private WidelineConverter widelineConverter;

    @Autowired
    private Warehouse warehouse;

    @Test
    public void testPopulateWarehouse() throws Exception {
        WideLine line1 = new WideLine();
        line1.setAlder(23);
        line1.setDiagnosavsnitt("111");
        line1.setDiagnoskapitel("111");
        line1.setDiagnoskategori("111");
        line1.setEnhet("e1");
        line1.setKon(0);
        line1.setLkf("8001");
        line1.setLakaralder(33);
        line1.setLakarbefattning("B");
        line1.setLakarintyg(1L);
        line1.setLakarkon(0);
        line1.setPatientid("123456");
        line1.setSjukskrivningsgrad(100);
        line1.setSlutdatum(4999);
        line1.setStartdatum(4997);
        widelineConverter.saveWideline(line1);

        factPopulator.populateWarehouse();

        warehouse.get("");
    }
}
