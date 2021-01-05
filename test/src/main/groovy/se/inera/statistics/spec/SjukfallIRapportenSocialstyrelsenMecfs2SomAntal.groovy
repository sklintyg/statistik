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

class SjukfallIRapportenSocialstyrelsenMecfs2SomAntal extends Rapport {

    String kön
    String åldersgrupp
    def antal
    def q1
    def median
    def q3

    void setKön(kön) {
        this.kön = kön
    }

    void setÅldersgrupp(åldersgrupp) {
        this.åldersgrupp = åldersgrupp
    }

    def antal() {
        return antal
    }

    def q1() {
        return q1
    }

    def median() {
        return median
    }

    def q3() {
        return q3
    }

    public void executeTabell(report) {
        def row = report.find { r -> kön.equalsIgnoreCase(String.valueOf((Object) r.kon)) && åldersgrupp.equalsIgnoreCase(r.ageGroup) }
        antal = row == null ? -1 : row.amount
        q1 = row == null ? -1 : row.q1
        median = row == null ? -1 : row.median
        q3 = row == null ? -1 : row.q3
    }

    def getReport() {
        return reportsUtil.getSocialstyrelsenMecfs2Report();
    }

    public void doExecute() {
        def report = getReport()
        executeTabell(report)
    }

}
