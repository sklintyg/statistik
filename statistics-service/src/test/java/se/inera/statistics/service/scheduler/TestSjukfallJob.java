package se.inera.statistics.service.scheduler;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import se.inera.statistics.service.sjukfall.SjukfallService;

@RunWith(MockitoJUnitRunner.class)
public class TestSjukfallJob extends SjukfallJob {

    @Mock
    private SjukfallService sjukfallService = mock(SjukfallService.class);

    @InjectMocks
    private SjukfallJob job = new SjukfallJob();

    @Test
    public void test_run_sjukfall_job() {
        when(sjukfallService.expire(any(LocalDate.class))).thenReturn(1);

        job.cleanupSjukfall();

        verify(sjukfallService).expire(any(LocalDate.class));
    }
}
