/*
 * Copyright (C) 2023 Inera AB (http://www.inera.se)
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
package se.inera.statistics.web.service.endpoints;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import se.inera.statistics.web.api.LoginInfoService;
import se.inera.statistics.web.model.LoginInfo;
import se.inera.statistics.web.service.LoginServiceUtil;

public class LoginInfoServiceTest {

    @Mock
    private LoginServiceUtil loginServiceUtil;

    @InjectMocks
    private LoginInfoService loginInfoService;


    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getLoginInfoTest() {
        final LoginInfo loginInfo = new LoginInfo();
        Mockito.when(loginServiceUtil.getLoginInfo()).thenReturn(loginInfo);
        LoginInfo info = loginInfoService.getLoginInfo();
        Assert.assertEquals(loginInfo, info);
    }

}
