/*
 * Copyright (C) 2022 Inera AB (http://www.inera.se)
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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import se.inera.statistics.service.report.model.KonDataResponse;
import se.inera.statistics.service.report.model.KonDataRow;
import se.inera.statistics.service.report.model.KonField;
import se.inera.statistics.service.warehouse.message.MsgAmne;
import se.inera.statistics.web.model.ChartData;
import se.inera.statistics.web.model.DualSexStatisticsData;
import se.inera.statistics.web.model.SimpleDetailsData;
import se.inera.statistics.web.service.dto.FilterSettings;

public class MessageAmnePerTypeTvarsnittConverter extends MultiDualSexConverter {

    public MessageAmnePerTypeTvarsnittConverter(String tableHeader, String tableSeriesHeader) {
        super(tableHeader, tableSeriesHeader);
    }

    private static final Map<String, String> COLORS = Arrays.stream(MsgAmne.values())
        .collect(Collectors.toMap(MsgAmne::getText, msgAmne -> msgAmne.getColor().getColor()));

    public SimpleDetailsData convert(KonDataResponse data, FilterSettings filterSettings) {
        final List<MsgAmne> groups = data.getGroups().stream().map(MsgAmne::parse).collect(Collectors.toList());
        final List<KonDataRow> rows = data.getRows();
        int indexOfEmptyInternalIcd10Group = getIndexOfGroupToRemove(groups, rows);
        while (indexOfEmptyInternalIcd10Group >= 0) {
            removeGroupWithIndex(indexOfEmptyInternalIcd10Group, groups, rows);
            indexOfEmptyInternalIcd10Group = getIndexOfGroupToRemove(groups, rows);
        }
        final KonDataResponse konDataResponse = new KonDataResponse(data.getAvailableFilters(), convertGroupNamesToText(groups), rows);
        final DualSexStatisticsData dssd = super.convert(konDataResponse, filterSettings, null, "%1$s", COLORS);
        final ChartData chartData = MessageAmnePerTypeConverter.merge(dssd.getFemaleChart(), dssd.getMaleChart());
        return new SimpleDetailsData(dssd.getTableData(), chartData, dssd.getPeriod(), data.getAvailableFilters(),
            dssd.getFilter(), dssd.getMessages());
    }

    private List<String> convertGroupNamesToText(List<MsgAmne> groups) {
        return groups.stream().map(MsgAmne::getText).collect(Collectors.toList());
    }

    private int getIndexOfGroupToRemove(List<MsgAmne> data, List<KonDataRow> rows) {
        for (int i = 0; i < data.size(); i++) {
            final MsgAmne amne = data.get(i);
            if (!amne.isShowEmpty()) {
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

    private void removeGroupWithIndex(int index, List<MsgAmne> groupNames, List<KonDataRow> rows) {
        groupNames.remove(index);
        for (KonDataRow row : rows) {
            row.getData().remove(index);
        }
    }

}
