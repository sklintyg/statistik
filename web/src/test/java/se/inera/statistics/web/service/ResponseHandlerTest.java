/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.ws.rs.core.Response;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.processlog.Enhet;
import se.inera.statistics.service.report.model.AvailableFilters;
import se.inera.statistics.service.report.model.Icd;
import se.inera.statistics.service.report.util.AgeGroup;
import se.inera.statistics.service.report.util.Icd10;
import se.inera.statistics.service.report.util.SjukfallsLangdGroup;
import se.inera.statistics.service.warehouse.IntygType;
import se.inera.statistics.service.warehouse.Warehouse;
import se.inera.statistics.web.MessagesText;
import se.inera.statistics.web.error.ErrorSeverity;
import se.inera.statistics.web.error.ErrorType;
import se.inera.statistics.web.error.Message;
import se.inera.statistics.web.model.ChartData;
import se.inera.statistics.web.model.FilteredDataReport;
import se.inera.statistics.web.model.SimpleDetailsData;
import se.inera.statistics.web.model.TableData;

public class ResponseHandlerTest {

    @InjectMocks
    private ResponseHandler responseHandler;

    @Mock
    private Icd10 icd10;

    @Mock
    private Warehouse warehouse;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetResponseWithNullData() {
        //When
        final Response response = responseHandler
            .getResponse(new SimpleDetailsData(null, null, null, AvailableFilters.getForSjukfall(), null), null, null,
                new ReportInfo(Report.V_ALDERSGRUPP, ReportType.TVARSNITT));

        //Then
        assertTrue((Boolean) ((Map) response.getEntity()).get("empty"));
        assertTrue((Boolean) ((Map) response.getEntity()).get(ResponseKeys.ALL_AVAILABLE_DXS_SELECTED_IN_FILTER));
        assertTrue((Boolean) ((Map) response.getEntity()).get(ResponseKeys.ALL_AVAILABLE_ENHETS_SELECTED_IN_FILTER));
    }

    @Test
    public void testGetResponseWithNullResult() {
        //When
        final Response response = responseHandler.getResponse(null, null, null, new ReportInfo(Report.V_ALDERSGRUPP, ReportType.TVARSNITT));

        //Then
        assertTrue((Boolean) ((Map) response.getEntity()).get(ResponseKeys.ALL_AVAILABLE_DXS_SELECTED_IN_FILTER));
        assertTrue((Boolean) ((Map) response.getEntity()).get(ResponseKeys.ALL_AVAILABLE_ENHETS_SELECTED_IN_FILTER));
    }

    @Test
    public void testGetResponseAllDxsEqual() {
        //Given
        Mockito.when(icd10.getIcdStructure()).thenReturn(createIcdList(1, 2, 3));
        FilterDataResponse filterDataResponse = new FilterDataResponse(null, Arrays.asList("1", "2", "3"), null, null, null, null, true);

        //When
        final Response response = responseHandler
            .getResponse(new SimpleDetailsData(null, null, null, AvailableFilters.getForSjukfall(), filterDataResponse), null, null,
                new ReportInfo(Report.V_ALDERSGRUPP, ReportType.TVARSNITT));

        //Then
        assertTrue((Boolean) ((Map) response.getEntity()).get(ResponseKeys.ALL_AVAILABLE_DXS_SELECTED_IN_FILTER));
        validateNoDataNoFilterMessage(response);
    }

    @Test
    public void testGetResponseDifferentDxsOrder() {
        //Given
        Mockito.when(icd10.getIcdStructure()).thenReturn(createIcdList(1, 2, 3));
        FilterDataResponse filterDataResponse = new FilterDataResponse(null, Arrays.asList("2", "1", "3"), null, null, null, null, true);

        //When
        final Response response = responseHandler
            .getResponse(new SimpleDetailsData(null, null, null, AvailableFilters.getForSjukfall(), filterDataResponse), null, null,
                new ReportInfo(Report.V_ALDERSGRUPP, ReportType.TVARSNITT));

        //Then
        assertTrue((Boolean) ((Map) response.getEntity()).get(ResponseKeys.ALL_AVAILABLE_DXS_SELECTED_IN_FILTER));
        validateNoDataNoFilterMessage(response);
    }

