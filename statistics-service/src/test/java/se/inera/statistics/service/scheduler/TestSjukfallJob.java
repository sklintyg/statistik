package se.inera.statistics.service.scheduler;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.joda.time.LocalDate;
import org.junit.Test;

import se.inera.statistics.service.sjukfall.SjukfallService;

public class TestSjukfallJob extends SjukfallJob {

    @Test
    public void test_run_sjukfall_job() {
        this.sjukfallService = mock(SjukfallService.class);
        when(sjukfallService.expire(any(LocalDate.class))).thenReturn(1);

        this.cleanupSjukfall();

        verify(sjukfallService).expire(any(LocalDate.class));
    }
}
