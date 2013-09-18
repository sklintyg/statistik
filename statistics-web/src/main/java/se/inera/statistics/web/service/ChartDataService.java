package se.inera.statistics.web.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.stereotype.Service;

import se.inera.statistics.web.model.TableData;
import se.inera.statistics.web.model.TableRow;
import se.inera.statistics.web.model.overview.BarChartData;
import se.inera.statistics.web.model.overview.DonutChartData;
import se.inera.statistics.web.model.overview.NumberOfCasesPerMonthOverview;
import se.inera.statistics.web.model.overview.OverviewData;
import se.inera.statistics.web.model.overview.SickLeaveLengthOverview;

@Service("chartService")
public class ChartDataService {

    @GET
    @Path("getNumberOfCasesPerMonth")
    @Produces({ MediaType.APPLICATION_JSON })
    public TableData getChartData() {
        return createTableMockData();
    }

    private TableData createTableMockData() {
        ArrayList<TableRow> rows = new ArrayList<TableRow>();
        rows.add(new TableRow("Jan 2013", Arrays.asList(new Number[] { 1, 3, 4 })));
        rows.add(new TableRow("Feb 2013", Arrays.asList(new Number[] { 2, 2, 4 })));
        rows.add(new TableRow("Mar 2013", Arrays.asList(new Number[] { 4, 2, 6 })));
        rows.add(new TableRow("Apr 2013", Arrays.asList(new Number[] { 4, 3, 7 })));
        rows.add(new TableRow("Maj 2013", Arrays.asList(new Number[] { 3, 1, 4 })));
        List<String> headers = Arrays.asList(new String[] { "Antal män", "Antal kvinnor", "Totalt antal" });
        return new TableData(rows, headers);
    }

    @GET
    @Path("getOverview")
    @Produces({ MediaType.APPLICATION_JSON })
    public OverviewData getOverviewData() {
        return createMockOverviewData();
    }

    private OverviewData createMockOverviewData() {
        NumberOfCasesPerMonthOverview casesPerMonth = new NumberOfCasesPerMonthOverview(56, 44, 5);

        ArrayList<DonutChartData> diagnosisGroups = new ArrayList<DonutChartData>();
        diagnosisGroups.add(new DonutChartData("A-E G-L N Somatiska", 140, 2));
        diagnosisGroups.add(new DonutChartData("M - Muskuloskeletala", 140, -4));
        diagnosisGroups.add(new DonutChartData("F - Psykiska", 40, 5));
        diagnosisGroups.add(new DonutChartData("S - Skador", 5, 3));
        diagnosisGroups.add(new DonutChartData("O - Graviditet och förlossning", 3, -3));
        
        ArrayList<DonutChartData> ageGroups = new ArrayList<DonutChartData>();
        ageGroups.add(new DonutChartData("<35 år", 140, 2));
        ageGroups.add(new DonutChartData("36-40 år", 140, -4));
        ageGroups.add(new DonutChartData("41-45 år", 40, 5));
        ageGroups.add(new DonutChartData("46-50 år", 25, 0));
        ageGroups.add(new DonutChartData("51-55 år", 32, -3));
        ageGroups.add(new DonutChartData("56-60 år", 20, -4));
        ageGroups.add(new DonutChartData(">60 år", 15, 5));
        
        ArrayList<DonutChartData> degreeOfSickLeaveGroups = new ArrayList<DonutChartData>();
        degreeOfSickLeaveGroups.add(new DonutChartData("25%", 3, 15));
        degreeOfSickLeaveGroups.add(new DonutChartData("50%", 15, 0));
        degreeOfSickLeaveGroups.add(new DonutChartData("75%", 7, -15));
        degreeOfSickLeaveGroups.add(new DonutChartData("100%", 75, 15));
        
        ArrayList<BarChartData> sickLeaveLengthData = new ArrayList<BarChartData>();
        sickLeaveLengthData.add(new BarChartData("<14", 12));
        sickLeaveLengthData.add(new BarChartData("15-30", 17));
        sickLeaveLengthData.add(new BarChartData("31-90", 14));
        sickLeaveLengthData.add(new BarChartData("91-180", 17));
        sickLeaveLengthData.add(new BarChartData("181-360", 9));
        sickLeaveLengthData.add(new BarChartData("<360", 12));
        SickLeaveLengthOverview sickLeaveLength = new SickLeaveLengthOverview(sickLeaveLengthData, 105, 10);
        
        return new OverviewData(casesPerMonth, diagnosisGroups, ageGroups, degreeOfSickLeaveGroups, sickLeaveLength);
    }

}
