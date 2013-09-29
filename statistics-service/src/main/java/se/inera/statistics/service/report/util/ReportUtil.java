package se.inera.statistics.service.report.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public final class ReportUtil {

    private static final int NR_OF_PERIODS = 18;
    public static final List<String> PERIODS = createPeriods();

    private ReportUtil() {
    }

    private static List<String> createPeriods() {
        Locale sweden = new Locale("SV", "se");
        Calendar c = new GregorianCalendar(sweden);
        c.add(Calendar.MONTH, -NR_OF_PERIODS);
        List<String> names = new ArrayList<>();
        for (int i = 0; i < NR_OF_PERIODS; i++) {
            names.add(String.format(sweden, "%1$tb %1$tY", c));
            c.add(Calendar.MONTH, 1);
        }
        return names;
    }
}
