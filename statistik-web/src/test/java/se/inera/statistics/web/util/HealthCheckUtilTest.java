/**
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import se.inera.ifv.statistics.spi.authorization.impl.HSAWebServiceCalls;
import se.inera.statistics.web.service.ChartDataService;
import se.inera.statistics.web.util.HealthCheckUtil.Status;

import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class HealthCheckUtilTest {

    @Mock
    private ChartDataService dataService = Mockito.mock(ChartDataService.class);

    @Mock
    private HttpClient client = Mockito.mock(HttpClient.class);

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

    @Test
    public void testMeasurementsForAccessingHighcharts() throws IOException {
        Mockito.when(client.executeMethod(Mockito.any(GetMethod.class))).thenReturn(HttpStatus.OK.value());
        Status status = healthCheck.getHighchartsExportStatus();
        assertTrue(status.getMeasurement() >= 0);
        assertTrue(status.isOk());
    }

    @Test
    public void testMeasurementsForAccessingFailingHighcharts() throws IOException {
        Mockito.when(client.executeMethod(Mockito.any(GetMethod.class))).thenReturn(HttpStatus.METHOD_NOT_ALLOWED.value());
        Status status = healthCheck.getHighchartsExportStatus();
        assertTrue(status.getMeasurement() >= 0);
        assertFalse(status.isOk());
    }

    @Test
    public void testWorkloadStatus() throws Exception {
        Status status = healthCheck.getWorkloadStatus();
        assertTrue(status.getMeasurement() >= 0 && status.getMeasurement() <= 100);
        assertTrue(status.isOk());
    }

}
