package se.inera.statistics.integration.hsa.services;

import se.inera.statistics.integration.hsa.model.GetStatisticsCareGiverResponseDto;
import se.inera.statistics.integration.hsa.model.GetStatisticsHsaUnitResponseDto;
import se.inera.statistics.integration.hsa.model.GetStatisticsNamesResponseDto;
import se.inera.statistics.integration.hsa.model.GetStatisticsPersonResponseDto;

public interface HsaStatisticsService {

    GetStatisticsCareGiverResponseDto getStatisticsCareGiver(String careGiverId);
    GetStatisticsHsaUnitResponseDto getStatisticsHsaUnit(String unitId);
    GetStatisticsPersonResponseDto getStatisticsPerson(String personId);
    GetStatisticsNamesResponseDto getStatisticsNames(String personId);
}
