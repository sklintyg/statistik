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

import se.inera.statistics.web.model.UserSettingsDTO
import se.inera.statistics.web.reports.ReportsUtil

class AnvandarensInstallningar {

    protected final ReportsUtil reportsUtil = new ReportsUtil()

    def användare
    boolean visaLäkarrapporten

    public void reset() {
        användare = null
        visaLäkarrapporten = null
    }

    public void setKommentar(String kommentar) {}

    public void setVisaLäkarrapporten(String visa) {
        visaLäkarrapporten = "JA".equalsIgnoreCase(visa)
    }

    public void execute() {
        reportsUtil.login(användare, true)
        def settings = new UserSettingsDTO(visaLäkarrapporten);
        reportsUtil.updateUserSettings(settings);
        def vgId = reportsUtil.getVardgivareForUser(användare)
    }

}
