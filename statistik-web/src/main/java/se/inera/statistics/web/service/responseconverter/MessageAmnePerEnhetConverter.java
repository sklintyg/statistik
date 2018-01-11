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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import se.inera.statistics.service.report.model.KonDataResponse;
import se.inera.statistics.service.report.model.KonDataRow;
import se.inera.statistics.service.report.model.KonField;
import se.inera.statistics.service.warehouse.message.MsgAmne;
import se.inera.statistics.service.warehouse.query.MessagesQuery;
import se.inera.statistics.web.model.DualSexStatisticsData;
import se.inera.statistics.web.service.FilterSettings;

public class MessageAmnePerEnhetConverter extends MultiDualSexConverter {

    private static final Map<String, String> COLORS = Arrays.stream(MsgAmne.values())
            .collect(Collectors.toMap(Enum::name, msgAmne -> msgAmne.getColor().getColor()));

    public DualSexStatisticsData convert(KonDataResponse data, FilterSettings filterSettings) {
        final List<String[]> separatedGroups = data.getGroups().stream()
                .map(s -> s.split(MessagesQuery.GROUP_NAME_SEPARATOR)).collect(Collectors.toList());
        final List<MsgAmne> amnes = separatedGroups.stream().map(strings -> MsgAmne.parse(strings[1])).collect(Collectors.toList());
        final List<KonDataRow> rows = data.getRows();
        int indexOfEmptyInternalIcd10Group = getIndexOfGroupToRemove(amnes, rows);
        while (indexOfEmptyInternalIcd10Group >= 0) {
            removeGroupWithIndex(indexOfEmptyInternalIcd10Group, amnes, rows);
            indexOfEmptyInternalIcd10Group = getIndexOfGroupToRemove(amnes, rows);
        }
        final KonDataResponse konDataResponse = new KonDataResponse(convertGroupNamesToText(amnes, separatedGroups), rows);
        return super.convert(konDataResponse, filterSettings, null, "%1$s", COLORS);
    }

    private List<String> convertGroupNamesToText(List<MsgAmne> groups, List<String[]> separatedGroups) {
        final List<String> groupNames = Lists.newArrayListWithCapacity(groups.size());
        for (int i = 0; i < groups.size(); i++) {
             groupNames.add(separatedGroups.get(i)[0] + MessagesQuery.GROUP_NAME_SEPARATOR + groups.get(i).getText());
        }
        return groupNames;
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
