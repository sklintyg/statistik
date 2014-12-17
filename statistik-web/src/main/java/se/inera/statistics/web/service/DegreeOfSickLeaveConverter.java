/**
 * Copyright (C) 2014 Inera AB (http://www.inera.se)
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
import java.util.List;

import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.KonDataRow;
import se.inera.statistics.service.report.model.SjukskrivningsgradResponse;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.web.model.ChartData;
import se.inera.statistics.web.model.ChartSeries;
import se.inera.statistics.web.model.DualSexStatisticsData;
import se.inera.statistics.web.model.NamedData;
import se.inera.statistics.web.model.TableData;
import se.inera.statistics.web.model.TableHeader;

public class DegreeOfSickLeaveConverter {

    DualSexStatisticsData convert(SjukskrivningsgradResponse degreeOfSickLeave, Range range) {
        TableData tableData = convertTable(degreeOfSickLeave);
        ChartData maleChart = extractChartData(degreeOfSickLeave, Kon.Male);
        ChartData femaleChart = extractChartData(degreeOfSickLeave, Kon.Female);
        return new DualSexStatisticsData(tableData, maleChart, femaleChart, range.toString());
    }

    private ChartData extractChartData(SjukskrivningsgradResponse data, Kon sex) {
        List<ChartSeries> series = getChartSeries(data, sex);
        return new ChartData(series, data.getPeriods());
    }

    private List<ChartSeries> getChartSeries(SjukskrivningsgradResponse data, Kon sex) {
        List<ChartSeries> series = new ArrayList<>();
        for (int i = 0; i < data.getDegreesOfSickLeave().size(); i++) {
            List<Integer> indexData = data.getDataFromIndex(i, sex);
            series.add(new ChartSeries("Antal sjukfall med " + data.getDegreesOfSickLeave().get(i) + "% sjukskrivningsgrad", indexData, true));
        }
        return series;
    }

    static TableData convertTable(SjukskrivningsgradResponse resp) {
        List<NamedData> rows = getTableRows(resp);
        List<List<TableHeader>> headers = getTableHeaders(resp);
        return new TableData(rows, headers);
    }

    private static List<NamedData> getTableRows(SjukskrivningsgradResponse resp) {
        List<NamedData> rows = new ArrayList<>();
        for (KonDataRow row : resp.getRows()) {
            List<Integer> mergedSexData = ServiceUtil.getMergedSexData(row);
            int sum = 0;
            for (Integer dataField : mergedSexData) {
                sum += dataField;
            }
            mergedSexData.add(0, sum);
            rows.add(new NamedData(row.getName(), mergedSexData));
        }
        return rows;
    }

    private static List<List<TableHeader>> getTableHeaders(SjukskrivningsgradResponse resp) {
        List<TableHeader> topHeaderRow = new ArrayList<>();
        topHeaderRow.add(new TableHeader(""));
        topHeaderRow.add(new TableHeader(""));
        List<String> degreesOfSickLeave = resp.getDegreesOfSickLeave();
        for (String groupName : degreesOfSickLeave) {
                topHeaderRow.add(new TableHeader("Antal sjukfall med " + groupName + "% sjukskrivningsgrad", 2));
        }

        List<TableHeader> subHeaderRow = new ArrayList<>();
        subHeaderRow.add(new TableHeader("Period"));
        subHeaderRow.add(new TableHeader("Antal sjukfall totalt"));
        for (String s : degreesOfSickLeave) {
            subHeaderRow.add(new TableHeader("Kvinnor"));
            subHeaderRow.add(new TableHeader("Män"));
        }

        List<List<TableHeader>> headers = new ArrayList<>();
        headers.add(topHeaderRow);
        headers.add(subHeaderRow);
        return headers;
    }

}
