/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.hsa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import se.inera.statistics.integration.hsa.model.GetStatisticsCareGiverResponseDto;
import se.inera.statistics.integration.hsa.model.GetStatisticsHsaUnitResponseDto;
import se.inera.statistics.integration.hsa.model.GetStatisticsNamesResponseDto;
import se.inera.statistics.integration.hsa.model.GetStatisticsPersonResponseDto;
import se.inera.statistics.integration.hsa.model.StatisticsHsaUnitDto;
import se.inera.statistics.integration.hsa.model.StatisticsNameInfoDto;
import se.inera.statistics.integration.hsa.services.HsaStatisticsService;

@RunWith(MockitoJUnitRunner.class)
public class HSAServiceImplTest {

    public static final GetStatisticsHsaUnitResponseDto WS_ENHET = createGetStatisticsHsaUnitResponseDto();
    private final Clock clock = Clock.systemDefaultZone();

    private static GetStatisticsHsaUnitResponseDto createGetStatisticsHsaUnitResponseDto() {
        GetStatisticsHsaUnitResponseDto responseType = new GetStatisticsHsaUnitResponseDto();
        StatisticsHsaUnitDto unit = new StatisticsHsaUnitDto();
        unit.setHsaIdentity("UnitId");
        unit.setCareGiverHsaIdentity("vgId");
        responseType.setStatisticsCareUnit(unit);
        responseType.setStatisticsUnit(unit);
        return responseType;
    }

    private static GetStatisticsHsaUnitResponseDto createGetStatisticsHsaUnitResponseTypeWhereHuvudenhetHasVg() {
        GetStatisticsHsaUnitResponseDto responseType = new GetStatisticsHsaUnitResponseDto();
        StatisticsHsaUnitDto unit = new StatisticsHsaUnitDto();
        unit.setHsaIdentity("UnitId");
        responseType.setStatisticsUnit(unit);
        StatisticsHsaUnitDto careUnit = new StatisticsHsaUnitDto();
        careUnit.setHsaIdentity("UnitId2");
        careUnit.setCareGiverHsaIdentity("vgId");
        responseType.setStatisticsCareUnit(careUnit);
        return responseType;
    }

    public static final GetStatisticsCareGiverResponseDto WS_VG = createGetStatisticsCareGiverResponseDto();

    private static GetStatisticsCareGiverResponseDto createGetStatisticsCareGiverResponseDto() {
        GetStatisticsCareGiverResponseDto response = new GetStatisticsCareGiverResponseDto();
        response.setHsaIdentity("CareGiverId");
        return response;
    }

    public static final GetStatisticsPersonResponseDto WS_PERSON = createGetStatisticsPersonResponseDto();

    private static GetStatisticsPersonResponseDto createGetStatisticsPersonResponseDto() {
        GetStatisticsPersonResponseDto responseType = new GetStatisticsPersonResponseDto();
        responseType.setHsaIdentity("PersonId");
        return responseType;
    }

    public static final GetStatisticsNamesResponseDto WS_NAME = createGetStatisticsNamesResponseDto();

    private static GetStatisticsNamesResponseDto createGetStatisticsNamesResponseDto() {
        GetStatisticsNamesResponseDto responseType = new GetStatisticsNamesResponseDto();
        final ArrayList<StatisticsNameInfoDto> statisticsNameInfos = new ArrayList<>();
        responseType.setStatisticsNameInfos(statisticsNameInfos);
        StatisticsNameInfoDto nameInfo = new StatisticsNameInfoDto();
        nameInfo.setPersonGivenName("PersonName");
        statisticsNameInfos.add(nameInfo);
        return responseType;
    }

    @Mock
    private HsaStatisticsService wsCalls;

    @InjectMocks
    private HSAServiceImpl serviceImpl = new HSAServiceImpl();

    @Test
    public void serviceReturnsNullOnException() throws Exception {
        HSAKey key = new HSAKey("vardgivareId", "enhetId", "lakareId");
        when(wsCalls.getStatisticsHsaUnit("ENHETID")).thenThrow(new IllegalStateException("This WS generated and exception"));
        HsaInfo hsaInfo = serviceImpl.getHSAInfo(key);
        assertNull(hsaInfo);
    }

    private HsaInfo getFullJsonNode() {
        return new HsaInfo(getHsaInfoEnhet("cachedvgid"), getHsaInfoEnhet("cachedvgid"), getHsaInfoVg(), getHsaInfoPersonal());
    }

    private HsaInfoPersonal getHsaInfoPersonal() {
        return new HsaInfoPersonal("", "", "", Arrays.asList(""), Arrays.asList(""), Arrays.asList(""), false, "", "");
    }

    private HsaInfoVg getHsaInfoVg() {
        return new HsaInfoVg("", "", LocalDateTime.now(clock), LocalDateTime.now(clock), false);
    }

