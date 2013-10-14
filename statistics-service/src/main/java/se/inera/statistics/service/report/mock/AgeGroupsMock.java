package se.inera.statistics.service.report.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.joda.time.Period;

import se.inera.statistics.service.report.api.AgeGroups;
import se.inera.statistics.service.report.model.AgeGroupsResponse;
import se.inera.statistics.service.report.model.AgeGroupsRow;
import se.inera.statistics.service.report.model.Range;

public class AgeGroupsMock implements AgeGroups {

    private Random random = new Random();
    public static final List<String> GROUPS = Arrays.asList("<21", "21-25", "26-30", "31-35", "36-40", "41-45", "46-50", "51-55", "56-60", ">60");

    // CHECKSTYLE:OFF MagicNumber
    @Override
    public AgeGroupsResponse getAgeGroups(String hsaId, Range range) {
        final List<AgeGroupsRow> rows = new ArrayList<AgeGroupsRow>();
        for (String group : GROUPS) {
            int women = (int) (random.nextGaussian() * 2000 + 10000);
            int men = (int) (random.nextGaussian() * 2000 + 10000);
            rows.add(new AgeGroupsRow(group, women, men));
        }
        final Period period = new Period(range.getFrom(), range.getTo());
        final int monthsInPeriod = period.getMonths() + period.getYears() * 12;
        return new AgeGroupsResponse(rows, monthsInPeriod);
    }

    // CHECKSTYLE:ON

}
