package se.inera.statistics.web.service.monitoring;

/**
 * Interface used when logging to monitoring file. Used to ensure that the log entries are uniform and easy to parse.
 */
public interface MonitoringLogService {

    void logUserLogin(String id);
}
