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

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

import se.inera.statistics.service.report.model.KonDataResponse;
import se.inera.statistics.service.report.model.KonDataRow;
import se.inera.statistics.service.report.model.KonField;
import se.inera.statistics.service.warehouse.IntygType;
import se.inera.statistics.service.warehouse.query.AndelExtras;
import se.inera.statistics.web.model.DualSexStatisticsData;
import se.inera.statistics.web.model.TableData;
import se.inera.statistics.web.model.TableHeader;
import se.inera.statistics.web.service.FilterSettings;

public class AndelKompletteringarConverter extends MultiDualSexConverter {

    static final int PERCENTAGE_CONVERT = 100;

    public AndelKompletteringarConverter() {
        super("Total andel som fått komplettering, alla intygstyper inkluderade");
    }

    public DualSexStatisticsData convert(KonDataResponse data, FilterSettings filterSettings) {
        final List<IntygType> groups = data.getGroups().stream().map(IntygType::parseString).collect(Collectors.toList());
        final List<KonDataRow> rows = data.getRows();
        int indexOfEmptyInternalGroup = getIndexOfGroupToRemove(groups, rows);
        while (indexOfEmptyInternalGroup >= 0) {
            removeGroupWithIndex(indexOfEmptyInternalGroup, groups, rows);
            indexOfEmptyInternalGroup = getIndexOfGroupToRemove(groups, rows);
        }
        final KonDataResponse konDataResponse = new KonDataResponse(data.getAvailableFilters(),
                convertGroupNamesToText(groups), rows);
        return super.convert(konDataResponse, filterSettings, null, "%1$s");
    }

    private List<String> convertGroupNamesToText(List<IntygType> groups) {
        return groups.stream().map(IntygType::getText).collect(Collectors.toList());
    }

    private int getIndexOfGroupToRemove(List<IntygType> data, List<KonDataRow> rows) {
        for (int i = 0; i < data.size(); i++) {
            final IntygType intygType = data.get(i);
            if (IntygType.UNKNOWN.equals(intygType)) {
                if (getTotalCountForIndex(i, rows) < 1) {
                    return i;
                }
            }
        }
        return -1;
    }

    private int getTotalCountForIndex(int index, List<KonDataRow> rows) {
        int count = 0;
        for (KonDataRow row : rows) {
            final KonField konField = row.getData().get(index);
            count += konField.getFemale() + konField.getMale();
        }
        return count;
    }

    private void removeGroupWithIndex(int index, List<IntygType> groupNames, List<KonDataRow> rows) {
        groupNames.remove(index);
        for (KonDataRow row : rows) {
            row.getData().remove(index);
        }
    }

    @Override
    protected Object getRowSum(KonDataRow row) {
        final AndelExtras emptyAndelExtras = new AndelExtras(0, 0);
        final BinaryOperator<AndelExtras> andelExtrasCombiner = (andelExtras, andelExtras2) ->
            new AndelExtras(andelExtras.getPart() + andelExtras2.getPart(), andelExtras.getWhole() + andelExtras2.getWhole());

        final BiFunction<AndelExtras, KonField, AndelExtras> andelExtraExtractor = (andelExtras, konField) -> {
            if (konField.getExtras() instanceof AndelExtras) {
                return andelExtrasCombiner.apply(andelExtras, (AndelExtras) konField.getExtras());
            }
            return andelExtras;
        };
        final AndelExtras total = row.getData().stream().reduce(emptyAndelExtras, andelExtraExtractor, andelExtrasCombiner);
        final int tot = total.getWhole() == 0 ? 0 : PERCENTAGE_CONVERT * total.getPart() / total.getWhole();
        return tot + "%";
    }

    @Override
    protected List<Object> getMergedSexData(KonDataRow row) {
        List<Object> data = new ArrayList<>();
        for (KonField konField : row.getData()) {
            final AndelExtras tot = konField.getExtras() instanceof AndelExtras
                    ? (AndelExtras) konField.getExtras() : new AndelExtras(0, 0);
            final int total = tot.getWhole() == 0 ? 0 : PERCENTAGE_CONVERT * tot.getPart() / tot.getWhole();
            data.add(total + "%");
            data.add(konField.getFemale() + "%");
            data.add(konField.getMale() + "%");
        }
        return data;
    }

    /**
     * Add specific tooltips in table headers for this report.
     */
    @Override
    TableData convertTable(KonDataResponse resp, String seriesNameTemplate) {
        final TableData tableData = super.convertTable(resp, seriesNameTemplate);
        final List<List<TableHeader>> newHeaders = tableData.getHeaders().stream().map(row -> row.stream().map(
                this::getTableHeaderWithTitle).collect(Collectors.toList())).collect(Collectors.toList());
        return new TableData(tableData.getRows(), newHeaders);
    }

    private TableHeader getTableHeaderWithTitle(TableHeader tableHeader) {
        return new TableHeader(tableHeader.getText(), tableHeader.getColspan(), getTitle(tableHeader), tableHeader.getMeta());
    }

    private String getTitle(TableHeader tableHeader) {
        final String text = tableHeader.getText();
        final String meta = tableHeader.getMeta();
        switch (text) {
            case MultiDualSexConverter.TOTAL:
                return "Kolumnen Totalt visar hur stor andel av alla utfärdade "
                        + meta + " som har fått en komplettering.";
            case MultiDualSexConverter.MAN:
                return "Kolumnen Män visar hur stor andel av alla "
                        + meta + " utfärdade till manliga patienter som har fått en komplettering.";
            case MultiDualSexConverter.KVINNOR:
                return "Kolumnen Kvinnor visar hur stor andel av alla "
                        + meta + " utfärdade till kvinnliga patienter som har fått en komplettering.";
        }
        return null;
    }

}
