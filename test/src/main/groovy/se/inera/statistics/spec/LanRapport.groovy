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
package se.inera.statistics.spec

abstract class LanRapport extends Rapport {

    String län
    String kolumngrupp

    def antalInvånare() {
        return antalInvånare
    }

    def sjukfallPer1000Invånare() {
        return sjukfallPer1000Invånare
    }

    public void executeDiagram(report) {
        def index = report.chartData.categories.findIndexOf { item -> item.name.equals(län) }
        markerad = report.chartData.categories[index].marked
        totalt = index < 0 ? -1 : report.chartData.series[0].data[index]
        kvinnor = index < 0 ? -1 : report.chartData.series[1].data[index]
        män = index < 0 ? -1 : report.chartData.series[2].data[index]
    }

    public void executeTabell(report) {
        def headerIndex = report.tableData.headers[0].findIndexOf { item ->
            item.text != null && item.text.toLowerCase(Locale.ENGLISH).contains(kolumngrupp.toLowerCase(Locale.ENGLISH))
        }
        def row = report.tableData.rows.find { currentRow -> currentRow.name == (län)  }
        def totalIndex = ((headerIndex - 1) * 3)
        def womenIndex = totalIndex + 1
        def menIndex = womenIndex + 1
        totalt = row == null ? -1 : row.data[totalIndex]
        kvinnor = row == null ? -1 : row.data[womenIndex]
        män = row == null ? -1 : row.data[menIndex]
        markerad = row == null ? false : row.marked
    }

    def getReport() {
        if (inloggad) {
            throw new RuntimeException("Report -Län- is not available on logged in level");
        }
        return reportsUtil.getReportCasesPerCounty();
    }

}
