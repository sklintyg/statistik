package se.inera.statistics.web.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import se.inera.statistics.service.report.model.CasesPerCountyResponse;
import se.inera.statistics.service.report.model.CasesPerCountyRow;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.Sex;
import se.inera.statistics.web.model.CasesPerCountyData;
import se.inera.statistics.web.model.ChartData;
import se.inera.statistics.web.model.ChartSeries;
import se.inera.statistics.web.model.NamedData;
import se.inera.statistics.web.model.TableData;
import se.inera.statistics.web.model.TableHeader;

public class CasesPerCountyConverter {

    private final CasesPerCountyResponse respNewest;
    private final CasesPerCountyResponse respOldest;
    private final String rangeTextOld;
    private final String rangeTextNew;

    public CasesPerCountyConverter(CasesPerCountyResponse respNewest, CasesPerCountyResponse respOldest, Range rangeNewest, Range rangeOldest) {
        assert respNewest.getRows().size() == respOldest.getRows().size();
        this.respNewest = respNewest;
        this.respOldest = respOldest;
        Locale sv = new Locale("sv", "SE");
        rangeTextOld = rangeOldest.getFrom().toString("MMM", sv) + "-" + rangeOldest.getTo().toString("MMM yyyy", sv);
        rangeTextNew = rangeNewest.getFrom().toString("MMM", sv) + "-" + rangeNewest.getTo().toString("MMM yyyy", sv);
    }

    private TableData convertToTable() {
        List<NamedData> data = new ArrayList<>();
        int accumulatedSumNewest = 0;
        int accumulatedSumOldest = 0;
        for (int i = 0; i < respNewest.getRows().size(); i++) {
            CasesPerCountyRow newestRow = respNewest.getRows().get(i);
            CasesPerCountyRow oldestRow = respOldest.getRows().get(i);
            assert newestRow.getName().equals(oldestRow.getName());

            int rowSumNewest = newestRow.getFemale() + newestRow.getMale();
            accumulatedSumNewest += rowSumNewest;
            int rowSumOldest = oldestRow.getFemale() + oldestRow.getMale();
            accumulatedSumOldest += rowSumOldest;

            final List<Integer> rowData = Arrays.asList(rowSumOldest, oldestRow.getFemale(), oldestRow.getMale(), accumulatedSumOldest,
                                                        rowSumNewest, newestRow.getFemale(), newestRow.getMale(), accumulatedSumNewest);
            data.add(new NamedData(oldestRow.getName(), rowData));
        }
        final int topHeaderSpan = 4;
        List<TableHeader> topHeaders = Arrays.asList(new TableHeader("", 1), new TableHeader(rangeTextOld, topHeaderSpan), new TableHeader(rangeTextNew, topHeaderSpan), new TableHeader("", 1));
        final List<String> subHeaderTexts = Arrays.asList("Län", "Antal sjukfall", "Antal kvinnor", "Antal män", "Summering", "Antal sjukfall", "Antal kvinnor", "Antal män", "Summering");
        List<TableHeader> subHeaders = TableData.toTableHeaderList(subHeaderTexts, 1);
        List<List<TableHeader>> headerRows = new ArrayList<>();
        headerRows.add(topHeaders);
        headerRows.add(subHeaders);
        return new TableData(data, headerRows);
    }

    private ChartData convertToChart() {
        assert respNewest.getGroups().equals(respOldest.getGroups());
        List<String> groups = respNewest.getGroups();
        ArrayList<ChartSeries> series = new ArrayList<>();
        List<Integer> femaleDataOld = respOldest.getDataForSex(Sex.Female);
        series.add(new ChartSeries("Sjukfall " + rangeTextOld + " Kvinnor", femaleDataOld, "old"));
        List<Integer> maleDataOld = respOldest.getDataForSex(Sex.Male);
        series.add(new ChartSeries("Sjukfall " + rangeTextOld + " Män", maleDataOld, "old"));
        List<Integer> femaleDataNew = respNewest.getDataForSex(Sex.Female);
        series.add(new ChartSeries("Sjukfall " + rangeTextNew + " Kvinnor", femaleDataNew, "new"));
        List<Integer> maleDataNew = respNewest.getDataForSex(Sex.Male);
        series.add(new ChartSeries("Sjukfall " + rangeTextNew + " Män", maleDataNew, "new"));
        return new ChartData(series, groups);
    }

    CasesPerCountyData convert() {
        TableData tableData = convertToTable();
        ChartData chartData = convertToChart();
        int monthsIncluded = respOldest.getNumberOfMonthsCalculated() + respNewest.getNumberOfMonthsCalculated();
        return new CasesPerCountyData(tableData, chartData, monthsIncluded);
    }
}
