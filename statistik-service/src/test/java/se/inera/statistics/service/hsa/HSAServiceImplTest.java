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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.joda.time.LocalDateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import se.inera.ifv.hsawsresponder.v3.GetStatisticsCareGiverResponseType;
import se.inera.ifv.hsawsresponder.v3.GetStatisticsHsaUnitResponseType;
import se.inera.ifv.hsawsresponder.v3.GetStatisticsNamesResponseType;
import se.inera.ifv.hsawsresponder.v3.GetStatisticsPersonResponseType;
import se.inera.ifv.hsawsresponder.v3.StatisticsHsaUnit;
import se.inera.ifv.hsawsresponder.v3.StatisticsNameInfo;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HSAServiceImplTest {

    public static final GetStatisticsHsaUnitResponseType WS_ENHET = createGetStatisticsHsaUnitResponseType();

    private static GetStatisticsHsaUnitResponseType createGetStatisticsHsaUnitResponseType() {
        GetStatisticsHsaUnitResponseType responseType = new GetStatisticsHsaUnitResponseType();
        StatisticsHsaUnit unit = new StatisticsHsaUnit();
        unit.setHsaIdentity("UnitId");
        unit.setCareGiverHsaIdentity("vgId");
        responseType.setStatisticsCareUnit(unit);
        responseType.setStatisticsUnit(unit);
        return responseType;
    }

    private static GetStatisticsHsaUnitResponseType createGetStatisticsHsaUnitResponseTypeWhereHuvudenhetHasVg() {
        GetStatisticsHsaUnitResponseType responseType = new GetStatisticsHsaUnitResponseType();
        StatisticsHsaUnit unit = new StatisticsHsaUnit();
        unit.setHsaIdentity("UnitId");
        responseType.setStatisticsUnit(unit);
        StatisticsHsaUnit careUnit = new StatisticsHsaUnit();
        careUnit.setHsaIdentity("UnitId2");
        careUnit.setCareGiverHsaIdentity("vgId");
        responseType.setStatisticsCareUnit(careUnit);
        return responseType;
    }

    public static final GetStatisticsCareGiverResponseType WS_VG = createGetStatisticsCareGiverResponseType();

    private static GetStatisticsCareGiverResponseType createGetStatisticsCareGiverResponseType() {
        GetStatisticsCareGiverResponseType response = new GetStatisticsCareGiverResponseType();
        response.setHsaIdentity("CareGiverId");
        return response;
    }

    public static final GetStatisticsPersonResponseType WS_PERSON = createGetStatisticsPersonResponseType();

    private static GetStatisticsPersonResponseType createGetStatisticsPersonResponseType() {
        GetStatisticsPersonResponseType responseType = new GetStatisticsPersonResponseType();
        responseType.setHsaIdentity("PersonId");
        return responseType;
    }

    public static final GetStatisticsNamesResponseType WS_NAME = createGetStatisticsNamesResponseType();

    private static GetStatisticsNamesResponseType createGetStatisticsNamesResponseType() {
        GetStatisticsNamesResponseType responseType = new GetStatisticsNamesResponseType();
        GetStatisticsNamesResponseType.StatisticsNameInfos nameInfos = new GetStatisticsNamesResponseType.StatisticsNameInfos();
        StatisticsNameInfo nameInfo = new StatisticsNameInfo();
        nameInfo.setPersonGivenName("PersonName");
        nameInfos.getStatisticsNameInfo().add(nameInfo);
        responseType.setStatisticsNameInfos(nameInfos);
        return responseType;
    }

    @Mock
    private HsaWebService wsCalls;

    @InjectMocks
    private HSAServiceImpl serviceImpl = new HSAServiceImpl();

    @Test
    public void hsUnitWithMissingGeography() throws Exception {
        GetStatisticsHsaUnitResponseType response = getHsaUnitResponse("GetStatisticsHsaUnit-small.xml");
        when(wsCalls.getStatisticsHsaUnit("ENHETID")).thenReturn(response);

        HSAKey key = new HSAKey("vardgivareId", "enhetId", "lakareId");
        JsonNode info = HSADecorator.toJsonNode(serviceImpl.getHSAInfo(key));
        assertNotNull(info);
        assertEquals("IFV1239877878-103H", info.get("enhet").get("id").textValue());
        assertFalse(info.get("enhet").has("geografi"));
    }

    @Test
    public void wsFindsNothing() throws Exception {
        HSAKey key = new HSAKey("vardgivareId", "enhetId", "lakareId");
        JsonNode info = HSADecorator.toJsonNode(serviceImpl.getHSAInfo(key));
        assertNotNull(info);
        assertFalse(info.has("enhet"));
        assertFalse(info.has("huvudenhet"));
        assertFalse(info.has("vardgivare"));
        assertFalse(info.has("personal"));
    }

    @Test
    public void serviceReturnsNullOnException() throws Exception {
        HSAKey key = new HSAKey("vardgivareId", "enhetId", "lakareId");
        when(wsCalls.getStatisticsHsaUnit("ENHETID")).thenThrow(new IllegalStateException("This WS generated and exception"));
        HsaInfo hsaInfo = serviceImpl.getHSAInfo(key);
        assertNull(hsaInfo);
    }

    private GetStatisticsHsaUnitResponseType getHsaUnitResponse(String name) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(GetStatisticsHsaUnitResponseType.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        InputStream input = getClass().getResourceAsStream("/soap-response/" + name);
        @SuppressWarnings("unchecked")
        JAXBElement<GetStatisticsHsaUnitResponseType> o = (JAXBElement<GetStatisticsHsaUnitResponseType>) unmarshaller.unmarshal(input);
        return o.getValue();
    }

    private HsaInfo getFullJsonNode() {
        return new HsaInfo(getHsaInfoEnhet("cachedvgid"), getHsaInfoEnhet("cachedvgid"), getHsaInfoVg(), getHsaInfoPersonal());
    }

    private HsaInfoPersonal getHsaInfoPersonal() {
        return new HsaInfoPersonal("", "", "", Arrays.asList(""), Arrays.asList(""), Arrays.asList(""), false, "", "");
    }

    private HsaInfoVg getHsaInfoVg() {
        return new HsaInfoVg("", "", new LocalDateTime(), new LocalDateTime(), false);
    }

    private HsaInfoEnhet getHsaInfoEnhet(String vgid) {
        return new HsaInfoEnhet("", Arrays.asList(""), Arrays.asList(""), new LocalDateTime(), new LocalDateTime(), false, Arrays.asList(""), Arrays.asList(""), new HsaInfoEnhetGeo(new HsaInfoCoordinate("", "", ""), "", "", "", "", ""), vgid);
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
        assertEquals(null, result.getEnhet().getVgid());
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
