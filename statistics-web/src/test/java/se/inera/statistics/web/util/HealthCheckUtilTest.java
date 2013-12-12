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
