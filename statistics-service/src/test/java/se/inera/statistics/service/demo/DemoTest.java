package se.inera.statistics.service.demo;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;

import se.inera.statistics.service.common.CommonPersistence;
import se.inera.statistics.service.helper.DocumentHelper;
import se.inera.statistics.service.queue.Receiver;
import se.inera.statistics.service.report.model.DiagnosisGroup;
import se.inera.statistics.service.report.repository.NationellUpdater;
import se.inera.statistics.service.report.util.DiagnosisGroupsUtil;

import com.fasterxml.jackson.databind.JsonNode;

@RunWith(MockitoJUnitRunner.class)
public class DemoTest {

    @Mock
    private CommonPersistence persistence = Mockito.mock(CommonPersistence.class);

    @Mock
    private Receiver receiver = Mockito.mock(Receiver.class);

    @Mock
    private NationellUpdater nationellUpdater = Mockito.mock(NationellUpdater.class);
    
    @Mock
    DiagnosisGroupsUtil util = Mockito.mock(DiagnosisGroupsUtil.class);
    
    @InjectMocks
    InjectUtlatande injectUtlatande = new InjectUtlatande();
    
    @Before
    public void setup() {
        Mockito.when(util.getSubGroups(Mockito.anyString())).thenReturn(Arrays.asList(new DiagnosisGroup("A10-A20", "test1"), new DiagnosisGroup("A21-A30", "test2")));
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
