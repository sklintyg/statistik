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
package se.inera.statistics.spec

import se.inera.statistics.service.countypopulation.CountyPopulation
import se.inera.statistics.service.report.model.KonField
import se.inera.statistics.web.reports.ReportsUtil

import java.time.LocalDate

class AngivnaInvanarantal {

    private final ReportsUtil reportsUtil = new ReportsUtil()

    String länkod
    int kvinnor
    int män
    String datum

    def values = [:]

    public void beginTable() {
        reportsUtil.clearCountyPopulation()

    }

    public void reset() {
        länkod = null
        kvinnor = 0
        män = 0
    }

    public void setKommentar(String kommentar) {}

    public void execute() {
        def valuesPerDate = values.get(datum, [:])
        valuesPerDate[länkod] = new KonField(kvinnor, män)
    }

    public void endTable() {
        values.each { k, v ->
            reportsUtil.insertCountyPopulation(v, k)
        }
    }

}
