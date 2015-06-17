/**
 * Copyright (C) 2015 Inera AB (http://www.inera.se)
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
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;
import se.inera.auth.model.User;
import se.inera.statistics.hsa.model.HsaId;
import se.inera.statistics.hsa.model.Vardenhet;
import se.inera.statistics.hsa.services.HsaOrganizationsService;

import java.util.ArrayList;
import java.util.List;

public class UserDetailsService implements SAMLUserDetailsService {

    private static final Logger LOG = LoggerFactory.getLogger(UserDetailsService.class);
    public static final String GLOBAL_VG_ACCESS_PREFIX = "INTYG;Statistik-";

    @Autowired
    private HsaOrganizationsService hsaOrganizationsService;

    @Override
    public Object loadUserBySAML(SAMLCredential credential) {
        LOG.info("User authentication was successful. SAML credential is " + credential);

        SakerhetstjanstAssertion assertion = getSakerhetstjanstAssertion(credential);

        final HsaId hsaId = new HsaId(assertion.getHsaId());
        List<Vardenhet> authorizedVerksamhets = hsaOrganizationsService.getAuthorizedEnheterForHosPerson(hsaId);

        Vardenhet selectedVerksamhet = getLoginVerksamhet(authorizedVerksamhets, new HsaId(assertion.getEnhetHsaId()));
        HsaId vardgivare = selectedVerksamhet != null ? selectedVerksamhet.getVardgivarId() : null;
        List<Vardenhet> filtered = filterByVardgivare(authorizedVerksamhets, vardgivare);

        final boolean processledare = isProcessledare(assertion, vardgivare);
        final String name = assertion.getFornamn() + ' ' + assertion.getMellanOchEfternamn();
        return new User(hsaId, name, processledare, selectedVerksamhet, filtered);
    }

    private boolean isProcessledare(SakerhetstjanstAssertion assertion, HsaId vardgivare) {
        for (String systemRole : assertion.getSystemRoles()) {
            if ((GLOBAL_VG_ACCESS_PREFIX + vardgivare).equalsIgnoreCase(systemRole)) {
                return true;
            }
        }
        return false;
    }

    SakerhetstjanstAssertion getSakerhetstjanstAssertion(SAMLCredential credential) {
        return new SakerhetstjanstAssertion(credential.getAuthenticationAssertion());
    }

    private List<Vardenhet> filterByVardgivare(List<Vardenhet> vardenhets, HsaId vardgivarId) {
        ArrayList<Vardenhet> filtered = new ArrayList<>();
        for (Vardenhet vardenhet: vardenhets) {
            if (vardenhet.getVardgivarId() != null && vardenhet.getVardgivarId().equals(vardgivarId)) {
                filtered.add(vardenhet);
            }
        }
        return filtered;
    }

    Vardenhet getLoginVerksamhet(List<Vardenhet> vardenhets, HsaId enhetHsaId) {
        for (Vardenhet vardenhet : vardenhets) {
            if (vardenhet.getId().equals(enhetHsaId)) {
                return vardenhet;
            }
        }
        return null;
    }

}
