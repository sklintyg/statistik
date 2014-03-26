package se.inera.statistics.tool;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public final class DateGenerator {

    public static final LocalDate BASE = new LocalDate("1900-01-01");
    public static final LocalDate LAST = new LocalDate("2016-01-01");
    public static final DateTimeFormatter PERSONNUMMER = DateTimeFormat.forPattern("yyyyMMdd'");
    public static final int CAPACITY = 300;
    private static StringBuilder builder;

    private DateGenerator() {
    }

    public static void main(String[] args) throws IOException {
        try (OutputStream out = new FileOutputStream("../extras/datum.csv")) {
            int id = 0;
            LocalDate now = BASE;
            while (now.isBefore(LAST)) {
                start();
                add(id);
                add(String.format("%1$d-%2$02d", now.getYear(), now.getMonthOfYear()));
                end();
                out.write(builder.toString().getBytes());
                id++;
                now = BASE.plusDays(id);
            }
            out.close();
        }
    }

    private static void end() {
        builder.append('\n');
    }

    private static void add(String value) {
        comma();
        builder.append(value);
    }

    private static void add(int value) {
        comma();
        builder.append(value);
    }

    private static void comma() {
        if (builder.length() > 0) {
            builder.append(',');
        }
    }

    private static void start() {
        builder = new StringBuilder(CAPACITY);
    }

}
