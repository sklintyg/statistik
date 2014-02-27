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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import se.inera.auth.model.User;
import se.inera.statistics.hsa.model.Vardenhet;
import se.inera.statistics.service.report.api.VerksamhetOverview;
import se.inera.statistics.service.report.model.Range;

@RunWith(MockitoJUnitRunner.class)
public class ProtectedChartDataServiceTest {
    @Mock
    private VerksamhetOverview mock = Mockito.mock(VerksamhetOverview.class);
    private HttpServletRequest request;

    @InjectMocks
    private ProtectedChartDataService chartDataService = new ProtectedChartDataService();

    @Before
    public void init() {
        request = Mockito.mock(HttpServletRequest.class);
        List<Vardenhet> vardenhets = Arrays.asList(new Vardenhet("verksamhet1", "Närhälsan i Småmåla"), new Vardenhet("verksamhet2", "Småmålas akutmottagning"));

        User user = new User("hsaId", "name",  vardenhets.get(0), vardenhets);
        UsernamePasswordAuthenticationToken principal = Mockito.mock(UsernamePasswordAuthenticationToken.class);
        Mockito.when(request.getUserPrincipal()).thenReturn(principal);
        Mockito.when(principal.getDetails()).thenReturn(user);
    }

    @Test
    public void getOverviewDataForSpecificVerksamhetTest() {
        init();

        try {
            chartDataService.getOverviewData(request, "verksamhet2");
            fail("Current implementation can not use null data");
        } catch (NullPointerException e) {
            assertTrue(true);
        }
        Mockito.verify(mock).getOverview(anyString(), any(Range.class));
    }

    @Test
    public void checkDeniedAccessToVerksamhetTest() {
        boolean result = ProtectedChartDataService.Helper.hasAccessTo(request, "verksamhet3");

        assertEquals(false, result);
    }

    @Test
    public void checkAllowedAccessToVerksamhetTest() {
        boolean result = ProtectedChartDataService.Helper.hasAccessTo(request, "verksamhet2");

        assertEquals(true, result);
    }

}
