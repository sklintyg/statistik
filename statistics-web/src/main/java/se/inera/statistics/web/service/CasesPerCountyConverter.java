package se.inera.statistics.web.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.Sex;
import se.inera.statistics.service.report.model.SimpleDualSexDataRow;
import se.inera.statistics.service.report.model.SimpleDualSexResponse;
import se.inera.statistics.web.model.CasesPerCountyData;
import se.inera.statistics.web.model.ChartData;
import se.inera.statistics.web.model.ChartSeries;
import se.inera.statistics.web.model.NamedData;
import se.inera.statistics.web.model.TableData;
import se.inera.statistics.web.model.TableHeader;

public class CasesPerCountyConverter {

    private final SimpleDualSexResponse<SimpleDualSexDataRow> respNewest;
    private final SimpleDualSexResponse<SimpleDualSexDataRow> respOldest;
    private final Range rangeOld;
    private final Range rangeNew;

    public CasesPerCountyConverter(SimpleDualSexResponse<SimpleDualSexDataRow> respNewest, SimpleDualSexResponse<SimpleDualSexDataRow> respOldest, Range rangeNewest, Range rangeOldest) {
        assert respNewest.getRows().size() == respOldest.getRows().size();
        this.respNewest = respNewest;
        this.respOldest = respOldest;
        this.rangeNew = rangeNewest;
        this.rangeOld = rangeOldest;
    }

    private TableData convertToTable() {
        List<NamedData> data = new ArrayList<>();
        int accumulatedSumNewest = 0;
        int accumulatedSumOldest = 0;
        for (int i = 0; i < respNewest.getRows().size(); i++) {
            SimpleDualSexDataRow newestRow = respNewest.getRows().get(i);
            SimpleDualSexDataRow oldestRow = respOldest.getRows().get(i);
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
        List<TableHeader> topHeaders = Arrays.asList(new TableHeader("", 1), new TableHeader(rangeOld.toStringAbbreviated(), topHeaderSpan), new TableHeader(rangeNew.toStringAbbreviated(), topHeaderSpan), new TableHeader("", 1));
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
        List<ChartSeries> series = new ArrayList<>();
        List<Integer> femaleDataOld = respOldest.getDataForSex(Sex.Female);
        series.add(new ChartSeries("Sjukfall " + rangeOld.toStringAbbreviated() + " kvinnor", femaleDataOld, "old", Sex.Female));
        List<Integer> maleDataOld = respOldest.getDataForSex(Sex.Male);
        series.add(new ChartSeries("Sjukfall " + rangeOld.toStringAbbreviated() + " män", maleDataOld, "old", Sex.Male));
        List<Integer> femaleDataNew = respNewest.getDataForSex(Sex.Female);
        series.add(new ChartSeries("Sjukfall " + rangeNew.toStringAbbreviated() + " kvinnor", femaleDataNew, "new", Sex.Female));
        List<Integer> maleDataNew = respNewest.getDataForSex(Sex.Male);
        series.add(new ChartSeries("Sjukfall " + rangeNew.toStringAbbreviated() + " män", maleDataNew, "new", Sex.Male));
        return new ChartData(series, groups);
    }

    CasesPerCountyData convert() {
        TableData tableData = convertToTable();
        ChartData chartData = convertToChart();
        Range fullRange = new Range(rangeOld.getFrom(), rangeNew.getTo());
        return new CasesPerCountyData(tableData, chartData, fullRange.getMonths(), fullRange.toString());
    }
}
