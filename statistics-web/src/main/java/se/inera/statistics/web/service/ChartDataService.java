package se.inera.statistics.web.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.stereotype.Service;

import se.inera.statistics.web.model.DiagnosisGroup;
import se.inera.statistics.web.model.TableData;
import se.inera.statistics.web.model.TableRow;
import se.inera.statistics.web.model.overview.BarChartData;
import se.inera.statistics.web.model.overview.DonutChartData;
import se.inera.statistics.web.model.overview.NumberOfCasesPerMonthOverview;
import se.inera.statistics.web.model.overview.OverviewData;
import se.inera.statistics.web.model.overview.SickLeaveLengthOverview;

@Service("chartService")
public class ChartDataService {

    private static List<String> PERIODS = createPeriods();

    private Random random = new Random();

    @GET
    @Path("getNumberOfCasesPerMonth")
    @Produces({ MediaType.APPLICATION_JSON })
    public TableData getNumberOfCasesPerMonth() {
        return createCasesPerMonthTableMockData();
    }

    private static List<String> createPeriods() {
        Locale sweden = new Locale("SV", "se");
        Calendar c = new GregorianCalendar(sweden);
        c.add(Calendar.MONTH, -18);
        List<String> names = new ArrayList<>();
        for (int i = 0; i < 18; i ++) {
            names.add(String.format(sweden, "%1$tb %1$tY", c));
            c.add(Calendar.MONTH, 1);
        }
        return names;
    }

    private TableData createCasesPerMonthTableMockData() {
        List<String> headers = Arrays.asList(new String[] { "Antal män", "Antal kvinnor", "Totalt antal" });
        ArrayList<TableRow> rows = new ArrayList<TableRow>();
        for (String periodName: PERIODS) {
            rows.add(new TableRow(periodName, randomCasesPerMonthData()));
        }
        return new TableData(rows, headers);
    }

    private List<Number> randomCasesPerMonthData() {
        int men = (int) (random.nextGaussian() * 2000 + 10000);
        int women = (int) (random.nextGaussian() * 2000 + 10000);
        return Arrays.asList(new Number[] { men, women, men + women });
    }

