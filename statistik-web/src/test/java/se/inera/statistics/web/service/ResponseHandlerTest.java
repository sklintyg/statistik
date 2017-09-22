/*
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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
package se.inera.statistics.web.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.processlog.Enhet;
import se.inera.statistics.service.report.model.Icd;
import se.inera.statistics.service.report.util.AgeGroup;
import se.inera.statistics.service.report.util.Icd10;
import se.inera.statistics.service.report.util.SjukfallsLangdGroup;
import se.inera.statistics.service.warehouse.Warehouse;
import se.inera.statistics.web.error.Message;
import se.inera.statistics.web.model.FilteredDataReport;
import se.inera.statistics.web.model.SimpleDetailsData;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;

public class ResponseHandlerTest {

    @InjectMocks
    private ResponseHandler responseHandler;

    @Mock
    private Icd10 icd10;

    @Mock
    private Warehouse warehouse;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetResponseWithNullData() throws Exception {
        //When
        final Response response = responseHandler.getResponse(new SimpleDetailsData(null, null, null, null), null, null, Report.V_ALDERSGRUPP);

        //Then
        assertTrue((Boolean)((Map)response.getEntity()).get("empty"));
        assertTrue((Boolean)((Map)response.getEntity()).get(ResponseHandler.ALL_AVAILABLE_DXS_SELECTED_IN_FILTER));
        assertTrue((Boolean)((Map)response.getEntity()).get(ResponseHandler.ALL_AVAILABLE_ENHETS_SELECTED_IN_FILTER));
    }

    @Test
    public void testGetResponseWithNullResult() throws Exception {
        //When
        final Response response = responseHandler.getResponse(null, null, null, Report.V_ALDERSGRUPP);

        //Then
        assertTrue((Boolean)((Map)response.getEntity()).get(ResponseHandler.ALL_AVAILABLE_DXS_SELECTED_IN_FILTER));
        assertTrue((Boolean)((Map)response.getEntity()).get(ResponseHandler.ALL_AVAILABLE_ENHETS_SELECTED_IN_FILTER));
    }

    @Test
    public void testGetResponseAllDxsEqual() throws Exception {
        //Given
        Mockito.when(icd10.getIcdStructure()).thenReturn(createIcdList(1, 2, 3));

        //When
        final Response response = responseHandler.getResponse(new SimpleDetailsData(null, null, null, new FilterDataResponse(null, Arrays.asList("1", "2", "3"), null, null, null)), null, null, Report.V_ALDERSGRUPP);

        //Then
        assertTrue((Boolean)((Map)response.getEntity()).get(ResponseHandler.ALL_AVAILABLE_DXS_SELECTED_IN_FILTER));
    }

    @Test
    public void testGetResponseDifferentDxsOrder() throws Exception {
        //Given
        Mockito.when(icd10.getIcdStructure()).thenReturn(createIcdList(1, 2, 3));

        //When
        final Response response = responseHandler.getResponse(new SimpleDetailsData(null, null, null, new FilterDataResponse(null, Arrays.asList("2", "1", "3"), null, null, null)), null, null, Report.V_ALDERSGRUPP);

        //Then
        assertTrue((Boolean)((Map)response.getEntity()).get(ResponseHandler.ALL_AVAILABLE_DXS_SELECTED_IN_FILTER));
    }

    @Test
    public void testGetResponseMissingDxInFilter() throws Exception {
        //Given
        Mockito.when(icd10.getIcdStructure()).thenReturn(createIcdList(1, 2, 3));

        //When
        final Response response = responseHandler.getResponse(new SimpleDetailsData(null, null, null, new FilterDataResponse(null, Arrays.asList("1", "2"), null, null, null)), null, null, Report.V_ALDERSGRUPP);

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
        final Response response = responseHandler.getResponse(new SimpleDetailsData(null, null, null, new FilterDataResponse(null, null, enhets, null, null)), null, enhets, Report.V_ALDERSGRUPP);

        //Then
        assertTrue((Boolean)((Map)response.getEntity()).get(ResponseHandler.ALL_AVAILABLE_ENHETS_SELECTED_IN_FILTER));
    }

    @Test
    public void testGetResponseDifferentEnhetsOrder() throws Exception {
        //Given
        final List<HsaIdEnhet> availableEnhets = Arrays.asList(new HsaIdEnhet("1"), new HsaIdEnhet("2"), new HsaIdEnhet("3"));
        final List<HsaIdEnhet> filteredEnhets = Arrays.asList(new HsaIdEnhet("1"), new HsaIdEnhet("3"), new HsaIdEnhet("2"));

        //When
        final Response response = responseHandler.getResponse(new SimpleDetailsData(null, null, null, new FilterDataResponse(null, null, filteredEnhets, null, null)), null, availableEnhets, Report.V_ALDERSGRUPP);

        //Then
        assertTrue((Boolean)((Map)response.getEntity()).get(ResponseHandler.ALL_AVAILABLE_ENHETS_SELECTED_IN_FILTER));
    }

    @Test
    public void testGetResponseMissingEnhetInFilter() throws Exception {
        //Given
        final List<HsaIdEnhet> availableEnhets = Arrays.asList(new HsaIdEnhet("1"), new HsaIdEnhet("2"), new HsaIdEnhet("3"));
        final List<HsaIdEnhet> filteredEnhets = Arrays.asList(new HsaIdEnhet("1"), new HsaIdEnhet("2"));

        //When
        final Response response = responseHandler.getResponse(new SimpleDetailsData(null, null, null, new FilterDataResponse(null, null, filteredEnhets, null, null)), null, availableEnhets, Report.V_ALDERSGRUPP);

        //Then
        assertFalse((Boolean)((Map)response.getEntity()).get(ResponseHandler.ALL_AVAILABLE_ENHETS_SELECTED_IN_FILTER));
    }

    // CHECKSTYLE:OFF
    @Test
    public void testGetFilterEnhetnamnMissingEnhet() throws Exception {
        //Given
        final String filterHash = "abc";
        final List<HsaIdEnhet> enhets = Arrays.asList(new HsaIdEnhet("1"), new HsaIdEnhet("2"));
        Mockito.doReturn(Collections.singletonList(createEnhet("1", "ett"))).when(warehouse).getEnhetsWithHsaId(any(Collection.class));
        final FilteredDataReport report = new FilteredDataReport() {
            @Override
            public FilterDataResponse getFilter() {
                return new FilterDataResponse(filterHash, Collections.emptyList(), enhets, null, null);
            }

            @Override
            public List<Message> getMessages() {
                return Collections.emptyList();
            }

            @Override
            public boolean isEmpty() {
                return false;
            }
        };

        //When
        final Response response = responseHandler.getResponseForDataReport(report, new ArrayList<>());

        //Then
        final Map<String, Object> entity = (Map<String, Object>) response.getEntity();
        final List<String> enhetsNames = (List<String>) entity.get(ResponseHandler.FILTERED_ENHETS);
        assertEquals(1, enhetsNames.size());
        assertEquals("ett", enhetsNames.get(0));
    }

    @Test
    public void testGetFilterEnhetnamnIdIsAddedToNameWhenDuplicate() throws Exception {
        //Given
        final String filterHash = "abc";
        final List<HsaIdEnhet> enhets = Arrays.asList(new HsaIdEnhet("1"), new HsaIdEnhet("2"));
        Mockito.doReturn(Arrays.asList(createEnhet("1", "ett"), createEnhet("2", "ett"))).when(warehouse).getEnhetsWithHsaId(any(Collection.class));
        final FilteredDataReport report = new FilteredDataReport() {
            @Override
            public FilterDataResponse getFilter() {
                return new FilterDataResponse(filterHash, Collections.emptyList(), enhets, null, null);
            }

            @Override
            public List<Message> getMessages() {
                return Collections.emptyList();
            }

            @Override
            public boolean isEmpty() {
                return false;
            }
        };

        //When
        final Response response = responseHandler.getResponseForDataReport(report, new ArrayList<>());

        //Then
        final Map<String, Object> entity = (Map<String, Object>) response.getEntity();
        final List<String> enhetsNames = (List<String>) entity.get(ResponseHandler.FILTERED_ENHETS);
        assertEquals(2, enhetsNames.size());
        assertEquals("ett 1", enhetsNames.get(0));
        assertEquals("ett 2", enhetsNames.get(1));
    }
    // CHECKSTYLE:ON

    private Enhet createEnhet(String id, String namn) {
        return new Enhet(new HsaIdVardgivare(""), new HsaIdEnhet(id), namn, null, null, null);
    }


    @Test
    public void testGetResponseAllSjukskrivningslangdsEqual() throws Exception {
        //Given
        final List<String> sjukskrivningslangds = Arrays.stream(SjukfallsLangdGroup.values()).map(SjukfallsLangdGroup::getGroupName).collect(Collectors.toList());

        //When
        final Response response = responseHandler.getResponse(new SimpleDetailsData(null, null, null, new FilterDataResponse(null, null, null, sjukskrivningslangds, null)), null, null, Report.V_ALDERSGRUPP);

        //Then
        assertTrue((Boolean)((Map)response.getEntity()).get(ResponseHandler.ALL_AVAILABLE_SJUKSKRIVNINGSLANGDS_SELECTED_IN_FILTER));
    }

    @Test
    public void testGetResponseDifferentSjukskrivningslangdsOrder() throws Exception {
        //Given
        final List<String> sjukskrivningslangds = Arrays.stream(SjukfallsLangdGroup.values()).map(SjukfallsLangdGroup::getGroupName).collect(Collectors.toList());
        Collections.shuffle(sjukskrivningslangds);

        //When
        final Response response = responseHandler.getResponse(new SimpleDetailsData(null, null, null, new FilterDataResponse(null, null, null, sjukskrivningslangds, null)), null, null, Report.V_ALDERSGRUPP);

        //Then
        assertTrue((Boolean)((Map)response.getEntity()).get(ResponseHandler.ALL_AVAILABLE_SJUKSKRIVNINGSLANGDS_SELECTED_IN_FILTER));
    }

    @Test
    public void testGetResponseMissingSjukskrivningslangdInFilter() throws Exception {
        //Given
        final List<String> sjukskrivningslangds = Arrays.stream(SjukfallsLangdGroup.values()).map(SjukfallsLangdGroup::getGroupName).collect(Collectors.toList());

        //When
        final Response response = responseHandler.getResponse(new SimpleDetailsData(null, null, null, new FilterDataResponse(null, null, null, sjukskrivningslangds.subList(0, sjukskrivningslangds.size() - 1), null)), null, null, Report.V_ALDERSGRUPP);

        //Then
        assertFalse((Boolean)((Map)response.getEntity()).get(ResponseHandler.ALL_AVAILABLE_SJUKSKRIVNINGSLANGDS_SELECTED_IN_FILTER));
    }

    @Test
    public void testGetResponseAllAldersgruppEqual() throws Exception {
        //Given
        final List<String> ageGroups = Arrays.stream(AgeGroup.values()).map(AgeGroup::getGroupName).collect(Collectors.toList());

        //When
        final Response response = responseHandler.getResponse(new SimpleDetailsData(null, null, null, new FilterDataResponse(null, null, null, null, ageGroups)), null, null, Report.V_ALDERSGRUPP);

        //Then
        assertTrue((Boolean)((Map)response.getEntity()).get(ResponseHandler.ALL_AVAILABLE_AGEGROUPS_SELECTED_IN_FILTER));
    }

    @Test
    public void testGetResponseDifferentAldersgruppOrder() throws Exception {
        //Given
        final List<String> ageGroups = Arrays.stream(AgeGroup.values()).map(AgeGroup::getGroupName).collect(Collectors.toList());
        Collections.shuffle(ageGroups);

        //When
        final Response response = responseHandler.getResponse(new SimpleDetailsData(null, null, null, new FilterDataResponse(null, null, null, null, ageGroups)), null, null, Report.V_ALDERSGRUPP);

        //Then
        assertTrue((Boolean)((Map)response.getEntity()).get(ResponseHandler.ALL_AVAILABLE_AGEGROUPS_SELECTED_IN_FILTER));
    }

    @Test
    public void testGetResponseMissingAldersgruppInFilter() throws Exception {
        //Given
        final List<String> ageGroups = Arrays.stream(AgeGroup.values()).map(AgeGroup::getGroupName).collect(Collectors.toList());

        //When
        final Response response = responseHandler.getResponse(new SimpleDetailsData(null, null, null, new FilterDataResponse(null, null, null, null, ageGroups.subList(0, ageGroups.size() - 1))), null, null, Report.V_ALDERSGRUPP);

        //Then
        assertFalse((Boolean)((Map)response.getEntity()).get(ResponseHandler.ALL_AVAILABLE_AGEGROUPS_SELECTED_IN_FILTER));
    }

}
