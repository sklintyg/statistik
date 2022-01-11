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

class SjukfallISpecialrapportenSjukfallPerSjukskrivningslangd extends Rapport {

    def sjukskrivningslängd
    def antal

    def sjukskrivningslängd() {
        return sjukskrivningslängd
    }

    def antal() {
        return antal
    }

    public void executeTabell(report) {
        def row = report.find { currentRow -> currentRow.group.startsWith(sjukskrivningslängd) }
        antal = row == null ? -1 : row.antal
    }

    def getReport() {
        return reportsUtil.getSpecialReportSjukfallLength();
    }

    public void doExecute() {
        def report = getReport()
        executeTabell(report)
    }

}
