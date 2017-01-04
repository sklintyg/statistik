/**
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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

import com.google.common.collect.Maps;
import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.service.landsting.persistance.landstingenhet.LandstingEnhet;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.web.error.Message;
import se.inera.statistics.web.model.ChartCategory;
import se.inera.statistics.web.model.ChartData;
import se.inera.statistics.web.model.ChartSeries;
import se.inera.statistics.web.model.NamedData;
import se.inera.statistics.web.model.SimpleDetailsData;
import se.inera.statistics.web.model.TableData;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SjukfallPerPatientsPerEnhetConverter {

    private final Map<HsaIdEnhet, Integer> listningarPerEnhet;
    private final List<HsaIdEnhet> connectedEnhetIds;

    public SjukfallPerPatientsPerEnhetConverter(List<LandstingEnhet> landstingEnhets, List<HsaIdEnhet> connectedEnhetIds) {
        this.connectedEnhetIds = connectedEnhetIds;
        if (landstingEnhets == null) {
            listningarPerEnhet = Collections.emptyMap();
            return;
        }
        listningarPerEnhet = Maps.newHashMapWithExpectedSize(landstingEnhets.size());
        for (LandstingEnhet landstingEnhet : landstingEnhets) {
            final Integer listadePatienterObj = landstingEnhet.getListadePatienter();
            final int listadePatienter = listadePatienterObj == null ? 0 : listadePatienterObj;
            if (listadePatienter > 0) {
                listningarPerEnhet.put(landstingEnhet.getEnhetensHsaId(), listadePatienter);
            }
        }
    }

    public SimpleDetailsData convert(SimpleKonResponse<SimpleKonDataRow> casesPerMonth, FilterSettings filterSettings, Message message) {
        TableData tableData = convertToTableData(casesPerMonth.getRows());
        ChartData chartData = convertToChartData(casesPerMonth);
        final Filter filter = filterSettings.getFilter();
        final FilterDataResponse filterResponse = new FilterDataResponse(filter);
        final Range range = filterSettings.getRange();
        final List<Message> combinedMessage = Converters.combineMessages(filterSettings.getMessage(), message);
        return new SimpleDetailsData(tableData, chartData, range.toString(), filterResponse, combinedMessage);
    }

    private TableData convertToTableData(List<SimpleKonDataRow> list) {
        Collections.sort(list, new Comparator<SimpleKonDataRow>() {
            @Override
            public int compare(SimpleKonDataRow o1, SimpleKonDataRow o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });
        List<NamedData> data = new ArrayList<>();
        for (SimpleKonDataRow row : list) {
            final HsaIdEnhet enhetId = (HsaIdEnhet) row.getExtras();
            final Integer listadePatienter = listningarPerEnhet.get(enhetId);
            if (listadePatienter != null) {
                final Integer female = row.getFemale();
                final Integer male = row.getMale();
                final int antalSjukfall = female + male;
                final float thousand = 1000F;
                final float sjukfallPerThousandListadePatienter = antalSjukfall / (listadePatienter / thousand);
                final String sjukfallPerThousandListadePatienterRounded = roundToTwoDecimalsAndFormatToString(sjukfallPerThousandListadePatienter);
                data.add(new NamedData(row.getName(), Arrays.asList(antalSjukfall, listadePatienter, sjukfallPerThousandListadePatienterRounded), isMarked(row)));
            }
        }
        return TableData.createWithSingleHeadersRow(data, Arrays.asList("Vårdenhet", "Antal sjukfall", "Antal listningar", "Antal sjukfall per 1000 listningar"));
    }

    static String roundToTwoDecimalsAndFormatToString(float number) {
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
        decimalFormatSymbols.setDecimalSeparator(',');
        DecimalFormat twoDecimalsFormat = new DecimalFormat("0.00", decimalFormatSymbols);
        return twoDecimalsFormat.format(number);
    }

    static double roundToTwoDecimals(double number) {
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
        DecimalFormat twoDecimalsFormat = new DecimalFormat("0.00", decimalFormatSymbols);
        return Double.valueOf(twoDecimalsFormat.format(number));
    }

    private ChartData convertToChartData(SimpleKonResponse<SimpleKonDataRow> casesPerMonth) {
        final ArrayList<ChartCategory> categories = new ArrayList<>();
        final List<Number> filteredSummedData = new ArrayList<>();

        final ArrayList<EnhetWithPatients> enhetsWithPatients = toSortedEnhetsWithPatients(casesPerMonth);

        for (EnhetWithPatients enhet : enhetsWithPatients) {
            final SimpleKonDataRow dataRow = enhet.getRow();
            final String name = dataRow.getName();
            categories.add(new ChartCategory(name, isMarked(dataRow)));

            final float sjukfallPerThousandListadePatienter = enhet.getSjukfallPerThousandListadePatienter();
            filteredSummedData.add(roundToTwoDecimals(sjukfallPerThousandListadePatienter));
        }

        if (filteredSummedData.isEmpty()) {
            filteredSummedData.add(0);
            categories.add(new ChartCategory("Totalt"));
        }

        final ArrayList<ChartSeries> series = new ArrayList<>();
        series.add(new ChartSeries("Antal sjukfall per 1000 listningar", filteredSummedData));

        return new ChartData(series, categories);
    }

    private ArrayList<EnhetWithPatients> toSortedEnhetsWithPatients(SimpleKonResponse<SimpleKonDataRow> casesPerMonth) {
        final ArrayList<EnhetWithPatients> enhetsWithPatients = toEnhetsWithPatients(casesPerMonth);
        Collections.sort(enhetsWithPatients, new Comparator<EnhetWithPatients>() {
            @Override
            public int compare(EnhetWithPatients o1, EnhetWithPatients o2) {
                return Float.compare(o2.getSjukfallPerThousandListadePatienter(), o1.getSjukfallPerThousandListadePatienter());
            }
        });
        return enhetsWithPatients;
    }

    private ArrayList<EnhetWithPatients> toEnhetsWithPatients(SimpleKonResponse<SimpleKonDataRow> casesPerMonth) {
        final ArrayList<EnhetWithPatients> enhetsWithPatients = new ArrayList<>();
        final List<Integer> summedData = casesPerMonth.getSummedData();
        for (int i = 0; i < casesPerMonth.getRows().size(); i++) {
            final SimpleKonDataRow dataRow = casesPerMonth.getRows().get(i);
            final HsaIdEnhet enhetId = (HsaIdEnhet) dataRow.getExtras();
            final Integer listadePatienter = listningarPerEnhet.get(enhetId);
            if (listadePatienter != null) {
                final Integer antalSjukfall = summedData.get(i);
                final float thousand = 1000F;
                final float sjukfallPerThousandListadePatienter = antalSjukfall / (listadePatienter / thousand);
                enhetsWithPatients.add(new EnhetWithPatients(dataRow, sjukfallPerThousandListadePatienter));
            }
        }
        return enhetsWithPatients;
    }

    private boolean isMarked(SimpleKonDataRow row) {
        final Object extras = row.getExtras();
        return extras instanceof HsaIdEnhet && connectedEnhetIds.contains(extras);
    }

}
