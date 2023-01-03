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

class PatienterIRapportenSocialstyrelsenMecfs1PatienterSomAntal extends Rapport {

    String år
    String kön
    String åldersgrupp
    def antal

    void setår(år) {
        this.år = Integer.parseInt(år)
    }

    void setKön(kön) {
        this.kön = kön
    }

    void setÅldersgrupp(åldersgrupp) {
        this.åldersgrupp = åldersgrupp
    }

    def antal() {
        return antal
    }

    public void executeTabell(report) {
        def row = report.find { r -> r.year == Integer.parseInt(år) && kön.equalsIgnoreCase(r.kon) && åldersgrupp.equalsIgnoreCase(r.ageGroup) }
        antal = row == null ? -1 : row.amount
    }

    def getReport() {
        return reportsUtil.getSocialstyrelsenMecfs1Report();
    }

    public void doExecute() {
        def report = getReport()
        executeTabell(report)
    }

}
