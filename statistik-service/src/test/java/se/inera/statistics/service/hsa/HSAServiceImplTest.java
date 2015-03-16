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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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
    public void hsUnitWithMissingGeography() throws Exception {
        GetStatisticsHsaUnitResponseType response = getHsaUnitResponse("GetStatisticsHsaUnit-small.xml");
        when(wsCalls.getStatisticsHsaUnit("enhetId")).thenReturn(response);

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
        when(wsCalls.getStatisticsHsaUnit("enhetId")).thenThrow(new IllegalStateException("This WS generated and exception"));
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
}
