/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
import se.inera.statistics.hsa.model.GetStatisticsNamesResponseDto;
import se.inera.statistics.hsa.model.GetStatisticsPersonResponseDto;

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
        Mockito.when(respMock.getStatisticsNameInfos()).thenReturn(new GetStatisticsNamesResponseType.StatisticsNameInfos());
        Mockito.when(serverInterface.getStatisticsNames(any(AttributedURIType.class), any(AttributedURIType.class), any(GetStatisticsNamesType.class))).thenReturn(respMock);

        //When
        final GetStatisticsNamesResponseDto names = hsaWebServiceCalls.getStatisticsNames("testid");

        //Then
        assertEquals(respMock.getStatisticsNameInfos().getStatisticsNameInfo().size(), names.getStatisticsNameInfos().size());
        Mockito.verify(serverInterface).getStatisticsNames(any(AttributedURIType.class), any(AttributedURIType.class), namesParameters.capture());
        assertNotNull(namesParameters.getValue());
    }

    @Test
    public void testGetStatisticsPerson() throws Exception {
        //Given
        final GetStatisticsPersonResponseType respMock = Mockito.mock(GetStatisticsPersonResponseType.class);
        Mockito.when(respMock.getAge()).thenReturn("MyAge");
        Mockito.when(serverInterface.getStatisticsPerson(any(AttributedURIType.class), any(AttributedURIType.class), any(GetStatisticsPersonType.class))).thenReturn(respMock);

        //When
        final GetStatisticsPersonResponseDto names = hsaWebServiceCalls.getStatisticsPerson("testid");

        //Then
        assertEquals(respMock.getAge(), names.getAge());
        Mockito.verify(serverInterface).getStatisticsPerson(any(AttributedURIType.class), any(AttributedURIType.class), personParameters.capture());
        assertNotNull(personParameters.getValue());
    }

}
