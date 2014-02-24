package se.inera.statistics.service.demo;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import se.inera.statistics.service.common.CommonPersistence;
import se.inera.statistics.service.helper.DocumentHelper;
import se.inera.statistics.service.helper.UtlatandeBuilder;
import se.inera.statistics.service.queue.JmsReceiver;
import se.inera.statistics.service.report.model.Avsnitt;
import se.inera.statistics.service.report.repository.NationellUpdater;
import se.inera.statistics.service.report.util.DiagnosUtil;

import com.fasterxml.jackson.databind.JsonNode;

@RunWith(MockitoJUnitRunner.class)
public class DemoTest {

    @Mock
    private CommonPersistence persistence = Mockito.mock(CommonPersistence.class);

    @Mock
    private JmsReceiver receiver = Mockito.mock(JmsReceiver.class);

    @Mock
    private NationellUpdater nationellUpdater = Mockito.mock(NationellUpdater.class);

    @Mock
    DiagnosUtil util = Mockito.mock(DiagnosUtil.class);

    @InjectMocks
    InjectUtlatande injectUtlatande = new InjectUtlatande();

    @Before
    public void setup() {
        Mockito.when(util.getSubGroups(Mockito.anyString())).thenReturn(Arrays.asList(new Avsnitt("A10-A20", "test1"), new Avsnitt("A21-A30", "test2")));
    }

    @Test
    public void permutateIntyg() {
        UtlatandeBuilder builder = new UtlatandeBuilder();

        JsonNode result = injectUtlatande.permutate(builder, "19121212-1212");

        assertEquals("19121212-1212", result.path("patient").path("id").path("extension").asText());
        assertEquals("A21", DocumentHelper.getDiagnos(result));
        assertEquals(Arrays.asList("25"), DocumentHelper.getArbetsformaga(result));
    }

}
