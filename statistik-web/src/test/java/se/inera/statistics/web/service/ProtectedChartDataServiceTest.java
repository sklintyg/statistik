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

import com.google.common.base.Predicate;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import se.inera.auth.model.User;
import se.inera.statistics.hsa.model.Vardenhet;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.warehouse.Fact;
import se.inera.statistics.service.warehouse.SjukfallUtil;
import se.inera.statistics.web.model.LoginInfo;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProtectedChartDataServiceTest {
    @Mock
    private WarehouseService warehouse;

    @Mock
    private HttpServletRequest request;

    @Mock
    private LoginServiceUtil loginServiceUtil;

    @InjectMocks
    private ProtectedChartDataService chartDataService = new ProtectedChartDataService();

    @Before
    public void init() {
        List<Vardenhet> vardenhets = Arrays.asList(new Vardenhet("verksamhet1", "Närhälsan i Småmåla", "VG1"), new Vardenhet("verksamhet2", "Småmålas akutmottagning", "VG2"));

        User user = new User("hsaId", "name", false, vardenhets.get(0), vardenhets);
        UsernamePasswordAuthenticationToken principal = Mockito.mock(UsernamePasswordAuthenticationToken.class);
        when(request.getUserPrincipal()).thenReturn(principal);
        when(principal.getDetails()).thenReturn(user);
        when(loginServiceUtil.getLoginInfo(any(HttpServletRequest.class))).thenReturn(new LoginInfo());
    }

    @Test
    public void dummy() {
       
    }

    @Ignore
    public void getOverviewDataForSpecificVerksamhetTest() {
        init();

        try {
            chartDataService.getOverviewData(request, "VG2", null, null, null, null);
            fail("Current implementation can not use null data");
        } catch (NullPointerException e) {
            assertTrue(true);
        }
        Mockito.verify(warehouse).getOverview(any(Predicate.class), any(Range.class), anyString());
    }

    @Ignore
    public void checkDeniedAccessToVerksamhetTest() {
        boolean result = chartDataService.hasAccessTo(request, "VG3");
        assertEquals(false, result);
    }

    @Ignore
    public void checkAllowedAccessToVerksamhetTest() {
        boolean result = chartDataService.hasAccessTo(request, "VG2");
        assertEquals(true, result);
    }

}
