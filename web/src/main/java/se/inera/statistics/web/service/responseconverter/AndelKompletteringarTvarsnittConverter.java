/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.warehouse.IntygType;
import se.inera.statistics.service.warehouse.query.AndelExtras;
import se.inera.statistics.web.MessagesText;
import se.inera.statistics.web.error.Message;
import se.inera.statistics.web.model.ChartCategory;
import se.inera.statistics.web.model.ChartData;
import se.inera.statistics.web.model.ChartSeries;
import se.inera.statistics.web.model.SimpleDetailsData;
import se.inera.statistics.web.service.FilterSettings;

public final class AndelKompletteringarTvarsnittConverter extends SimpleDualSexConverter {

    private AndelKompletteringarTvarsnittConverter(String tableGroupTitle, String seriesNameTemplate, String totalColumnName,
                                                   String femaleColumnName, String maleColumnName) {
        super(tableGroupTitle, seriesNameTemplate, totalColumnName, femaleColumnName, maleColumnName);
    }

    public static AndelKompletteringarTvarsnittConverter newTvarsnitt() {
        return new AndelKompletteringarTvarsnittConverter("",
                "%1$s",
                MessagesText.REPORT_COLUMN_ANDEL_INTYG_TOTALT,
                MessagesText.REPORT_COLUMN_ANDEL_INTYG_FEMALE,
                MessagesText.REPORT_COLUMN_ANDEL_INTYG_MALE);
    }

    @Override
    public SimpleDetailsData convert(SimpleKonResponse casesPerMonth, FilterSettings filterSettings, Message message) {
        List<SimpleKonDataRow> rowsToShow = casesPerMonth.getRows().stream().map(simpleKonDataRow -> {
            final String name = simpleKonDataRow.getName();
            final Optional<IntygType> intygType = IntygType.parseStringOptional(name);
            final String parsedName = intygType.isPresent() ? intygType.get().getText() : name;
            return new SimpleKonDataRow(parsedName, simpleKonDataRow.getData(), simpleKonDataRow.getExtras());
        }).collect(Collectors.toList());

        return super.convert(new SimpleKonResponse(casesPerMonth.getAvailableFilters(), rowsToShow), filterSettings, message);
    }

    @Override
    protected ChartData convertToChartData(SimpleKonResponse casesPerMonth) {
        final ArrayList<ChartCategory> categories = new ArrayList<>();
        List<Integer> totals = new ArrayList<>();
        for (SimpleKonDataRow casesPerMonthRow : casesPerMonth.getRows()) {
            final String seriesName = String.format(getSeriesNameTemplate(), casesPerMonthRow.getName());
            categories.add(new ChartCategory(seriesName, isMarked(casesPerMonthRow)));

            final AndelExtras tot = casesPerMonthRow.getExtras() instanceof AndelExtras
                    ? (AndelExtras) casesPerMonthRow.getExtras() : new AndelExtras(0, 0, 0, 0);
            final boolean emptyTot = tot.getWhole() == 0;
            final int total = emptyTot ? 0 : AndelKompletteringarConverter.PERCENTAGE_CONVERT * tot.getPart() / tot.getWhole();
            totals.add(total);
        }

        final ArrayList<ChartSeries> series = new ArrayList<>();
        series.add(new ChartSeries(MessagesText.REPORT_GROUP_TOTALT, totals));
        series.add(new ChartSeries(MessagesText.REPORT_GROUP_FEMALE, casesPerMonth.getDataForSex(Kon.FEMALE), Kon.FEMALE));
        series.add(new ChartSeries(MessagesText.REPORT_GROUP_MALE, casesPerMonth.getDataForSex(Kon.MALE), Kon.MALE));

        return new ChartData(series, categories);
    }


    @Override
    protected List<Object> getMergedSexData(SimpleKonDataRow row) {
        List<Object> data = new ArrayList<>();
        final AndelExtras tot = row.getExtras() instanceof AndelExtras
                ? (AndelExtras) row.getExtras() : new AndelExtras(0, 0, 0, 0);
        final int total = tot.getWhole() == 0 ? 0 : AndelKompletteringarConverter.PERCENTAGE_CONVERT * tot.getPart() / tot.getWhole();
        data.add(total + "%");
        data.add(row.getFemale() + "%");
        data.add(row.getMale() + "%");
        return data;
    }

}