    private HsaInfoEnhet getHsaInfoEnhet(String vgid) {
        return new HsaInfoEnhet("", Arrays.asList(""), Arrays.asList(""), LocalDateTime.now(clock), LocalDateTime.now(clock), false,
            Arrays.asList(""), Arrays.asList(""), new HsaInfoEnhetGeo(new HsaInfoCoordinate("", "", ""), "", "", "", "", ""), vgid);
    }

    @Test
    public void testGetHsaInfoCorrectResultAndDoesNotCallWsWhenBaseInfoIsFull() throws Exception {
        //Given
        HsaInfo baseHsaInfo = getFullJsonNode();

        //When
        HsaInfo result = serviceImpl.getHSAInfo(new HSAKey("a", "b", "c"), baseHsaInfo);

        //Then
        assertEquals(baseHsaInfo.getEnhet(), result.getEnhet());
        assertEquals(baseHsaInfo.getHuvudenhet(), result.getHuvudenhet());
        assertEquals(baseHsaInfo.getPersonal(), result.getPersonal());
        assertEquals(baseHsaInfo.getVardgivare(), result.getVardgivare());

        Mockito.verify(wsCalls, times(0)).getStatisticsCareGiver(anyString());
        Mockito.verify(wsCalls, times(0)).getStatisticsHsaUnit(anyString());
        Mockito.verify(wsCalls, times(0)).getStatisticsNames(anyString());
        Mockito.verify(wsCalls, times(0)).getStatisticsPerson(anyString());
    }

    @Test
    public void testGetHsaInfoCorrectResultAndDoesOnlyCallCorrectWsWhenBaseInfoIsMissingEnhet() throws Exception {
        //Given
        setupHsaWsCalls();
        final HsaInfo baseHsaInfo = new HsaInfo(null, getHsaInfoEnhet("cachedvgid"), getHsaInfoVg(), getHsaInfoPersonal());

        //When
        HsaInfo result = serviceImpl.getHSAInfo(new HSAKey("a", "b", "c"), baseHsaInfo);

        //Then
        assertNotEquals(baseHsaInfo.getEnhet(), result.getEnhet());
        assertEquals("vgId", result.getHuvudenhet().getVgid());
        assertEquals(baseHsaInfo.getPersonal(), result.getPersonal());
        assertEquals(baseHsaInfo.getVardgivare(), result.getVardgivare());

        Mockito.verify(wsCalls, times(0)).getStatisticsCareGiver(anyString());
        Mockito.verify(wsCalls, times(1)).getStatisticsHsaUnit(anyString());
        Mockito.verify(wsCalls, times(0)).getStatisticsNames(anyString());
        Mockito.verify(wsCalls, times(0)).getStatisticsPerson(anyString());
    }

    @Test
    public void testGetHsaInfoDoesCallWsWhenBaseInfoIsMissingEnhetAndHuvudenhetIsMissingVg() throws Exception {
        //Given
        String enhetId = "b";
        setupHsaWsCalls();
        Mockito.doReturn(createGetStatisticsHsaUnitResponseTypeWhereHuvudenhetHasVg()).when(wsCalls).getStatisticsHsaUnit(anyString());
        final HsaInfo baseHsaInfo = new HsaInfo(null, getHsaInfoEnhet("cachedvgid"), null, getHsaInfoPersonal());

        //When
        HsaInfo result = serviceImpl.getHSAInfo(new HSAKey("a", enhetId, "c"), baseHsaInfo);

        //Then
        assertNull(result.getEnhet().getVgid());
        assertEquals("vgId", result.getHuvudenhet().getVgid());
        Mockito.verify(wsCalls, times(1)).getStatisticsHsaUnit(enhetId.toUpperCase());
        Mockito.verify(wsCalls, times(1)).getStatisticsCareGiver("vgId".toUpperCase());
    }

    @Test
    public void testGetHsaInfoCorrectResultAndDoesOnlyCallCorrectWsWhenBaseInfoIsMissingHuvudenhet() throws Exception {
        //Given
        setupHsaWsCalls();
        final HsaInfo baseHsaInfo = new HsaInfo(getHsaInfoEnhet(null), null, getHsaInfoVg(), getHsaInfoPersonal());

        //When
        HsaInfo result = serviceImpl.getHSAInfo(new HSAKey("a", "b", "c"), baseHsaInfo);

        //Then
        assertEquals("vgId", result.getHuvudenhet().getVgid());
        assertEquals(baseHsaInfo.getPersonal(), result.getPersonal());
        assertEquals(baseHsaInfo.getVardgivare(), result.getVardgivare());

        Mockito.verify(wsCalls, times(0)).getStatisticsCareGiver(anyString());
        Mockito.verify(wsCalls, times(1)).getStatisticsHsaUnit(anyString());
        Mockito.verify(wsCalls, times(0)).getStatisticsNames(anyString());
        Mockito.verify(wsCalls, times(0)).getStatisticsPerson(anyString());
    }

