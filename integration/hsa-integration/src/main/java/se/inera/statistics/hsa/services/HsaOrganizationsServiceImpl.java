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
package se.inera.statistics.hsa.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.inera.intyg.common.integration.hsa.client.AuthorizationManagementService;
import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.hsa.model.HsaIdUser;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.hsa.model.Vardenhet;
import se.riv.infrastructure.directory.authorizationmanagement.v1.GetCredentialsForPersonIncludingProtectedPersonResponseType;
import se.riv.infrastructure.directory.v1.CommissionType;
import se.riv.infrastructure.directory.v1.CredentialInformationType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author andreaskaltenbach
 */
@Service
public class HsaOrganizationsServiceImpl implements HsaOrganizationsService {

    private static final Logger LOG = LoggerFactory.getLogger(HsaOrganizationsServiceImpl.class);

    @Autowired
    private AuthorizationManagementService authorizationManagementService;


    @Override
    public List<Vardenhet> getAuthorizedEnheterForHosPerson(HsaIdUser hosPersonHsaId) {
        List<Vardenhet> vardenhetList = new ArrayList<>();

        GetCredentialsForPersonIncludingProtectedPersonResponseType response = authorizationManagementService.getAuthorizationsForPerson(hosPersonHsaId.getId(), "", "");

        if (response != null && response.getCredentialInformation() != null) {
            LOG.debug("User with HSA-Id " + hosPersonHsaId + " has " + response.getCredentialInformation().size() + " medarbetaruppdrag");

            for (CredentialInformationType info : response.getCredentialInformation()) {
                for (CommissionType commissionType : info.getCommission()) {
                    if (Medarbetaruppdrag.STATISTIK.equalsIgnoreCase(commissionType.getCommissionPurpose())) {
                        vardenhetList.add(new Vardenhet(new HsaIdEnhet(commissionType.getHealthCareUnitHsaId()), commissionType.getHealthCareUnitName(), new HsaIdVardgivare(commissionType.getHealthCareProviderHsaId()), commissionType.getHealthCareProviderName()));
                    }
                }
            }
            vardenhetList = vardenhetList.stream().distinct().collect(Collectors.toList());
        }
        LOG.debug("User with HSA-Id " + hosPersonHsaId + " has active 'Statistik' for " + vardenhetList.size() + " enheter");

        return vardenhetList;
    }

}
