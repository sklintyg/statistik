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

import java.util.Map;
import se.inera.statistics.service.report.model.KonDataResponse;
import se.inera.statistics.service.report.util.AgeGroup;
import se.inera.statistics.web.model.DualSexStatisticsData;
import se.inera.statistics.web.service.FilterSettings;

public class SimpleMultiDualSexConverter extends MultiDualSexConverter {

    private static final Map<String, String> COLORS = AgeGroup.getColors();

    public SimpleMultiDualSexConverter() {
    }

    public SimpleMultiDualSexConverter(String tableHeader) {
        super(tableHeader);
    }

    public DualSexStatisticsData convert(KonDataResponse data, FilterSettings filterSettings) {
        return super.convert(data, filterSettings, null, "%1$s", COLORS);
    }

}
