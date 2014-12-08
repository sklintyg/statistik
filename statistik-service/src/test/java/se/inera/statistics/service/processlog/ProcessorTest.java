package se.inera.statistics.service.processlog;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import se.inera.statistics.service.helper.JSONParser;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.warehouse.WidelineManager;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class ProcessorTest {

    private JsonNode utlatande;

    @Mock
    private WidelineManager widelineManager = mock(WidelineManager.class);

    @Mock
    private VardgivareManager vardgivareManager = mock(VardgivareManager.class);

    @Mock
    private LakareManager lakareManager = mock(LakareManager.class);

    @InjectMocks
    private Processor processor = new Processor();

    @Before
    public void init() {
        utlatande = JSONParser.parse(this.getClass().getResourceAsStream("/json/fk7263_M_template.json"));
    }

    @Test
    public void processorCallsListener() {
        ArgumentCaptor<JsonNode> utlatandeCaptor = ArgumentCaptor.forClass(JsonNode.class);
        ArgumentCaptor<JsonNode> hsaCaptor = ArgumentCaptor.forClass(JsonNode.class);
        Mockito.doNothing().when(widelineManager).accept(utlatandeCaptor.capture(), hsaCaptor.capture(), anyLong(), anyString(), any(EventType.class));
        Mockito.doNothing().when(vardgivareManager).saveEnhet(any(JsonNode.class));

        processor.accept(utlatande, null, 1L, "1", EventType.CREATED);

        assertEquals("33", utlatandeCaptor.getValue().path("patient").path("alder").asText());
        assertEquals(Kon.Male.toString(), utlatandeCaptor.getValue().path("patient").path("kon").asText());
    }

}
