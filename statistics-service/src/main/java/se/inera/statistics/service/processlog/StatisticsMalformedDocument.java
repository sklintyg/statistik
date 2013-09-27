package se.inera.statistics.service.processlog;

import java.text.ParseException;

public class StatisticsMalformedDocument extends RuntimeException {
    public StatisticsMalformedDocument(String message, Throwable e) {
        super(message, e);
    }
}
