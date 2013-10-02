package se.inera.statistics.service.processlog;

public class StatisticsMalformedDocument extends RuntimeException {
    public StatisticsMalformedDocument(String message, Throwable e) {
        super(message, e);
    }
}
