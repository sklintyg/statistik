package se.inera.statistics.web.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import se.inera.auth.model.User;
import se.inera.statistics.hsa.model.Vardenhet;
import se.inera.statistics.service.report.api.VerksamhetOverview;
import se.inera.statistics.service.report.model.Range;

public class ProtectedChartDataServiceTest {
    private VerksamhetOverview mock;
    private HttpServletRequest request;

    @Before
    public void init() {
        mock = Mockito.mock(VerksamhetOverview.class);
        request = Mockito.mock(HttpServletRequest.class);
        List<Vardenhet> vardenhets = Arrays.asList(new Vardenhet("verksamhet1", "Närhälsan i Småmåla"), new Vardenhet("verksamhet2", "Småmålas akutmottagning"));

        User user = new User("hsaId", "name",  vardenhets);
        UsernamePasswordAuthenticationToken principal = Mockito.mock(UsernamePasswordAuthenticationToken.class);
        Mockito.when(request.getUserPrincipal()).thenReturn(principal);
        Mockito.when(principal.getDetails()).thenReturn(user);
    }

    @Test
    public void getOverviewDataForSpecificVerksamhetTest() {
        init();

        ProtectedChartDataService chartDataService = new ProtectedChartDataService(mock, null, null, null, null, null, null);
        try {
            chartDataService.getOverviewData(request, "verksamhet2");
            fail("Current implementation can not use null data");
        } catch (NullPointerException e) {
            assertTrue(true);
        }
        Mockito.verify(mock).getOverview(anyString(), any(Range.class));
    }

    @Test
    public void checkDeniedAccessToVerksamhetTest() {
        boolean result = ProtectedChartDataService.Helper.hasAccessTo(request, "verksamhet3");

        assertEquals(false, result);
    }

    @Test
    public void checkAllowedAccessToVerksamhetTest() {
        boolean result = ProtectedChartDataService.Helper.hasAccessTo(request, "verksamhet2");

        assertEquals(true, result);
    }

}