    @Test
    public void testGetResponseMissingDxInFilter() {
        //Given
        Mockito.when(icd10.getIcdStructure()).thenReturn(createIcdList(1, 2, 3));
        FilterDataResponse filterDataResponse = new FilterDataResponse(null, Arrays.asList("1", "2"), null, null, null, null, true);

        //When
        final Response response = responseHandler
            .getResponse(new SimpleDetailsData(null, null, null, AvailableFilters.getForSjukfall(), filterDataResponse), null, null,
                new ReportInfo(Report.V_ALDERSGRUPP, ReportType.TVARSNITT));

        //Then
        assertFalse((Boolean) ((Map) response.getEntity()).get(ResponseKeys.ALL_AVAILABLE_DXS_SELECTED_IN_FILTER));
        validateNoDataFilterMessage(response);
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
    public void testGetResponseAllEnhetsEqual() {
        //Given
        final List<HsaIdEnhet> enhets = Arrays.asList(new HsaIdEnhet("1"), new HsaIdEnhet("2"), new HsaIdEnhet("3"));
        FilterDataResponse filterDataResponse = new FilterDataResponse(null, null, enhets, null, null, null, true);

        //When
        final Response response = responseHandler
            .getResponse(new SimpleDetailsData(null, null, null, AvailableFilters.getForSjukfall(), filterDataResponse), null, enhets,
                new ReportInfo(Report.V_ALDERSGRUPP, ReportType.TVARSNITT));

        //Then
        assertTrue((Boolean) ((Map) response.getEntity()).get(ResponseKeys.ALL_AVAILABLE_ENHETS_SELECTED_IN_FILTER));
        validateNoDataNoFilterMessage(response);
    }

    @Test
    public void testGetResponseDifferentEnhetsOrder() {
        //Given
        final List<HsaIdEnhet> availableEnhets = Arrays.asList(new HsaIdEnhet("1"), new HsaIdEnhet("2"), new HsaIdEnhet("3"));
        final List<HsaIdEnhet> filteredEnhets = Arrays.asList(new HsaIdEnhet("1"), new HsaIdEnhet("3"), new HsaIdEnhet("2"));
        FilterDataResponse filterDataResponse = new FilterDataResponse(null, null, filteredEnhets, null, null, null, true);

        //When
        final Response response = responseHandler
            .getResponse(new SimpleDetailsData(null, null, null, AvailableFilters.getForSjukfall(), filterDataResponse), null,
                availableEnhets, new ReportInfo(Report.V_ALDERSGRUPP, ReportType.TVARSNITT));

        //Then
        assertTrue((Boolean) ((Map) response.getEntity()).get(ResponseKeys.ALL_AVAILABLE_ENHETS_SELECTED_IN_FILTER));
        validateNoDataNoFilterMessage(response);
    }

    @Test
    public void testGetResponseMissingEnhetInFilter() {
        //Given
        final List<HsaIdEnhet> availableEnhets = Arrays.asList(new HsaIdEnhet("1"), new HsaIdEnhet("2"), new HsaIdEnhet("3"));
        final List<HsaIdEnhet> filteredEnhets = Arrays.asList(new HsaIdEnhet("1"), new HsaIdEnhet("2"));
        FilterDataResponse filterDataResponse = new FilterDataResponse(null, null, filteredEnhets, null, null, null, true);

        //When
        final Response response = responseHandler
            .getResponse(new SimpleDetailsData(null, null, null, AvailableFilters.getForSjukfall(), filterDataResponse), null,
                availableEnhets, new ReportInfo(Report.V_ALDERSGRUPP, ReportType.TVARSNITT));

        //Then
        assertFalse((Boolean) ((Map) response.getEntity()).get(ResponseKeys.ALL_AVAILABLE_ENHETS_SELECTED_IN_FILTER));
        validateNoDataFilterMessage(response);
    }

    // CHECKSTYLE:OFF
    @Test
    public void testGetFilterEnhetnamnMissingEnhet() {
        //Given
        final String filterHash = "abc";
        final List<HsaIdEnhet> enhets = Arrays.asList(new HsaIdEnhet("1"), new HsaIdEnhet("2"));
        Mockito.doReturn(Collections.singletonList(createEnhet("1", "ett"))).when(warehouse).getEnhetsWithHsaId(any(Collection.class));
        final FilteredDataReport report = new FilteredDataReport() {
            @Override
            public FilterDataResponse getFilter() {
                return new FilterDataResponse(filterHash, Collections.emptyList(), enhets, null, null, null, true);
            }

            @Override
            public List<Message> getMessages() {
                return Collections.emptyList();
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public AvailableFilters getAvailableFilters() {
                return null;
            }
        };

        //When
        final Response response = responseHandler.getResponseForDataReport(report, new ArrayList<>());

        //Then
        final Map<String, Object> entity = (Map<String, Object>) response.getEntity();
        final List<String> enhetsNames = (List<String>) entity.get(ResponseKeys.FILTERED_ENHETS);
        assertEquals(2, enhetsNames.size());
        assertEquals("2", enhetsNames.get(0));
        assertEquals("ett", enhetsNames.get(1));
    }

    @Test
    public void testGetFilterEnhetnamnIdIsAddedToNameWhenDuplicate() {
        //Given
        final String filterHash = "abc";
        final List<HsaIdEnhet> enhets = Arrays.asList(new HsaIdEnhet("1"), new HsaIdEnhet("2"));
        Mockito.doReturn(Arrays.asList(createEnhet("1", "ett"), createEnhet("2", "ett"))).when(warehouse)
            .getEnhetsWithHsaId(any(Collection.class));
        final FilteredDataReport report = new FilteredDataReport() {
            @Override
            public FilterDataResponse getFilter() {
                return new FilterDataResponse(filterHash, Collections.emptyList(), enhets, null, null, null, true);
            }

            @Override
            public List<Message> getMessages() {
                return Collections.emptyList();
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public AvailableFilters getAvailableFilters() {
                return null;
            }
        };

        //When
        final Response response = responseHandler.getResponseForDataReport(report, new ArrayList<>());

        //Then
        final Map<String, Object> entity = (Map<String, Object>) response.getEntity();
        final List<String> enhetsNames = (List<String>) entity.get(ResponseKeys.FILTERED_ENHETS);
        assertEquals(2, enhetsNames.size());
        assertEquals("ett 1", enhetsNames.get(0));
        assertEquals("ett 2", enhetsNames.get(1));
    }
    // CHECKSTYLE:ON

    private Enhet createEnhet(String id, String namn) {
        return new Enhet(new HsaIdVardgivare(""), new HsaIdEnhet(id), namn, null, null, null, "ve" + id);
    }


    @Test
    public void testGetResponseAllSjukskrivningslangdsEqual() {
        //Given
        final List<String> sjukskrivningslangds = Arrays.stream(SjukfallsLangdGroup.values()).map(SjukfallsLangdGroup::getGroupName)
            .collect(Collectors.toList());
        FilterDataResponse filterDataResponse = new FilterDataResponse(null, null, null, sjukskrivningslangds, null, null, true);

        //When
        final Response response = responseHandler
            .getResponse(new SimpleDetailsData(null, null, null, AvailableFilters.getForSjukfall(), filterDataResponse), null, null,
                new ReportInfo(Report.V_ALDERSGRUPP, ReportType.TVARSNITT));

        //Then
        assertTrue((Boolean) ((Map) response.getEntity()).get(ResponseKeys.ALL_AVAILABLE_SJUKSKRIVNINGSLANGDS_SELECTED_IN_FILTER));
        validateNoDataNoFilterMessage(response);
    }

    @Test
    public void testGetResponseDifferentSjukskrivningslangdsOrder() {
        //Given
        final List<String> sjukskrivningslangds = Arrays.stream(SjukfallsLangdGroup.values()).map(SjukfallsLangdGroup::getGroupName)
            .collect(Collectors.toList());
        Collections.shuffle(sjukskrivningslangds);
        FilterDataResponse filterDataResponse = new FilterDataResponse(null, null, null, sjukskrivningslangds, null, null, true);

        //When
        final Response response = responseHandler
            .getResponse(new SimpleDetailsData(null, null, null, AvailableFilters.getForSjukfall(), filterDataResponse), null, null,
                new ReportInfo(Report.V_ALDERSGRUPP, ReportType.TVARSNITT));

        //Then
        assertTrue((Boolean) ((Map) response.getEntity()).get(ResponseKeys.ALL_AVAILABLE_SJUKSKRIVNINGSLANGDS_SELECTED_IN_FILTER));
        validateNoDataNoFilterMessage(response);
    }

    @Test
    public void testGetResponseMissingSjukskrivningslangdInFilter() {
        //Given
        final List<String> sjukskrivningslangds = Arrays.stream(SjukfallsLangdGroup.values()).map(SjukfallsLangdGroup::getGroupName)
            .collect(Collectors.toList());
        FilterDataResponse filterDataResponse = new FilterDataResponse(null, null, null,
            sjukskrivningslangds.subList(0, sjukskrivningslangds.size() - 1), null, null, true);

        //When
        final Response response = responseHandler
            .getResponse(new SimpleDetailsData(null, null, null, AvailableFilters.getForSjukfall(), filterDataResponse), null, null,
                new ReportInfo(Report.V_ALDERSGRUPP, ReportType.TVARSNITT));

        //Then
        assertFalse((Boolean) ((Map) response.getEntity()).get(ResponseKeys.ALL_AVAILABLE_SJUKSKRIVNINGSLANGDS_SELECTED_IN_FILTER));
        validateNoDataFilterMessage(response);
    }

    @Test
    public void testGetResponseAllAldersgruppEqual() {
        //Given
        final List<String> ageGroups = Arrays.stream(AgeGroup.values()).map(AgeGroup::getGroupName).collect(Collectors.toList());
        FilterDataResponse filterDataResponse = new FilterDataResponse(null, null, null, null, ageGroups, null, true);

        //When
        final Response response = responseHandler
            .getResponse(new SimpleDetailsData(null, null, null, AvailableFilters.getForSjukfall(), filterDataResponse), null, null,
                new ReportInfo(Report.V_ALDERSGRUPP, ReportType.TVARSNITT));

        //Then
        assertTrue((Boolean) ((Map) response.getEntity()).get(ResponseKeys.ALL_AVAILABLE_AGEGROUPS_SELECTED_IN_FILTER));
        validateNoDataNoFilterMessage(response);
    }

    @Test
    public void testGetResponseDifferentAldersgruppOrder() {
        //Given
        final List<String> ageGroups = Arrays.stream(AgeGroup.values()).map(AgeGroup::getGroupName).collect(Collectors.toList());
        Collections.shuffle(ageGroups);
        FilterDataResponse filterDataResponse = new FilterDataResponse(null, null, null, null, ageGroups, null, true);

        //When
        final Response response = responseHandler
            .getResponse(new SimpleDetailsData(null, null, null, AvailableFilters.getForSjukfall(), filterDataResponse), null, null,
                new ReportInfo(Report.V_ALDERSGRUPP, ReportType.TVARSNITT));

        //Then
        assertTrue((Boolean) ((Map) response.getEntity()).get(ResponseKeys.ALL_AVAILABLE_AGEGROUPS_SELECTED_IN_FILTER));
        validateNoDataNoFilterMessage(response);
    }

    @Test
    public void testGetResponseMissingAldersgruppInFilter() {
        //Given
        final List<String> ageGroups = Arrays.stream(AgeGroup.values()).map(AgeGroup::getGroupName).collect(Collectors.toList());
        FilterDataResponse filterDataResponse = new FilterDataResponse(null, null, null, null, ageGroups.subList(0, ageGroups.size() - 1),
            null, true);

        //When
        final Response response = responseHandler
            .getResponse(new SimpleDetailsData(null, null, null, AvailableFilters.getForSjukfall(), filterDataResponse), null, null,
                new ReportInfo(Report.V_ALDERSGRUPP, ReportType.TVARSNITT));

        //Then
        assertFalse((Boolean) ((Map) response.getEntity()).get(ResponseKeys.ALL_AVAILABLE_AGEGROUPS_SELECTED_IN_FILTER));
        validateNoDataFilterMessage(response);
    }

    @Test
    public void testGetResponseAllIntygstyperEqual() {
        //Given
        final List<String> intygstyper = IntygType.getInIntygtypFilter().stream().map(IntygType::getText).collect(Collectors.toList());
        FilterDataResponse filterDataResponse = new FilterDataResponse(null, null, null, null, null, intygstyper, true);

        //When
        final Response response = responseHandler
            .getResponse(new SimpleDetailsData(null, null, null, AvailableFilters.getForSjukfall(), filterDataResponse), null, null,
                new ReportInfo(Report.V_ALDERSGRUPP, ReportType.TVARSNITT));

        //Then
        assertTrue((Boolean) ((Map) response.getEntity()).get(ResponseKeys.ALL_AVAILABLE_INTYGTYPES_SELECTED_IN_FILTER));
        validateNoDataNoFilterMessage(response);
    }

    @Test
    public void testGetResponseDifferentIntygstyperOrder() {
        //Given
        final List<String> intygstyper = IntygType.getInIntygtypFilter().stream().map(IntygType::getText).collect(Collectors.toList());
        Collections.shuffle(intygstyper);
        FilterDataResponse filterDataResponse = new FilterDataResponse(null, null, null, null, null, intygstyper, true);

        //When
        final Response response = responseHandler
            .getResponse(new SimpleDetailsData(null, null, null, AvailableFilters.getForSjukfall(), filterDataResponse), null, null,
                new ReportInfo(Report.V_ALDERSGRUPP, ReportType.TVARSNITT));

        //Then
        assertTrue((Boolean) ((Map) response.getEntity()).get(ResponseKeys.ALL_AVAILABLE_INTYGTYPES_SELECTED_IN_FILTER));
        validateNoDataNoFilterMessage(response);
    }

    @Test
    public void testGetResponseMissingIntygstyperInFilter() {
        //Given
        final List<String> intygstyper = IntygType.getInIntygtypFilter().stream().map(IntygType::getText).collect(Collectors.toList());
        FilterDataResponse filterDataResponse = new FilterDataResponse(null, null, null, null, null,
            intygstyper.subList(0, intygstyper.size() - 1), true);

        //When
        final Response response = responseHandler
            .getResponse(new SimpleDetailsData(null, null, null, AvailableFilters.getForSjukfall(), filterDataResponse), null, null,
                new ReportInfo(Report.V_ALDERSGRUPP, ReportType.TVARSNITT));

        //Then
        assertFalse((Boolean) ((Map) response.getEntity()).get(ResponseKeys.ALL_AVAILABLE_INTYGTYPES_SELECTED_IN_FILTER));
        validateNoDataFilterMessage(response);
    }

    @Test
    public void testGetResponseUseDefaultPeriod() {
        //Given
        boolean useDefaultPeriod = true;
        FilterDataResponse filterDataResponse = new FilterDataResponse(null, null, null, null, null, null, useDefaultPeriod);

        //When
        Response response = responseHandler
            .getResponse(new SimpleDetailsData(null, null, null, AvailableFilters.getForSjukfall(), filterDataResponse), null, null,
                new ReportInfo(Report.V_ALDERSGRUPP, ReportType.TVARSNITT));

        //Then
        assertTrue((Boolean) ((Map) ((Map) response.getEntity()).get("filter")).get("useDefaultPeriod"));
        validateNoDataNoFilterMessage(response);
    }

    @Test
    public void testGetResponseNotDefaultPeriod() {
        //Given
        boolean useDefaultPeriod = false;
        FilterDataResponse filterDataResponse = new FilterDataResponse(null, null, null, null, null, null, useDefaultPeriod);

        //When
        Response response = responseHandler
            .getResponse(new SimpleDetailsData(null, null, null, AvailableFilters.getForSjukfall(), filterDataResponse), null, null,
                new ReportInfo(Report.V_ALDERSGRUPP, ReportType.TVARSNITT));

        //Then
        assertFalse((Boolean) ((Map) ((Map) response.getEntity()).get("filter")).get("useDefaultPeriod"));
        validateNoDataFilterMessage(response);
    }

    @Test
    public void testEmptyReportNoFilter() {
        //Given
        FilterDataResponse filterDataResponse = FilterDataResponse.empty();

        //When
        Response response = responseHandler
            .getResponse(new SimpleDetailsData(null, null, null, AvailableFilters.getForSjukfall(), filterDataResponse), null, null,
                new ReportInfo(Report.V_ALDERSGRUPP, ReportType.TVARSNITT));

        //Then
        validateNoDataNoFilterMessage(response);
    }

    @Test
    public void testEmptyReportFilter() {
        //Given
        FilterDataResponse filterDataResponse = new FilterDataResponse(null, null, null, null, null, null, false);

        //When
        Response response = responseHandler
            .getResponse(new SimpleDetailsData(null, null, null, AvailableFilters.getForSjukfall(), filterDataResponse), null, null,
                new ReportInfo(Report.V_ALDERSGRUPP, ReportType.TVARSNITT));

        //Then
        validateNoDataFilterMessage(response);
    }

    private void validateNoDataFilterMessage(Response response) {
        Message message = (Message) ((List) ((Map) response.getEntity()).get(ResponseHandler.MESSAGE_KEY)).get(0);
        assertEquals(ErrorType.FILTER, message.getType());
        assertEquals(ErrorSeverity.WARN, message.getSeverity());
        assertEquals(MessagesText.FILTER_NO_DATA, message.getMessage());
    }

    private void validateNoDataNoFilterMessage(Response response) {
        Message message = (Message) ((List) ((Map) response.getEntity()).get(ResponseHandler.MESSAGE_KEY)).get(0);
        assertEquals(ErrorType.UNSET, message.getType());
        assertEquals(ErrorSeverity.INFO, message.getSeverity());
        assertEquals(MessagesText.MESSAGE_NO_DATA, message.getMessage());
    }

    @Test
    public void testGetXlsxFilenameContainsNoSpacesAllReportsINTYG6852() {
        for (Report report : Report.values()) {
            final String filename = getFilenameForReport(report);
            assertFalse("Xslx filename contains space: " + filename, filename.matches(".*\\s.*"));
        }
    }

    private String getFilenameForReport(Report report) {
        final SimpleDetailsData data = new SimpleDetailsData(new TableData(Collections.emptyList(), Collections.emptyList()),
            new ChartData(Collections.emptyList(), Collections.emptyList()), "", AvailableFilters.getForSjukfall(),
            FilterDataResponse.empty());
        final Response xlsx = responseHandler.getXlsx(data, Collections.emptyList(), report);
        final String header = xlsx.getHeaderString("Content-Disposition");
        final String startStr = "filename=";
        final String endStr = ".xlsx";
        final int startIndex = header.indexOf(startStr) + startStr.length();
        final int endIndex = header.indexOf(endStr);
        return header.substring(startIndex, endIndex);
    }

}
