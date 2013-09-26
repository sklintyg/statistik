package se.inera.statistics.service.scheduler;

import org.junit.Test;
import se.inera.statistics.service.sjukfall.SjukfallService;

import java.util.Date;

import static org.mockito.Mockito.*;

public class testSjukfallJob extends SjukfallJob {

    @Test
    public void test_run_sjukfall_job() {
        this.sjukfallService = mock(SjukfallService.class);
        when(sjukfallService.expire((Date) anyObject())).thenReturn(1);

        this.cleanupSjukfall();

        verify(sjukfallService).expire((Date) anyObject());
    }
}
