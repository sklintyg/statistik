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

package se.inera.statistics.web.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import se.inera.auth.model.User;
import se.inera.statistics.hsa.model.Vardenhet;
import se.inera.statistics.web.model.LoginInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;

public class LoginInfoServiceTest {
    private HttpServletRequest request;
    private HttpSession session;

    @Before
    public void init() {
        request = Mockito.mock(HttpServletRequest.class);
        session = Mockito.mock(HttpSession.class);

        Mockito.when(request.getSession(true)).thenReturn(session);
        Mockito.doNothing().when(session).setAttribute(anyString(), anyObject());
    }

    @Ignore
    public void getNotLoggedInLoginInfoTest() {
        LoginInfoService loginInfoService = new LoginInfoService();

        LoginInfo info = loginInfoService.getLoginInfo(request);

        Assert.assertEquals(false, info.isLoggedIn());
    }

    @Ignore
    public void getLoginInfoTest() {
        LoginInfoService loginInfoService = new LoginInfoService();
        List<Vardenhet> vardenhets = Collections.<Vardenhet>singletonList(new Vardenhet("verksamhetid", "verksamhetnamn", "VG1"));
        User user = new User("hsaId", "name", false, vardenhets.get(0), vardenhets);
        UsernamePasswordAuthenticationToken principal = Mockito.mock(UsernamePasswordAuthenticationToken.class);
        Mockito.when(request.getUserPrincipal()).thenReturn(principal);
        Mockito.when(principal.getDetails()).thenReturn(user);

        LoginInfo info = loginInfoService.getLoginInfo(request);

        Assert.assertEquals(true, info.isLoggedIn());
        Assert.assertEquals("hsaId", info.getHsaId());
        Assert.assertEquals("name", info.getName());
        Assert.assertEquals(1, info.getBusinesses().size());
        Assert.assertEquals("verksamhetid", info.getBusinesses().get(0).getId());
        Assert.assertEquals("verksamhetnamn", info.getBusinesses().get(0).getName());
    }
}
