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

class SjukfallIRapportenSocialstyrelsenSomAntal extends SjukfallIRapportenSocialstyrelsenSom {

    def under15DagarM
    def från15Till30DagarM
    def från31Till60DagarM
    def från61Till90DagarM
    def från91Till180DagarM
    def från181Till365DagarM
    def över365DagarM
    def under15DagarK
    def från15Till30DagarK
    def från31Till60DagarK
    def från61Till90DagarK
    def från91Till180DagarK
    def från181Till365DagarK
    def över365DagarK

    def under15DagarM() { return under15DagarM }
    def från15Till30DagarM() { return från15Till30DagarM }
    def från31Till60DagarM() { return från31Till60DagarM }
    def från61Till90DagarM() { return från61Till90DagarM }
    def från91Till180DagarM() { return från91Till180DagarM }
    def från181Till365DagarM() { return från181Till365DagarM }
    def över365DagarM() { return över365DagarM }
    def under15DagarK() { return under15DagarK }
    def från15Till30DagarK() { return från15Till30DagarK }
    def från31Till60DagarK() { return från31Till60DagarK }
    def från61Till90DagarK() { return från61Till90DagarK }
    def från91Till180DagarK() { return från91Till180DagarK }
    def från181Till365DagarK() { return från181Till365DagarK }
    def över365DagarK() { return över365DagarK }

    def getReport() {
        return reportsUtil.getSocialstyrelsenAntalReport(startår, slutår, diagnoser, bröstcancervariant);
    }

    public void executeTabell(report) {
        super.executeTabell(report)
        def row = report.find { currentRow -> currentRow.diagnos == diagnos  }

        under15DagarM = row == null ? -1 : row["Under 15 dagar M"]
        från15Till30DagarM = row == null ? -1 : row["15-30 dagar M"]
        från31Till60DagarM = row == null ? -1 : row["31-60 dagar M"]
        från61Till90DagarM = row == null ? -1 : row["61-90 dagar M"]
        från91Till180DagarM = row == null ? -1 : row["91-180 dagar M"]
        från181Till365DagarM = row == null ? -1 : row["181-365 dagar M"]
        över365DagarM = row == null ? -1 : row["Över 365 dagar M"]

        under15DagarK = row == null ? -1 : row["Under 15 dagar K"]
        från15Till30DagarK = row == null ? -1 : row["15-30 dagar K"]
        från31Till60DagarK = row == null ? -1 : row["31-60 dagar K"]
        från61Till90DagarK = row == null ? -1 : row["61-90 dagar K"]
        från91Till180DagarK = row == null ? -1 : row["91-180 dagar K"]
        från181Till365DagarK = row == null ? -1 : row["181-365 dagar K"]
        över365DagarK = row == null ? -1 : row["Över 365 dagar K"]
    }

}
