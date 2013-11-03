package se.inera.statistics.web.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import se.inera.statistics.service.report.api.VerksamhetOverview;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.web.model.Verksamhet;

public class ProtectedChartDataServiceTest {
    private VerksamhetOverview mock;
    private HttpServletRequest request;
    private HttpSession session;
    private List<Verksamhet> verksamhets;

    @Before
    public void init() {
        mock = Mockito.mock(VerksamhetOverview.class);
        request = Mockito.mock(HttpServletRequest.class);
        session = Mockito.mock(HttpSession.class);
        verksamhets = Arrays.asList(new Verksamhet("verksamhet1", "Närhälsan i Småmåla"), new Verksamhet("verksamhet2", "Småmålas akutmottagning"));

        Mockito.when(request.getSession()).thenReturn(session);
        Mockito.when(session.getAttribute(anyString())).thenReturn(verksamhets);
    }

    @Test
    public void getOverviewDataForSpecificVerksamhetTest() {
        init();

        ProtectedChartDataService chartDataService = new ProtectedChartDataService(mock, null, null, null, null, null, null);
        try {
            chartDataService.getOverviewData(request, "verksamhet2");
            fail("Current implemnetaion can not use null data");
        } catch (NullPointerException e) {
            assertTrue(true);
        }
        Mockito.verify(mock).getOverview(anyString(), any(Range.class));
    }

    @Test
    public void checkDeniedAccessToVerksamhetTest() {
        ProtectedChartDataService chartDataService = new ProtectedChartDataService(mock, null, null, null, null, null, null);
        boolean result = ProtectedChartDataService.Helper.hasAccessTo(request, "verksamhet3");

        assertEquals(false, result);
    }

    @Test
    public void checkAllowedAccessToVerksamhetTest() {
        ProtectedChartDataService chartDataService = new ProtectedChartDataService(mock, null, null, null, null, null, null);
        boolean result = ProtectedChartDataService.Helper.hasAccessTo(request, "verksamhet2");

        assertEquals(true, result);
    }

}
