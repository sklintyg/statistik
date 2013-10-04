package se.inera.statistics.web.service;

import org.joda.time.LocalDate;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mockito.Mockito;
import org.mockito.verification.VerificationMode;
import se.inera.statistics.service.report.api.CasesPerMonth;
import se.inera.statistics.service.report.api.DiagnosisGroups;
import se.inera.statistics.service.report.api.DiagnosisSubGroups;
import se.inera.statistics.service.report.api.Overview;
import se.inera.statistics.service.report.model.DiagnosisGroup;
import se.inera.statistics.web.model.DiagnosisGroupsData;
import se.inera.statistics.web.model.TableData;
import se.inera.statistics.web.model.overview.OverviewData;

import java.util.List;

public class ChartDataServiceTest {

    @Test
    public void getOverviewDataTest(){
        Overview mock = Mockito.mock(Overview.class);
        ChartDataService chartDataService = new ChartDataService(mock, null, null, null, null);
        try {
            chartDataService.getOverviewData();
        } catch (NullPointerException e) {}
        Mockito.verify(mock).getOverview();
    }

    @Test
    public void getNumberOfCasesPerMonthTest(){
        CasesPerMonth mock = Mockito.mock(CasesPerMonth.class);
        ChartDataService chartDataService = new ChartDataService(null, mock, null, null, null);
        chartDataService.getNumberOfCasesPerMonth();
        Mockito.verify(mock).getCasesPerMonth(Mockito.any(LocalDate.class), Mockito.any(LocalDate.class));
    }

    @Test
    public void getDiagnosisGroupsTest(){
        ChartDataService chartDataService = new ChartDataService(null, null, null, null, null);
        List<DiagnosisGroup> diagnosisGroups = chartDataService.getDiagnosisGroups();
        assertEquals(22, diagnosisGroups.size());
        assertTrue(diagnosisGroups.toString().contains("E00-E90 Endokrina sjukdomar"));
    }

    @Test
    public void getDiagnosisGroupStatisticsTest(){
        DiagnosisGroups mock = Mockito.mock(DiagnosisGroups.class);
        ChartDataService chartDataService = new ChartDataService(null, null, mock, null, null);
        try {
            chartDataService.getDiagnosisGroupStatistics();
        } catch (NullPointerException e) {}
        Mockito.verify(mock).getDiagnosisGroups();
    }

    @Test
    public void getDiagnosisSubGroupStatisticsTest(){
        DiagnosisSubGroups mock = Mockito.mock(DiagnosisSubGroups.class);
        ChartDataService chartDataService = new ChartDataService(null, null, null, mock, null);
        try {
            chartDataService.getDiagnosisSubGroupStatistics("testId");
        } catch (NullPointerException e) {}
        Mockito.verify(mock).getDiagnosisSubGroups("testId");
    }

}
