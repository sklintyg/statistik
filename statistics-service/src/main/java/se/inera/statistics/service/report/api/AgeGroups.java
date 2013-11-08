package se.inera.statistics.service.report.api;

import org.joda.time.LocalDate;

import org.springframework.transaction.annotation.Transactional;
import se.inera.statistics.service.report.listener.AldersGruppListener;
import se.inera.statistics.service.report.model.AgeGroupsResponse;
import se.inera.statistics.service.report.model.Sex;
import se.inera.statistics.service.report.model.db.AldersgruppKey;
import se.inera.statistics.service.report.repository.RollingLength;
import se.inera.statistics.service.report.util.Verksamhet;

import java.util.HashMap;

public interface AgeGroups {

    void count(String period, String hsaId, String group, RollingLength rollingLength, Verksamhet typ, Sex sex);

    AgeGroupsResponse getCurrentAgeGroups(String hsaId);

    AgeGroupsResponse getHistoricalAgeGroups(String hsaId, LocalDate when, RollingLength rollingLength);

    @Transactional
    void count(AldersgruppKey key, AldersGruppListener.AldersgruppValue value);

    void countAll(HashMap<AldersgruppKey, AldersGruppListener.AldersgruppValue> cache);
}
