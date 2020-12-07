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
package se.inera.statistics.web.service.monitoring;

import se.inera.statistics.integration.hsa.model.HsaIdUser;
import se.inera.statistics.integration.hsa.model.HsaIdVardgivare;

/**
 * Interface used when logging to monitoring file. Used to ensure that the log entries are uniform and easy to parse.
 */
public interface MonitoringLogService {

    void logUserLogin(HsaIdUser hsaUser);

    void logFileUpload(HsaIdUser hsaUser, HsaIdVardgivare hsaVardgivare, String fileName, Integer rows);

    void logTrackAccessProtectedChartData(HsaIdUser hsaUser, HsaIdVardgivare hsaVardgivare, String uri);

    void logTrackAccessAnonymousChartData(String uri);

    void logBrowserInfo(String browserName, String browserVersion, String osFamily, String osVersion, String width, String height);

    // Saml
    void logSamlStatusForFailedLogin(String issuer, String samlStatus);
}
