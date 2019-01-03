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

import java.time.Clock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import se.inera.statistics.service.report.model.AvailableFilters;
import se.inera.statistics.service.report.model.KonDataResponse;
import se.inera.statistics.service.report.model.KonDataRow;
import se.inera.statistics.service.report.model.KonField;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.warehouse.message.MsgAmne;
import se.inera.statistics.web.model.DualSexStatisticsData;
import se.inera.statistics.web.service.Filter;
import se.inera.statistics.web.service.FilterSettings;

import static org.junit.Assert.assertEquals;

public class MessageAmneConverterTest {

    @Test
    public void testConvertEmptySeriesWillRemoveOkantAmne() {
        //Given
        final MessageAmneConverter messageAmneConverter = new MessageAmneConverter();
        final Filter filter = Filter.empty();
        final List<String> groups = Arrays.stream(MsgAmne.values()).map(Enum::name).collect(Collectors.toList());
        final List<KonField> data = new ArrayList<>();
        for (int i = 0; i < groups.size(); i++) {
            data.add(new KonField(0, 0));
        }
        final List<KonDataRow> rows = Collections.singletonList(new KonDataRow("rowname", data));
        final FilterSettings filterSettings = new FilterSettings(filter, Range.year(Clock.systemUTC()));

        //When
        final KonDataResponse response = new KonDataResponse(AvailableFilters.getForMeddelanden(), groups, rows);
        final DualSexStatisticsData convert = messageAmneConverter.convert(response, filterSettings);

        //Then
        assertEquals(MsgAmne.values().length - 1, convert.getFemaleChart().getSeries().size());
    }

    @Test
    public void testConvertNonEmptySeriesWillKeepOkantAmne() {
        //Given
        final MessageAmneConverter messageAmneConverter = new MessageAmneConverter();
        final Filter filter = Filter.empty();
        final List<String> groups = Arrays.stream(MsgAmne.values()).map(Enum::name).collect(Collectors.toList());
        final List<KonField> data = new ArrayList<>();
        for (int i = 0; i < groups.size(); i++) {
            data.add(new KonField(1, 0));
        }
        final List<KonDataRow> rows = Collections.singletonList(new KonDataRow("rowname", data));
        final FilterSettings filterSettings = new FilterSettings(filter, Range.year(Clock.systemUTC()));

        //When
        final KonDataResponse response = new KonDataResponse(AvailableFilters.getForMeddelanden(), groups, rows);
        final DualSexStatisticsData convert = messageAmneConverter.convert(response, filterSettings);

        //Then
        assertEquals(MsgAmne.values().length, convert.getFemaleChart().getSeries().size());
    }

}
