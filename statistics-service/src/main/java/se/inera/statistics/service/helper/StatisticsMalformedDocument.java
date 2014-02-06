package se.inera.statistics.service.helper;

public class StatisticsMalformedDocument extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public StatisticsMalformedDocument(String message, Throwable e) {
        super(message, e);
    }
}
