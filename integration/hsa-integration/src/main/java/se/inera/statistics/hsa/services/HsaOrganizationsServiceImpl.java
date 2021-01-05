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
package se.inera.statistics.hsa.services;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.integration.hsa.client.AuthorizationManagementService;
import se.inera.intyg.infra.integration.hsa.client.OrganizationUnitService;
import se.inera.intyg.infra.integration.hsa.exception.HsaServiceCallException;
import se.inera.intyg.infra.integration.hsa.model.Vardgivare;
import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.hsa.model.HsaIdUser;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.hsa.model.Vardenhet;
import se.riv.infrastructure.directory.organization.getunitresponder.v1.UnitType;
import se.riv.infrastructure.directory.v1.CommissionType;
import se.riv.infrastructure.directory.v1.CredentialInformationType;

/**
 * Interfaces with the {@link AuthorizationManagementService} in order to fetch Medarbetaruppdrag for the given
 * employeeHsaId. Will filter out any non "Statistik"-purposed MiU:s.
 *
 * @author erikl
 */
@Service
public class HsaOrganizationsServiceImpl implements HsaOrganizationsService {

    private static final Logger LOG = LoggerFactory.getLogger(HsaOrganizationsServiceImpl.class);

    @Autowired
    private AuthorizationManagementService authorizationManagementService;

    @Autowired
    private OrganizationUnitService organizationUnitService;

    private final VardenhetComparator veComparator = new VardenhetComparator();

    @Override
    public UserAuthorization getAuthorizedEnheterForHosPerson(HsaIdUser hosPersonHsaId) {
        List<Vardenhet> vardenhetList = new ArrayList<>();
        List<String> systemRoles = new ArrayList<>();

        List<CredentialInformationType> response = null;
        try {
            response = authorizationManagementService.getAuthorizationsForPerson(hosPersonHsaId.getId(), "", "");
        } catch (HsaServiceCallException e) {
            LOG.error("Error loading authorizations", e);
        }

        if (response != null) {
            LOG.debug("User with HSA-Id " + hosPersonHsaId + " has " + response.size() + " medarbetaruppdrag");

            for (CredentialInformationType info : response) {
                vardenhetList.addAll(getAllVardenhetsWithMuWithStatistikPurpose(info));
                systemRoles.addAll(
                    info.getHsaSystemRole().stream().map(sr -> sr.getSystemId() + ";" + sr.getRole()).collect(Collectors.toList()));
            }
            vardenhetList = vardenhetList.stream().distinct().sorted(veComparator).collect(Collectors.toList());
        }
        LOG.debug("User with HSA-Id " + hosPersonHsaId + " has active 'Statistik' for " + vardenhetList.size() + " enheter");

        return new UserAuthorization(vardenhetList, systemRoles);
    }

    @Override
    public Vardgivare getVardgivare(HsaIdVardgivare hsaIdVardgivare) {
        UnitType unit = null;
        try {
            unit = organizationUnitService.getUnit(hsaIdVardgivare.getId());
            return new Vardgivare(unit.getUnitHsaId(), unit.getUnitName());
        } catch (HsaServiceCallException e) {
            LOG.error("Error loading unit details for vardgivare {}", hsaIdVardgivare.getId());
            return null;
        }
    }

    private List<Vardenhet> getAllVardenhetsWithMuWithStatistikPurpose(CredentialInformationType info) {
        List<Vardenhet> enhets = new ArrayList<>();
        for (CommissionType commissionType : info.getCommission()) {
            if (Medarbetaruppdrag.STATISTIK.equalsIgnoreCase(commissionType.getCommissionPurpose())) {
                enhets.add(new Vardenhet(new HsaIdEnhet(commissionType.getHealthCareUnitHsaId()), commissionType.getHealthCareUnitName(),
                    new HsaIdVardgivare(commissionType.getHealthCareProviderHsaId()), commissionType.getHealthCareProviderName()));
            }
        }
        return enhets;
    }

    private final class VardenhetComparator implements Comparator<Vardenhet> {

        @Override
        public int compare(Vardenhet o1, Vardenhet o2) {
            return o1.getId().getId().compareTo(o2.getId().getId());
        }
    }

}
