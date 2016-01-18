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

import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
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
import se.inera.statistics.web.model.Verksamhet;
import se.inera.statistics.web.service.LoginInfoService;

import java.security.Principal;
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
        final HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        final AbstractAuthenticationToken abstractAuthenticationToken = Mockito.mock(AbstractAuthenticationToken.class);
        final Vardenhet vardenhet = new Vardenhet(new HsaIdEnhet("ENHETID"), "e1namn", new HsaIdVardgivare("VG1"));
        Mockito.when(abstractAuthenticationToken.getDetails()).thenReturn(new User(new HsaIdUser("USERID"), "UserName", false, vardenhet, Arrays.asList(vardenhet)));
        Mockito.when(req.getUserPrincipal()).thenReturn(abstractAuthenticationToken);

        warehouse.accept(new Enhet(new HsaIdVardgivare("VG2"), "VgName", new HsaIdEnhet("ENHETID"), "EnhetNamn", Lan.OVRIGT_ID, Kommun.OVRIGT_ID, VerksamhetsTyp.OVRIGT_ID));
        warehouse.completeEnhets();

        //When
        final LoginInfo loginInfo = loginInfoService.getLoginInfo(req);

        //Then
        final String expected = Arrays.toString(Collections.singleton(VerksamhetsTyp.OVRIGT_ID).toArray());
        final String actual = Arrays.toString(loginInfo.getBusinesses().get(0).getVerksamhetsTyper().toArray());
        assertEquals(expected, actual);
    }

}
