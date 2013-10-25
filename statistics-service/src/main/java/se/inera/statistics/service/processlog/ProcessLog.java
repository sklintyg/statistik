package se.inera.statistics.service.processlog;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public interface ProcessLog {

    long store(EventType type, String string, String correlationId, long timestamp);

    void confirm(long id);

    List<IntygEvent> getPending(int max);

}
