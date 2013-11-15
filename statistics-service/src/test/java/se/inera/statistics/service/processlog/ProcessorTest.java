package se.inera.statistics.service.processlog;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import se.inera.statistics.service.helper.JSONParser;
import se.inera.statistics.service.sjukfall.SjukfallInfo;
import se.inera.statistics.service.sjukfall.SjukfallKey;
import se.inera.statistics.service.sjukfall.SjukfallService;

import com.fasterxml.jackson.databind.JsonNode;

@RunWith(MockitoJUnitRunner.class)
public class ProcessorTest {

    private JsonNode utlatande;

    @Mock
    private ProcessorListener listener = mock(ProcessorListener.class);

    @Mock
    private SjukfallService sjukfallService = mock(SjukfallService.class);

    @InjectMocks
    private Processor processor = new Processor();

    @Before
    public void init() {
        utlatande = JSONParser.parse(this.getClass().getResourceAsStream("/json/fk7263_M_template.json"));
    }

    @Test(expected = StatisticsMalformedDocument.class)
    public void processorAccepstTwoEvents() {
        JsonNode event1 = null;
        JsonNode event2 = null;

        processor.accept(event1, event2, 1);

    }

    @Test
    public void processorExtractsSjukfallsId() {
        JsonNode hsa = null;

        when(sjukfallService.register(any(SjukfallKey.class))).thenReturn(new SjukfallInfo(null, null, null, null));

        processor.accept(utlatande, hsa, 1L);

        verify(sjukfallService).register(any(SjukfallKey.class));
    }

    @Test
    public void processorCallsListener() {
        JsonNode hsa = null;

        ArgumentCaptor<JsonNode> utlatandeCaptor = ArgumentCaptor.forClass(JsonNode.class);
        ArgumentCaptor<JsonNode> hsaCaptor = ArgumentCaptor.forClass(JsonNode.class);
        Mockito.doNothing().when(listener).accept(any(SjukfallInfo.class), utlatandeCaptor.capture(), hsaCaptor.capture(), anyLong());

        processor.accept(utlatande, hsa, 1L);

        assertEquals("33", utlatandeCaptor.getValue().path("patient").path("alder").asText());
        assertEquals("man", utlatandeCaptor.getValue().path("patient").path("kon").asText());
        assertNull(utlatandeCaptor.getValue().get("patient").get("id"));
        assertNull(utlatandeCaptor.getValue().get("patient").get("fornamn"));
        assertNull(utlatandeCaptor.getValue().get("patient").get("efternamn"));
    }

}
