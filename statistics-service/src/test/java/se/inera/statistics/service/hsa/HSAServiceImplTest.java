package se.inera.statistics.service.hsa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import se.inera.ifv.hsawsresponder.v3.GetStatisticsHsaUnitResponseType;
import se.inera.ifv.statistics.spi.authorization.impl.HSAWebServiceCalls;

import com.fasterxml.jackson.databind.JsonNode;

@RunWith(MockitoJUnitRunner.class)
public class HSAServiceImplTest {

    @Mock
    private HSAWebServiceCalls wsCalls;

    @InjectMocks
    private HSAServiceImpl serviceImpl = new HSAServiceImpl();

    @Test
    public void test() throws Exception {
        GetStatisticsHsaUnitResponseType response = getResponse("GetStatisticsHsaUnit-small.xml");
        when(wsCalls.getStatisticsHsaUnit("enhetId")).thenReturn(response);
        
        
        HSAKey key = new HSAKey("vardgivareId", "enhetId", "lakareId");
        JsonNode info = serviceImpl.getHSAInfo(key);
        assertNotNull(info);
        assertEquals("IFV1239877878-103H", info.path("enhet").path("id").textValue());
        
    }

    private GetStatisticsHsaUnitResponseType getResponse(String name) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(GetStatisticsHsaUnitResponseType.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        InputStream input = getClass().getResourceAsStream("/soap-response/" + name);
        JAXBElement<GetStatisticsHsaUnitResponseType> o = (JAXBElement<GetStatisticsHsaUnitResponseType>) unmarshaller.unmarshal(input);
        return o.getValue();
    }

}
