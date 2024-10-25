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
package se.inera.auth;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import se.inera.auth.model.User;
import se.inera.statistics.integration.hsa.model.HsaIdUser;
import se.inera.statistics.integration.hsa.model.HsaIdVardgivare;
import se.inera.statistics.integration.hsa.model.StatisticsPersonInformation;
import se.inera.statistics.integration.hsa.model.UserAuthorization;
import se.inera.statistics.integration.hsa.model.Vardgivare;
import se.inera.statistics.integration.hsa.services.HsaOrganizationsService;
import se.inera.statistics.integration.hsa.services.HsaPersonService;
import se.inera.statistics.web.service.monitoring.MonitoringLogService;

@Service
public class UserDetailsService {

    public static final String GLOBAL_VG_ACCESS_PREFIX = "INTYG;Statistik-";

    @Autowired
    private HsaOrganizationsService hsaOrganizationsService;

    @Autowired
    private HsaPersonService hsaPersonService;

    @Autowired
    @Qualifier("webMonitoringLogService")
    private MonitoringLogService monitoringLogService;

    public User buildUserPrincipal(String employeeHsaId, LoginMethod loginMethod) {
        final HsaIdUser hsaId = new HsaIdUser(employeeHsaId);
        List<StatisticsPersonInformation> hsaPersonInfo = hsaPersonService.getHsaPersonInfo(hsaId.getId());
        UserAuthorization userAuthorization = hsaOrganizationsService.getAuthorizedEnheterForHosPerson(hsaId);
        final List<Vardgivare> vardgivareWithProcessledarStatusList = getVgsWithProcessledarStatus(userAuthorization.getSystemRoles())
            .stream().map(vgHsaId -> hsaOrganizationsService.getVardgivare(vgHsaId))
            .toList();

        monitoringLogService.logUserLogin(hsaId, loginMethod);

        final String name = extractPersonName(hsaPersonInfo);
        return new User(hsaId, name, vardgivareWithProcessledarStatusList, userAuthorization.getVardenhetList(), loginMethod);
    }

    private String extractPersonName(List<StatisticsPersonInformation> hsaPersonInfo) {
        if (hsaPersonInfo != null && !hsaPersonInfo.isEmpty()) {
            return hsaPersonInfo.get(0).getGivenName() + " " + hsaPersonInfo.get(0).getMiddleAndSurName();
        } else {
            return "";
        }
    }

    private List<HsaIdVardgivare> getVgsWithProcessledarStatus(List<String> systemRoles) {
        final ArrayList<HsaIdVardgivare> statistikRoles = new ArrayList<>();
        for (String systemRole : systemRoles) {
            if (systemRole.toLowerCase().startsWith(GLOBAL_VG_ACCESS_PREFIX.toLowerCase())) {
                final HsaIdVardgivare vgId = new HsaIdVardgivare(systemRole.substring(GLOBAL_VG_ACCESS_PREFIX.length()));
                statistikRoles.add(vgId);
            }
        }
        return statistikRoles;
    }
}