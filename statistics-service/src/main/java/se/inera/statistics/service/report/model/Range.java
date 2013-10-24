package se.inera.statistics.service.report.model;

import java.util.Locale;

import org.joda.time.LocalDate;
import org.joda.time.Months;

public final class Range {
    private static final int DEFAULT_PERIOD = 18;
    private static final int YEAR_PERIOD = 12;
    private static final int QUARTER_PERIOD = 3;
    private final LocalDate from;
    private final LocalDate to;

    public Range() {
        this(DEFAULT_PERIOD);
    }

    public Range(LocalDate from, LocalDate to) {
        this.from = from;
        this.to = to;
    }

    public Range(int months) {
        to = new LocalDate().withDayOfMonth(1).minusMonths(1);
        from = to.minusMonths(months - 1);
    }

    public LocalDate getFrom() {
        return from;
    }

    public LocalDate getTo() {
        return to;
    }

    public String toString() {
        Locale sv = new Locale("sv", "SE");
        if (from.getYear() == to.getYear()) {
            return from.toString("MMM", sv) + "-" + to.toString("MMM yyyy", sv);
        } else {
            return from.toString("MMM yyyy", sv) + "-" + to.toString("MMM yyyy", sv);
        }
    }

    public int getMonths() {
        return Months.monthsBetween(from, to).getMonths() + 1;
    }
    
    public static Range year() {
        return new Range(YEAR_PERIOD);
    }

    public static Range quarter() {
        return new Range(QUARTER_PERIOD);
    }
}
