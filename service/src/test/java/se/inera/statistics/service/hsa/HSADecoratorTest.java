/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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

import static junit.framework.TestCase.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Arrays;
import javax.persistence.EntityManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import se.inera.statistics.service.JSONSource;

@RunWith(MockitoJUnitRunner.class)
public class HSADecoratorTest {

    private final Clock clock = Clock.systemDefaultZone();

    @Mock
    private HSAService hsaService;// = Mockito.mock(HSAService.class);

    @Mock
    private EntityManager manager;

    @InjectMocks
    private HSADecorator hsaDecorator = new HSADecorator();

    @Test
    public void decorate_document() throws IOException {
        JsonNode doc = new ObjectMapper().readTree(JSONSource.readTemplateAsString());

        hsaDecorator.decorate(doc, "aaa");

        verify(hsaService).getHSAInfo(any(HSAKey.class), any(HsaInfo.class));
    }

    @Test
    public void serviceOnlyCalledWhenCacheIsMissingData() throws IOException {
        //Given
        HSADecorator hsaDecoratorSpy = Mockito.spy(this.hsaDecorator);
        Mockito.doReturn(null).when(hsaDecoratorSpy).getHSAInfo(anyString());

        JsonNode doc = new ObjectMapper().readTree(JSONSource.readTemplateAsString());

        //When
        hsaDecoratorSpy.decorate(doc, "aaa");

        //Then
        verify(hsaService, times(1)).getHSAInfo(any(HSAKey.class), any(HsaInfo.class));
        verify(hsaDecoratorSpy, times(1)).storeHSAInfo(eq("aaa"), any(HsaInfo.class));
    }

    private HsaInfo getFullJsonNode() {
        final HsaInfoEnhet enhet = getHsaEnhet("cachedvgid");
        final HsaInfoVg vardgivare = new HsaInfoVg("", "", LocalDateTime.now(clock), LocalDateTime.now(clock), false);
        final HsaInfoPersonal personal = new HsaInfoPersonal("", "", "", Arrays.asList(""), Arrays.asList(""), Arrays.asList(""), false, "",
            "");
        return new HsaInfo(enhet, enhet, vardgivare, personal);
    }

    private HsaInfoEnhet getHsaEnhet(String vgid) {
        return new HsaInfoEnhet("", Arrays.asList(""), Arrays.asList(""), LocalDateTime.now(clock), LocalDateTime.now(clock), false,
            Arrays.asList(""), Arrays.asList(""), new HsaInfoEnhetGeo(new HsaInfoCoordinate("", "", ""), "", "", "", "", ""), vgid);
    }

    @Test
    public void serviceOnlyCalledWhenCacheIsMissingEnhetData() throws IOException {
        //Given
        HSADecorator hsaDecoratorSpy = Mockito.spy(this.hsaDecorator);
        HsaInfo fullJsonNode = getFullJsonNode();
        HsaInfo jsonNode = new HsaInfo(null, fullJsonNode.getHuvudenhet(), fullJsonNode.getVardgivare(), fullJsonNode.getPersonal());
        Mockito.doReturn(jsonNode).when(hsaDecoratorSpy).getHSAInfo(anyString());

        JsonNode doc = new ObjectMapper().readTree(JSONSource.readTemplateAsString());

        //When
        hsaDecoratorSpy.decorate(doc, "aaa");

        //Then
        verify(hsaService, times(1)).getHSAInfo(any(HSAKey.class), any(HsaInfo.class));
        verify(hsaDecoratorSpy, times(1)).storeHSAInfo(eq("aaa"), any(HsaInfo.class));
    }

    @Test
    public void serviceOnlyCalledWhenCacheIsMissingHuvudenhetData() throws IOException {
        //Given
        HSADecorator hsaDecoratorSpy = Mockito.spy(this.hsaDecorator);
        HsaInfo fullJsonNode = getFullJsonNode();
        HsaInfo jsonNode = new HsaInfo(fullJsonNode.getEnhet(), null, fullJsonNode.getVardgivare(), fullJsonNode.getPersonal());
        Mockito.doReturn(jsonNode).when(hsaDecoratorSpy).getHSAInfo(anyString());

        JsonNode doc = new ObjectMapper().readTree(JSONSource.readTemplateAsString());

        //When
        hsaDecoratorSpy.decorate(doc, "aaa");

        //Then
        verify(hsaService, times(1)).getHSAInfo(any(HSAKey.class), any(HsaInfo.class));
        verify(hsaDecoratorSpy, times(1)).storeHSAInfo(eq("aaa"), any(HsaInfo.class));
    }

