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
package se.inera.statistics.spec

abstract class SjukfallIRapportenSocialstyrelsenSom extends Rapport {

    def diagnos
    def totalt
    def kvinnor
    def män
    def lan10
    def lan20
    def lan13
    def lan08
    def lan07
    def lan09
    def lan21
    def lan23
    def lan06
    def lan25
    def lan12
    def lan01
    def lan04
    def lan03
    def lan17
    def lan24
    def lan22
    def lan19
    def lan14
    def lan18
    def lan05
    def lan00

    def startår
    def slutår
    def diagnoser
    boolean bröstcancervariant

    void reset() {
        super.reset()
        this.startår = null
        this.slutår = null
        this.diagnoser = null
        bröstcancervariant = false
    }

    void setStartår(startår) {
        if (startår != null && !startår.isEmpty()) {
            this.startår = Integer.parseInt(startår)
        }
    }

    void setSlutår(slutår) {
        if (slutår != null && !slutår.isEmpty()) {
            this.slutår = Integer.parseInt(slutår)
        }
    }

    void setDiagnoser(diagnoser) {
        if (diagnoser != null && !diagnoser.trim().isEmpty()) {
            this.diagnoser = diagnoser.split(",")*.trim().collect{ it }
        }
    }

    void setBröstcancervariant(String variant) {
        this.bröstcancervariant = "JA".equalsIgnoreCase(variant)
        setDiagnoser("C50")
    }

    def totalt() { return totalt }
    def kvinnor() { return kvinnor }
    def män() { return män }
    def lan10() { return lan10 }
    def lan20() { return lan20 }
    def lan13() { return lan13 }
    def lan08() { return lan08 }
    def lan07() { return lan07 }
    def lan09() { return lan09 }
    def lan21() { return lan21 }
    def lan23() { return lan23 }
    def lan06() { return lan06 }
    def lan25() { return lan25 }
    def lan12() { return lan12 }
    def lan01() { return lan01 }
    def lan04() { return lan04 }
    def lan03() { return lan03 }
    def lan17() { return lan17 }
    def lan24() { return lan24 }
    def lan22() { return lan22 }
    def lan19() { return lan19 }
    def lan14() { return lan14 }
    def lan18() { return lan18 }
    def lan05() { return lan05 }
    def lan00() { return lan00 }

    public void executeTabell(report) {
        def row = report.find { currentRow -> currentRow.diagnos == diagnos  }
        totalt = row == null ? -1 : row.totalt
        kvinnor = row == null ? -1 : row.kvinnor
        män = row == null ? -1 : row.man
        lan10 = row == null ? -1 : row.blekingeLan
        lan20 = row == null ? -1 : row.dalarnasLan
        lan13 = row == null ? -1 : row.hallandsLan
        lan08 = row == null ? -1 : row.kalmarLan
        lan07 = row == null ? -1 : row.kronobergsLan
        lan09 = row == null ? -1 : row.gotlandsLan
        lan21 = row == null ? -1 : row.gavleborgsLan
        lan23 = row == null ? -1 : row.jamtlandsLan
        lan06 = row == null ? -1 : row.jonkopingsLan
        lan25 = row == null ? -1 : row.norrbottensLan
        lan12 = row == null ? -1 : row.skaneLan
        lan01 = row == null ? -1 : row.stockholmsLan
        lan04 = row == null ? -1 : row.sodermanlandsLan
        lan03 = row == null ? -1 : row.uppsalaLan
        lan17 = row == null ? -1 : row.varmlandsLan
        lan24 = row == null ? -1 : row.vasterbottensLan
        lan22 = row == null ? -1 : row.vasternorrlandsLan
        lan19 = row == null ? -1 : row.vastmanlandsLan
        lan14 = row == null ? -1 : row.vastraGotalandsLan
        lan18 = row == null ? -1 : row.orebroLan
        lan05 = row == null ? -1 : row.ostergotlandsLan
        lan00 = row == null ? -1 : row.okantLan
    }

    public void doExecute() {
        def report = getReport()
        executeTabell(report)
    }

    def abstract getReport();

}
