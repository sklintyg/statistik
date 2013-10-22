package se.inera.statistics.service.report.util;

import java.util.Arrays;
import java.util.List;

public final class AldersgroupUtil {
    public static final List<Group> GROUPS = Arrays.asList(new Group("<21", 21), new Group("21-25", 26), new Group("26-30", 31), new Group("31-35", 36), new Group("36-40", 41), new Group("41-45", 46), new Group("46-50", 51), new Group("51-55", 56), new Group("56-60", 61), new Group(">60", Integer.MAX_VALUE));

    private AldersgroupUtil() {
    }

    public static String lookupGroupForAge(int age) {
        for (Group row : GROUPS) {
            if (age < row.cutoff) {
                return row.groupName;
            }
        }
        throw new IllegalStateException("Groups have no tbeen defines correctly. Missing group for " + age);
    }

    public static final class Group {
        private final String groupName;
        private final int cutoff;

        private Group(String groupName, int cutoff) {
            this.groupName = groupName;
            this.cutoff = cutoff;
        }

        public String getGroupName() {
            return groupName;
        }
    }
}
