/*
 * Copyright (C) 2023 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.countypopulation;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;
import se.inera.statistics.service.report.model.KonField;

public class CountyPopulation {

    private Map<String, KonField> populationPerCountyCode;
    private LocalDate date;

    public CountyPopulation(Map<String, KonField> populationPerCountyCode, LocalDate date) {
        this.populationPerCountyCode = populationPerCountyCode;
        this.date = date;
    }

    public Map<String, KonField> getPopulationPerCountyCode() {
        return populationPerCountyCode;
    }

    public LocalDate getDate() {
        return date;
    }

    public static CountyPopulation empty() {
        return new CountyPopulation(Collections.emptyMap(), LocalDate.ofEpochDay(0));
    }

}