    @Test
    public void testGetHsaInfoCorrectResultAndDoesOnlyCallCorrectWsWhenBaseInfoIsMissingPersonal() throws Exception {
        //Given
        setupHsaWsCalls();
        final HsaInfo baseHsaInfo = new HsaInfo(getHsaInfoEnhet("cachedvgid"), getHsaInfoEnhet("cachedvgid"), getHsaInfoVg(), null);

        //When
        final HsaInfo result = serviceImpl.getHSAInfo(new HSAKey("a", "b", "c"), baseHsaInfo);

        //Then
        assertEquals(baseHsaInfo.getEnhet(), result.getEnhet());
        assertEquals(baseHsaInfo.getHuvudenhet(), result.getHuvudenhet());
        assertNotEquals(baseHsaInfo.getPersonal(), result.getPersonal());
        assertEquals(baseHsaInfo.getVardgivare(), result.getVardgivare());

        Mockito.verify(wsCalls, times(0)).getStatisticsCareGiver(anyString());
        Mockito.verify(wsCalls, times(0)).getStatisticsHsaUnit(anyString());
        Mockito.verify(wsCalls, times(1)).getStatisticsNames(anyString());
        Mockito.verify(wsCalls, times(1)).getStatisticsPerson(anyString());
    }

    @Test
    public void testGetHsaInfoCorrectResultAndDoesOnlyCallCorrectWsWhenBaseInfoIsMissingVg() throws Exception {
        //Given
        setupHsaWsCalls();
        final HsaInfo baseHsaInfo = new HsaInfo(getHsaInfoEnhet("cachedvgid"), getHsaInfoEnhet("cachedvgid"), null, getHsaInfoPersonal());

        //When
        final HsaInfo result = serviceImpl.getHSAInfo(new HSAKey("a", "b", "c"), baseHsaInfo);

        //Then
        assertEquals(baseHsaInfo.getEnhet(), result.getEnhet());
        assertEquals(baseHsaInfo.getHuvudenhet(), result.getHuvudenhet());
        assertEquals(baseHsaInfo.getPersonal(), result.getPersonal());
        assertNotEquals(baseHsaInfo.getVardgivare(), result.getVardgivare());

        Mockito.verify(wsCalls, times(1)).getStatisticsCareGiver(anyString());
        Mockito.verify(wsCalls, times(0)).getStatisticsHsaUnit(anyString());
        Mockito.verify(wsCalls, times(0)).getStatisticsNames(anyString());
        Mockito.verify(wsCalls, times(0)).getStatisticsPerson(anyString());
    }

    @Test
    public void testGetHsaInfoForHuvudenhetIsNotCalledWhenEnhetHasVgid() throws Exception {
        //Given
        setupHsaWsCalls();
        final HsaInfo baseHsaInfo = new HsaInfo(getHsaInfoEnhet("cachedvgid"), null, getHsaInfoVg(), getHsaInfoPersonal());

        //When
        HsaInfo result = serviceImpl.getHSAInfo(new HSAKey("a", "b", "c"), baseHsaInfo);

        //Then
        Mockito.verify(wsCalls, times(0)).getStatisticsHsaUnit(anyString());
    }

    @Test
    public void testGetHsaInfoForHuvudenhetIsNotCalledWhenHuvudenhetWithVgExists() throws Exception {
        //Given
        setupHsaWsCalls();
        final HsaInfo baseHsaInfo = new HsaInfo(getHsaInfoEnhet(null), getHsaInfoEnhet("cachedvgid"), getHsaInfoVg(), getHsaInfoPersonal());

        //When
        HsaInfo result = serviceImpl.getHSAInfo(new HSAKey("a", "b", "c"), baseHsaInfo);

        //Then
        Mockito.verify(wsCalls, times(0)).getStatisticsHsaUnit(anyString());
    }

    @Test
    public void testGetHsaInfoForHuvudenhetIsOnlyCalledForHuvudenhetWhenEnhetHasNoVgidAndHuvudenhetIsMissing() throws Exception {
        //Given
        setupHsaWsCalls();
        final HsaInfo baseHsaInfo = new HsaInfo(getHsaInfoEnhet(null), null, getHsaInfoVg(), getHsaInfoPersonal());

        //When
        HsaInfo result = serviceImpl.getHSAInfo(new HSAKey("a", "b", "c"), baseHsaInfo);

        //Then
        Mockito.verify(wsCalls, times(1)).getStatisticsHsaUnit(anyString());
    }

    private void setupHsaWsCalls() {
        Mockito.doReturn(WS_ENHET).when(wsCalls).getStatisticsHsaUnit(anyString());
        Mockito.doReturn(WS_VG).when(wsCalls).getStatisticsCareGiver(anyString());
        Mockito.doReturn(WS_PERSON).when(wsCalls).getStatisticsPerson(anyString());
        Mockito.doReturn(WS_NAME).when(wsCalls).getStatisticsNames(anyString());
    }

}
