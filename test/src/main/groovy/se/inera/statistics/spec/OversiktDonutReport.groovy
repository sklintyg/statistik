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

abstract class OversiktDonutReport extends Rapport {

    String grupp
    String antal
    String förändring
    String färg

    void setGrupp(String grupp) {
        this.grupp = grupp
    }

    def antal() {
        return antal
    }

    def förändring() {
        return förändring
    }

    def färg() {
        return färg
    }

    @Override
    public void doExecute() {
        def report = getVerksamhetsoversikt()
        executeTabell(report)
    }

    public void executeTabell(report) {
        executeWithReport(report)
        def row = getData(report).find { item -> item.name.contains(grupp) }
        antal = row != null ? row.quantity : -1
        förändring = row != null ? row.alternation : -1
        färg = row != null ? row.color : null
    }

    abstract def getData(report);

}
