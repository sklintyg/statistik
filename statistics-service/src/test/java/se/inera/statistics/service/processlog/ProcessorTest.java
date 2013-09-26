package se.inera.statistics.service.processlog;

import static junit.framework.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import junit.framework.Assert;
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

import java.util.Date;

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

        processor.accept(event1, event2);

    }

    @Test
    public void processorExtractsSjukfallsId() {
        JsonNode hsa = null;

        when(sjukfallService.register(any(SjukfallKey.class))).thenReturn(new SjukfallInfo(null, null));

        processor.accept(utlatande, hsa);

        verify(sjukfallService).register(any(SjukfallKey.class));
    }

    @Test
    public void processor_extract_alder_from_intyg() {
        String personId = "19121212-1212";
        Date date = new Date(0L); // 1970

        int alder = processor.extractAlder(personId, date);

        assertEquals(57, alder);
    }

    @Test
    public void processor_extract_kon_man_from_intyg() {
        String personId = "19121212-1212";

        String kon = processor.extractKon(personId);

        assertEquals("man", kon);
    }

    @Test
    public void processor_extract_kon_kvinna_from_intyg() {
        String personId = "19121212-0000";

        String kon = processor.extractKon(personId);

        assertEquals("kvinna", kon);
    }

    @Test
    public void processorCallsListener() {
        JsonNode hsa = null;

        ArgumentCaptor<JsonNode> utlatandeCaptor = ArgumentCaptor.forClass(JsonNode.class);
        ArgumentCaptor<JsonNode> hsaCaptor = ArgumentCaptor.forClass(JsonNode.class);
        Mockito.doNothing().when(listener).accept(any(SjukfallInfo.class), utlatandeCaptor.capture(), hsaCaptor.capture());

        processor.accept(utlatande, hsa);

        assertEquals("33", utlatandeCaptor.getValue().path("patient").path("alder").asText());
        assertEquals("man", utlatandeCaptor.getValue().path("patient").path("kon").asText());
        assertNull(utlatandeCaptor.getValue().get("patient").get("id"));
        assertNull(utlatandeCaptor.getValue().get("patient").get("fornamn"));
        assertNull(utlatandeCaptor.getValue().get("patient").get("efternamn"));
    }

}
