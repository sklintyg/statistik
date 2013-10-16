package se.inera.statistics.service.report.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.joda.time.Period;

import se.inera.statistics.service.report.api.SickLeaveLength;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SickLeaveLengthResponse;
import se.inera.statistics.service.report.model.SickLeaveLengthRow;

public class SickLeaveLengthMock implements SickLeaveLength {

    private Random random = new Random();
    public static final List<String> GROUPS = Arrays.asList("< 15 dagar", "15-30 dagar", "31-90 dagar", "91-180 dagar", "181-360 dagar", "> 360 dagar");

    // CHECKSTYLE:OFF MagicNumber
    @Override
    public SickLeaveLengthResponse getStatistics(String hsaId, Range range) {
        final List<SickLeaveLengthRow> rows = new ArrayList<>();
        for (String group : GROUPS) {
            int women = (int) (random.nextGaussian() * 2000 + 10000);
            int men = (int) (random.nextGaussian() * 2000 + 10000);
            rows.add(new SickLeaveLengthRow(group, women, men));
        }
        final Period period = new Period(range.getFrom(), range.getTo());
        final int monthsInPeriod = period.getMonths() + period.getYears() * 12;
        return new SickLeaveLengthResponse(rows, monthsInPeriod);
    }

    // CHECKSTYLE:ON

}
