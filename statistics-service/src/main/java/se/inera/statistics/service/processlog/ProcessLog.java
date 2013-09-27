package se.inera.statistics.service.processlog;

import org.springframework.stereotype.Component;

@Component
public interface ProcessLog {

    long store(EventType type, String string, String correlationId, long timestamp);

}
