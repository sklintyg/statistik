/**
 * Copyright (C) 2014 Inera AB (http://www.inera.se)
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
        assertTrue(status.getMeasurement() >= 0);
        assertTrue(status.isOk());
    }

    @Test
    public void exceptionIsThrownForFailingTime() {
        Mockito.when(dataService.getOverviewData()).thenThrow(new IllegalStateException());
        Status status = healthCheck.getOverviewStatus();
        assertTrue(status.getMeasurement() >= 0);
        assertFalse(status.isOk());
    }

    @Test
    public void falseIsReturnedForFailingCheck() {
        Mockito.when(dataService.getOverviewData()).thenThrow(new IllegalStateException());
        Status status = healthCheck.getOverviewStatus();
        assertTrue(status.getMeasurement() >= 0);
        assertFalse(status.isOk());
    }

    @Test
    public void getMeasurementForAccessingHsa() {
        Status status = healthCheck.getHsaStatus();
        assertTrue(status.getMeasurement() >= 0);
        assertTrue(status.isOk());
    }

    @Test
    public void getMeasurementForAccessingFailingHsa() throws Exception {
        Mockito.doThrow(new IllegalStateException()).when(hsaCalls).callPing();
        Status status = healthCheck.getHsaStatus();
        assertTrue(status.getMeasurement() >= 0);
        assertFalse(status.isOk());
    }
}
