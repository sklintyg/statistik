package se.inera.statistics.web.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import se.inera.statistics.service.report.api.CasesPerMonth;
import se.inera.statistics.service.report.api.DiagnosisGroups;
import se.inera.statistics.service.report.api.DiagnosisSubGroups;
import se.inera.statistics.service.report.model.CasesPerMonthRow;
import se.inera.statistics.service.report.model.DiagnosisGroup;
import se.inera.statistics.service.report.model.DiagnosisGroupResponse;
import se.inera.statistics.service.report.model.DiagnosisGroupRow;
import se.inera.statistics.service.report.model.Sex;
import se.inera.statistics.service.report.util.DiagnosisGroupsUtil;
import se.inera.statistics.web.model.DiagnosisGroupsData;
import se.inera.statistics.web.model.NamedData;
import se.inera.statistics.web.model.TableData;
import se.inera.statistics.web.model.overview.BarChartData;
import se.inera.statistics.web.model.overview.DonutChartData;
import se.inera.statistics.web.model.overview.NumberOfCasesPerMonthOverview;
import se.inera.statistics.web.model.overview.OverviewData;
import se.inera.statistics.web.model.overview.SickLeaveLengthOverview;

@Service("chartService")
public class ChartDataService {

    private static final String DIAGNOSIS_CHART_GROUP_1 = "Somatiska sjukdomar (A00-E90, G00-L99, N00-N99)";
    private static final String DIAGNOSIS_CHART_GROUP_2 = "Psykiska sjukdomar (F00-F99)";
    private static final String DIAGNOSIS_CHART_GROUP_3 = "Muskuloskeletala sjukdomar (M00-M99)";
    private static final String DIAGNOSIS_CHART_GROUP_4 = "Graviditet och förlossning (O00-O99)";
    private static final String DIAGNOSIS_CHART_GROUP_5 = "Övrigt (P00-P96, Q00-Q99, S00-Y98)";
    private static final String DIAGNOSIS_CHART_GROUP_6 = "Symtomdiagnoser (R00-R99)";
    private static final String DIAGNOSIS_CHART_GROUP_7 = "Faktorer av betydelse för hälsotillståndet och för kontakter med hälso- och sjukvården (Z00-Z99)";

    private static final int NUMBER_OF_CHART_SERIES = 6;

    @Autowired
    private CasesPerMonth datasourceCasesPerMonth;

    @Autowired
    private DiagnosisGroups datasourceDiagnosisGroups;

    @Autowired
    private DiagnosisSubGroups datasourceDiagnosisSubGroups;

    @GET
    @Path("getNumberOfCasesPerMonth")
    @Produces({ MediaType.APPLICATION_JSON })
    public TableData getNumberOfCasesPerMonth() {
        List<CasesPerMonthRow> casesPerMonth = datasourceCasesPerMonth.getCasesPerMonth();
        return new TableData(convertCasesPerMonthData(casesPerMonth), Arrays.asList(new String[] { "Period", "Antal kvinnor", "Antal män", "Summering" }));
    }

    private List<NamedData> convertCasesPerMonthData(List<CasesPerMonthRow> casesPerMonth) {
        List<NamedData> data = new ArrayList<>();
        for (CasesPerMonthRow row : casesPerMonth) {
            data.add(new NamedData(row.getPeriod(), Arrays.asList(new Integer[] { row.getFemale(), row.getMale(), row.getFemale() + row.getMale() })));
        }
        return data;
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
        DiagnosisGroupResponse diagnosisGroups = datasourceDiagnosisGroups.getDiagnosisGroups();
        TableData maleTable = convertDiagnosisGroupsTableData(diagnosisGroups, Sex.Male);
        TableData femaleTable = convertDiagnosisGroupsTableData(diagnosisGroups, Sex.Female);
        TableData maleChart = convertDiagnosisGroupsChartData(diagnosisGroups, Sex.Male);
        TableData femaleChart = convertDiagnosisGroupsChartData(diagnosisGroups, Sex.Female);
        return new DiagnosisGroupsData(maleTable, femaleTable, maleChart, femaleChart);
    }

