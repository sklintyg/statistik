/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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

import com.google.common.collect.Maps;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import se.inera.statistics.integration.hsa.model.HsaIdEnhet;
import se.inera.statistics.service.region.persistance.regionenhet.RegionEnhet;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.web.service.dto.MessagesText;
import se.inera.statistics.web.error.Message;
import se.inera.statistics.web.model.ChartCategory;
import se.inera.statistics.web.model.ChartData;
import se.inera.statistics.web.model.ChartSeries;
import se.inera.statistics.web.model.NamedData;
import se.inera.statistics.web.model.SimpleDetailsData;
import se.inera.statistics.web.model.TableData;
import se.inera.statistics.web.service.dto.EnhetWithPatients;
import se.inera.statistics.web.service.dto.Filter;
import se.inera.statistics.web.service.dto.FilterDataResponse;
import se.inera.statistics.web.service.dto.FilterSettings;

public class SjukfallPerPatientsPerEnhetConverter {

    private final Map<HsaIdEnhet, Integer> listningarPerEnhet;
    private final List<HsaIdEnhet> connectedEnhetIds;
    public static final DecimalFormat TWO_DECIMALS_FORMAT_FOR_STRING = getDecimalFormatForString();
    public static final DecimalFormat TWO_DECIMALS_FORMAT_FOR_DOUBLE = getDecimalFormatForDouble();

    public SjukfallPerPatientsPerEnhetConverter(List<RegionEnhet> regionEnhets, List<HsaIdEnhet> connectedEnhetIds) {
        this.connectedEnhetIds = connectedEnhetIds;
        if (regionEnhets == null) {
            listningarPerEnhet = Collections.emptyMap();
            return;
        }
        listningarPerEnhet = Maps.newHashMapWithExpectedSize(regionEnhets.size());
        for (RegionEnhet regionEnhet : regionEnhets) {
            final Integer listadePatienterObj = regionEnhet.getListadePatienter();
            final int listadePatienter = listadePatienterObj == null ? 0 : listadePatienterObj;
            if (listadePatienter > 0) {
                listningarPerEnhet.put(regionEnhet.getEnhetensHsaId(), listadePatienter);
            }
        }
    }

    public SimpleDetailsData convert(SimpleKonResponse casesPerMonth, FilterSettings filterSettings, Message message) {
        TableData tableData = convertToTableData(casesPerMonth.getRows());
        ChartData chartData = convertToChartData(casesPerMonth);
        final Filter filter = filterSettings.getFilter();
        final FilterDataResponse filterResponse = new FilterDataResponse(filter);
        final Range range = filterSettings.getRange();
        final List<Message> combinedMessage = Converters.combineMessages(filterSettings.getMessage(), message);
        return new SimpleDetailsData(tableData, chartData, range.toString(), casesPerMonth.getAvailableFilters(),
            filterResponse, combinedMessage);
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
                final String sjukfallPerThousandListadePatienterRounded = roundToTwoDecimalsAndFormatToString(
                    sjukfallPerThousandListadePatienter);
                data.add(new NamedData(row.getName(),
                    Arrays.asList(antalSjukfall, listadePatienter, sjukfallPerThousandListadePatienterRounded), isMarked(row)));
            }
        }
        return TableData.createWithSingleHeadersRow(data,
            Arrays.asList(MessagesText.REPORT_VARDENHET, MessagesText.REPORT_ANTAL_SJUKFALL,
                MessagesText.REPORT_ANTAL_LISTNINGAR, MessagesText.REPORT_ANTAL_SJUKFALL_PER_1000_LISTNINGAR));
    }

    static String roundToTwoDecimalsAndFormatToString(float number) {
        return TWO_DECIMALS_FORMAT_FOR_STRING.format(number);
    }

    private static DecimalFormat getDecimalFormatForString() {
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
        decimalFormatSymbols.setDecimalSeparator(',');
        return new DecimalFormat("0.00", decimalFormatSymbols);
    }

    private static DecimalFormat getDecimalFormatForDouble() {
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
        return new DecimalFormat("0.00", decimalFormatSymbols);
    }

    static double roundToTwoDecimals(double number) {
        return Double.valueOf(TWO_DECIMALS_FORMAT_FOR_DOUBLE.format(number));
    }

    private ChartData convertToChartData(SimpleKonResponse casesPerMonth) {
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
            categories.add(new ChartCategory(MessagesText.REPORT_GROUP_TOTALT));
        }

        final ArrayList<ChartSeries> series = new ArrayList<>();
        series.add(new ChartSeries(MessagesText.REPORT_ANTAL_SJUKFALL_PER_1000, filteredSummedData));

        return new ChartData(series, categories);
    }

    private ArrayList<EnhetWithPatients> toSortedEnhetsWithPatients(SimpleKonResponse casesPerMonth) {
        final ArrayList<EnhetWithPatients> enhetsWithPatients = toEnhetsWithPatients(casesPerMonth);
        Collections.sort(enhetsWithPatients, new Comparator<EnhetWithPatients>() {
            @Override
            public int compare(EnhetWithPatients o1, EnhetWithPatients o2) {
                return Float.compare(o2.getSjukfallPerThousandListadePatienter(), o1.getSjukfallPerThousandListadePatienter());
            }
        });
        return enhetsWithPatients;
    }

    private ArrayList<EnhetWithPatients> toEnhetsWithPatients(SimpleKonResponse casesPerMonth) {
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
