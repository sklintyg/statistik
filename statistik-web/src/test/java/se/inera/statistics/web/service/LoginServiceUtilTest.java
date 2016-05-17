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

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import se.inera.auth.LoginVisibility;
import se.inera.statistics.service.landsting.LandstingEnhetHandler;
import se.inera.statistics.service.warehouse.Warehouse;
import se.inera.statistics.web.model.AppSettings;

import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;

public class LoginServiceUtilTest {

    @Mock
    private Warehouse warehouse;

    @Mock
    private LandstingEnhetHandler landstingEnhetHandler;

    @Mock
    private LoginVisibility loginVisibility;

    @InjectMocks
    private LoginServiceUtil loginServiceUtilInjected;

    private LoginServiceUtil loginServiceUtil;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        loginServiceUtil = Mockito.spy(loginServiceUtilInjected);
        Mockito.doReturn(true).when(loginServiceUtil).isLoggedIn(any());
    }

    @Test
    public void testGetSettings() throws Exception {
        //When
        final AppSettings settings = loginServiceUtil.getSettings(null);

        //Then
        assertEquals(7, settings.getSjukskrivningLengths().size());
        assertEquals("Under 15 dagar", settings.getSjukskrivningLengths().get("GROUP1_0TO14"));
    }

}