    private TableData convertDiagnosisGroupsChartData(DiagnosisGroupResponse resp, Sex sex) {
        Map<String, List<Integer>> allGroups = extractAllGroups(resp, sex);
        Map<String, List<Integer>> mergedGroups = mergeChartGroups(allGroups);
        ArrayList<NamedData> rows = new ArrayList<NamedData>();
        for (Entry<String, List<Integer>> entry : mergedGroups.entrySet()) {
            rows.add(new NamedData(entry.getKey(), entry.getValue()));
        }

        List<String> headers = resp.getPeriods();
        return new TableData(rows, headers);
    }

    private Map<String, List<Integer>> mergeChartGroups(Map<String, List<Integer>> allGroups) {
        Map<String, List<Integer>> mergedGroups = new HashMap<>();
        for (Entry<String, List<Integer>> entry : allGroups.entrySet()) {
            addGroupToMergedChartGroups(mergedGroups, entry.getKey(), entry.getValue());
        }
        return mergedGroups;
    }

    private Map<String, List<Integer>> extractAllGroups(DiagnosisGroupResponse resp, Sex sex) {
        Map<String, List<Integer>> allGroups = new HashMap<>();
        for (int i = 0; i < resp.getDiagnosisGroups().size(); i++) {
            DiagnosisGroup groupName = resp.getDiagnosisGroups().get(i);
            allGroups.put(groupName.getId(), resp.getDataFromIndex(i, sex));
        }
        return allGroups;
    }

    private void addGroupToMergedChartGroups(Map<String, List<Integer>> mergedGroups, String groupId, List<Integer> values) {
        String mergedName = getMergedChartGroupName(groupId);
        if (mergedGroups.containsKey(mergedName)) {
            List<Integer> sumOfLists = sumLists(mergedGroups.get(mergedName), values);
            mergedGroups.put(mergedName, sumOfLists);
        } else {
            mergedGroups.put(mergedName, values);
        }
    }

