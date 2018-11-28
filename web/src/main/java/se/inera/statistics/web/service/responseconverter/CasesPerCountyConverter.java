/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.web.service.responseconverter;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import se.inera.statistics.service.countypopulation.CountyPopulation;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.KonField;
import se.inera.statistics.service.report.model.Lan;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.web.MessagesText;
import se.inera.statistics.web.model.CasesPerCountyData;
import se.inera.statistics.web.model.ChartCategory;
import se.inera.statistics.web.model.ChartData;
import se.inera.statistics.web.model.ChartSeries;
import se.inera.statistics.web.model.NamedData;
import se.inera.statistics.web.model.TableData;
import se.inera.statistics.web.model.TableHeader;

import com.google.common.collect.Lists;
import se.inera.statistics.web.service.FilterDataResponse;

public class CasesPerCountyConverter {

    private static final float THOUSAND = 1000F;
    private static final String SAMTLIGA_LAN = MessagesText.REPORT_SAMTLIGA_LAN;
    private static final String LAN = MessagesText.REPORT_LAN;
    private static final String H_ANTAL_SJUKFALL = MessagesText.REPORT_ANTAL_SJUKFALL;
    private static final String H_ANTAL_INVANARE = MessagesText.REPORT_ANTAL_INVANARE;
    private static final String H_ANTAL_SJUKFALL_PER_1000_INVANARE = MessagesText.REPORT_ANTAL_SJUKFALL_PER_1000_INVANARE;
    private static final String H_TOTALT = MessagesText.REPORT_GROUP_TOTALT;
    private static final String H_KVINNOR = MessagesText.REPORT_GROUP_FEMALE;
    private static final String H_MAN = MessagesText.REPORT_GROUP_MALE;
    private static final int COLSPAN = 3;
    private final SimpleKonResponse resp;
    private final Range range;
    private final CountyPopulation countyPopulation;

    public CasesPerCountyConverter(SimpleKonResponse sjukfallPerLan, CountyPopulation countyPopulation, Range range) {
        this.resp = sjukfallPerLan;
        this.range = range;
        this.countyPopulation = countyPopulation;
    }

    private TableData convertToTable() {
        List<NamedData> data = new ArrayList<>();

        final List<Object> samtligaLanRowData = getSamtligaLanRowData();
        data.add(new NamedData(SAMTLIGA_LAN, samtligaLanRowData));

        for (int i = 0; i < resp.getRows().size(); i++) {
            SimpleKonDataRow row = resp.getRows().get(i);

            final KonField populationObject = countyPopulation.getPopulationPerCountyCode().get(row.getExtras().toString());

            final int totalNumberOfSjukfall = row.getFemale() + row.getMale();
            final int totalPopulation = populationObject == null ? 0 : populationObject.getMale() + populationObject.getFemale();
            final Object totalPopulationValueToShow = populationObject != null ? totalPopulation : "-";
            final Object totalSjukfallPerKPopulation = calculateSjukfallPerKPopulation(populationObject, totalNumberOfSjukfall,
                    totalPopulation);

            final int femaleNumberOfSjukfall = row.getFemale();
            final int femalePopulation = populationObject == null ? 0 : populationObject.getFemale();
            final Object femalePopulationValueToShow = populationObject != null ? femalePopulation : "-";
            final Object femaleSjukfallPerKPopulation = calculateSjukfallPerKPopulation(populationObject, femaleNumberOfSjukfall,
                    femalePopulation);

            final int maleNumberOfSjukfall = row.getMale();
            final int malePopulation = populationObject == null ? 0 : populationObject.getMale();
            final Object malePopulationValueToShow = populationObject != null ? malePopulation : "-";
            final Object maleSjukfallPerKPopulation = calculateSjukfallPerKPopulation(populationObject, maleNumberOfSjukfall,
                    malePopulation);

            final List<Object> rowData = Arrays.asList(
                    totalNumberOfSjukfall, femaleNumberOfSjukfall, maleNumberOfSjukfall,
                    totalPopulationValueToShow, femalePopulationValueToShow, malePopulationValueToShow,
                    totalSjukfallPerKPopulation, femaleSjukfallPerKPopulation, maleSjukfallPerKPopulation);
            if (!Lan.OVRIGT_ID.equals(row.getExtras()) || totalNumberOfSjukfall > 0) {
                data.add(new NamedData(row.getName(), rowData));
            }
        }

        List<TableHeader> headers1 = Arrays.asList(new TableHeader(""), new TableHeader(H_ANTAL_SJUKFALL, COLSPAN),
                new TableHeader(H_ANTAL_INVANARE, COLSPAN), new TableHeader(H_ANTAL_SJUKFALL_PER_1000_INVANARE, COLSPAN));
        final List<String> headerTexts = Arrays.asList(LAN, H_TOTALT, H_KVINNOR, H_MAN, H_TOTALT, H_KVINNOR, H_MAN, H_TOTALT, H_KVINNOR,
                H_MAN);
        List<TableHeader> headers2 = TableData.toTableHeaderList(headerTexts, 1);
        List<List<TableHeader>> headerRows = new ArrayList<>();
        headerRows.add(headers1);
        headerRows.add(headers2);
        return new TableData(data, headerRows);
    }

