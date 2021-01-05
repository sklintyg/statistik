/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
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

import static org.junit.Assert.assertEquals;

import java.time.Clock;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Test;
import se.inera.statistics.service.report.model.AvailableFilters;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.warehouse.message.MsgAmne;
import se.inera.statistics.web.model.SimpleDetailsData;
import se.inera.statistics.web.service.Filter;
import se.inera.statistics.web.service.FilterSettings;

public class MessageAmneTvarsnittConverterTest {

    @Test
    public void testConvertEmptyDataWillRemoveOkantAmne() {
        //Given
        final FilterSettings filterSettings = new FilterSettings(Filter.empty(), Range.year(Clock.systemDefaultZone()));
        final List<SimpleKonDataRow> simpleKonDataRows = Arrays.stream(MsgAmne.values())
            .map(msgAmne -> new SimpleKonDataRow(msgAmne.name(), 0, 0, msgAmne))
            .collect(Collectors.toList());
        final SimpleKonResponse casesPerMonth = new SimpleKonResponse(AvailableFilters.getForMeddelanden(), simpleKonDataRows);

        //When
        final MessageAmneTvarsnittConverter converter = MessageAmneTvarsnittConverter.newTvarsnitt();
        final SimpleDetailsData converted = converter.convert(casesPerMonth, filterSettings);

        //Then
        assertEquals(MsgAmne.values().length - 1, converted.getChartData().getCategories().size());
    }

    @Test
    public void testConvertNonEmptyDataWillKeepOkantAmne() {
        //Given
        final FilterSettings filterSettings = new FilterSettings(Filter.empty(), Range.year(Clock.systemDefaultZone()));
        final List<SimpleKonDataRow> simpleKonDataRows = Arrays.stream(MsgAmne.values())
            .map(msgAmne -> new SimpleKonDataRow(msgAmne.name(), 1, 0, msgAmne))
            .collect(Collectors.toList());
        final SimpleKonResponse casesPerMonth = new SimpleKonResponse(AvailableFilters.getForMeddelanden(), simpleKonDataRows);

        //When
        final MessageAmneTvarsnittConverter converter = MessageAmneTvarsnittConverter.newTvarsnitt();
        final SimpleDetailsData converted = converter.convert(casesPerMonth, filterSettings);

        //Then
        assertEquals(MsgAmne.values().length, converted.getChartData().getCategories().size());
    }

}
