/**
 * Copyright (C) 2015 Inera AB (http://www.inera.se)
 *
 * This file is part of statistik (https://github.com/sklintyg/statistik).
 *
 * statistik is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * statistik is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.web.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.web.model.CasesPerCountyData;
import se.inera.statistics.web.model.ChartData;
import se.inera.statistics.web.model.ChartSeries;
import se.inera.statistics.web.model.NamedData;
import se.inera.statistics.web.model.TableData;
import se.inera.statistics.web.model.TableHeader;

public class CasesPerCountyConverter {

    private final SimpleKonResponse<SimpleKonDataRow> respNewest;
    private final SimpleKonResponse<SimpleKonDataRow> respOldest;
    private final Range rangeOld;
    private final Range rangeNew;

    public CasesPerCountyConverter(SimpleKonResponse<SimpleKonDataRow> respNewest, SimpleKonResponse<SimpleKonDataRow> respOldest, Range rangeNewest, Range rangeOldest) {
        assert respNewest.getRows().size() == respOldest.getRows().size();
        this.respNewest = respNewest;
        this.respOldest = respOldest;
        this.rangeNew = rangeNewest;
        this.rangeOld = rangeOldest;
    }

    private TableData convertToTable() {
        List<NamedData> data = new ArrayList<>();
        for (int i = 0; i < respNewest.getRows().size(); i++) {
            SimpleKonDataRow newestRow = respNewest.getRows().get(i);
            SimpleKonDataRow oldestRow = respOldest.getRows().get(i);
            assert newestRow.getName().equals(oldestRow.getName());

            int rowSumNewest = newestRow.getFemale() + newestRow.getMale();
            int rowSumOldest = oldestRow.getFemale() + oldestRow.getMale();

            final List<Integer> rowData = Arrays.asList(rowSumOldest, oldestRow.getFemale(), oldestRow.getMale(),
                                                        rowSumNewest, newestRow.getFemale(), newestRow.getMale());
            data.add(new NamedData(oldestRow.getName(), rowData));
        }

        final int topHeaderSpan = 3;
        List<TableHeader> topHeaders = Arrays.asList(new TableHeader("", 1), new TableHeader(rangeOld.toStringAbbreviated(), topHeaderSpan), new TableHeader(rangeNew.toStringAbbreviated(), topHeaderSpan));
        final List<String> subHeaderTexts = Arrays.asList("Län", "Antal sjukfall totalt", "Antal sjukfall för kvinnor", "Antal sjukfall för män", "Antal sjukfall", "Antal sjukfall för kvinnor", "Antal sjukfall för män");
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
        List<Integer> femaleDataOld = respOldest.getDataForSex(Kon.Female);
        series.add(new ChartSeries("Sjukfall " + rangeOld.toStringAbbreviated() + " kvinnor", femaleDataOld, "old", Kon.Female));
        List<Integer> maleDataOld = respOldest.getDataForSex(Kon.Male);
        series.add(new ChartSeries("Sjukfall " + rangeOld.toStringAbbreviated() + " män", maleDataOld, "old", Kon.Male));
        List<Integer> femaleDataNew = respNewest.getDataForSex(Kon.Female);
        series.add(new ChartSeries("Sjukfall " + rangeNew.toStringAbbreviated() + " kvinnor", femaleDataNew, "new", Kon.Female));
        List<Integer> maleDataNew = respNewest.getDataForSex(Kon.Male);
        series.add(new ChartSeries("Sjukfall " + rangeNew.toStringAbbreviated() + " män", maleDataNew, "new", Kon.Male));
        return new ChartData(series, groups);
    }

    CasesPerCountyData convert() {
        TableData tableData = convertToTable();
        ChartData chartData = convertToChart();
        Range fullRange = new Range(rangeOld.getFrom(), rangeNew.getTo());
        return new CasesPerCountyData(tableData, chartData, fullRange.toString(), new FilterDataResponse(null, null));
    }
}