    private List<Integer> sumLists(List<Integer> list, List<Integer> values) {
        assert list.size() == values.size();
        List<Integer> sum = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            sum.add(list.get(i) + values.get(i));
        }
        return sum;
    }

    private String getMergedChartGroupName(String groupId) {
        List<String> g1 = Arrays.asList(new String[] { "A00-B99", "C00-D48", "D50-D89", "E00-E90", "G00-G99", "H00-H59", "H00-H59", "H60-H95", "I00-I99",
                "J00-J99", "K00-K93", "L00-L99", "N00-N99" });
        List<String> g2 = Arrays.asList(new String[] { "F00-F99" });
        List<String> g3 = Arrays.asList(new String[] { "M00-M99" });
        List<String> g4 = Arrays.asList(new String[] { "O00-O99" });
        List<String> g5 = Arrays.asList(new String[] { "P00-P96", "Q00-Q99", "S00-T98", "U00-U99", "V01-Y98" });
        List<String> g6 = Arrays.asList(new String[] { "R00-R99" });
        List<String> g7 = Arrays.asList(new String[] { "Z00-Z99" });
        if (g1.contains(groupId)) {
            return DIAGNOSIS_CHART_GROUP_1;
        } else if (g2.contains(groupId)) {
            return DIAGNOSIS_CHART_GROUP_2;
        } else if (g3.contains(groupId)) {
            return DIAGNOSIS_CHART_GROUP_3;
        } else if (g4.contains(groupId)) {
            return DIAGNOSIS_CHART_GROUP_4;
        } else if (g5.contains(groupId)) {
            return DIAGNOSIS_CHART_GROUP_5;
        } else if (g6.contains(groupId)) {
            return DIAGNOSIS_CHART_GROUP_6;
        } else if (g7.contains(groupId)) {
            return DIAGNOSIS_CHART_GROUP_7;
        } else {
            // Unknown groups should never occur, but if it do than add it to
            // the Ovrigt-group (or fail in development)
            assert false;
            return DIAGNOSIS_CHART_GROUP_5;
        }
    }

    private TableData convertDiagnosisGroupsTableData(DiagnosisGroupResponse resp, Sex sex) {
        List<String> headers = resp.getDiagnosisGroupsAsStrings();
        ArrayList<NamedData> rows = new ArrayList<NamedData>();
        for (DiagnosisGroupRow row : resp.getRows()) {
            rows.add(new NamedData(row.getPeriod(), row.getDiagnosisGroupData(sex)));
        }
        return new TableData(rows, headers);
    }

    private TableData convertDiagnosisSubGroupsTableData(DiagnosisGroupResponse resp, Sex sex) {
        return convertDiagnosisGroupsTableData(resp, sex);
    }

    @GET
    @Path("getDiagnosisSubGroupStatistics")
    @Produces({ MediaType.APPLICATION_JSON })
    public DiagnosisGroupsData getDiagnosisSubGroupStatistics(@QueryParam("groupId") String groupId) {
        DiagnosisGroupResponse diagnosisGroups = datasourceDiagnosisSubGroups.getDiagnosisSubGroups(groupId);
        TableData maleTable = convertDiagnosisSubGroupsTableData(diagnosisGroups, Sex.Male);
        TableData femaleTable = convertDiagnosisSubGroupsTableData(diagnosisGroups, Sex.Female);
        List<Integer> topIndexes = getTopColumnIndexes(maleTable, femaleTable);
        TableData maleChart = extractChartData(maleTable, topIndexes);
        TableData femaleChart = extractChartData(femaleTable, topIndexes);
        return new DiagnosisGroupsData(maleTable, femaleTable, maleChart, femaleChart);
    }

    private TableData extractChartData(TableData data, List<Integer> topIndexes) {
        List<NamedData> topColumns = getTopColumns(data, topIndexes);
        return new TableData(topColumns, getDataRowNames(data));
    }

    private List<NamedData> getTopColumns(TableData data, List<Integer> topIndexes) {
        List<NamedData> topColumns = new ArrayList<>();
        for (Integer index : topIndexes) {
            List<Integer> indexData = getDataAtIndex(index, data);
            topColumns.add(new NamedData(data.getHeaders().get(index), indexData));
        }
        if (data.getHeaders().size() > NUMBER_OF_CHART_SERIES) {
            List<Integer> remainingData = sumRemaining(topIndexes, data);
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

    private List<Integer> sumRemaining(List<Integer> topIndexes, TableData data) {
        List<Integer> remaining = new ArrayList<>();
        for (int i = 0; i < data.getRows().size(); i++) {
            remaining.add(0);
        }
        List<NamedData> rows = data.getRows();
        for (int r = 0; r < rows.size(); r++) {
            List<Integer> data2 = rows.get(r).getData();
            for (int i = 0; i < data2.size(); i++) {
                if (!topIndexes.contains(i)) {
                    remaining.set(r, remaining.get(r).intValue() + data2.get(i).intValue());
                }
            }
        }
        return remaining;
    }

    private int sum(List<Integer> numbers) {
        int sum = 0;
        for (Integer number : numbers) {
            sum += number.intValue();
        }
        return sum;
    }

    private List<Integer> getDataAtIndex(Integer index, TableData data) {
        List<Integer> indexData = new ArrayList<>(data.getRows().size());
        List<NamedData> rows = data.getRows();
        for (NamedData tableRow : rows) {
            List<Integer> data2 = tableRow.getData();
            for (int i = 0; i < data2.size(); i++) {
                if (i == index) {
                    indexData.add(data2.get(i));
                }
            }
        }
        return indexData;
    }

    private List<Integer> getTopColumnIndexes(TableData maleData, TableData femaleData) {
        TreeMap<Integer, Integer> columnSums = new TreeMap<>();
        int dataSize = maleData.getRows().get(0).getData().size();
        for (int i = 0; i < dataSize; i++) {
            columnSums.put(sum(getDataAtIndex(i, maleData)) + sum(getDataAtIndex(i, femaleData)), i);
        }
        ArrayList<Integer> arrayList = new ArrayList<>(columnSums.descendingMap().values());
        return arrayList.subList(0, Math.min(NUMBER_OF_CHART_SERIES, arrayList.size()));
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
