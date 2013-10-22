package se.inera.statistics.service.hsa;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

import org.mockito.runners.MockitoJUnitRunner;

import se.inera.statistics.service.JSONSource;
import se.inera.statistics.service.processlog.OrderedProcess;

import java.io.IOException;

@RunWith(MockitoJUnitRunner.class)
@Ignore
public class HSADecoratorTest {

    @Mock
    private HSAService hsaService;

    @Mock
    private OrderedProcess orderedProcess;


    @InjectMocks
    private HSADecorator hsaDecorator = new HSADecorator();

    @Test
    public void decorate_document() throws IOException {
        String docId = "aaa";
        JsonNode doc = new ObjectMapper().readTree(JSONSource.readTemplateAsString().toString());

        hsaDecorator.decorate(doc, docId);

        verify(hsaService).getHSAInfo(any(HSAKey.class));
    }
}
