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

import se.inera.statistics.web.reports.ReportsUtil

class UppladdadeRegionsfiler {

    protected final ReportsUtil reportsUtil = new ReportsUtil()

    def användare
    def filnamn
    def statusmeddelande

    public void reset() {
        användare = null
        filnamn = null
        statusmeddelande = null
    }

    public void setKommentar(String kommentar) {}

    def statusmeddelande() {
        return statusmeddelande
    }

    public void execute() {
        def vgId = reportsUtil.getVardgivareForUser(användare)
        reportsUtil.insertRegion(vgId)

        reportsUtil.login(användare, true)
        def file = getClass().getResourceAsStream('/' + filnamn)
        if (file == null) {
            throw new RuntimeException("File not found: " + filnamn)
        }
        def result = reportsUtil.uploadFile(vgId, file, filnamn)
        statusmeddelande = result.message
    }

}
