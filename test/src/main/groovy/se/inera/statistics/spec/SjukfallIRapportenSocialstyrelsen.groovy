/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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

class SjukfallIRapportenSocialstyrelsen extends Rapport {

    def startår
    def slutår
    def diagnoser
    boolean bröstcancervariant

    def diagnos
    def kön
    def län
    def sjukskrivningslängd

    void setStartår(startår) {
        this.startår = Integer.parseInt(startår)
    }

    void setSlutår(slutår) {
        this.slutår = Integer.parseInt(slutår)
    }

    void setBröstcancervariant(String variant) {
        this.bröstcancervariant = "JA".equalsIgnoreCase(variant)
        setDiagnoser("C50")
    }

    void setDiagnoser(diagnoser) {
        if (diagnoser != null && !diagnoser.trim().isEmpty()) {
            this.diagnoser = diagnoser.split(",")*.trim().collect{ it }
        }
    }

    def diagnos() {
        return diagnos
    }

    def kön() {
        return kön
    }

    def län() {
        return län
    }

    def sjukskrivningslängd() {
        return sjukskrivningslängd
    }

    public void executeTabell(report) {
        def row = report.find { currentRow -> currentRow.diagnos == diagnos  }
        kön = row == null ? -1 : row.kon
        län = row == null ? -1 : row.lanId
        sjukskrivningslängd = row == null ? -1 : row.length
    }

    def getReport() {
        return reportsUtil.getSocialstyrelsenReport(startår, slutår, diagnoser, bröstcancervariant);
    }

    public void doExecute() {
        def report = getReport()
        executeTabell(report)
    }

}
