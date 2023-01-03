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
package se.inera.statistics.spec

abstract class RegionSjukfallPerListningarPerEnhetReport extends Rapport {

    def vårdenhet
    def antalSjukfall
    def antalListningar
    def antalSjukfallPerTusenListningar

    def antalSjukfall() {
        return antalSjukfall
    }

    def antalListningar() {
        return antalListningar
    }

    def antalSjukfallPerTusenListningar() {
        return antalSjukfallPerTusenListningar
    }

    public void reset() {
        super.reset();
        antalSjukfall = -1
        antalListningar = -1
        antalSjukfallPerTusenListningar = -1
    }

    public void executeDiagram(report) {
        executeWithReport(report)
        def index = report.chartData.categories.findIndexOf { item ->
            println("item:" + item + " ; matcher: " + vårdenhet)
            item.name.contains(vårdenhet)
        }
        if (index >= 0) {
            markerad = report.chartData.categories[index].marked
            def series = report.chartData.series[0]
            antalSjukfallPerTusenListningar = series.data[index]
        }
    }

    void executeTabell(report) {
        executeWithReport(report)
        def row = report.tableData.rows.find { currentRow ->
            currentRow.name.contains(vårdenhet) }
        if (row == null) {
            antalSjukfall = -1
            antalListningar = -1
            antalSjukfallPerTusenListningar = -1
        } else {
            antalSjukfall = row.data[0]
            antalListningar = row.data[1]
            antalSjukfallPerTusenListningar = row.data[2]
            markerad = row.marked
        }
    }

    def getReportSjukfallPerListningarPerEnhetRegion() {
        if (inloggad) {
            return reportsUtil.getReportSjukfallPerListningarPerEnhetRegion(vg, filter);
        }
        throw new RuntimeException("Report -Region Sjukfall per listningar per enhet- is not available on national level");
    }

}
