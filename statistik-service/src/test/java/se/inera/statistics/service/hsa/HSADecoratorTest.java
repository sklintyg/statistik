/**
 * Copyright (C) 2015 Inera AB (http://www.inera.se)
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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import se.inera.statistics.service.JSONSource;
import se.inera.statistics.service.helper.DocumentHelper;

import javax.persistence.EntityManager;
import java.io.IOException;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class HSADecoratorTest {

    @Mock
    private HSAService hsaService;// = Mockito.mock(HSAService.class);

    @Mock
    private EntityManager manager;

    @InjectMocks
    private HSADecorator hsaDecorator = new HSADecorator();

    @Test
    public void decorate_document() throws IOException {
        JsonNode doc = new ObjectMapper().readTree(JSONSource.readTemplateAsString(DocumentHelper.IntygVersion.VERSION1));

        hsaDecorator.decorate(doc, "aaa");

        verify(hsaService).getHSAInfo(any(HSAKey.class), any(JsonNode.class));
    }

    @Test
    public void serviceOnlyCalledWhenCacheIsMissingData() throws IOException {
        //Given
        HSADecorator hsaDecoratorSpy = Mockito.spy(this.hsaDecorator);
        Mockito.doReturn(null).when(hsaDecoratorSpy).getHSAInfo(anyString());

        JsonNode doc = new ObjectMapper().readTree(JSONSource.readTemplateAsString(DocumentHelper.IntygVersion.VERSION1));

        //When
        hsaDecoratorSpy.decorate(doc, "aaa");

        //Then
        verify(hsaService, times(1)).getHSAInfo(any(HSAKey.class), any(JsonNode.class));
        verify(hsaDecoratorSpy, times(1)).storeHSAInfo(eq("aaa"), any(JsonNode.class));
    }

    private ObjectNode getFullJsonNode() {
        ObjectNode jsonNode = JsonNodeFactory.instance.objectNode();
        jsonNode.put(HSAService.HSA_INFO_ENHET, "Any info");
        jsonNode.put(HSAService.HSA_INFO_HUVUDENHET, "Any info");
        jsonNode.put(HSAService.HSA_INFO_VARDGIVARE, "Any info");
        jsonNode.put(HSAService.HSA_INFO_PERSONAL, "Any info");
        return jsonNode;
    }

    @Test
    public void serviceOnlyCalledWhenCacheIsMissingEnhetData() throws IOException {
        //Given
        HSADecorator hsaDecoratorSpy = Mockito.spy(this.hsaDecorator);
        ObjectNode jsonNode = getFullJsonNode();
        jsonNode.remove(HSAService.HSA_INFO_ENHET);
        Mockito.doReturn(jsonNode).when(hsaDecoratorSpy).getHSAInfo(anyString());

        JsonNode doc = new ObjectMapper().readTree(JSONSource.readTemplateAsString(DocumentHelper.IntygVersion.VERSION1));

        //When
        hsaDecoratorSpy.decorate(doc, "aaa");

        //Then
        verify(hsaService, times(1)).getHSAInfo(any(HSAKey.class), any(JsonNode.class));
        verify(hsaDecoratorSpy, times(1)).storeHSAInfo(eq("aaa"), any(JsonNode.class));
    }

    @Test
    public void serviceOnlyCalledWhenCacheIsMissingHuvudenhetData() throws IOException {
        //Given
        HSADecorator hsaDecoratorSpy = Mockito.spy(this.hsaDecorator);
        ObjectNode jsonNode = getFullJsonNode();
        jsonNode.remove(HSAService.HSA_INFO_HUVUDENHET);
        Mockito.doReturn(jsonNode).when(hsaDecoratorSpy).getHSAInfo(anyString());

        JsonNode doc = new ObjectMapper().readTree(JSONSource.readTemplateAsString(DocumentHelper.IntygVersion.VERSION1));

        //When
        hsaDecoratorSpy.decorate(doc, "aaa");

        //Then
        verify(hsaService, times(1)).getHSAInfo(any(HSAKey.class), any(JsonNode.class));
        verify(hsaDecoratorSpy, times(1)).storeHSAInfo(eq("aaa"), any(JsonNode.class));
    }

    @Test
    public void serviceOnlyCalledWhenCacheIsMissingVardgivareData() throws IOException {
        //Given
        HSADecorator hsaDecoratorSpy = Mockito.spy(this.hsaDecorator);
        ObjectNode jsonNode = getFullJsonNode();
        jsonNode.remove(HSAService.HSA_INFO_VARDGIVARE);
        Mockito.doReturn(jsonNode).when(hsaDecoratorSpy).getHSAInfo(anyString());

        JsonNode doc = new ObjectMapper().readTree(JSONSource.readTemplateAsString(DocumentHelper.IntygVersion.VERSION1));

        //When
        hsaDecoratorSpy.decorate(doc, "aaa");

        //Then
        verify(hsaService, times(1)).getHSAInfo(any(HSAKey.class), any(JsonNode.class));
        verify(hsaDecoratorSpy, times(1)).storeHSAInfo(eq("aaa"), any(JsonNode.class));
    }

    @Test
    public void serviceOnlyCalledWhenCacheIsMissingPersonalData() throws IOException {
        //Given
        HSADecorator hsaDecoratorSpy = Mockito.spy(this.hsaDecorator);
        ObjectNode jsonNode = getFullJsonNode();
        jsonNode.remove(HSAService.HSA_INFO_PERSONAL);
        Mockito.doReturn(jsonNode).when(hsaDecoratorSpy).getHSAInfo(anyString());

        JsonNode doc = new ObjectMapper().readTree(JSONSource.readTemplateAsString(DocumentHelper.IntygVersion.VERSION1));

        //When
        hsaDecoratorSpy.decorate(doc, "aaa");

        //Then
        verify(hsaService, times(1)).getHSAInfo(any(HSAKey.class), any(JsonNode.class));
        verify(hsaDecoratorSpy, times(1)).storeHSAInfo(eq("aaa"), any(JsonNode.class));
    }

    @Test
    public void serviceNotCalledWhenCacheFullyPopulated() throws IOException {
        //Given
        HSADecorator hsaDecoratorSpy = Mockito.spy(this.hsaDecorator);
        ObjectNode jsonNode = getFullJsonNode();
        Mockito.doReturn(jsonNode).when(hsaDecoratorSpy).getHSAInfo(anyString());

        JsonNode doc = new ObjectMapper().readTree(JSONSource.readTemplateAsString(DocumentHelper.IntygVersion.VERSION1));

        //When
        hsaDecoratorSpy.decorate(doc, "aaa");

        //Then
        verify(hsaService, times(0)).getHSAInfo(any(HSAKey.class), any(JsonNode.class));
        verify(hsaDecoratorSpy, times(0)).storeHSAInfo(eq("aaa"), any(JsonNode.class));
    }

}