    private Object calculateSjukfallPerKPopulation(KonField populationObject, int numberOfSjukfall, int population) {
        final boolean calculateThisRow = population != 0 || populationObject != null;
        return calculateThisRow
                ? SjukfallPerPatientsPerEnhetConverter.roundToTwoDecimalsAndFormatToString(numberOfSjukfall / (population / THOUSAND))
                : "-";
    }

    private List<Object> getSamtligaLanRowData() {
        final int totalSjukfall = resp.getRows().stream().mapToInt(value -> value.getFemale() + value.getMale()).sum();
        final int totalPopulation = countyPopulation.getPopulationPerCountyCode().values().stream()
                .mapToInt(value -> value.getFemale() + value.getMale()).sum();
        final String totalSjukfallPerKPopulationForAllLan = SjukfallPerPatientsPerEnhetConverter
                .roundToTwoDecimalsAndFormatToString(((float) totalSjukfall) / (totalPopulation / THOUSAND));

        final int femaleSjukfall = resp.getRows().stream().mapToInt(value -> value.getFemale()).sum();
        final int femalePopulation = countyPopulation.getPopulationPerCountyCode().values().stream().mapToInt(value -> value.getFemale())
                .sum();
        final String femaleSjukfallPerKPopulationForAllLan = SjukfallPerPatientsPerEnhetConverter
                .roundToTwoDecimalsAndFormatToString(((float) femaleSjukfall) / (femalePopulation / THOUSAND));

        final int maleSjukfall = resp.getRows().stream().mapToInt(value -> value.getMale()).sum();
        final int malePopulation = countyPopulation.getPopulationPerCountyCode().values().stream().mapToInt(value -> value.getMale()).sum();
        final String maleSjukfallPerKPopulationForAllLan = SjukfallPerPatientsPerEnhetConverter
                .roundToTwoDecimalsAndFormatToString(((float) maleSjukfall) / (malePopulation / THOUSAND));

        return Arrays.asList(
                totalSjukfall, femaleSjukfall, maleSjukfall,
                totalPopulation, femalePopulation, malePopulation,
                totalSjukfallPerKPopulationForAllLan, femaleSjukfallPerKPopulationForAllLan, maleSjukfallPerKPopulationForAllLan);
    }

    private ChartData convertToChart() {
        final List<String> chartGroups = new ArrayList<>(resp.getGroups());
        final List<SimpleKonDataRow> chartRows = new ArrayList<>(resp.getRows());

        final int ovrigaLanIndex = getIndexOfLan(resp, Lan.OVRIGT_ID);
        if (ovrigaLanIndex > -1) {
            chartGroups.remove(ovrigaLanIndex);
            chartRows.remove(ovrigaLanIndex);
        }

        final List<ChartCategory> groups = new ArrayList<>(Lists.transform(chartGroups, ChartCategory::new));
        groups.add(0, new ChartCategory(SAMTLIGA_LAN));
        final double sjukfallPerKPopulationForAllLanFemale = getSjukfallPerKPopulationForAllLanFemale(resp.getRows());
        final double sjukfallPerKPopulationForAllLanMale = getSjukfallPerKPopulationForAllLanMale(resp.getRows());
        final double sjukfallPerKPopulationForAllLanTotal = getSjukfallPerKPopulationForAllLanTotal(resp.getRows());

        List<ChartSeries> series = new ArrayList<>();
        final Stream<Number> sjukfallPerKPopulationStreamFemale = chartRows.stream().map(this::getSjukfallPerKPopulationFemale);
        final List<Number> sjukfallPerPopulationFemale = Stream
                .concat(Stream.of(sjukfallPerKPopulationForAllLanFemale), sjukfallPerKPopulationStreamFemale).collect(Collectors.toList());

        final Stream<Number> sjukfallPerKPopulationStreamMale = chartRows.stream().map(this::getSjukfallPerKPopulationMale);
        final List<Number> sjukfallPerPopulationMale = Stream
                .concat(Stream.of(sjukfallPerKPopulationForAllLanMale), sjukfallPerKPopulationStreamMale).collect(Collectors.toList());

        final Stream<Number> sjukfallPerKPopulationStreamTotal = chartRows.stream().map(this::getSjukfallPerKPopulationTotal);
        final List<Number> sjukfallPerPopulationTotal = Stream
                .concat(Stream.of(sjukfallPerKPopulationForAllLanTotal), sjukfallPerKPopulationStreamTotal).collect(Collectors.toList());

        series.add(new ChartSeries(H_TOTALT, sjukfallPerPopulationTotal));
        series.add(new ChartSeries(H_KVINNOR, sjukfallPerPopulationFemale, Kon.FEMALE));
        series.add(new ChartSeries(H_MAN, sjukfallPerPopulationMale, Kon.MALE));
        return new ChartData(series, groups);
    }

