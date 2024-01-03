/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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
package se.inera.statistics.web.model;

import se.inera.statistics.service.report.model.AvailableFilters;
import se.inera.statistics.web.service.dto.FilterDataResponse;

public class CasesPerCountyData extends SimpleDetailsData {

    private String sourceDate;

    public CasesPerCountyData(TableData tableData, ChartData chartData, String period, AvailableFilters availableFilters,
        FilterDataResponse filter, String sourceDate) {
        super(tableData, chartData, period, availableFilters, filter);
        this.sourceDate = sourceDate;
    }

    public String getSourceDate() {
        return sourceDate;
    }

}
