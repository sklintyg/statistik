package se.inera.statistics.service.report.util;

import static se.inera.statistics.service.report.util.Ranges.range;

public final class AldersgroupUtil {
    public static final Ranges RANGES = new Ranges(range("<21", 21), range("21-25", 26), range("26-30", 31), range("31-35", 36), range("36-40", 41), range("41-45", 46), range("46-50", 51), range("51-55", 56), range("56-60", 61), range(">60", Integer.MAX_VALUE));

    private AldersgroupUtil() {
    }
}
