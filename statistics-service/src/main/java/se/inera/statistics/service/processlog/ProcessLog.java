package se.inera.statistics.service.processlog;

public interface ProcessLog {

    long store(EventType type, String string);

}
