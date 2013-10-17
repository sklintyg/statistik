package se.inera.statistics.service.common;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public interface CommonPersistence {

    @Transactional
    void cleanDb();
}
