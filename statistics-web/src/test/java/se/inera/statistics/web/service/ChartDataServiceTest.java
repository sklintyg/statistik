package se.inera.statistics.web.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;

import static org.mockito.MockitoAnnotations.Mock;

import org.mockito.runners.MockitoJUnitRunner;
import se.inera.statistics.service.report.api.CasesPerMonth;
import se.inera.statistics.service.report.api.DiagnosisGroups;
import se.inera.statistics.service.report.api.DiagnosisSubGroups;
import se.inera.statistics.service.report.api.Overview;
import se.inera.statistics.service.report.model.DiagnosisGroup;
import se.inera.statistics.service.report.model.Range;

@RunWith(MockitoJUnitRunner.class)
public class ChartDataServiceTest {

    @Mock
    private Overview overviewMock = Mockito.mock(Overview.class);

    @Mock
    private CasesPerMonth casesPerMonthMock = Mockito.mock(CasesPerMonth.class);

    @Mock
    private DiagnosisGroups diagnosisGroupsMock = Mockito.mock(DiagnosisGroups.class);

    @Mock
    private DiagnosisSubGroups diagnosisSubGroupsMock = Mockito.mock(DiagnosisSubGroups.class);

    @InjectMocks
    private ChartDataService chartDataService = new ChartDataService();

    // CHECKSTYLE:OFF EmptyBlock
    // CHECKSTYLE:OFF MagicNumber

    @Test
    public void getOverviewDataTest() {
        try {
            chartDataService.getOverviewData();
        } catch (NullPointerException e) { }
        Mockito.verify(overviewMock).getOverview(any(Range.class));
    }

    @Test
    public void getNumberOfCasesPerMonthTest() {
        try {
        chartDataService.getNumberOfCasesPerMonth();
        } catch (NullPointerException e) { }
        Mockito.verify(casesPerMonthMock).getCasesPerMonth(anyString(), any(Range.class));
    }

    @Test
    public void getDiagnosisGroupsTest() {
        List<DiagnosisGroup> diagnosisGroups = chartDataService.getDiagnosisGroups();
        assertEquals(22, diagnosisGroups.size());
        assertTrue(diagnosisGroups.toString().contains("{\"DiagnosisGroup\":{\"id\":\"E00-E90\", \"name\":\"Endokrina sjukdomar, nutritionsrubbningar och ämnesomsättningssjukdomar\", \"firstId\":\"E00\", \"lastId\":\"E90\"}}"));
    }

    @Test
    public void getDiagnosisGroupStatisticsTest() {
        try {
            chartDataService.getDiagnosisGroupStatistics();
        } catch (NullPointerException e) { }
        Mockito.verify(diagnosisGroupsMock).getDiagnosisGroups(anyString(), any(Range.class));
    }

    @Test
    public void getDiagnosisSubGroupStatisticsTest() {
        try {
            chartDataService.getDiagnosisSubGroupStatistics("testId");
        } catch (NullPointerException e) { }
        Mockito.verify(diagnosisSubGroupsMock).getDiagnosisGroups(anyString(), any(Range.class), eq("testId"));
    }

    // CHECKSTYLE:ON MagicNumber
    // CHECKSTYLE:ON EmptyBlock

}
