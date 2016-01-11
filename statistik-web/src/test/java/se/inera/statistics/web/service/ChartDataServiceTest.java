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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.processlog.Enhet;
import se.inera.statistics.service.report.model.Icd;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.util.Icd10;
import se.inera.statistics.service.warehouse.NationellData;
import se.inera.statistics.service.warehouse.NationellOverviewData;
import se.inera.statistics.service.warehouse.Warehouse;
import se.inera.statistics.web.service.monitoring.MonitoringLogService;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;

@RunWith(MockitoJUnitRunner.class)
public class ChartDataServiceTest {

    @Mock
    private NationellOverviewData overviewMock = Mockito.mock(NationellOverviewData.class);

    @Mock
    private NationellData nationellData = Mockito.mock(NationellData.class);

    @Mock
    private Icd10 icd10;

    @Mock
    private MonitoringLogService monitoringLogService;

    @Mock
    private FilterHashHandler filterHashHandler;

    @Mock
    private Warehouse warehouse;

    @InjectMocks
    private ChartDataService chartDataService = new ChartDataService();

    // CHECKSTYLE:OFF EmptyBlock
    // CHECKSTYLE:OFF MagicNumber

    @Test
    public void getOverviewDataTest() {
        try {
            chartDataService.buildOverview();
        } catch (NullPointerException e) {
        }
        Mockito.verify(overviewMock).getOverview(any(Range.class));
    }

    @Test
    public void getNumberOfCasesPerMonthTest() {
        try {
            chartDataService.buildNumberOfCasesPerMonth();
        } catch (NullPointerException e) {
        }
        Mockito.verify(nationellData).getCasesPerMonth(any(Range.class));
    }

    @Test
    public void getDiagnosisGroupsTest() {
        Mockito.when(icd10.getKapitel(anyBoolean())).thenReturn(Arrays.asList(new Icd10.Kapitel("A00-B99", "Vissa infektionssjukdomar och parasitsjukdomar"), new Icd10.Kapitel("C00-D48", "Tumörer")));
        HttpServletRequest request = null;
        List<Icd> kapitel = chartDataService.getDiagnoskapitel(request);
        assertEquals(2, kapitel.size());
    }

    @Test
    public void getDiagnosisGroupStatisticsTest() {
        try {
            chartDataService.buildDiagnosgrupper();
        } catch (NullPointerException e) {
        }
        Mockito.verify(nationellData).getDiagnosgrupper(any(Range.class));
    }

    @Test
    public void getDiagnosisSubGroupStatisticsTest() {
        Mockito.when(icd10.getKapitel(anyBoolean())).thenReturn(Arrays.asList(new Icd10.Kapitel("A00-B99", "Vissa infektionssjukdomar och parasitsjukdomar"), new Icd10.Kapitel("C00-D48", "Tumörer")));
        try {
            chartDataService.buildDiagnoskapitel();
        } catch (NullPointerException e) {
        }
        Mockito.verify(nationellData).getDiagnosavsnitt(any(Range.class), eq("A00-B99"));
    }

    @Test
    public void testGetFilterEnhetnamnMissingFilterHash() throws Exception {
        //Given
        final String filterHash = "abc";
        Mockito.when(filterHashHandler.getFilterFromHash(filterHash)).thenThrow(new FilterHashMissingException(""));

        //When
        final Response filterEnhetnamn = chartDataService.getFilterEnhetnamn(filterHash);

        //Then
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), filterEnhetnamn.getStatus());
    }

    @Test
    public void testGetFilterEnhetnamnMissingEnhet() throws Exception {
        //Given
        final String filterHash = "abc";
        Mockito.when(filterHashHandler.getFilterFromHash(filterHash)).thenReturn(new FilterData(null, Arrays.asList("1", "2"), null, null, null, true));
        Mockito.doReturn(Arrays.asList(createEnhet("1", "ett"))).when(warehouse).getEnhetsWithHsaId(any(Collection.class));

        //When
        final Response filterEnhetnamn = chartDataService.getFilterEnhetnamn(filterHash);

        //Then
        final List<String> entity = (List<String>) filterEnhetnamn.getEntity();
        assertEquals(1, entity.size());
        assertEquals("ett", entity.get(0));
        // Att testa: Saknade enheter, saknad filterhash, att id läggs till när namnen är samma (oavsett case), etc
    }

    @Test
    public void testGetFilterEnhetnamnIdIsAddedToNameWhenDuplicate() throws Exception {
        //Given
        final String filterHash = "abc";
        Mockito.when(filterHashHandler.getFilterFromHash(filterHash)).thenReturn(new FilterData(null, Arrays.asList("1", "2"), null, null, null, true));
        Mockito.doReturn(Arrays.asList(createEnhet("1", "ett"), createEnhet("2", "ett"))).when(warehouse).getEnhetsWithHsaId(any(Collection.class));

        //When
        final Response filterEnhetnamn = chartDataService.getFilterEnhetnamn(filterHash);

        //Then
        final List<String> entity = (List<String>) filterEnhetnamn.getEntity();
        assertEquals(2, entity.size());
        assertEquals("ett 1", entity.get(0));
        assertEquals("ett 2", entity.get(1));
    }

    private Enhet createEnhet(String id, String namn) {
        return new Enhet(new HsaIdVardgivare(""), null, new HsaIdEnhet(id), namn, null, null, null);
    }


    // CHECKSTYLE:ON MagicNumber
    // CHECKSTYLE:ON EmptyBlock

}
