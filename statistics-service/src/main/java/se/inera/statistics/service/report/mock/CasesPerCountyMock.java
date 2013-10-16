package se.inera.statistics.service.report.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.joda.time.Period;

import se.inera.statistics.service.report.api.CasesPerCounty;
import se.inera.statistics.service.report.model.CasesPerCountyResponse;
import se.inera.statistics.service.report.model.CasesPerCountyRow;
import se.inera.statistics.service.report.model.Range;

public class CasesPerCountyMock implements CasesPerCounty {

    private Random random = new Random();
    public static final List<String> GROUPS = Arrays.asList("Blekinge län", "Dalarnas län", "Gotlands län", "Gävleborgs län", "Hallands län", "Jämtlands län",
            "Jönköpings län", "Kalmar län", "Kronobergs län", "Norrbottens län", "Skåne län", "Stockholms län", "Södermanlands län", "Uppsala län",
            "Värmlands län", "Västerbottens län", "Västernorrlands län", "Västmanlands län", "Västra Götalands län", "Örebro län", "Östergötlands län");

    // CHECKSTYLE:OFF MagicNumber
    @Override
    public CasesPerCountyResponse getStatistics(String hsaId, Range range) {
        final List<CasesPerCountyRow> rows = new ArrayList<>();
        for (String group : GROUPS) {
            int women = (int) (random.nextGaussian() * 2000 + 10000);
            int men = (int) (random.nextGaussian() * 2000 + 10000);
            rows.add(new CasesPerCountyRow(group, women, men));
        }
        final Period period = new Period(range.getFrom(), range.getTo());
        final int monthsInPeriod = period.getMonths() + period.getYears() * 12;
        return new CasesPerCountyResponse(rows, monthsInPeriod);
    }

    // CHECKSTYLE:ON

}
