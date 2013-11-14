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
