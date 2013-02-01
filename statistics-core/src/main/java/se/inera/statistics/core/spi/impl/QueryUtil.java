package se.inera.statistics.core.spi.impl;

public class QueryUtil {

    public static final String FEMALE = "Female";

    public static final String MALE = "Male";

    public static boolean isAllSelected(String disability) {
        return "all".equals(disability);
    }

}
