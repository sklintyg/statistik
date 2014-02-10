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

package se.inera.statistics.web.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import se.inera.ifv.statistics.spi.authorization.impl.HSAWebServiceCalls;
import se.inera.statistics.web.service.ChartDataService;
import se.inera.statistics.web.util.HealthCheckUtil.Status;

@RunWith(MockitoJUnitRunner.class)
public class HealthCheckUtilTest {

    @Mock
    private ChartDataService dataService = Mockito.mock(ChartDataService.class);

    @Mock
    private HSAWebServiceCalls hsaCalls = Mockito.mock(HSAWebServiceCalls.class);

    @InjectMocks
    private HealthCheckUtil healthCheck = new HealthCheckUtil();

    @Test
    public void timeIsReturnedForOkResult() {
        Status status = healthCheck.getOverviewStatus();
        assertTrue(status.getTime() >= 0);
        assertTrue(status.isOk());
    }

    @Test
    public void okIsReturnedForOkResult() {
        boolean result = healthCheck.isOverviewOk();
        assertTrue(result);
    }

    @Test
    public void exceptionIsThrownForFailingTime() {
        Mockito.when(dataService.getOverviewData()).thenThrow(new IllegalStateException());
        Status status = healthCheck.getOverviewStatus();
        assertTrue(status.getTime() >= 0);
        assertFalse(status.isOk());
    }

    @Test
    public void falseIsReturnedForFailingCheck() {
        Mockito.when(dataService.getOverviewData()).thenThrow(new IllegalStateException());
        boolean result = healthCheck.isOverviewOk();
        assertFalse(result);
    }

    @Test
    public void getTimeForAccessingHsa() {
        Status status = healthCheck.getHsaStatus();
        assertTrue(status.getTime() >= 0);
        assertTrue(status.isOk());
    }

    @Test
    public void getTimeForAccessingFailingHsa() throws Exception {
        Mockito.doThrow(new IllegalStateException()).when(hsaCalls).callPing();
        Status status = healthCheck.getHsaStatus();
        assertTrue(status.getTime() >= 0);
        assertFalse(status.isOk());
    }
}
