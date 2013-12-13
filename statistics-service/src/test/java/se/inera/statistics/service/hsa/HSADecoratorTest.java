package se.inera.statistics.service.hsa;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import se.inera.statistics.service.JSONSource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(MockitoJUnitRunner.class)
public class HSADecoratorTest {

    @Mock
    private HSAService hsaService;// = Mockito.mock(HSAService.class);

    @Mock
    private EntityManager manager;

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
