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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import se.inera.statistics.service.report.model.OverviewChartRowExtended;
import se.inera.statistics.web.error.Message;

public final class Converters {

    private Converters() {
    }

    public static List<Message> combineMessages(Message... messages) {
        return Stream.of(messages)
            .filter(m -> m != null && m.getMessage() != null && !m.getMessage().isEmpty())
            .collect(Collectors.toList());
    }

    public static List<OverviewChartRowExtended> convert(List<OverviewChartRowExtended> rows, int maxRows,
        String extraText, String extraColor) {
        List<OverviewChartRowExtended> result = new ArrayList<>();
        int i = 0;
        int numberOfRows = rows.size();
        int displayedGroups = numberOfRows > maxRows ? maxRows - 1 : maxRows;

        for (; i < displayedGroups && i < numberOfRows; i++) {
            OverviewChartRowExtended row = rows.get(i);
            final int alternation = row.getAlternation();
            int previous = row.getQuantity() - alternation;
            int percentChange = calculatePercentage(alternation, previous);
            result.add(new OverviewChartRowExtended(row.getName(), row.getQuantity(), percentChange, row.getColor()));
        }

        if (numberOfRows > maxRows) {
            int restQuantity = 0;
            int restAlternation = 0;
            for (; i < rows.size(); i++) {
                OverviewChartRowExtended row = rows.get(i);
                restQuantity += row.getQuantity();
                restAlternation += row.getAlternation();
            }
            int percentChange = calculatePercentage(restAlternation, restQuantity - restAlternation);
            result.add(new OverviewChartRowExtended(extraText, restQuantity, percentChange, extraColor));
        }

        return result;
    }

    private static int calculatePercentage(int part, int whole) {
        if (whole == 0) {
            return 0;
        }
        final double percentage = 100.0;
        return (int) Math.round(part * percentage / whole);
    }

}
