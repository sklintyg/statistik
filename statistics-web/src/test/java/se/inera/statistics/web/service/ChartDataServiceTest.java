package se.inera.statistics.web.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;

import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;

import se.inera.statistics.service.report.api.CasesPerMonth;
import se.inera.statistics.service.report.api.DiagnosisGroups;
import se.inera.statistics.service.report.api.DiagnosisSubGroups;
import se.inera.statistics.service.report.api.Overview;
import se.inera.statistics.service.report.model.DiagnosisGroup;
import se.inera.statistics.service.report.model.Range;

public class ChartDataServiceTest {

    // CHECKSTYLE:OFF EmptyBlock
    // CHECKSTYLE:OFF MagicNumber

    @Test
    public void getOverviewDataTest() {
        Overview mock = Mockito.mock(Overview.class);
        ChartDataService chartDataService = new ChartDataService(mock, null, null, null, null, null, null, null);
        try {
            chartDataService.getOverviewData();
        } catch (NullPointerException e) { }
        Mockito.verify(mock).getOverview(any(Range.class));
    }

    @Test
    public void getNumberOfCasesPerMonthTest() {
        CasesPerMonth mock = Mockito.mock(CasesPerMonth.class);
        ChartDataService chartDataService = new ChartDataService(null, mock, null, null, null, null, null, null);
        try {
        chartDataService.getNumberOfCasesPerMonth();
        } catch (NullPointerException e) { }
        Mockito.verify(mock).getCasesPerMonth(anyString(), any(Range.class));
    }

    @Test
    public void getDiagnosisGroupsTest() {
        ChartDataService chartDataService = new ChartDataService(null, null, null, null, null, null, null, null);
        List<DiagnosisGroup> diagnosisGroups = chartDataService.getDiagnosisGroups();
        assertEquals(22, diagnosisGroups.size());
        assertTrue(diagnosisGroups.toString().contains("{\"DiagnosisGroup\":{\"id\":\"E00-E90\", \"name\":\"Endokrina sjukdomar, nutritionsrubbningar och ämnesomsättningssjukdomar\", \"firstId\":\"E00\", \"lastId\":\"E90\"}}"));
    }

    @Test
    public void getDiagnosisGroupStatisticsTest() {
        DiagnosisGroups mock = Mockito.mock(DiagnosisGroups.class);
        ChartDataService chartDataService = new ChartDataService(null, null, mock, null, null, null, null, null);
        try {
            chartDataService.getDiagnosisGroupStatistics();
        } catch (NullPointerException e) { }
        Mockito.verify(mock).getDiagnosisGroups(anyString(), any(Range.class));
    }

    @Test
    public void getDiagnosisSubGroupStatisticsTest() {
        DiagnosisSubGroups mock = Mockito.mock(DiagnosisSubGroups.class);
        ChartDataService chartDataService = new ChartDataService(null, null, null, mock, null, null, null, null);
        try {
            chartDataService.getDiagnosisSubGroupStatistics("testId");
        } catch (NullPointerException e) { }
        Mockito.verify(mock).getDiagnosisGroups(anyString(), any(Range.class), eq("testId"));
    }

    // CHECKSTYLE:ON MagicNumber
    // CHECKSTYLE:ON EmptyBlock

}
