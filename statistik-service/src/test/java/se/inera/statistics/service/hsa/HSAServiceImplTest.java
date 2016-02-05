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
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
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
import se.inera.ifv.statistics.spi.authorization.impl.HSAWebServiceCalls;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.util.ArrayList;

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
        JsonNode info = serviceImpl.getHSAInfo(key);
        assertNotNull(info);
        assertEquals("IFV1239877878-103H", info.get("enhet").get("id").textValue());
        assertFalse(info.get("enhet").has("geografi"));
    }

    @Test
    public void wsFindsNothing() throws Exception {
        HSAKey key = new HSAKey("vardgivareId", "enhetId", "lakareId");
        JsonNode info = serviceImpl.getHSAInfo(key);
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
        JsonNode hsaInfo = serviceImpl.getHSAInfo(key);
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

    private ObjectNode getFullJsonNode() {
        ObjectNode jsonNode = JsonNodeFactory.instance.objectNode();
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("vgid", "cachedvgid");
        jsonNode.put(HSAService.HSA_INFO_ENHET, node);
        jsonNode.put(HSAService.HSA_INFO_HUVUDENHET, HSAService.HSA_INFO_HUVUDENHET);
        jsonNode.put(HSAService.HSA_INFO_VARDGIVARE, HSAService.HSA_INFO_VARDGIVARE);
        jsonNode.put(HSAService.HSA_INFO_PERSONAL, HSAService.HSA_INFO_PERSONAL);
        return jsonNode;
    }

    private ObjectNode getFullJsonNodeWithoutVg() {
        ObjectNode jsonNode = JsonNodeFactory.instance.objectNode();
        jsonNode.put(HSAService.HSA_INFO_ENHET, HSAService.HSA_INFO_ENHET);
        jsonNode.put(HSAService.HSA_INFO_HUVUDENHET, HSAService.HSA_INFO_HUVUDENHET);
        jsonNode.put(HSAService.HSA_INFO_VARDGIVARE, HSAService.HSA_INFO_VARDGIVARE);
        jsonNode.put(HSAService.HSA_INFO_PERSONAL, HSAService.HSA_INFO_PERSONAL);
        return jsonNode;
    }

    @Test
    public void testGetHsaInfoCorrectResultAndDoesNotCallWsWhenBaseInfoIsFull() throws Exception {
        //Given
        ObjectNode baseHsaInfo = getFullJsonNode();

        //When
        ObjectNode result = serviceImpl.getHSAInfo(new HSAKey("a", "b", "c"), baseHsaInfo);

        //Then
        ArrayList<String> fields = Lists.newArrayList(result.fieldNames());
        assertEquals(4, fields.size());
        assertEquals(baseHsaInfo.get(HSAService.HSA_INFO_ENHET), result.get(HSAService.HSA_INFO_ENHET));
        assertEquals(baseHsaInfo.get(HSAService.HSA_INFO_HUVUDENHET), result.get(HSAService.HSA_INFO_HUVUDENHET));
        assertEquals(baseHsaInfo.get(HSAService.HSA_INFO_PERSONAL), result.get(HSAService.HSA_INFO_PERSONAL));
        assertEquals(baseHsaInfo.get(HSAService.HSA_INFO_VARDGIVARE), result.get(HSAService.HSA_INFO_VARDGIVARE));

        Mockito.verify(wsCalls, times(0)).getStatisticsCareGiver(anyString());
        Mockito.verify(wsCalls, times(0)).getStatisticsHsaUnit(anyString());
        Mockito.verify(wsCalls, times(0)).getStatisticsNames(anyString());
        Mockito.verify(wsCalls, times(0)).getStatisticsPerson(anyString());
    }

    @Test
    public void testGetHsaInfoCorrectResultAndDoesOnlyCallCorrectWsWhenBaseInfoIsMissingEnhet() throws Exception {
        //Given
        setupHsaWsCalls();
        ObjectNode baseHsaInfo = getFullJsonNode();
        baseHsaInfo.remove(HSAService.HSA_INFO_ENHET);

        //When
        ObjectNode result = serviceImpl.getHSAInfo(new HSAKey("a", "b", "c"), baseHsaInfo);

        //Then
        ArrayList<String> fields = Lists.newArrayList(result.fieldNames());
        assertEquals(4, fields.size());
        assertNotEquals(baseHsaInfo.get(HSAService.HSA_INFO_ENHET), result.get(HSAService.HSA_INFO_ENHET));
        assertEquals("\"vgId\"", result.get(HSAService.HSA_INFO_HUVUDENHET).get("vgid").toString());
        assertEquals(baseHsaInfo.get(HSAService.HSA_INFO_PERSONAL), result.get(HSAService.HSA_INFO_PERSONAL));
        assertEquals(baseHsaInfo.get(HSAService.HSA_INFO_VARDGIVARE), result.get(HSAService.HSA_INFO_VARDGIVARE));

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
        ObjectNode baseHsaInfo = getFullJsonNodeWithoutVg();
        baseHsaInfo.remove(HSAService.HSA_INFO_VARDGIVARE);

        //When
        ObjectNode result = serviceImpl.getHSAInfo(new HSAKey("a", enhetId, "c"), baseHsaInfo);

        //Then
        assertEquals(null, result.get(HSAService.HSA_INFO_ENHET).get("vgid"));
        assertEquals("\"vgId\"", result.get(HSAService.HSA_INFO_HUVUDENHET).get("vgid").toString());
        Mockito.verify(wsCalls, times(1)).getStatisticsHsaUnit(enhetId.toUpperCase());
        Mockito.verify(wsCalls, times(1)).getStatisticsCareGiver("vgId".toUpperCase());
    }

    @Test
    public void testGetHsaInfoCorrectResultAndDoesOnlyCallCorrectWsWhenBaseInfoIsMissingHuvudenhet() throws Exception {
        //Given
        setupHsaWsCalls();
        ObjectNode baseHsaInfo = getFullJsonNode();
        baseHsaInfo.remove(HSAService.HSA_INFO_HUVUDENHET);
        baseHsaInfo.put(HSAService.HSA_INFO_ENHET, "nodeWithoutVgid");

        //When
        ObjectNode result = serviceImpl.getHSAInfo(new HSAKey("a", "b", "c"), baseHsaInfo);

        //Then
        ArrayList<String> fields = Lists.newArrayList(result.fieldNames());
        assertEquals(4, fields.size());
        assertEquals("\"vgId\"", result.get(HSAService.HSA_INFO_HUVUDENHET).get("vgid").toString());
        assertNotEquals(baseHsaInfo.get(HSAService.HSA_INFO_HUVUDENHET), result.get(HSAService.HSA_INFO_HUVUDENHET));
        assertEquals(baseHsaInfo.get(HSAService.HSA_INFO_PERSONAL), result.get(HSAService.HSA_INFO_PERSONAL));
        assertEquals(baseHsaInfo.get(HSAService.HSA_INFO_VARDGIVARE), result.get(HSAService.HSA_INFO_VARDGIVARE));

        Mockito.verify(wsCalls, times(0)).getStatisticsCareGiver(anyString());
        Mockito.verify(wsCalls, times(1)).getStatisticsHsaUnit(anyString());
        Mockito.verify(wsCalls, times(0)).getStatisticsNames(anyString());
        Mockito.verify(wsCalls, times(0)).getStatisticsPerson(anyString());
    }

    @Test
    public void testGetHsaInfoCorrectResultAndDoesOnlyCallCorrectWsWhenBaseInfoIsMissingPersonal() throws Exception {
        //Given
        setupHsaWsCalls();
        ObjectNode baseHsaInfo = getFullJsonNode();
        baseHsaInfo.remove(HSAService.HSA_INFO_PERSONAL);

        //When
        ObjectNode result = serviceImpl.getHSAInfo(new HSAKey("a", "b", "c"), baseHsaInfo);

        //Then
        ArrayList<String> fields = Lists.newArrayList(result.fieldNames());
        assertEquals(4, fields.size());
        assertEquals(baseHsaInfo.get(HSAService.HSA_INFO_ENHET), result.get(HSAService.HSA_INFO_ENHET));
        assertEquals(baseHsaInfo.get(HSAService.HSA_INFO_HUVUDENHET), result.get(HSAService.HSA_INFO_HUVUDENHET));
        assertNotEquals(baseHsaInfo.get(HSAService.HSA_INFO_PERSONAL), result.get(HSAService.HSA_INFO_PERSONAL));
        assertEquals(baseHsaInfo.get(HSAService.HSA_INFO_VARDGIVARE), result.get(HSAService.HSA_INFO_VARDGIVARE));

        Mockito.verify(wsCalls, times(0)).getStatisticsCareGiver(anyString());
        Mockito.verify(wsCalls, times(0)).getStatisticsHsaUnit(anyString());
        Mockito.verify(wsCalls, times(1)).getStatisticsNames(anyString());
        Mockito.verify(wsCalls, times(1)).getStatisticsPerson(anyString());
    }

    @Test
    public void testGetHsaInfoCorrectResultAndDoesOnlyCallCorrectWsWhenBaseInfoIsMissingVg() throws Exception {
        //Given
        setupHsaWsCalls();
        ObjectNode baseHsaInfo = getFullJsonNode();
        baseHsaInfo.remove(HSAService.HSA_INFO_VARDGIVARE);

        //When
        ObjectNode result = serviceImpl.getHSAInfo(new HSAKey("a", "b", "c"), baseHsaInfo);

        //Then
        ArrayList<String> fields = Lists.newArrayList(result.fieldNames());
        assertEquals(4, fields.size());
        assertEquals(baseHsaInfo.get(HSAService.HSA_INFO_ENHET), result.get(HSAService.HSA_INFO_ENHET));
        assertEquals(baseHsaInfo.get(HSAService.HSA_INFO_HUVUDENHET), result.get(HSAService.HSA_INFO_HUVUDENHET));
        assertEquals(baseHsaInfo.get(HSAService.HSA_INFO_PERSONAL), result.get(HSAService.HSA_INFO_PERSONAL));
        assertNotEquals(baseHsaInfo.get(HSAService.HSA_INFO_VARDGIVARE), result.get(HSAService.HSA_INFO_VARDGIVARE));

        Mockito.verify(wsCalls, times(1)).getStatisticsCareGiver(anyString());
        Mockito.verify(wsCalls, times(0)).getStatisticsHsaUnit(anyString());
        Mockito.verify(wsCalls, times(0)).getStatisticsNames(anyString());
        Mockito.verify(wsCalls, times(0)).getStatisticsPerson(anyString());
    }

    @Test
    public void testGetHsaInfoForHuvudenhetIsNotCalledWhenEnhetHasVgid() throws Exception {
        //Given
        setupHsaWsCalls();
        ObjectNode baseHsaInfo = getFullJsonNode();
        baseHsaInfo.remove(HSAService.HSA_INFO_HUVUDENHET);

        //When
        ObjectNode result = serviceImpl.getHSAInfo(new HSAKey("a", "b", "c"), baseHsaInfo);

        //Then
        Mockito.verify(wsCalls, times(0)).getStatisticsHsaUnit(anyString());
    }

    @Test
    public void testGetHsaInfoForHuvudenhetIsNotCalledWhenHuvudenhetWithVgExists() throws Exception {
        //Given
        setupHsaWsCalls();
        ObjectNode baseHsaInfo = getFullJsonNode();
        baseHsaInfo.put(HSAService.HSA_INFO_ENHET, "nodeWithoutVgid");
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("vgid", "cachedvgid");
        baseHsaInfo.put(HSAService.HSA_INFO_HUVUDENHET, node);

        //When
        ObjectNode result = serviceImpl.getHSAInfo(new HSAKey("a", "b", "c"), baseHsaInfo);

        //Then
        Mockito.verify(wsCalls, times(0)).getStatisticsHsaUnit(anyString());
    }

    @Test
    public void testGetHsaInfoForHuvudenhetIsOnlyCalledForHuvudenhetWhenEnhetHasNoVgidAndHuvudenhetIsMissing() throws Exception {
        //Given
        setupHsaWsCalls();
        ObjectNode baseHsaInfo = getFullJsonNode();
        baseHsaInfo.put(HSAService.HSA_INFO_ENHET, "nodeWithoutVgid");
        baseHsaInfo.remove(HSAService.HSA_INFO_HUVUDENHET);

        //When
        ObjectNode result = serviceImpl.getHSAInfo(new HSAKey("a", "b", "c"), baseHsaInfo);

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
