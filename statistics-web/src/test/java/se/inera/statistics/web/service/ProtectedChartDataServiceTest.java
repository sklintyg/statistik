package se.inera.statistics.web.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import se.inera.statistics.service.report.api.CasesPerMonth;
import se.inera.statistics.service.report.api.DiagnosisGroups;
import se.inera.statistics.service.report.api.DiagnosisSubGroups;
import se.inera.statistics.service.report.api.Overview;
import se.inera.statistics.service.report.model.DiagnosisGroup;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.web.model.Business;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;

public class ProtectedChartDataServiceTest {
    Overview mock;
    HttpServletRequest request;
    HttpSession session;
    List<Business> verksamhets;

    @Before
    public void init() {
        mock = Mockito.mock(Overview.class);
        request = Mockito.mock(HttpServletRequest.class);
        session = Mockito.mock(HttpSession.class);
        verksamhets = Arrays.asList(new Business("verksamhet1", "Närhälsan i Småmåla"), new Business("verksamhet2", "Småmålas akutmottagning"));

        Mockito.when(request.getSession()).thenReturn(session);
        Mockito.when(session.getAttribute(anyString())).thenReturn(verksamhets);
    }

    @Test
    public void getOverviewDataWithDefaultVerksamhetTest() {
        init();

        ProtectedChartDataService chartDataService = new ProtectedChartDataService(mock, null, null, null, null);
        try {
            chartDataService.getOverviewData(request);
        } catch (NullPointerException e) {}
        Mockito.verify(mock).getOverview("verksamhet1");
    }

    @Test
    public void getOverviewDataForSpecificVerksamhetTest() {
        init();

        ProtectedChartDataService chartDataService = new ProtectedChartDataService(mock, null, null, null, null);
        try {
            chartDataService.getOverviewData(request, "verksamhet2");
        } catch (NullPointerException e) {}
        Mockito.verify(mock).getOverview("verksamhet2");
    }

    @Test
    public void checkDeniedAccessToVerksamhetTest() {
        ProtectedChartDataService chartDataService = new ProtectedChartDataService(mock, null, null, null, null);
        boolean result = chartDataService.hasAccessTo(request, "verksamhet3");

        assertEquals(false, result);
    }

    @Test
    public void checkAllowedAccessToVerksamhetTest() {
        ProtectedChartDataService chartDataService = new ProtectedChartDataService(mock, null, null, null, null);
        boolean result = chartDataService.hasAccessTo(request, "verksamhet2");

        assertEquals(true, result);
    }

    @Test
    public void checkAllowedAccessToDefaultTest() {
        verksamhets = Arrays.asList(new Business("1","a"));
        Mockito.when(session.getAttribute(anyString())).thenReturn(verksamhets);

        ProtectedChartDataService chartDataService = new ProtectedChartDataService(mock, null, null, null, null);
        boolean result = chartDataService.hasAccessToAtLeastOne(request);

        assertEquals(true, result);
    }

    @Test
    public void checkDeniedAccessToDefaultTest() {
        verksamhets = new ArrayList<>();
        Mockito.when(session.getAttribute(anyString())).thenReturn(verksamhets);

        ProtectedChartDataService chartDataService = new ProtectedChartDataService(mock, null, null, null, null);
        boolean result = chartDataService.hasAccessToAtLeastOne(request);

        assertEquals(false, result);
    }

}
