/**
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
 *
 * This file is part of statistik (https://github.com/sklintyg/statistik).
 *
 * statistik is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * statistik is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.auth;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;

import se.inera.auth.model.User;
import se.inera.intyg.infra.integration.hsa.services.HsaPersonService;
import se.inera.statistics.hsa.model.HsaIdUser;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.hsa.services.HsaOrganizationsService;
import se.inera.statistics.hsa.services.UserAuthorization;
import se.inera.statistics.web.service.monitoring.MonitoringLogService;
import se.riv.infrastructure.directory.v1.PersonInformationType;

public class UserDetailsService implements SAMLUserDetailsService {

    private static final Logger LOG = LoggerFactory.getLogger(UserDetailsService.class);
    public static final String GLOBAL_VG_ACCESS_PREFIX = "INTYG;Statistik-";

    @Autowired
    private HsaOrganizationsService hsaOrganizationsService;

    @Autowired
    private HsaPersonService hsaPersonService;

    @Autowired
    @Qualifier("webMonitoringLogService")
    private MonitoringLogService monitoringLogService;

    @Override
    public Object loadUserBySAML(SAMLCredential credential) {
        LOG.info("User authentication was successful. SAML credential is " + credential);
        SakerhetstjanstAssertion assertion = getSakerhetstjanstAssertion(credential);

        final HsaIdUser hsaId = new HsaIdUser(assertion.getHsaId());
        List<PersonInformationType> hsaPersonInfo = hsaPersonService.getHsaPersonInfo(hsaId.getId());
        UserAuthorization userAuthorization = hsaOrganizationsService.getAuthorizedEnheterForHosPerson(hsaId);

        final List<HsaIdVardgivare> vgsWithProcessledarStatus = getVgsWithProcessledarStatus(userAuthorization.getSystemRoles());
        monitoringLogService.logUserLogin(hsaId);

        final String name = extractPersonName(hsaPersonInfo);
        return new User(hsaId, name, vgsWithProcessledarStatus, userAuthorization.getVardenhetList());
    }

    private String extractPersonName(List<PersonInformationType> hsaPersonInfo) {
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

    private SakerhetstjanstAssertion getSakerhetstjanstAssertion(SAMLCredential credential) {
        return new SakerhetstjanstAssertion(credential.getAuthenticationAssertion());
    }
}
