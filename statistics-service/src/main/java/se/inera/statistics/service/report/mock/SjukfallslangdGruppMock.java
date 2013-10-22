package se.inera.statistics.service.report.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.joda.time.LocalDate;

import se.inera.statistics.service.report.api.SjukfallslangdGrupp;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.Sex;
import se.inera.statistics.service.report.model.SickLeaveLengthResponse;
import se.inera.statistics.service.report.model.SickLeaveLengthRow;
import se.inera.statistics.service.report.model.SimpleDualSexDataRow;
import se.inera.statistics.service.report.model.SimpleDualSexResponse;
import se.inera.statistics.service.report.repository.RollingLength;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.report.util.SjukfallslangdUtil;
import se.inera.statistics.service.report.util.SjukfallslangdUtil.Group;
import se.inera.statistics.service.report.util.Verksamhet;

public class SjukfallslangdGruppMock implements SjukfallslangdGrupp {

    private Random random = new Random();

    // CHECKSTYLE:OFF MagicNumber
    @Override
    public SickLeaveLengthResponse getHistoricalStatistics(String hsaId, LocalDate when, RollingLength length) {
        final List<SickLeaveLengthRow> rows = new ArrayList<>();
        for (Group group : SjukfallslangdUtil.GROUPS) {
            int women = (int) (random.nextGaussian() * 2000 + 10000);
            int men = (int) (random.nextGaussian() * 2000 + 10000);
            rows.add(new SickLeaveLengthRow(null, group.getGroupName(), length.getPeriods(), women, men));
        }
        return new SickLeaveLengthResponse(rows, length.getPeriods());
    }

    @Override
    public SickLeaveLengthResponse getCurrentStatistics(String hsaId) {
        return getHistoricalStatistics(hsaId, null, RollingLength.SINGLE_MONTH);
    }

    @Override
    public SimpleDualSexResponse<SimpleDualSexDataRow> getLongSickLeaves(String decodeId, Range range) {
        List<SimpleDualSexDataRow> rows = new ArrayList<>();
        for (String periodName : ReportUtil.PERIODS) {
            int men = (int) (random.nextGaussian() * 2000 + 10000);
            int women = (int) (random.nextGaussian() * 2000 + 10000);
            rows.add(new SimpleDualSexDataRow(periodName, women, men));
        }
        return new SimpleDualSexResponse<SimpleDualSexDataRow>(rows, 18);
    }

    @Override
    public void count(String period, String hsaId, String group, RollingLength length, Verksamhet typ, Sex sex) {
    }
    // CHECKSTYLE:ON

}
