/*
 * Copyright (C) 2013 - 2014 Inera AB (http://www.inera.se)
 *
 *     This file is part of Inera Statistics (http://code.google.com/p/inera-statistics/).
 *
 *     Inera Statistics is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Inera Statistics is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU LESSER GENERAL PUBLIC LICENSE for more details.
 *
 *     You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.inera.auth;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;

import se.inera.auth.model.User;
import se.inera.statistics.hsa.model.Vardenhet;
import se.inera.statistics.hsa.services.HsaOrganizationsService;

public class UserDetailsService implements SAMLUserDetailsService {

    private static final Logger LOG = LoggerFactory.getLogger(UserDetailsService.class);
    public static final String GLOBAL_VG_ACCESS_FLAG = "INTYG;Statistik";

    @Autowired
    private HsaOrganizationsService hsaOrganizationsService;

    @Override
    public Object loadUserBySAML(SAMLCredential credential) {
        LOG.info("User authentication was successful. SAML credential is " + credential);

        SakerhetstjanstAssertion assertion = new SakerhetstjanstAssertion(credential.getAuthenticationAssertion());

        List<Vardenhet> authorizedVerksamhets = hsaOrganizationsService.getAuthorizedEnheterForHosPerson(assertion.getHsaId());

        Vardenhet selectedVerksamhet = getLoginVerksamhet(authorizedVerksamhets, assertion.getEnhetHsaId());
        List<Vardenhet> filtered = filterByVardgivare(authorizedVerksamhets, selectedVerksamhet.getVardgivarId());

        return new User(assertion.getHsaId(), assertion.getFornamn() + ' ' + assertion.getMellanOchEfternamn(), assertion.getSystemRolls().contains(GLOBAL_VG_ACCESS_FLAG), selectedVerksamhet, filtered);
    }

    private List<Vardenhet> filterByVardgivare(List<Vardenhet> vardenhets, String vardgivarId) {
        ArrayList<Vardenhet> filtered = new ArrayList<>();
        for (Vardenhet vardenhet: vardenhets) {
            if (vardenhet.getVardgivarId() != null && vardenhet.getVardgivarId().equals(vardgivarId)) {
                filtered.add(vardenhet);
            }
        }
        return filtered;
    }

    private Vardenhet getLoginVerksamhet(List<Vardenhet> vardenhets, String enhetHsaId) {
        for (Vardenhet vardenhet : vardenhets) {
            if (vardenhet.getId().equals(enhetHsaId)) {
                return vardenhet;
            }
        }
        return null;
    }

}
