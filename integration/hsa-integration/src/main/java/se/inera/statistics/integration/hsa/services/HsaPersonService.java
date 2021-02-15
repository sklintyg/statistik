package se.inera.statistics.integration.hsa.services;

import se.inera.statistics.integration.hsa.model.StatisticsPersonInformation;

import java.util.List;

public interface HsaPersonService {
    List<StatisticsPersonInformation> getHsaPersonInfo(String var1);
}
