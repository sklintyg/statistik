package se.inera.statistics.tool;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateGenerator {

    public static LocalDate BASE = new LocalDate("1900-01-01");
    public static LocalDate LAST = new LocalDate("2016-01-01");
    public static DateTimeFormatter PERSONNUMMER = DateTimeFormat.forPattern("yyyyMMdd'");
    private static StringBuilder builder;

    public static void main(String[] args) throws IOException {
        try (OutputStream out = new FileOutputStream("../extras/datum.csv")) {
            int id = 0;
            LocalDate now;
            while ((now = BASE.plusDays(id)).isBefore(LAST)) {
                start();
                add(id);
                add(String.format("%1$d-%2$02d", now.getYear(), now.getMonthOfYear()));
                end();
                out.write(builder.toString().getBytes());
                id++;
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
        builder = new StringBuilder(300);
    }

}
