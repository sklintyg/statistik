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
import se.inera.statistics.web.model.overview.DonutChartData;
import se.inera.statistics.web.model.overview.NumberOfCasesPerMonthOverview;
import se.inera.statistics.web.model.overview.OverviewData;

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
        TableData tableData = new TableData(rows, headers);
        return tableData;
    }

    @GET
    @Path("getOverview")
    @Produces({ MediaType.APPLICATION_JSON })
    public OverviewData getOverviewData() {
        return createMockOverviewData();
    }

    private OverviewData createMockOverviewData() {
        ArrayList<DonutChartData> diagnosisGroups = new ArrayList<DonutChartData>();
        diagnosisGroups.add(new DonutChartData("A-E G-L N Somatiska", 140, 2));
        diagnosisGroups.add(new DonutChartData("M - Muskuloskeletala", 140, -4));
        diagnosisGroups.add(new DonutChartData("F - Psykiska", 40, 5));
        diagnosisGroups.add(new DonutChartData("S - Skador", 5, 3));
        diagnosisGroups.add(new DonutChartData("O - Graviditet och förlossning", 3, -3));
        return new OverviewData(new NumberOfCasesPerMonthOverview(56, 44, 5), diagnosisGroups);
    }
}
