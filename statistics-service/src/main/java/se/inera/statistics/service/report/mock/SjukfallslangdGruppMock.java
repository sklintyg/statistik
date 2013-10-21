package se.inera.statistics.service.report.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.joda.time.LocalDate;

import se.inera.statistics.service.report.api.SjukfallslangdGrupp;
import se.inera.statistics.service.report.model.Sex;
import se.inera.statistics.service.report.model.SickLeaveLengthResponse;
import se.inera.statistics.service.report.model.SickLeaveLengthRow;
import se.inera.statistics.service.report.util.SjukfallslangdUtil;
import se.inera.statistics.service.report.util.SjukfallslangdUtil.Group;
import se.inera.statistics.service.report.util.Verksamhet;

public class SjukfallslangdGruppMock implements SjukfallslangdGrupp {

    private Random random = new Random();

    // CHECKSTYLE:OFF MagicNumber
    @Override
    public SickLeaveLengthResponse getStatistics(String hsaId, LocalDate when, int periods) {
        final List<SickLeaveLengthRow> rows = new ArrayList<>();
        for (Group group : SjukfallslangdUtil.GROUPS) {
            int women = (int) (random.nextGaussian() * 2000 + 10000);
            int men = (int) (random.nextGaussian() * 2000 + 10000);
            rows.add(new SickLeaveLengthRow(null, group.getGroupName(), periods, women, men));
        }
        return new SickLeaveLengthResponse(rows, periods);
    }

    @Override
    public void count(String period, String hsaId, String group, int periods, Verksamhet typ, Sex sex) {
    }
    // CHECKSTYLE:ON
}
