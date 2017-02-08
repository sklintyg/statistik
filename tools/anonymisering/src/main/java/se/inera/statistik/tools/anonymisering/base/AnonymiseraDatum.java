package se.inera.statistik.tools.anonymisering.base;

import java.time.LocalDate;
import java.util.Random;

public class AnonymiseraDatum {

    public static final int DATE_RANGE = 28;

    private Random random = new Random();

    // CHECKSTYLE:OFF MagicNumber
    public String anonymiseraDatum(String datum) {
        if (datum == null) {
            return null;
        }
        try {
            LocalDate date = LocalDate.parse(datum);
            // random days from -14 to +14, but not 0
            int days = random.nextInt(DATE_RANGE) - DATE_RANGE / 2;
            if (days == 0) {
                days = DATE_RANGE / 2;
            }
            date = date.plusDays(days);
            return date.toString();
        } catch (Exception e) {
            return datum;
        }
    }
    // CHECKSTYLE:ON MagicNumber

}
