package se.inera.statistics.web.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TreeMap;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import se.inera.statistics.service.report.api.CasesPerMonth;
import se.inera.statistics.service.report.model.CasesPerMonthRow;
import se.inera.statistics.web.model.DiagnosisGroup;
import se.inera.statistics.web.model.DiagnosisGroupsData;
import se.inera.statistics.web.model.TableData;
import se.inera.statistics.web.model.NamedData;
import se.inera.statistics.web.model.overview.BarChartData;
import se.inera.statistics.web.model.overview.DonutChartData;
import se.inera.statistics.web.model.overview.NumberOfCasesPerMonthOverview;
import se.inera.statistics.web.model.overview.OverviewData;
import se.inera.statistics.web.model.overview.SickLeaveLengthOverview;
import se.inera.statistics.web.util.DiagnosisGroupsUtil;

@Service("chartService")
public class ChartDataService {

    private static final int NUMBER_OF_CHART_SERIES = 6;

    private static final int NR_OF_PERIDS = 18;

    private static final List<String> PERIODS = createPeriods();

    @Autowired
    private CasesPerMonth datasourceCasesPerMonth;

    @GET
    @Path("getNumberOfCasesPerMonth")
    @Produces({ MediaType.APPLICATION_JSON })
    public TableData getNumberOfCasesPerMonth() {
        List<CasesPerMonthRow> casesPerMonth = datasourceCasesPerMonth.getCasesPerMonth();
        return new TableData(convertCasesPerMonthData(casesPerMonth), Arrays.asList(new String[]{"Period", "Antal kvinnor", "Antal män", "Summering"}));
    }

    private List<NamedData> convertCasesPerMonthData(List<CasesPerMonthRow> casesPerMonth) {
        List<NamedData> data = new ArrayList<>();
        for (CasesPerMonthRow row : casesPerMonth) {
            data.add(new NamedData(row.getPeriod(), Arrays.asList(new Number[]{row.getFemale(), row.getMale(), row.getFemale() + row.getMale()})));
        }
        return data;
    }

    private static List<String> createPeriods() {
        Locale sweden = new Locale("SV", "se");
        Calendar c = new GregorianCalendar(sweden);
        c.add(Calendar.MONTH, -NR_OF_PERIDS);
        List<String> names = new ArrayList<>();
        for (int i = 0; i < NR_OF_PERIDS; i++) {
            names.add(String.format(sweden, "%1$tb %1$tY", c));
            c.add(Calendar.MONTH, 1);
        }
        return names;
    }

    // CHECKSTYLE:OFF MagicNumber
    @GET
    @Path("getDiagnosisGroups")
    @Produces({ MediaType.APPLICATION_JSON })
    public List<DiagnosisGroup> getDiagnosisGroups() {
        return DiagnosisGroupsUtil.getAllDiagnosisGroups();
    }

    @GET
    @Path("getDiagnosisGroupStatistics")
    @Produces({ MediaType.APPLICATION_JSON })
    public DiagnosisGroupsData getDiagnosisGroupStatistics() {
        return new DiagnosisGroupsData(createDiagnosisGroupTableMockData(), createDiagnosisGroupTableMockData(), createDiagnosisGroupChartMockData(), createDiagnosisGroupChartMockData());
    }

    @GET
    @Path("getDiagnosisSubGroupStatistics")
    @Produces({ MediaType.APPLICATION_JSON })
    public DiagnosisGroupsData getDiagnosisSubGroupStatistics(@QueryParam("groupId") String groupId) {
        TableData maleData = createSubDiagnosisGroupTableMockData(groupId);
        TableData femaleData = createSubDiagnosisGroupTableMockData(groupId);
        List<Integer> topIndexes = getTopColumnIndexes(maleData, femaleData);
        TableData maleChartData = extractChartData(maleData, topIndexes);
        TableData femaleChartData = extractChartData(femaleData, topIndexes);
        return new DiagnosisGroupsData(maleData, femaleData, maleChartData, femaleChartData);
    }

    private TableData extractChartData(TableData data, List<Integer> topIndexes) {
        List<NamedData> topColumns = getTopColumns(data, topIndexes);
        return new TableData(topColumns, getDataRowNames(data));
    }

    private List<NamedData> getTopColumns(TableData data, List<Integer> topIndexes) {
        List<NamedData> topColumns = new ArrayList<>();
        for (Integer index : topIndexes) {
            List<Number> indexData = getDataAtIndex(index, data);
            topColumns.add(new NamedData(data.getHeaders().get(index), indexData));
        }
        if (data.getHeaders().size() > NUMBER_OF_CHART_SERIES) {
            List<Number> remainingData = sumRemaining(topIndexes, data);
            topColumns.add(new NamedData("Övrigt", remainingData));
        }
        return topColumns;
    }

