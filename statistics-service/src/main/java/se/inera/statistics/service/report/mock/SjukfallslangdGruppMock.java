package se.inera.statistics.service.report.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.joda.time.LocalDate;

import se.inera.statistics.service.report.api.SjukfallslangdGrupp;
import se.inera.statistics.service.report.model.Sex;
import se.inera.statistics.service.report.model.SickLeaveLengthResponse;
import se.inera.statistics.service.report.model.SickLeaveLengthRow;
import se.inera.statistics.service.report.util.Verksamhet;

public class SjukfallslangdGruppMock implements SjukfallslangdGrupp {

    private Random random = new Random();
    public static final List<String> GROUPS = Arrays.asList("< 15 dagar", "15-30 dagar", "31-90 dagar", "91-180 dagar", "181-360 dagar", "> 360 dagar");

    // CHECKSTYLE:OFF MagicNumber
    @Override
    public SickLeaveLengthResponse getStatistics(String hsaId, LocalDate when, int periods) {
        final List<SickLeaveLengthRow> rows = new ArrayList<>();
        for (String group : GROUPS) {
            int women = (int) (random.nextGaussian() * 2000 + 10000);
            int men = (int) (random.nextGaussian() * 2000 + 10000);
            rows.add(new SickLeaveLengthRow(group, women, men));
        }
        return new SickLeaveLengthResponse(rows, periods);
    }

    @Override
    public void count(String period, String hsaId, String group, int periods, Verksamhet typ, Sex sex) {
    }
    // CHECKSTYLE:ON
}
