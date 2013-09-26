package se.inera.statistics.service.queue;

public class StatisticsJMSException extends RuntimeException {
    public StatisticsJMSException(String s, Exception e) {
        super(s, e);
    }
}
