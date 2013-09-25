package se.inera.statistics.service.report.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import se.inera.statistics.service.report.api.CasesPerMonth;
import se.inera.statistics.service.report.model.CasesPerMonthRow;

public class CasesPerMonthMock implements CasesPerMonth {

    private Random random = new Random();

    @Override
    public List<CasesPerMonthRow> getCasesPerMonth() {
        List<CasesPerMonthRow> rows = new ArrayList<>();
        for (String periodName : ReportMockUtil.PERIODS) {
            int men = (int) (random.nextGaussian() * 2000 + 10000);
            int women = (int) (random.nextGaussian() * 2000 + 10000);
            rows.add(new CasesPerMonthRow(periodName, men, women));
        }
        return rows;
    }

}
