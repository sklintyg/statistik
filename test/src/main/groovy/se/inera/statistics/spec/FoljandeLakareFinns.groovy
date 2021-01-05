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

import se.inera.statistics.service.hsa.HsaKon
import se.inera.statistics.web.reports.ReportsUtil
import se.inera.testsupport.Personal

class FoljandeLakareFinns {

    private final ReportsUtil reportsUtil = new ReportsUtil()

    String id
    String förnamn
    String efternamn
    String kön
    int ålder
    String befattningar
    boolean skyddad

    public void reset() {
        förnamn = "Läkarförnamn"
        efternamn = "Läkarefternamn"
        kön = "UNKNOWN"
        ålder = 0
        skyddad = false
    }

    public void setKommentar(String kommentar) {}

    public void execute() {
        def hsaKon = HsaKon.valueOf(kön.toUpperCase())

        def befattningarEmpty = befattningar == null || befattningar.isEmpty()
        def befattningarList = befattningarEmpty ? Collections.emptyList() : Arrays.asList(befattningar.split(","))
        def personal = new Personal(id, förnamn, efternamn, hsaKon, ålder, befattningarList, skyddad)
        reportsUtil.insertPersonal(personal)
    }

}
