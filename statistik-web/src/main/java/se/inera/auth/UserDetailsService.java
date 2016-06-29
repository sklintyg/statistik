/**
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;
import se.inera.auth.model.User;
import se.inera.intyg.common.integration.hsa.services.HsaPersonService;
import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.hsa.model.HsaIdUser;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.hsa.model.Vardenhet;
import se.inera.statistics.hsa.services.HsaOrganizationsService;
import se.inera.statistics.hsa.services.UserAuthorization;
import se.inera.statistics.web.service.monitoring.MonitoringLogService;
import se.riv.infrastructure.directory.v1.PersonInformationType;

import java.util.ArrayList;
import java.util.List;

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

        Vardenhet selectedVerksamhet = userAuthorization.getVardenhetList().stream().findFirst().orElseThrow(() -> new IllegalStateException("Cannot log you in, no Vardenhet!"));
        //Vardenhet selectedVerksamhet = getLoginVerksamhet(userAuthorization.getVardenhetList(), new HsaIdEnhet(assertion.getEnhetHsaId()));
        HsaIdVardgivare vardgivare = selectedVerksamhet != null ? selectedVerksamhet.getVardgivarId() : null;

        HsaIdEnhet vardEnhet = selectedVerksamhet != null ? selectedVerksamhet.getId() : null;
        List<Vardenhet> filtered = filterByVardgivare(userAuthorization.getVardenhetList(), vardgivare);

        final boolean processledare = isProcessledare(userAuthorization.getSystemRoles(), vardgivare);
        monitoringLogService.logUserLogin(hsaId, vardgivare, vardEnhet, processledare);

        //final String name = assertion.getFornamn() + ' ' + assertion.getMellanOchEfternamn();
        final String name = extractPersonName(hsaPersonInfo);

        return new User(hsaId, name, processledare, selectedVerksamhet, filtered);
    }

    private String extractPersonName(List<PersonInformationType> hsaPersonInfo) {
        if (hsaPersonInfo != null && !hsaPersonInfo.isEmpty()) {
            return hsaPersonInfo.get(0).getGivenName() + " " + hsaPersonInfo.get(0).getMiddleAndSurName();
        } else {
            return "";
        }
    }

    private boolean isProcessledare(List<String> systemRoles, HsaIdVardgivare vardgivare) {
        for (String systemRole : systemRoles) {
            if ((GLOBAL_VG_ACCESS_PREFIX + vardgivare).equalsIgnoreCase(systemRole)) {
                return true;
            }
        }
        return false;
    }

    SakerhetstjanstAssertion getSakerhetstjanstAssertion(SAMLCredential credential) {
        return new SakerhetstjanstAssertion(credential.getAuthenticationAssertion());
    }

    private List<Vardenhet> filterByVardgivare(List<Vardenhet> vardenhets, HsaIdVardgivare vardgivarId) {
        ArrayList<Vardenhet> filtered = new ArrayList<>();
        for (Vardenhet vardenhet: vardenhets) {
            if (vardenhet.getVardgivarId() != null && vardenhet.getVardgivarId().equals(vardgivarId)) {
                filtered.add(vardenhet);
            }
        }
        return filtered;
    }

    Vardenhet getLoginVerksamhet(List<Vardenhet> vardenhets, HsaIdEnhet enhetHsaId) {
        for (Vardenhet vardenhet : vardenhets) {
            if (vardenhet.getId().equals(enhetHsaId)) {
                return vardenhet;
            }
        }
        return null;
    }

}
