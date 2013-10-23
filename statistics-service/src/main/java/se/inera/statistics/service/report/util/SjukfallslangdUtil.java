package se.inera.statistics.service.report.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public final class SjukfallslangdUtil {
    public static final List<Group> GROUPS = Arrays.asList(new Group("<15 dagar", 15), new Group("15-30 dagar", 31), new Group("31-90 dagar", 91), new Group("91-180 dagar", 181), new Group("181-365 dagar", 366), new Group(">365 dagar", Integer.MAX_VALUE));

    public static final Map<String, Integer> GROUP_MAP = new TreeMap<>();

    static {
        for (Group g : GROUPS) {
            GROUP_MAP.put(g.groupName, g.cutoff);
        }
    }

    private SjukfallslangdUtil() {
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

    /**
     * Returns list of groups where att least part of the range is higher than cutoff.
     *
     * @param days days
     * @return groups
     */
    public static List<String> lookupGroupsLongerThan(int days) {
        List<String> result = new ArrayList<>();
        for (Group g : GROUPS) {
            if (g.cutoff > days) {
                result.add(g.groupName);
            }
        }
        return result;
    }

    public static String lookupGroupForLangd(int langd) {
        for (Group row : GROUPS) {
            if (langd < row.cutoff) {
                return row.groupName;
            }
        }
        throw new IllegalStateException("Groups have not been defines correctly. Missing group for " + langd);
    }
}
