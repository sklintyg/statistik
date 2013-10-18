package se.inera.statistics.service.report.listener;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.inera.statistics.service.report.api.AgeGroups;
import se.inera.statistics.service.report.model.Sex;

import java.util.Arrays;
import java.util.List;

@Component
public class AldersGruppListener {
    private static final DateTimeFormatter PERIOD_FORMATTER = DateTimeFormat.forPattern("yyyy-MM");

    private static final List<GroupTableRow> GROUPS = Arrays.asList(new GroupTableRow("<21", 21), new GroupTableRow("21-25", 26), new GroupTableRow("26-30", 31), new GroupTableRow("31-35", 36), new GroupTableRow("36-40", 41), new GroupTableRow("41-45", 46), new GroupTableRow("46-50", 51), new GroupTableRow("51-55", 56), new GroupTableRow("56-60", 61), new GroupTableRow(">60", -1));

    @Autowired
    AgeGroups ageGroups;

    public void acceptPeriod(String hsaId, LocalDate startMonth, LocalDate endMonth, int age, Sex sex) {
        for (LocalDate month = startMonth; !month.isAfter(endMonth); month = month.plusMonths(1)) {
            accept(hsaId, month, age, sex);
        }
    }

    private void accept(String hsaId, LocalDate month, int age, Sex sex) {
        String period = PERIOD_FORMATTER.print(month);
        String group = lookupGroupForAge(age);
        ageGroups.count(period, hsaId, group, sex);
    }

    private String lookupGroupForAge(int age) {
        for (GroupTableRow row : GROUPS) {
            if (age < row.cutoff) {
                return row.groupName;
            }
        }
        return GROUPS.get(GROUPS.size() - 1).groupName;
    }

    private static final class GroupTableRow {
        private final String groupName;
        private final int cutoff;

        private GroupTableRow(String groupName, int cutoff) {
            this.groupName = groupName;
            this.cutoff = cutoff;
        }
    }

}
