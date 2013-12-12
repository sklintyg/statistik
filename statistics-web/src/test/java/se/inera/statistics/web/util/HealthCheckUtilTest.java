package se.inera.statistics.web.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import se.inera.statistics.web.service.ChartDataService;

@RunWith(MockitoJUnitRunner.class)
public class HealthCheckUtilTest {

    @Mock
    private ChartDataService dataService = Mockito.mock(ChartDataService.class);

    @InjectMocks
    private HealthCheckUtil healthCheck = new HealthCheckUtil();

    @Test
    public void timeIsReturnedForOkResult() {
        long time = healthCheck.getOverviewTime();
        assertTrue(time >= 0);
    }

    @Test
    public void okIsReturnedForOkResult() {
        boolean result = healthCheck.isOverviewOk();
        assertTrue(result);
    }

    @Test(expected = IllegalStateException.class)
    public void exceptionIsThrownForFailingTime() {
        Mockito.when(dataService.getOverviewData()).thenThrow(new IllegalStateException());
        healthCheck.getOverviewTime();
    }

    @Test
    public void falseIsReturnedForFailingCheck() {
        Mockito.when(dataService.getOverviewData()).thenThrow(new IllegalStateException());
        boolean result = healthCheck.isOverviewOk();
        assertFalse(result);
    }
}
