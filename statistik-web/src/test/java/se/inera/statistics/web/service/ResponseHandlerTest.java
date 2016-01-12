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
package se.inera.statistics.web.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.service.report.model.Icd;
import se.inera.statistics.service.report.util.Icd10;
import se.inera.statistics.web.model.SimpleDetailsData;

import javax.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class ResponseHandlerTest {

    @InjectMocks
    private ResponseHandler responseHandler;

    @Mock
    private Icd10 icd10;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetResponseWithNullData() throws Exception {
        //When
        final Response response = responseHandler.getResponse(new SimpleDetailsData(null, null, null, null), null, null);

        //Then
        assertTrue((Boolean)((Map)response.getEntity()).get("empty"));
        assertTrue((Boolean)((Map)response.getEntity()).get(ResponseHandler.ALL_AVAILABLE_DXS_SELECTED_IN_FILTER));
        assertTrue((Boolean)((Map)response.getEntity()).get(ResponseHandler.ALL_AVAILABLE_ENHETS_SELECTED_IN_FILTER));
    }

    @Test
    public void testGetResponseWithNullResult() throws Exception {
        //When
        final Response response = responseHandler.getResponse(null, null, null);

        //Then
        assertTrue((Boolean)((Map)response.getEntity()).get(ResponseHandler.ALL_AVAILABLE_DXS_SELECTED_IN_FILTER));
        assertTrue((Boolean)((Map)response.getEntity()).get(ResponseHandler.ALL_AVAILABLE_ENHETS_SELECTED_IN_FILTER));
    }

    @Test
    public void testGetResponseAllDxsEqual() throws Exception {
        //Given
        Mockito.when(icd10.getIcdStructure()).thenReturn(createIcdList(1, 2, 3));

        //When
        final Response response = responseHandler.getResponse(new SimpleDetailsData(null, null, null, new FilterDataResponse(null, Arrays.asList("1", "2", "3"), null)), null, null);

        //Then
        assertTrue((Boolean)((Map)response.getEntity()).get(ResponseHandler.ALL_AVAILABLE_DXS_SELECTED_IN_FILTER));
    }

    @Test
    public void testGetResponseDifferentDxsOrder() throws Exception {
        //Given
        Mockito.when(icd10.getIcdStructure()).thenReturn(createIcdList(1, 2, 3));

        //When
        final Response response = responseHandler.getResponse(new SimpleDetailsData(null, null, null, new FilterDataResponse(null, Arrays.asList("2", "1", "3"), null)), null, null);

        //Then
        assertTrue((Boolean)((Map)response.getEntity()).get(ResponseHandler.ALL_AVAILABLE_DXS_SELECTED_IN_FILTER));
    }

    @Test
    public void testGetResponseMissingDxInFilter() throws Exception {
        //Given
        Mockito.when(icd10.getIcdStructure()).thenReturn(createIcdList(1, 2, 3));

        //When
        final Response response = responseHandler.getResponse(new SimpleDetailsData(null, null, null, new FilterDataResponse(null, Arrays.asList("1", "2"), null)), null, null);

        //Then
        assertFalse((Boolean)((Map)response.getEntity()).get(ResponseHandler.ALL_AVAILABLE_DXS_SELECTED_IN_FILTER));
    }

    private List<Icd> createIcdList(int... ids) {
        final List<Icd> icds = new ArrayList<>();
        for (Integer id : ids) {
            final String strId = String.valueOf(id);
            icds.add(new Icd(strId, strId, id));
        }
        return icds;
    }

    @Test
    public void testGetResponseAllEnhetsEqual() throws Exception {
        //Given
        final List<HsaIdEnhet> enhets = Arrays.asList(new HsaIdEnhet("1"), new HsaIdEnhet("2"), new HsaIdEnhet("3"));

        //When
        final Response response = responseHandler.getResponse(new SimpleDetailsData(null, null, null, new FilterDataResponse(null, null, enhets)), null, enhets);

        //Then
        assertTrue((Boolean)((Map)response.getEntity()).get(ResponseHandler.ALL_AVAILABLE_ENHETS_SELECTED_IN_FILTER));
    }

    @Test
    public void testGetResponseDifferentEnhetsOrder() throws Exception {
        //Given
        final List<HsaIdEnhet> availableEnhets = Arrays.asList(new HsaIdEnhet("1"), new HsaIdEnhet("2"), new HsaIdEnhet("3"));
        final List<HsaIdEnhet> filteredEnhets = Arrays.asList(new HsaIdEnhet("1"), new HsaIdEnhet("3"), new HsaIdEnhet("2"));

        //When
        final Response response = responseHandler.getResponse(new SimpleDetailsData(null, null, null, new FilterDataResponse(null, null, filteredEnhets)), null, availableEnhets);

        //Then
        assertTrue((Boolean)((Map)response.getEntity()).get(ResponseHandler.ALL_AVAILABLE_ENHETS_SELECTED_IN_FILTER));
    }

    @Test
    public void testGetResponseMissingEnhetInFilter() throws Exception {
        //Given
        final List<HsaIdEnhet> availableEnhets = Arrays.asList(new HsaIdEnhet("1"), new HsaIdEnhet("2"), new HsaIdEnhet("3"));
        final List<HsaIdEnhet> filteredEnhets = Arrays.asList(new HsaIdEnhet("1"), new HsaIdEnhet("2"));

        //When
        final Response response = responseHandler.getResponse(new SimpleDetailsData(null, null, null, new FilterDataResponse(null, null, filteredEnhets)), null, availableEnhets);

        //Then
        assertFalse((Boolean)((Map)response.getEntity()).get(ResponseHandler.ALL_AVAILABLE_ENHETS_SELECTED_IN_FILTER));
    }

}
