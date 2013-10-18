package se.inera.statistics.service.report.listener;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import se.inera.statistics.service.report.api.AgeGroups;
import se.inera.statistics.service.report.util.Verksamhet;

@Component
public class AldersGruppListener extends GenericAbstractListener {

    public static final List<GroupTableRow> GROUPS = Arrays.asList(new GroupTableRow("<21", 21), new GroupTableRow("21-25", 26), new GroupTableRow("26-30", 31), new GroupTableRow("31-35", 36), new GroupTableRow("36-40", 41), new GroupTableRow("41-45", 46), new GroupTableRow("46-50", 51), new GroupTableRow("51-55", 56), new GroupTableRow("56-60", 61), new GroupTableRow(">60", -1));

    @Autowired
    private AgeGroups ageGroups;

    @Override
    void accept(GenericHolder token, String period) {
        String group = lookupGroupForAge(token.getAge());
        ageGroups.count(period, token.getEnhetId(), group, Verksamhet.ENHET, token.getKon());
        ageGroups.count(period, token.getVardgivareId(), group, Verksamhet.VARDGIVARE, token.getKon());
    }

    public static final class GroupTableRow {
        private final String groupName;
        private final int cutoff;
        
        private GroupTableRow(String groupName, int cutoff) {
            this.groupName = groupName;
            this.cutoff = cutoff;
        }

        public String getGroupName() {
            return groupName;
        }
    }

    private String lookupGroupForAge(int age) {
        for (GroupTableRow row : GROUPS) {
            if (age < row.cutoff) {
                return row.groupName;
            }
        }
        return GROUPS.get(GROUPS.size() - 1).groupName;
    }
}
