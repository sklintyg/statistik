package se.inera.statistics.service.report.util;

import java.util.Arrays;
import java.util.List;

public class SjukfallslangdUtil {
    public static final List<Group> GROUPS = Arrays.asList(new Group("<15 dagar", 15), new Group("15-30 dagar", 31), new Group("31-90 dagar", 91), new Group("90-365 dagar", 366), new Group(">365 dagar", Integer.MAX_VALUE));

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

    public static String lookupGroupForLangd(int langd) {
        for (Group row : GROUPS) {
            if (langd < row.cutoff) {
                return row.groupName;
            }
        }
        throw new IllegalStateException("Groups have no tbeen defines correctly. Missing group for " + langd);
    }
}
