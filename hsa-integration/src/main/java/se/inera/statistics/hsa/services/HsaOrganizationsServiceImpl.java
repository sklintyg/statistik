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
    public List<Vardenhet> getAuthorizedEnheterForHosPerson(String hosPersonHsaId) {
        List<Vardenhet> vardenhetList = new ArrayList<>();

        // Set hos person hsa ID
        GetMiuForPersonType parameters = new GetMiuForPersonType();
        parameters.setHsaIdentity(hosPersonHsaId);
        GetMiuForPersonResponseType response = client.callMiuRights(parameters);
        LOG.debug("User with HSA-Id " + hosPersonHsaId + " has " + response.getMiuInformation().size() + " medarbetaruppdrag");

        for (MiuInformationType info: response.getMiuInformation()) {
            if (Medarbetaruppdrag.STATISTIK.equalsIgnoreCase(info.getMiuPurpose())) {
                vardenhetList.add(new Vardenhet(info.getCareUnitHsaIdentity(), info.getCareUnitName()));
            }
        }
        LOG.debug("User with HSA-Id has active 'Vård och behandling' for " + vardenhetList.size() + " enheter");

        return vardenhetList;
    }

}
