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
package se.inera.statistics.web.service;

import se.inera.statistics.service.report.model.KonDataResponse;
import se.inera.statistics.service.warehouse.message.MsgAmne;
import se.inera.statistics.web.model.DualSexStatisticsData;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class MessageAmneConverter extends MultiDualSexConverter<KonDataResponse> {

    private static final Map<String, String> COLORS = Arrays.stream(MsgAmne.values())
            .collect(Collectors.toMap(Enum::name, msgAmne -> msgAmne.getColor().getColor()));

    DualSexStatisticsData convert(KonDataResponse data, FilterSettings filterSettings) {
        return super.convert(data, filterSettings, null, "%1$s", COLORS);
    }

}