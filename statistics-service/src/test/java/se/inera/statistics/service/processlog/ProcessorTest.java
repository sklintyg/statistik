package se.inera.statistics.service.processlog;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import se.inera.statistics.service.sjukfall.SjukfallKey;
import se.inera.statistics.service.sjukfall.SjukfallService;

import com.fasterxml.jackson.databind.JsonNode;

@RunWith(MockitoJUnitRunner.class)
public class ProcessorTest {

    @Mock
    private ProcessorListener listener = mock(ProcessorListener.class);

    @Mock
    private SjukfallService sjukfallService = mock(SjukfallService.class);

    @InjectMocks
    private Processor processor = new Processor();

    @Test
    public void processorAccepstTwoEvents() {
        JsonNode event1 = null;
        JsonNode event2 = null;

        processor.accept(event1, event2);
    }

    @Test
    public void processorExtractsSjukfallsId() {

        JsonNode utlatande = null;
        JsonNode hsa = null;

        when(sjukfallService.register(any(SjukfallKey.class))).thenReturn("theSjukfallId");

        processor.accept(utlatande, hsa);

        verify(sjukfallService).register(any(SjukfallKey.class));
    }

}
