package se.inera.statistics.service.hsa;

import se.inera.ifv.hsawsresponder.v3.GetMiuForPersonResponseType;
import se.inera.ifv.hsawsresponder.v3.GetMiuForPersonType;
import se.inera.ifv.hsawsresponder.v3.GetStatisticsCareGiverResponseType;
import se.inera.ifv.hsawsresponder.v3.GetStatisticsHsaUnitResponseType;
import se.inera.ifv.hsawsresponder.v3.GetStatisticsNamesResponseType;
import se.inera.ifv.hsawsresponder.v3.GetStatisticsPersonResponseType;

public interface HsaWebService {

    void setHsaLogicalAddress(String hsaLogicalAddress);

    void callPing();

    GetStatisticsHsaUnitResponseType getStatisticsHsaUnit(String unitId);

    GetStatisticsNamesResponseType getStatisticsNames(String personId);

    GetStatisticsPersonResponseType getStatisticsPerson(String personId);

    GetMiuForPersonResponseType callMiuRights(GetMiuForPersonType parameters);

    GetStatisticsCareGiverResponseType getStatisticsCareGiver(String careGiverId);

}
