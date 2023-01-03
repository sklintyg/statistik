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

class RapportenForsakringskassan extends Rapport {

    def diagnos
    def kön
    def län
    def antalsjukfall
    def medellängd
    def medianlängd

    def diagnos() {
        return diagnos
    }

    def kön() {
        return kön
    }

    def län() {
        return län
    }

    def antalsjukfall() {
        return antalsjukfall
    }

    def medellängd() {
        return medellängd
    }

    def medianlängd() {
        return medianlängd
    }

    public void executeTabell(report) {
        def row = report.find { currentRow -> currentRow.diagnos == diagnos && currentRow.lanId == län && currentRow.kon == kön }
        diagnos = row.diagnos
        län = row.lanId
        kön = row.kon
        antalsjukfall = row == null ? 0 : row.antal
        medellängd = row == null ? 0 : row.medel
        medianlängd = row == null ? 0 : row.median
    }

    def getReport() {
        return reportsUtil.getFkReport();
    }

    public void doExecute() {
        def report = getReport()
        executeTabell(report)
    }

}
