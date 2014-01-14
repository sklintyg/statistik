package se.inera.statistics.service.report.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import se.inera.statistics.service.report.api.SjukfallPerManad;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.Sex;
import se.inera.statistics.service.report.model.SimpleDualSexDataRow;
import se.inera.statistics.service.report.model.SimpleDualSexResponse;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.report.util.Verksamhet;

public class SjukfallPerManadMock implements SjukfallPerManad {

    private Random random = new Random();

    // CHECKSTYLE:OFF MagicNumber
    @Override
    public SimpleDualSexResponse<SimpleDualSexDataRow> getCasesPerMonth(String hsaId, Range range) {
        List<SimpleDualSexDataRow> rows = new ArrayList<>();
        for (String periodName : ReportUtil.PERIODS) {
            int men = (int) (random.nextGaussian() * 2000 + 10000);
            int women = (int) (random.nextGaussian() * 2000 + 10000);
            rows.add(new SimpleDualSexDataRow(periodName, women, men));
        }
        return new SimpleDualSexResponse<SimpleDualSexDataRow>(rows, 18);
    }

    @Override
    public void count(String hsaId, String period, Verksamhet typ, Sex sex) {
    }
    // CHECKSTYLE:ON

}
