package se.inera.statistics.service.processlog;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import se.inera.statistics.service.JSONSource;
import se.inera.statistics.service.helper.JSONParser;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OrderedProcessTest {

    @Mock
    private Processor processor;

    @InjectMocks
    private OrderedProcess orderedProcess = new OrderedProcess();

    @Test
    public void register_new_event() {
        String dokumentId = "C123";
        JsonNode intyg = null;

        orderedProcess.register(intyg, dokumentId);
    }

    @Test
    public void updateSlot_triggers_ProcessorAccept() {
        String dokumentId = "C123";
        JsonNode intyg = JSONParser.parse(JSONSource.readTemplateAsString());
        JsonNode hsaInfo = JSONParser.parse("{}");

        orderedProcess.register(intyg, dokumentId);
        orderedProcess.updateSlot(hsaInfo, dokumentId);

        verify(processor).accept(intyg, hsaInfo);
    }

    @Test
    public void delayedUpdateSlot_triggers_all_ProcessorAccept() {
        String dokumentId1 = "C123";
        JsonNode intyg1 = JSONParser.parse(JSONSource.readTemplateAsString());
        JsonNode hsaInfo1 = JSONParser.parse("{ \"nr\": 1 }");
        String dokumentId2 = "C124";
        JsonNode intyg2 = JSONParser.parse(JSONSource.readTemplateAsString());
        JsonNode hsaInfo2 = JSONParser.parse("{\"nr\": 2 }");

        orderedProcess.register(intyg1, dokumentId1);
        orderedProcess.register(intyg2, dokumentId2);

        orderedProcess.updateSlot(hsaInfo2, dokumentId2);

        verify(processor, never()).accept(any(JsonNode.class), any(JsonNode.class));

        orderedProcess.updateSlot(hsaInfo1, dokumentId1);

        InOrder order = inOrder(processor);


        order.verify(processor, atLeastOnce()).accept(intyg1, hsaInfo1);
        order.verify(processor, atLeastOnce()).accept(intyg2, hsaInfo2);
    }

}
