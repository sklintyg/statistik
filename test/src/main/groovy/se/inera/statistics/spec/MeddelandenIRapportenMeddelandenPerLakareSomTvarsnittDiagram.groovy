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
package se.inera.statistics.spec

class MeddelandenIRapportenMeddelandenPerLakareSomTvarsnittDiagram extends Rapport {

    def grupp
    def läkare

    @Override
    public void doExecute() {
        def report = getReport()
        executeDiagram(report)
    }

    def getRowNameMatcher() {
        return läkare
    }

    public void executeDiagram(report) {
        executeWithReport(report)
        def categoryNameMatcher = getRowNameMatcher();
        def index = report.chartData.categories.findIndexOf { item ->
            println("item:" + item + " ; matcher: " + categoryNameMatcher)
            item.name.toLowerCase().contains(categoryNameMatcher.toLowerCase())
        }
        markerad = index < 0 ? null : report.chartData.categories[index].marked
        def total = report.chartData.series.find { item -> item.name.equalsIgnoreCase(grupp) }
        totalt = index < 0 || total == null ? -1 : total.data[index]
        färg = total == null ? null : total.color
    }

    def getReport() {
        if (inloggad) {
            return reportsUtil.getReportAntalMeddelandenLakareTvarsnittInloggad(vg, filter)
        }
        return new RuntimeException("Report -Meddelanden per ämne per läkare som tvärsnitt- is not available on national level");
    }

}