    private ArrayList<String> getDataRowNames(TableData data) {
        List<NamedData> rows = data.getRows();
        ArrayList<String> names = new ArrayList<String>();
        for (NamedData tableRow : rows) {
            names.add(tableRow.getName());
        }
        return names;
    }

    private List<Number> sumRemaining(List<Integer> topIndexes, TableData data) {
        List<Number> remaining = new ArrayList<>();
        for (int i = 0; i < data.getRows().size(); i++) {
            remaining.add(0);
        }
        List<NamedData> rows = data.getRows();
        for (int r = 0; r < rows.size(); r++) {
            List<Number> data2 = rows.get(r).getData();
            for (int i = 0; i < data2.size(); i++) {
                if (!topIndexes.contains(i)) {
                    remaining.set(r, remaining.get(r).intValue() + data2.get(i).intValue());
                }
            }
        }
        return remaining;
    }

    private int sum(List<Number> numbers) {
        int sum = 0;
        for (Number number : numbers) {
            sum += number.intValue();
        }
        return sum;
    }

    private List<Number> getDataAtIndex(Integer index, TableData data) {
        List<Number> indexData = new ArrayList<>(data.getRows().size());
        List<NamedData> rows = data.getRows();
        for (NamedData tableRow : rows) {
            List<Number> data2 = tableRow.getData();
            for (int i = 0; i < data2.size(); i++) {
                if (i == index) {
                    indexData.add(data2.get(i));
                }
            }
        }
        return indexData;
    }

    private List<Integer> getTopColumnIndexes(TableData maleData, TableData femaleData) {
        TreeMap<Number, Integer> columnSums = new TreeMap<>();
        int dataSize = maleData.getRows().get(0).getData().size();
        for (int i = 0; i < dataSize; i++) {
            columnSums.put(sum(getDataAtIndex(i, maleData)) + sum(getDataAtIndex(i, femaleData)), i);
        }
        ArrayList<Integer> arrayList = new ArrayList<>(columnSums.descendingMap().values());
        return arrayList.subList(0, Math.min(NUMBER_OF_CHART_SERIES, arrayList.size()));
    }

    private TableData createDiagnosisGroupTableMockData() {
        List<String> headers = toListOfStrings(DiagnosisGroupsUtil.getAllDiagnosisGroups());
        ArrayList<NamedData> rows = new ArrayList<NamedData>();
        for (String periodName : PERIODS) {
            rows.add(new NamedData(periodName, randomData(headers.size())));
        }
        return new TableData(rows, headers);
    }

    private TableData createDiagnosisGroupChartMockData() {
        List<String> headers = PERIODS;
        ArrayList<NamedData> rows = new ArrayList<NamedData>();
        for (String periodName : getTopDiagnosisGroupsAsList()) {
            rows.add(new NamedData(periodName, randomData(headers.size())));
        }
        return new TableData(rows, headers);
    }

    private TableData createSubDiagnosisGroupTableMockData(String groupId) {
        List<String> headers = toListOfStrings(DiagnosisGroupsUtil.getSubGroups(groupId));
        ArrayList<NamedData> rows = new ArrayList<NamedData>();
        for (String periodName : PERIODS) {
            rows.add(new NamedData(periodName, randomData(headers.size())));
        }
        return new TableData(rows, headers);
    }

    private List<String> getTopDiagnosisGroupsAsList() {
        List<String> diagnosisGroups = new ArrayList<>();
        diagnosisGroups.add("Somatiska sjukdomar (A00-E90, G00-L99, N00-N99)");
        diagnosisGroups.add("Psykiska sjukdomar (F00-F99) ");
        diagnosisGroups.add("Muskuloskeletala sjukdomar (M00-M99)");
        diagnosisGroups.add("Graviditet och förlossning (O00-O99)");
        diagnosisGroups.add("Övrigt (P00-P96, Q00-Q99, S00-Y98)");
        diagnosisGroups.add("Symtomdiagnoser (R00-R99)");
        diagnosisGroups.add("Faktorer av betydelse för hälsotillståndet och för kontakter med hälso- och sjukvården (Z00-Z99)");
        return diagnosisGroups;
    }

    private List<String> toListOfStrings(List<DiagnosisGroup> subGroups) {
        if (subGroups == null) {
            return new ArrayList<>();
        }
        List<String> subGroupStrings = new ArrayList<>();
        for (DiagnosisGroup diagnosisGroup : subGroups) {
            subGroupStrings.add(diagnosisGroup.toString());
        }
        return subGroupStrings;
    }

    private List<Number> randomData(int size) {
        Number[] data = new Number[size];
        for (int i = 0; i < size; i++) {
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
    // CHECKSTYLE:ON MagicNumber

}