    @Test
    public void serviceOnlyCalledWhenCacheIsMissingVardgivareData() throws IOException {
        //Given
        HSADecorator hsaDecoratorSpy = Mockito.spy(this.hsaDecorator);
        HsaInfo fullJsonNode = getFullJsonNode();
        HsaInfo jsonNode = new HsaInfo(fullJsonNode.getEnhet(), fullJsonNode.getHuvudenhet(), null, fullJsonNode.getPersonal());
        Mockito.doReturn(jsonNode).when(hsaDecoratorSpy).getHSAInfo(anyString());

        JsonNode doc = new ObjectMapper().readTree(JSONSource.readTemplateAsString());

        //When
        hsaDecoratorSpy.decorate(doc, "aaa");

        //Then
        verify(hsaService, times(1)).getHSAInfo(any(HSAKey.class), any(HsaInfo.class));
        verify(hsaDecoratorSpy, times(1)).storeHSAInfo(eq("aaa"), any(HsaInfo.class));
    }

    @Test
    public void serviceOnlyCalledWhenCacheIsMissingPersonalData() throws IOException {
        //Given
        HSADecorator hsaDecoratorSpy = Mockito.spy(this.hsaDecorator);
        HsaInfo fullJsonNode = getFullJsonNode();
        HsaInfo jsonNode = new HsaInfo(fullJsonNode.getEnhet(), fullJsonNode.getHuvudenhet(), fullJsonNode.getVardgivare(), null);
        Mockito.doReturn(jsonNode).when(hsaDecoratorSpy).getHSAInfo(anyString());

        JsonNode doc = new ObjectMapper().readTree(JSONSource.readTemplateAsString());

        //When
        hsaDecoratorSpy.decorate(doc, "aaa");

        //Then
        verify(hsaService, times(1)).getHSAInfo(any(HSAKey.class), any(HsaInfo.class));
        verify(hsaDecoratorSpy, times(1)).storeHSAInfo(eq("aaa"), any(HsaInfo.class));
    }

    @Test
    public void serviceNotCalledWhenCacheFullyPopulated() throws IOException {
        //Given
        HSADecorator hsaDecoratorSpy = Mockito.spy(this.hsaDecorator);
        HsaInfo fullJsonNode = getFullJsonNode();
        HsaInfo jsonNode = new HsaInfo(fullJsonNode.getEnhet(), fullJsonNode.getHuvudenhet(), fullJsonNode.getVardgivare(),
            fullJsonNode.getPersonal());
        Mockito.doReturn(jsonNode).when(hsaDecoratorSpy).getHSAInfo(anyString());

        JsonNode doc = new ObjectMapper().readTree(JSONSource.readTemplateAsString());

        //When
        hsaDecoratorSpy.decorate(doc, "aaa");

        //Then
        verify(hsaService, times(0)).getHSAInfo(any(HSAKey.class), any(HsaInfo.class));
        verify(hsaDecoratorSpy, times(0)).storeHSAInfo(eq("aaa"), any(HsaInfo.class));
    }

    @Test
    public void testHsaInfoToJson() throws Exception {
        //Given
        final LocalDateTime startdatum = LocalDateTime.of(2016, 4, 12, 22, 48);
        final HsaInfoVg hsaInfoVg = new HsaInfoVg(null, null, startdatum, null, false);
        final HsaInfo hsaInfo = new HsaInfo(null, null, hsaInfoVg, null);

        //When
        final String result = HSADecorator.hsaInfoToJson(hsaInfo);

        //Then
        assertTrue(result.contains("2016-04-12"));
    }

    @Test
    public void testHsaInfoConversion() throws Exception {
        //Given
        String hsaDataFronTestDb = "{\"enhet\":{\"agarform\":[\"Privat\"],\"geografi\":{\"kommun\":\"80\",\"koordinat\":{\"typ\":\"RT_90\",\"x\":\"6638800\",\"y\":\"1603250\"},\"lan\":\"03\",\"plats\":\"Uppsala\"},\"id\":\"SE162321000024-0036142\",\"startdatum\":\"2013-01-11\",\"vardform\":[\"01\"],\"verksamhet\":[\"1500\"],\"vgid\":\"SE162321000024-0036141\"},\"personal\":{\"alder\":\"77\",\"befattning\":[\"203010\"],\"efternamn\":\"Hilding\",\"id\":\"SE149165871\",\"kon\":\"1\",\"skyddad\":false,\"specialitet\":[\"20\",\"24\"],\"tilltalsnamn\":\"S?ren\",\"yrkesgrupp\":[\"L?kare\"]},\"vardgivare\":{\"id\":\"SE162321000024-0036141\",\"orgnr\":\"556193-0693\",\"startdatum\":\"2013-01-11\"}}";

        //When
        final HsaInfo hsaInfo = HSADecorator.jsonToHsaInfo(hsaDataFronTestDb);
        final String result = HSADecorator.hsaInfoToJson(hsaInfo);

        //Then
        assertTrue(result.contains("\"id\":\"SE162321000024-0036142\""));
    }
}
