/**
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
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
package se.inera.statistics.web.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import se.inera.auth.AuthUtil;
import se.inera.auth.model.User;
import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.hsa.model.HsaIdUser;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.hsa.model.Vardenhet;
import se.inera.statistics.service.processlog.Enhet;
import se.inera.statistics.service.report.model.Kommun;
import se.inera.statistics.service.report.model.Lan;
import se.inera.statistics.service.report.model.VerksamhetsTyp;
import se.inera.statistics.service.warehouse.Warehouse;
import se.inera.statistics.web.model.LoginInfo;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:logininfo-integration-test.xml", "classpath:icd10.xml"  })
@DirtiesContext
public class LoginInfoServiceIntegrationTest {

    @Autowired
    private LoginInfoService loginInfoService;

    @Autowired
    private Warehouse warehouse;

    @Test
    public void getLoginInfoTestThatEnhetNotFoundInWarehouseGetsUnknownVerksamhetstypAssigned() {
        //Given
        final Vardenhet vardenhet = new Vardenhet(new HsaIdEnhet("ENHETID"), "e1namn", new HsaIdVardgivare("VG1"));
        final User user = new User(new HsaIdUser("USERID"), "UserName", Collections.emptyList(), Arrays.asList(vardenhet));
        AuthUtil.setUserToSecurityContext(user);

        warehouse.accept(new Enhet(new HsaIdVardgivare("VG2"), new HsaIdEnhet("ENHETID"), "EnhetNamn", Lan.OVRIGT_ID, Kommun.OVRIGT_ID, VerksamhetsTyp.OVRIGT_ID));
        warehouse.completeEnhets();

        //When
        final LoginInfo loginInfo = loginInfoService.getLoginInfo();

        //Then
        final String expected = Arrays.toString(Collections.singleton(VerksamhetsTyp.OVRIGT_ID).toArray());
        final String actual = Arrays.toString(loginInfo.getBusinessesForVg(new HsaIdVardgivare("VG1")).get(0).getVerksamhetsTyper().toArray());
        assertEquals(expected, actual);
    }

}
