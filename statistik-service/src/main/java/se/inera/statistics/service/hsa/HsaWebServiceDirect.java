package se.inera.statistics.service.hsa;

import org.springframework.beans.factory.annotation.Autowired;

import se.inera.ifv.hsawsresponder.v3.GetMiuForPersonResponseType;
import se.inera.ifv.hsawsresponder.v3.GetMiuForPersonType;
import se.inera.ifv.hsawsresponder.v3.GetStatisticsCareGiverResponseType;
import se.inera.ifv.hsawsresponder.v3.GetStatisticsHsaUnitResponseType;
import se.inera.ifv.hsawsresponder.v3.GetStatisticsNamesResponseType;
import se.inera.ifv.hsawsresponder.v3.GetStatisticsPersonResponseType;
import se.inera.ifv.statistics.spi.authorization.impl.HSAWebServiceCalls;

 /**
 * This implementation will simply delegate all calls directly to HSA
 */
public class HsaWebServiceDirect implements HsaWebService {

    @Autowired
    private HSAWebServiceCalls service;

    public void setHsaLogicalAddress(String hsaLogicalAddress) {
        service.setHsaLogicalAddress(hsaLogicalAddress);
    }

    public void callPing() {
        service.callPing();
    }

    public GetStatisticsHsaUnitResponseType getStatisticsHsaUnit(String unitId) {
        return service.getStatisticsHsaUnit(unitId);
    }

    public GetStatisticsNamesResponseType getStatisticsNames(String personId) {
        return service.getStatisticsNames(personId);
    }

    public GetStatisticsPersonResponseType getStatisticsPerson(String personId) {
        return service.getStatisticsPerson(personId);
    }

    public GetMiuForPersonResponseType callMiuRights(GetMiuForPersonType parameters) {
        return service.callMiuRights(parameters);
    }

    public GetStatisticsCareGiverResponseType getStatisticsCareGiver(String careGiverId) {
        return service.getStatisticsCareGiver(careGiverId);
    }

}
