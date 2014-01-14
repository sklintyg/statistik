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
import se.inera.statistics.service.report.api.SjukfallPerManad;
import se.inera.statistics.service.report.api.Diagnosgrupp;
import se.inera.statistics.service.report.api.Diagnoskapitel;
import se.inera.statistics.service.report.api.Overview;
import se.inera.statistics.service.report.model.DiagnosisGroup;
import se.inera.statistics.service.report.model.Range;

@RunWith(MockitoJUnitRunner.class)
public class ChartDataServiceTest {

    @Mock
    private Overview overviewMock = Mockito.mock(Overview.class);

    @Mock
    private SjukfallPerManad sjukfallPerManadMock = Mockito.mock(SjukfallPerManad.class);

    @Mock
    private Diagnosgrupp diagnosgruppMock = Mockito.mock(Diagnosgrupp.class);

    @Mock
    private Diagnoskapitel diagnoskapitelMock = Mockito.mock(Diagnoskapitel.class);

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
        Mockito.verify(sjukfallPerManadMock).getCasesPerMonth(anyString(), any(Range.class));
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
        Mockito.verify(diagnosgruppMock).getDiagnosisGroups(anyString(), any(Range.class));
    }

    @Test
    public void getDiagnosisSubGroupStatisticsTest() {
        try {
            chartDataService.getDiagnosisSubGroupStatistics("testId");
        } catch (NullPointerException e) { }
        Mockito.verify(diagnoskapitelMock).getDiagnosisGroups(anyString(), any(Range.class), eq("testId"));
    }

    // CHECKSTYLE:ON MagicNumber
    // CHECKSTYLE:ON EmptyBlock

}
