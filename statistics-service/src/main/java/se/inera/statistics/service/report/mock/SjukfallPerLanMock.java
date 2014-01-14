package se.inera.statistics.service.report.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import se.inera.statistics.service.report.api.SjukfallPerLan;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.Sex;
import se.inera.statistics.service.report.model.SimpleDualSexDataRow;
import se.inera.statistics.service.report.model.SimpleDualSexResponse;
import se.inera.statistics.service.report.repository.RollingLength;

public class SjukfallPerLanMock implements SjukfallPerLan {

    private Random random = new Random();
    public static final List<String> GROUPS = Arrays.asList("Blekinge län", "Dalarnas län", "Gotlands län", "Gävleborgs län", "Hallands län", "Jämtlands län",
            "Jönköpings län", "Kalmar län", "Kronobergs län", "Norrbottens län", "Skåne län", "Stockholms län", "Södermanlands län", "Uppsala län",
            "Värmlands län", "Västerbottens län", "Västernorrlands län", "Västmanlands län", "Västra Götalands län", "Örebro län", "Östergötlands län");

    // CHECKSTYLE:OFF MagicNumber
    @Override
    public SimpleDualSexResponse<SimpleDualSexDataRow> getStatistics(Range range) {
        final List<SimpleDualSexDataRow> rows = new ArrayList<>();
        for (String group : GROUPS) {
            int women = (int) (random.nextGaussian() * 2000 + 10000);
            int men = (int) (random.nextGaussian() * 2000 + 10000);
            rows.add(new SimpleDualSexDataRow(group, women, men));
        }
        final int monthsInPeriod = range.getMonths();
        return new SimpleDualSexResponse<SimpleDualSexDataRow>(rows, monthsInPeriod);
    }

    // CHECKSTYLE:ON

    @Override
    public void count(String period, String enhetId, String lanId, RollingLength length, Sex kon) {
    }

}
