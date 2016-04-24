/**
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
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
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import se.inera.statistics.service.report.model.Lan;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.web.model.CasesPerCountyData;
import se.inera.statistics.web.model.ChartCategory;
import se.inera.statistics.web.model.ChartData;
import se.inera.statistics.web.model.ChartSeries;
import se.inera.statistics.web.model.NamedData;
import se.inera.statistics.web.model.TableData;
import se.inera.statistics.web.model.TableHeader;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class CasesPerCountyConverter {

    public static final float THOUSAND = 1000F;
    public static final String SAMTLIGA_LAN = "Samtliga län";
    public static final String LAN = "Län";
    public static final String ANTAL_SJUKFALL = "Antal sjukfall";
    public static final String ANTAL_INVANARE = "Antal invånare";
    public static final String ANTAL_SJUKFALL_PER_1000_INVANARE = "Antal sjukfall per 1000 invånare";
    private final SimpleKonResponse<SimpleKonDataRow> resp;
    private final Range range;
    private final Map<String, Integer> populationPerCounty;

    public CasesPerCountyConverter(SimpleKonResponse<SimpleKonDataRow> sjukfallPerLan, Map<String, Integer> populationPerCounty, Range range) {
        this.resp = sjukfallPerLan;
        this.range = range;
        this.populationPerCounty = populationPerCounty;
    }

    private TableData convertToTable() {
        List<NamedData> data = new ArrayList<>();

        final List<Object> samtligaLanRowData = getSamtligaLanRowData();
        data.add(new NamedData(SAMTLIGA_LAN, samtligaLanRowData));

        for (int i = 0; i < resp.getRows().size(); i++) {
            SimpleKonDataRow row = resp.getRows().get(i);

            final int numberOfSjukfall = row.getFemale() + row.getMale();
            final Integer populationObject = populationPerCounty.get(row.getExtras().toString());
            final int population = populationObject == null ? 0 : populationObject;

            final boolean calculateThisRow = population != 0 || populationObject != null;
            final Object sjukfallPerKPopulation = calculateThisRow ? SjukfallPerPatientsPerEnhetConverter.roundToTwoDecimalsAndFormatToString(numberOfSjukfall / (population / THOUSAND)) : "-";

            final Object populationValueToShow = populationObject != null ? population : "-";
            final List<Object> rowData = Arrays.asList(numberOfSjukfall, populationValueToShow, sjukfallPerKPopulation);
            if (!Lan.OVRIGT_ID.equals(row.getExtras()) || numberOfSjukfall > 0) {
                data.add(new NamedData(row.getName(), rowData));
            }
        }

        final List<String> headerTexts = Arrays.asList(LAN, ANTAL_SJUKFALL, ANTAL_INVANARE, ANTAL_SJUKFALL_PER_1000_INVANARE);
        List<TableHeader> headers = TableData.toTableHeaderList(headerTexts, 1);
        List<List<TableHeader>> headerRows = new ArrayList<>();
        headerRows.add(headers);
        return new TableData(data, headerRows);
    }

    private List<Object> getSamtligaLanRowData() {
        final int totalSjukfall = resp.getRows().stream().mapToInt(value -> value.getFemale() + value.getMale()).sum();
        final int totalPopulation = populationPerCounty.values().stream().mapToInt(value -> value).sum();
        final String sjukfallPerKPopulationForAllLan = SjukfallPerPatientsPerEnhetConverter.roundToTwoDecimalsAndFormatToString(((float) totalSjukfall) / (totalPopulation / THOUSAND));
        return Arrays.asList(totalSjukfall, totalPopulation, sjukfallPerKPopulationForAllLan);
    }

    private ChartData convertToChart() {
        final List<String> chartGroups = new ArrayList<>(resp.getGroups());
        final List<SimpleKonDataRow> chartRows = new ArrayList<>(resp.getRows());

        final int ovrigaLanIndex = getIndexOfLan(resp, Lan.OVRIGT_ID);
        if (ovrigaLanIndex > -1) {
            chartGroups.remove(ovrigaLanIndex);
            chartRows.remove(ovrigaLanIndex);
        }

        final List<ChartCategory> groups = new ArrayList<>(Lists.transform(chartGroups, new Function<String, ChartCategory>() {
            @Override
            public ChartCategory apply(String name) {
                return new ChartCategory(name);
            }
        }));
        groups.add(0, new ChartCategory(SAMTLIGA_LAN));
        final double sjukfallPerKPopulationForAllLan = getSjukfallPerKPopulationForAllLan(resp.getRows());

        List<ChartSeries> series = new ArrayList<>();
        final Stream<Number> sjukfallPerKPopulationStream = chartRows.stream().map(this::getSjukfallPerKPopulation).map(SjukfallPerPatientsPerEnhetConverter::roundToTwoDecimals);
        final List<Number> sjukfallPerPopulation = Stream.concat(Stream.of(sjukfallPerKPopulationForAllLan), sjukfallPerKPopulationStream).collect(Collectors.toList());

        series.add(new ChartSeries(ANTAL_SJUKFALL_PER_1000_INVANARE, sjukfallPerPopulation, false));
        return new ChartData(series, groups);
    }

    private int getIndexOfLan(SimpleKonResponse<SimpleKonDataRow> resp, String lanId) {
        final List<SimpleKonDataRow> rows = resp.getRows();
        for (int i = 0; i < rows.size(); i++) {
            SimpleKonDataRow row = rows.get(i);
            if (lanId.equals(row.getExtras().toString())) {
                return i;
            }
        }
        return -1;
    }

    private double getSjukfallPerKPopulationForAllLan(List<SimpleKonDataRow> rows) {
        final int totalSjukfall = rows.stream().mapToInt(value -> value.getFemale() + value.getMale()).sum();
        final int totalPopulation = populationPerCounty.values().stream().mapToInt(value -> value).sum();
        if (totalPopulation == 0) {
            return 0F;
        }
        return SjukfallPerPatientsPerEnhetConverter.roundToTwoDecimals(((float) totalSjukfall) / (totalPopulation / THOUSAND));
    }

    private Float getSjukfallPerKPopulation(SimpleKonDataRow row) {
        final int rowSum = row.getFemale() + row.getMale();
        final Integer populationObject = populationPerCounty.get(row.getExtras().toString());
        final int population = populationObject == null ? 0 : populationObject;
        if (population == 0) {
            return 0F;
        }
        return rowSum / (population / THOUSAND);
    }

    CasesPerCountyData convert() {
        TableData tableData = convertToTable();
        ChartData chartData = convertToChart();
        Range fullRange = new Range(range.getFrom(), range.getTo());
        return new CasesPerCountyData(tableData, chartData, fullRange.toString(), FilterDataResponse.empty());
    }

}
