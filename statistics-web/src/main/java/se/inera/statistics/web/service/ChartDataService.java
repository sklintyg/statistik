package se.inera.statistics.web.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
    public TableData getNumberOfCasesPerMonth() {
        return createCasesPerMonthTableMockData();
    }

    private TableData createCasesPerMonthTableMockData() {
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
    @Path("getDiagnosisGroupStatistics")
    @Produces({ MediaType.APPLICATION_JSON })
    public Map<String, TableData> getDiagnosisGroupStatistics() {
        Map<String, TableData> diagnosisGroupData = new HashMap<>();
        diagnosisGroupData.put("male", createDiagnosisGroupTableMockData());
        diagnosisGroupData.put("female", createDiagnosisGroupTableMockData());
        return diagnosisGroupData;
    }
    
    private TableData createDiagnosisGroupTableMockData() {
        ArrayList<TableRow> rows = new ArrayList<TableRow>();
        rows.add(new TableRow("Jan 2013", Arrays.asList(new Number[] { g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g()})));
        rows.add(new TableRow("Feb 2013", Arrays.asList(new Number[] { g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g()})));
        rows.add(new TableRow("Mar 2013", Arrays.asList(new Number[] { g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g()})));
        rows.add(new TableRow("Apr 2013", Arrays.asList(new Number[] { g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g()})));
        rows.add(new TableRow("Maj 2013", Arrays.asList(new Number[] { g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g()})));
        rows.add(new TableRow("Jun 2013", Arrays.asList(new Number[] { g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g()})));
        rows.add(new TableRow("Jul 2013", Arrays.asList(new Number[] { g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g()})));
        rows.add(new TableRow("Aug 2013", Arrays.asList(new Number[] { g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g(), g()})));
        List<String> headers = Arrays
                .asList(new String[] {
                        "A00-B99     Vissa infektionssjukdomar och parasitsjukdomar",
                        "C00-D48     Tumörer",
                        "D50-D89     Sjukdomar i blod och blodbildande organ samt vissa rubbningar i immunsystemet",
                        "E00-E90     Endokrina sjukdomar, nutritionsrubbningar och ämnesomsättningssjukdomar",
                        "F00-F99     Psykiska sjukdomar och syndrom samt beteendestörningar",
                        "G00-G99     Sjukdomar i nervsystemet",
                        "H00-H59     Sjukdomar i ögat och närliggande organ",
                        "H60-H95     Sjukdomar i örat och mastoidutskottet",
                        "I00-I99     Cirkulationsorganens sjukdomar",
                        "J00-J99     Andningsorganens sjukdomar",
                        "K00-K93     Matsmältningsorganens sjukdomar",
                        "L00-L99     Hudens och underhudens sjukdomar",
                        "M00-M99     Sjukdomar i muskuloskeletala systemet och bindväven",
                        "N00-N99     Sjukdomar i urin- och könsorganen",
                        "O00-O99     Graviditet, förlossning och barnsängstid",
                        "P00-P96     Vissa perinatala tillstånd",
                        "Q00-Q99     Medfödda missbildningar, deformiteter och kromosomavvikelser",
                        "R00-R99     Symtom, sjukdomstecken och onormala kliniska fynd och laboratoriefynd som ej klassificeras annorstädes",
                        "S00-T98     Skador, förgiftningar och vissa andra följder av yttre orsaker",
                        "V01-Y98     Yttre orsaker till sjukdom och död",
                        "Z00-Z99     Faktorer av betydelse för hälsotillståndet och för kontakter med hälso- och sjukvården",
                        "U00-U99     Koder för särskilda ändamål" });
        return new TableData(rows, headers);
    }
    
    private Number g() {
        return new Random().nextInt(100);
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
        ageGroups.add(new DonutChartData("&lt;35 år", 140, 2));
        ageGroups.add(new DonutChartData("36-40 år", 140, -4));
        ageGroups.add(new DonutChartData("41-45 år", 40, 5));
        ageGroups.add(new DonutChartData("46-50 år", 25, 0));
        ageGroups.add(new DonutChartData("51-55 år", 32, -3));
        ageGroups.add(new DonutChartData("56-60 år", 20, -4));
        ageGroups.add(new DonutChartData("&gt;60 år", 15, 5));
        
        ArrayList<DonutChartData> degreeOfSickLeaveGroups = new ArrayList<DonutChartData>();
        degreeOfSickLeaveGroups.add(new DonutChartData("25%", 3, 15));
        degreeOfSickLeaveGroups.add(new DonutChartData("50%", 15, 0));
        degreeOfSickLeaveGroups.add(new DonutChartData("75%", 7, -15));
        degreeOfSickLeaveGroups.add(new DonutChartData("100%", 75, 15));
        
        ArrayList<BarChartData> sickLeaveLengthData = new ArrayList<BarChartData>();
        sickLeaveLengthData.add(new BarChartData("&lt;14", 12));
        sickLeaveLengthData.add(new BarChartData("15-30", 17));
        sickLeaveLengthData.add(new BarChartData("31-90", 14));
        sickLeaveLengthData.add(new BarChartData("91-180", 17));
        sickLeaveLengthData.add(new BarChartData("181-360", 9));
        sickLeaveLengthData.add(new BarChartData("&gt;360", 12));
        SickLeaveLengthOverview sickLeaveLength = new SickLeaveLengthOverview(sickLeaveLengthData, 105, 10);
        
        ArrayList<DonutChartData> perCounty = new ArrayList<DonutChartData>();
        perCounty.add(new DonutChartData("Stockholms län", 15, 2));
        perCounty.add(new DonutChartData("Västra götalands län", 12, -4));
        perCounty.add(new DonutChartData("Skåne län", 6, 5));
        perCounty.add(new DonutChartData("Östergötlands län", 5, 0));
        perCounty.add(new DonutChartData("Uppsala län", 4, -4));
        
        return new OverviewData(casesPerMonth, diagnosisGroups, ageGroups, degreeOfSickLeaveGroups, sickLeaveLength, perCounty);
    }

}
