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
package se.inera.statistics.hsa.services;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import se.inera.ifv.hsawsresponder.v3.GetMiuForPersonResponseType;
import se.inera.ifv.hsawsresponder.v3.GetMiuForPersonType;
import se.inera.ifv.hsawsresponder.v3.MiuInformationType;
import se.inera.ifv.statistics.spi.authorization.impl.HSAWebServiceCalls;
import se.inera.statistics.hsa.model.HsaId;
import se.inera.statistics.hsa.model.Vardenhet;
import se.inera.statistics.hsa.stub.Medarbetaruppdrag;

/**
 * @author andreaskaltenbach
 */
@Service
public class HsaOrganizationsServiceImpl implements HsaOrganizationsService {

    private static final Logger LOG = LoggerFactory.getLogger(HsaOrganizationsServiceImpl.class);

    @Autowired
    private HSAWebServiceCalls client;

    @Override
    public List<Vardenhet> getAuthorizedEnheterForHosPerson(HsaId hosPersonHsaId) {
        List<Vardenhet> vardenhetList = new ArrayList<>();

        // Set hos person hsa ID
        GetMiuForPersonType parameters = new GetMiuForPersonType();
        parameters.setHsaIdentity(hosPersonHsaId.getId());
        GetMiuForPersonResponseType response = client.callMiuRights(parameters);
        LOG.debug("User with HSA-Id " + hosPersonHsaId + " has " + response.getMiuInformation().size() + " medarbetaruppdrag");

        for (MiuInformationType info: response.getMiuInformation()) {
            if (Medarbetaruppdrag.STATISTIK.equalsIgnoreCase(info.getMiuPurpose())) {
                vardenhetList.add(new Vardenhet(new HsaId(info.getCareUnitHsaIdentity()), info.getCareUnitName(), new HsaId(info.getCareGiver()), info.getCareGiverName()));
            }
        }
        LOG.debug("User with HSA-Id has active 'Vård och behandling' for " + vardenhetList.size() + " enheter");

        return vardenhetList;
    }

}