    @GET
    @Path("getDiagnosisGroups")
    @Produces({ MediaType.APPLICATION_JSON })
    public List<DiagnosisGroup> getDiagnosisGroups() {
        return getAllDiagnosisGroups();
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
    
    @GET
    @Path("getDiagnosisSubGroupStatistics")
    @Produces({ MediaType.APPLICATION_JSON })
    public Map<String, TableData> getDiagnosisGroupStatistics(@QueryParam("groupId") String groupId) {
        Map<String, TableData> diagnosisGroupData = new HashMap<>();
        diagnosisGroupData.put("male", createSubDiagnosisGroupTableMockData(groupId));
        diagnosisGroupData.put("female", createSubDiagnosisGroupTableMockData(groupId));
        return diagnosisGroupData;
    }
    
    private TableData createDiagnosisGroupTableMockData() {
        List<String> headers = getTopDiagnosisGroupsAsList();
        ArrayList<TableRow> rows = new ArrayList<TableRow>();
       for (String periodName: PERIODS) {
           rows.add(new TableRow(periodName, randomData(headers.size())));
       }
       return new TableData(rows, headers);
    }

    private TableData createSubDiagnosisGroupTableMockData(String groupId) {
        List<String> headers = getSubDiagnosisGroupsAsList(groupId);
        ArrayList<TableRow> rows = new ArrayList<TableRow>();
        for (String periodName: PERIODS) {
            rows.add(new TableRow(periodName, randomData(headers.size())));
        }
        return new TableData(rows, headers);
    }
    
    private List<String> getAllDiagnosisGroupsAsList() {
        List<String> groups = new ArrayList<>();
        List<DiagnosisGroup> entries = getAllDiagnosisGroups();
        for (DiagnosisGroup entry : entries) {
            groups.add(entry.toString());
        }
        return groups;
    }
    
    private List<String> getTopDiagnosisGroupsAsList() {
        List<String> diagnosisGroups = new ArrayList<>();
        diagnosisGroups.add("F00-F99 Psykiska sjukdomar och syndrom samt beteendestörningar");
        diagnosisGroups.add("M00-M99 Sjukdomar i muskuloskeletala systemet och bindväven");
        diagnosisGroups.add("A00-B99 Vissa infektionssjukdomar och parasitsjukdomar");
        diagnosisGroups.add("E00-E90 Endokrina sjukdomar, nutritionsrubbningar och ämnesomsättningssjukdomar");
        diagnosisGroups.add("I00-I99 Cirkulationsorganens sjukdomar");
        diagnosisGroups.add("D50-D89 Sjukdomar i blod och blodbildande organ samt vissa rubbningar i immunsystemet");
        diagnosisGroups.add("Övrigt");
        return diagnosisGroups;
    }
    
    private List<String> getSubDiagnosisGroupsAsList(String groupId) {
        List<String> aGroups = new ArrayList<>();
        aGroups.add("A00-A09 Infektionssjukdomar utgående från mag-tarmkanalen");
        aGroups.add("A15-A19 Tuberkulos");
        aGroups.add("A20-A28 Vissa djurburna bakteriesjukdomar");
        aGroups.add("A30-A49 Andra bakteriesjukdomar");
        aGroups.add("A50-A64 Huvudsakligen sexuellt överförda infektioner");
        aGroups.add("A65-A69 Andra spiroketsjukdomar");
        aGroups.add("A70-A74 Andra sjukdomar orsakade av klamydia");
        aGroups.add("A75-A79 Sjukdomar orsakade av rickettsiaarter");
        aGroups.add("A80-A89 Virussjukdomar i centrala nervsystemet");
        aGroups.add("A90-A99 Febersjukdomar orsakade av virus överförda av leddjur och virusorsakade hemorragiska febrar");
        aGroups.add("B00-B09 Virussjukdomar med hudutslag och slemhinneutslag");
        aGroups.add("B15-B19 Virushepatit");
        aGroups.add("B20-B24 Sjukdom orsakad av humant immunbristvirus [HIV]");
        aGroups.add("B25-B34 Andra virussjukdomar");
        aGroups.add("B35-B49 Svampsjukdomar");
        aGroups.add("B50-B64 Protozosjukdomar");
        aGroups.add("B65-B83 Masksjukdomar");
        aGroups.add("B85-B89 Lusangrepp, acarinos (angrepp av kvalster) och andra infestationer");
        aGroups.add("B90-B94 Sena effekter av infektionssjukdomar och parasitsjukdomar");
        aGroups.add("B95-B98 Bakterier, virus och andra infektiösa organismer");
        aGroups.add("B99-B99 Andra infektionssjukdomar");

        List<String> cGroups = new ArrayList<>();
        cGroups.add("C00-C14 Maligna tumörer i läpp, munhåla och svalg");
        cGroups.add("C15-C26 Maligna tumörer i matsmältningsorganen");
        cGroups.add("C30-C39 Maligna tumörer i andningsorganen och brösthålans organ");
        cGroups.add("C40-C41 Maligna tumörer i ben och ledbrosk");
        cGroups.add("C43-C44 Melanom och andra maligna tumörer i huden");
        cGroups.add("C45-C49 Maligna tumörer i mesotelial (kroppshåletäckande) vävnad och mjukvävnad");
        cGroups.add("C50-C50 Malign tumör i bröstkörtel");
        cGroups.add("C51-C58 Maligna tumörer i de kvinnliga könsorganen");
        cGroups.add("C60-C63 Maligna tumörer i de manliga könsorganen");
        cGroups.add("C64-C68 Maligna tumörer i urinorganen");
        cGroups.add("C69-C72 Maligna tumörer i öga, hjärnan och andra delar av centrala nervsystemet");
        cGroups.add("C73-C75 Maligna tumörer i tyreoidea och andra endokrina körtlar");
        cGroups.add("C76-C80 Maligna tumörer med ofullständigt angivna, sekundära och icke specificerade lokalisationer");
        cGroups.add("C81-C96 Maligna tumörer i lymfatisk, blodbildande och besläktad vävnad");
        cGroups.add("C97-C97 Flera (primära) maligna tumörer med olika utgångspunkter");
        cGroups.add("D00-D09 Cancer in situ (lokalt begränsad cancer utgången från epitel)");
        cGroups.add("D10-D36 Benigna tumörer");
        cGroups.add("D37-D48 Tumörer av osäker eller okänd natur");

        List<String> dGroups = new ArrayList<>();
        dGroups.add("D50-D53 Nutritionsanemier");
        dGroups.add("D55-D59 Hemolytiska anemier (blodbrist på grund av ökad nedbrytning av röda blodkroppar)");
        dGroups.add("D60-D64 Aplastisk anemi (blodbrist på grund av upphörd eller minskad blodbildning i benmärgen) och andra anemier");
        dGroups.add("D65-D69 Koagulationsrubbningar, purpura (punktformiga blödningar i huden mm) och andra blödningstillstånd");
        dGroups.add("D70-D77 Andra sjukdomar i blod och blodbildande organ");
        dGroups.add("D80-D89 Vissa rubbningar i immunsystemet");

        List<String> eGroups = new ArrayList<>();
        eGroups.add("E00-E07 Sjukdomar i sköldkörteln");
        eGroups.add("E10-E14 Diabetes (sockersjuka)");
        eGroups.add("E15-E16 Andra rubbningar i glukosreglering och bukspottkörtelns inre sekretion");
        eGroups.add("E20-E35 Sjukdomar i andra endokrina körtlar");
        eGroups.add("E40-E46 Näringsbrist");
        eGroups.add("E50-E64 Andra näringsbristtillstånd");
        eGroups.add("E65-E68 Fetma och andra övernäringstillstånd");
        eGroups.add("E70-E90 Ämnesomsättningssjukdomar");

        Map<String, List<String>> subGroups = new HashMap<>();
        subGroups.put("A00-B99", aGroups);
        subGroups.put("C00-D48", cGroups);
        subGroups.put("D50-D89", dGroups);
        subGroups.put("E00-E90", eGroups);
        
        if (subGroups.containsKey(groupId)){
            return subGroups.get(groupId);
        } else {
            return getAllDiagnosisGroupsAsList();
        }
    }
    
    private List<DiagnosisGroup> getAllDiagnosisGroups() {
        ArrayList<DiagnosisGroup> groups = new ArrayList<>();
        groups.add(new DiagnosisGroup("A00-B99", "Vissa infektionssjukdomar och parasitsjukdomar"));
        groups.add(new DiagnosisGroup("C00-D48", "Tumörer"));
        groups.add(new DiagnosisGroup("D50-D89", "Sjukdomar i blod och blodbildande organ samt vissa rubbningar i immunsystemet"));
        groups.add(new DiagnosisGroup("E00-E90", "Endokrina sjukdomar, nutritionsrubbningar och ämnesomsättningssjukdomar"));
        groups.add(new DiagnosisGroup("F00-F99", "Psykiska sjukdomar och syndrom samt beteendestörningar"));
        groups.add(new DiagnosisGroup("G00-G99", "Sjukdomar i nervsystemet"));
        groups.add(new DiagnosisGroup("H00-H59", "Sjukdomar i ögat och närliggande organ"));
        groups.add(new DiagnosisGroup("H60-H95", "Sjukdomar i örat och mastoidutskottet"));
        groups.add(new DiagnosisGroup("I00-I99", "Cirkulationsorganens sjukdomar"));
        groups.add(new DiagnosisGroup("J00-J99", "Andningsorganens sjukdomar"));
        groups.add(new DiagnosisGroup("K00-K93", "Matsmältningsorganens sjukdomar"));
        groups.add(new DiagnosisGroup("L00-L99", "Hudens och underhudens sjukdomar"));
        groups.add(new DiagnosisGroup("M00-M99", "Sjukdomar i muskuloskeletala systemet och bindväven"));
        groups.add(new DiagnosisGroup("N00-N99", "Sjukdomar i urin- och könsorganen"));
        groups.add(new DiagnosisGroup("O00-O99", "Graviditet, förlossning och barnsängstid"));
        groups.add(new DiagnosisGroup("P00-P96", "Vissa perinatala tillstånd"));
        groups.add(new DiagnosisGroup("Q00-Q99", "Medfödda missbildningar, deformiteter och kromosomavvikelser"));
        groups.add(new DiagnosisGroup("R00-R99", "Symtom, sjukdomstecken och onormala kliniska fynd och laboratoriefynd som ej klassificeras annorstädes"));
        groups.add(new DiagnosisGroup("S00-T98", "Skador, förgiftningar och vissa andra följder av yttre orsaker"));
        groups.add(new DiagnosisGroup("V01-Y98", "Yttre orsaker till sjukdom och död"));
        groups.add(new DiagnosisGroup("Z00-Z99", "Faktorer av betydelse för hälsotillståndet och för kontakter med hälso- och sjukvården"));
        groups.add(new DiagnosisGroup("U00-U99", "Koder för särskilda ändamål"));
        return groups;
    }
    
    private List<Number> randomData(int size) {
        Number[] data = new Number[size];
        for (int i = 0; i < size; i++ ){
            data[i] = g();
        }
        return Arrays.asList(data);
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
