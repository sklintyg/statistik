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

import java.util.List;
import java.util.stream.Collectors;

import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.warehouse.message.MsgAmne;
import se.inera.statistics.web.error.Message;
import se.inera.statistics.web.model.SimpleDetailsData;
import se.inera.statistics.web.service.FilterSettings;

public final class MessageAmneTvarsnittConverter extends SimpleDualSexConverter {

    private MessageAmneTvarsnittConverter(String tableGroupTitle, String seriesNameTemplate, String totalColumnName,
                                   String femaleColumnName, String maleColumnName) {
        super(tableGroupTitle, seriesNameTemplate, totalColumnName, femaleColumnName, maleColumnName);
    }

    public static MessageAmneTvarsnittConverter newTvarsnitt() {
        return new MessageAmneTvarsnittConverter("",
                "%1$s",
                "Antal meddelanden totalt",
                "Antal meddelanden för kvinnor",
                "Antal meddelanden för män");
    }

    @Override
    public SimpleDetailsData convert(SimpleKonResponse casesPerMonth, FilterSettings filterSettings, Message message) {
        List<SimpleKonDataRow> rowsToShow = casesPerMonth.getRows().stream().filter(simpleKonDataRow -> {
            if (simpleKonDataRow.getFemale() + simpleKonDataRow.getMale() <= 0) {
                final Object amne = simpleKonDataRow.getExtras();
                if (amne instanceof MsgAmne) {
                    return ((MsgAmne) amne).isShowEmpty();
                }
            }
            return true;
        }).collect(Collectors.toList());

        return super.convert(new SimpleKonResponse(casesPerMonth.getAvailableFilters(), rowsToShow), filterSettings, message);
    }

}
