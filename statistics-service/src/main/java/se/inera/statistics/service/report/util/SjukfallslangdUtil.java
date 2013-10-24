package se.inera.statistics.service.report.util;

import static se.inera.statistics.service.report.util.Ranges.range;

public final class SjukfallslangdUtil {
    public static final Ranges RANGES = new Ranges(range("<15 dagar", 15), range("15-30 dagar", 31), range("31-90 dagar", 91), range("91-180 dagar", 181), range("181-365 dagar", 366), range(">365 dagar", Integer.MAX_VALUE));
    private SjukfallslangdUtil() {
    }
}