    private int getIndexOfLan(SimpleKonResponse resp, String lanId) {
        final List<SimpleKonDataRow> rows = resp.getRows();
        for (int i = 0; i < rows.size(); i++) {
            SimpleKonDataRow row = rows.get(i);
            if (lanId.equals(row.getExtras().toString())) {
                return i;
            }
        }
        return -1;
    }

    private double getSjukfallPerKPopulationForAllLanTotal(List<SimpleKonDataRow> rows) {
        final int totalSjukfall = rows.stream().mapToInt(row -> row.getMale() + row.getFemale()).sum();
        final int totalPopulation = countyPopulation.getPopulationPerCountyCode().values().stream()
                .mapToInt(field -> field.getMale() + field.getFemale()).sum();
        return calculateSjukfallPerKPopulation(totalSjukfall, totalPopulation);
    }

    private double getSjukfallPerKPopulationForAllLanFemale(List<SimpleKonDataRow> rows) {
        final int totalSjukfall = rows.stream().mapToInt(SimpleKonDataRow::getFemale).sum();
        final int totalPopulation = countyPopulation.getPopulationPerCountyCode().values().stream().mapToInt(KonField::getFemale).sum();
        return calculateSjukfallPerKPopulation(totalSjukfall, totalPopulation);
    }

    private double getSjukfallPerKPopulationForAllLanMale(List<SimpleKonDataRow> rows) {
        final int totalSjukfall = rows.stream().mapToInt(SimpleKonDataRow::getMale).sum();
        final int totalPopulation = countyPopulation.getPopulationPerCountyCode().values().stream().mapToInt(KonField::getMale).sum();
        return calculateSjukfallPerKPopulation(totalSjukfall, totalPopulation);
    }

    private double getSjukfallPerKPopulationTotal(SimpleKonDataRow row) {
        final int rowSum = row.getFemale() + row.getMale();
        final KonField populationObject = countyPopulation.getPopulationPerCountyCode().get(row.getExtras().toString());
        final int population = populationObject == null ? 0 : populationObject.getFemale() + populationObject.getMale();
        return calculateSjukfallPerKPopulation(rowSum, population);
    }

    private double getSjukfallPerKPopulationFemale(SimpleKonDataRow row) {
        final int rowSum = row.getFemale();
        final KonField populationObject = countyPopulation.getPopulationPerCountyCode().get(row.getExtras().toString());
        final int population = populationObject == null ? 0 : populationObject.getFemale();
        return calculateSjukfallPerKPopulation(rowSum, population);
    }

    private double getSjukfallPerKPopulationMale(SimpleKonDataRow row) {
        final int rowSum = row.getMale();
        final KonField populationObject = countyPopulation.getPopulationPerCountyCode().get(row.getExtras().toString());
        final int population = populationObject == null ? 0 : populationObject.getMale();
        return calculateSjukfallPerKPopulation(rowSum, population);
    }

    private double calculateSjukfallPerKPopulation(int numberOfSjukfall, int population) {
        if (population == 0) {
            return 0D;
        }
        return SjukfallPerPatientsPerEnhetConverter.roundToTwoDecimals((float) numberOfSjukfall / (population / THOUSAND));
    }

    public CasesPerCountyData convert() {
        TableData tableData = convertToTable();
        ChartData chartData = convertToChart();
        Range fullRange = new Range(range.getFrom(), range.getTo());
        final String originDate = countyPopulation.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return new CasesPerCountyData(tableData, chartData, fullRange.toString(), resp.getAvailableFilters(),
                FilterDataResponse.empty(), originDate);
    }

}
