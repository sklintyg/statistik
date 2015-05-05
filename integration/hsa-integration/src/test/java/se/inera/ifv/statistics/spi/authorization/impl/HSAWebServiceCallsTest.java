package se.inera.ifv.statistics.spi.authorization.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Captor;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import se.inera.ifv.hsaws.v3.HsaWsResponderInterface;
import se.inera.ifv.hsawsresponder.v3.GetStatisticsNamesResponseType;
import org.w3.wsaddressing10.AttributedURIType;
import se.inera.ifv.hsawsresponder.v3.GetStatisticsNamesType;
import se.inera.ifv.hsawsresponder.v3.GetStatisticsPersonType;
import se.inera.ifv.hsawsresponder.v3.GetStatisticsPersonResponseType;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;

public class HSAWebServiceCallsTest {

    @InjectMocks
    private HSAWebServiceCalls hsaWebServiceCalls;

    @Mock
    private HsaWsResponderInterface serverInterface;

    @Captor
    private ArgumentCaptor<GetStatisticsNamesType> namesParameters;

    @Captor
    private ArgumentCaptor<GetStatisticsPersonType> personParameters;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetStatisticsNames() throws Exception {
        //Given
        final GetStatisticsNamesResponseType respMock = Mockito.mock(GetStatisticsNamesResponseType.class);
        Mockito.when(serverInterface.getStatisticsNames(any(AttributedURIType.class), any(AttributedURIType.class), any(GetStatisticsNamesType.class))).thenReturn(respMock);

        //When
        final GetStatisticsNamesResponseType names = hsaWebServiceCalls.getStatisticsNames("testid");

        //Then
        assertEquals(respMock, names);
        Mockito.verify(serverInterface).getStatisticsNames(any(AttributedURIType.class), any(AttributedURIType.class), namesParameters.capture());
        assertNotNull(namesParameters.getValue());
    }

    @Test
    public void testGetStatisticsPerson() throws Exception {
        //Given
        final GetStatisticsPersonResponseType respMock = Mockito.mock(GetStatisticsPersonResponseType.class);
        Mockito.when(serverInterface.getStatisticsPerson(any(AttributedURIType.class), any(AttributedURIType.class), any(GetStatisticsPersonType.class))).thenReturn(respMock);

        //When
        final GetStatisticsPersonResponseType names = hsaWebServiceCalls.getStatisticsPerson("testid");

        //Then
        assertEquals(respMock, names);
        Mockito.verify(serverInterface).getStatisticsPerson(any(AttributedURIType.class), any(AttributedURIType.class), personParameters.capture());
        assertNotNull(personParameters.getValue());
    }

}
