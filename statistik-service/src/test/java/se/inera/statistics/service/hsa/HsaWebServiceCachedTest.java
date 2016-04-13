/**
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
 *
 * This file is part of statistik (https://github.com/sklintyg/statistik).
 *
 * statistik is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * statistik is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.service.hsa;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import se.inera.ifv.hsawsresponder.v3.GetStatisticsCareGiverResponseType;
import se.inera.ifv.hsawsresponder.v3.GetStatisticsHsaUnitResponseType;
import se.inera.ifv.hsawsresponder.v3.GetStatisticsNamesResponseType;
import se.inera.ifv.hsawsresponder.v3.GetStatisticsPersonResponseType;
import se.inera.ifv.statistics.spi.authorization.impl.HSAWebServiceCalls;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class HsaWebServiceCachedTest {

    @Mock
    private HSAWebServiceCalls service;

    @InjectMocks
    private HsaWebServiceCached cachedService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetStatisticsHsaUnitCachesNullResponse() throws Exception {
        //Given
        final String unitId = "HSAid";
        Mockito.doReturn(null).when(service).getStatisticsHsaUnit(unitId);

        //When
        GetStatisticsHsaUnitResponseType result1 = cachedService.getStatisticsHsaUnit(unitId);
        GetStatisticsHsaUnitResponseType result2 = cachedService.getStatisticsHsaUnit(unitId);

        //Then
        assertNull(result1);
        assertNull(result2);
        Mockito.verify(service, Mockito.times(1)).getStatisticsHsaUnit(Mockito.anyString());
    }

    @Test
    public void testGetStatisticsHsaUnitCachesCorrectly() throws Exception {
        //Given
        final String unitId1 = "HSAid1";
        final String unitId2 = "HSAid2";
        final GetStatisticsHsaUnitResponseType mock1 = Mockito.mock(GetStatisticsHsaUnitResponseType.class);
        final GetStatisticsHsaUnitResponseType mock2 = Mockito.mock(GetStatisticsHsaUnitResponseType.class);
        Mockito.doReturn(mock1).when(service).getStatisticsHsaUnit(unitId1);
        Mockito.doReturn(mock2).when(service).getStatisticsHsaUnit(unitId2);

        //When
        GetStatisticsHsaUnitResponseType result1 = cachedService.getStatisticsHsaUnit(unitId1);
        GetStatisticsHsaUnitResponseType result2 = cachedService.getStatisticsHsaUnit(unitId2);
        GetStatisticsHsaUnitResponseType result3 = cachedService.getStatisticsHsaUnit(unitId1);
        GetStatisticsHsaUnitResponseType result4 = cachedService.getStatisticsHsaUnit(unitId2);

        //Then
        assertEquals(mock1, result1);
        assertEquals(mock2, result2);
        assertEquals(mock1, result3);
        assertEquals(mock2, result4);
        Mockito.verify(service, Mockito.times(2)).getStatisticsHsaUnit(Mockito.anyString());
    }

    @Test
    public void testGetStatisticsNamesCachesNullResponse() throws Exception {
        //Given
        final String hsaId = "HSAid";
        Mockito.doReturn(null).when(service).getStatisticsNames(hsaId);

        //When
        GetStatisticsNamesResponseType result1 = cachedService.getStatisticsNames(hsaId);
        GetStatisticsNamesResponseType result2 = cachedService.getStatisticsNames(hsaId);

        //Then
        assertNull(result1);
        assertNull(result2);
        Mockito.verify(service, Mockito.times(1)).getStatisticsNames(Mockito.anyString());
    }

    @Test
    public void testGetStatisticsNamesCachesCorrectly() throws Exception {
        //Given
        final String hsaId1 = "HSAid1";
        final String hsaId2 = "HSAid2";
        final GetStatisticsNamesResponseType mock1 = Mockito.mock(GetStatisticsNamesResponseType.class);
        final GetStatisticsNamesResponseType mock2 = Mockito.mock(GetStatisticsNamesResponseType.class);
        Mockito.doReturn(mock1).when(service).getStatisticsNames(hsaId1);
        Mockito.doReturn(mock2).when(service).getStatisticsNames(hsaId2);

        //When
        GetStatisticsNamesResponseType result1 = cachedService.getStatisticsNames(hsaId1);
        GetStatisticsNamesResponseType result2 = cachedService.getStatisticsNames(hsaId2);
        GetStatisticsNamesResponseType result3 = cachedService.getStatisticsNames(hsaId1);
        GetStatisticsNamesResponseType result4 = cachedService.getStatisticsNames(hsaId2);

        //Then
        assertEquals(mock1, result1);
        assertEquals(mock2, result2);
        assertEquals(mock1, result3);
        assertEquals(mock2, result4);
        Mockito.verify(service, Mockito.times(2)).getStatisticsNames(Mockito.anyString());
    }

    @Test
    public void testGetStatisticsPersonCachesNullResponse() throws Exception {
        //Given
        final String hsaId = "HSAid";
        Mockito.doReturn(null).when(service).getStatisticsPerson(hsaId);

        //When
        GetStatisticsPersonResponseType result1 = cachedService.getStatisticsPerson(hsaId);
        GetStatisticsPersonResponseType result2 = cachedService.getStatisticsPerson(hsaId);

        //Then
        assertNull(result1);
        assertNull(result2);
        Mockito.verify(service, Mockito.times(1)).getStatisticsPerson(Mockito.anyString());
    }

    @Test
    public void testGetStatisticsPersonCachesCorrectly() throws Exception {
        //Given
        final String hsaId1 = "HSAid1";
        final String hsaId2 = "HSAid2";
        final GetStatisticsPersonResponseType mock1 = Mockito.mock(GetStatisticsPersonResponseType.class);
        final GetStatisticsPersonResponseType mock2 = Mockito.mock(GetStatisticsPersonResponseType.class);
        Mockito.doReturn(mock1).when(service).getStatisticsPerson(hsaId1);
        Mockito.doReturn(mock2).when(service).getStatisticsPerson(hsaId2);

        //When
        GetStatisticsPersonResponseType result1 = cachedService.getStatisticsPerson(hsaId1);
        GetStatisticsPersonResponseType result2 = cachedService.getStatisticsPerson(hsaId2);
        GetStatisticsPersonResponseType result3 = cachedService.getStatisticsPerson(hsaId1);
        GetStatisticsPersonResponseType result4 = cachedService.getStatisticsPerson(hsaId2);

        //Then
        assertEquals(mock1, result1);
        assertEquals(mock2, result2);
        assertEquals(mock1, result3);
        assertEquals(mock2, result4);
        Mockito.verify(service, Mockito.times(2)).getStatisticsPerson(Mockito.anyString());
    }

//    @Test
//    public void testCallMiuRightsCachesNullResponse() throws Exception {
//        //Given
//        final GetMiuForPersonType parameters = Mockito.mock(GetMiuForPersonType.class);
//        Mockito.doReturn(null).when(service).callMiuRights(parameters);
//
//        //When
//        GetMiuForPersonResponseType result1 = cachedService.callMiuRights(parameters);
//        GetMiuForPersonResponseType result2 = cachedService.callMiuRights(parameters);
//
//        //Then
//        assertNull(result1);
//        assertNull(result2);
//        Mockito.verify(service, Mockito.times(1)).callMiuRights(Mockito.any(GetMiuForPersonType.class));
//    }
//
//    @Test
//    public void testCallMiuRightsCachesCorrectly() throws Exception {
//        //Given
//        final GetMiuForPersonType parameters1 = Mockito.mock(GetMiuForPersonType.class);
//        final GetMiuForPersonType parameters2 = Mockito.mock(GetMiuForPersonType.class);
//        final GetMiuForPersonResponseType mock1 = Mockito.mock(GetMiuForPersonResponseType.class);
//        final GetMiuForPersonResponseType mock2 = Mockito.mock(GetMiuForPersonResponseType.class);
//        Mockito.doReturn(mock1).when(service).callMiuRights(parameters1);
//        Mockito.doReturn(mock2).when(service).callMiuRights(parameters2);
//
//        //When
//        GetMiuForPersonResponseType result1 = cachedService.callMiuRights(parameters1);
//        GetMiuForPersonResponseType result2 = cachedService.callMiuRights(parameters2);
//        GetMiuForPersonResponseType result3 = cachedService.callMiuRights(parameters1);
//        GetMiuForPersonResponseType result4 = cachedService.callMiuRights(parameters2);
//
//        //Then
//        assertEquals(mock1, result1);
//        assertEquals(mock2, result2);
//        assertEquals(mock1, result3);
//        assertEquals(mock2, result4);
//        Mockito.verify(service, Mockito.times(2)).callMiuRights(Mockito.any(GetMiuForPersonType.class));
//    }

    @Test
    public void testGetStatisticsCareGiverCachesNullResponse() throws Exception {
        //Given
        final String hsaId = "HSAid";
        Mockito.doReturn(null).when(service).getStatisticsCareGiver(hsaId);

        //When
        GetStatisticsCareGiverResponseType result1 = cachedService.getStatisticsCareGiver(hsaId);
        GetStatisticsCareGiverResponseType result2 = cachedService.getStatisticsCareGiver(hsaId);

        //Then
        assertNull(result1);
        assertNull(result2);
        Mockito.verify(service, Mockito.times(1)).getStatisticsCareGiver(Mockito.anyString());
    }

    @Test
    public void testGetStatisticsCareGiverCachesCorrectly() throws Exception {
        //Given
        final String hsaId1 = "HSAid1";
        final String hsaId2 = "HSAid2";
        final GetStatisticsCareGiverResponseType mock1 = Mockito.mock(GetStatisticsCareGiverResponseType.class);
        final GetStatisticsCareGiverResponseType mock2 = Mockito.mock(GetStatisticsCareGiverResponseType.class);
        Mockito.doReturn(mock1).when(service).getStatisticsCareGiver(hsaId1);
        Mockito.doReturn(mock2).when(service).getStatisticsCareGiver(hsaId2);

        //When
        GetStatisticsCareGiverResponseType result1 = cachedService.getStatisticsCareGiver(hsaId1);
        GetStatisticsCareGiverResponseType result2 = cachedService.getStatisticsCareGiver(hsaId2);
        GetStatisticsCareGiverResponseType result3 = cachedService.getStatisticsCareGiver(hsaId1);
        GetStatisticsCareGiverResponseType result4 = cachedService.getStatisticsCareGiver(hsaId2);

        //Then
        assertEquals(mock1, result1);
        assertEquals(mock2, result2);
        assertEquals(mock1, result3);
        assertEquals(mock2, result4);
        Mockito.verify(service, Mockito.times(2)).getStatisticsCareGiver(Mockito.anyString());
    }

}
