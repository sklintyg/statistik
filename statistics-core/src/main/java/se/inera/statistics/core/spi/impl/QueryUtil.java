package se.inera.statistics.core.spi.impl;

public final class QueryUtil {

    public static final String FEMALE = "Female";

    public static final String MALE = "Male";

    private QueryUtil() {
        // Hide constructor for utility class.
    }

    public static boolean isAllSelected(String disability) {
        return "all".equals(disability);
    }

}
