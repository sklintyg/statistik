package se.inera.statistics.service.report.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.joda.time.LocalDate;

import se.inera.statistics.service.report.api.CasesPerMonth;
import se.inera.statistics.service.report.model.CasesPerMonthRow;
import se.inera.statistics.service.report.model.Sex;
import se.inera.statistics.service.report.util.ReportUtil;

public class CasesPerMonthMock implements CasesPerMonth {

    private Random random = new Random();

    // CHECKSTYLE:OFF MagicNumber
    @Override
    public List<CasesPerMonthRow> getCasesPerMonth(LocalDate from, LocalDate to) {
        List<CasesPerMonthRow> rows = new ArrayList<>();
        for (String periodName : ReportUtil.PERIODS) {
            int men = (int) (random.nextGaussian() * 2000 + 10000);
            int women = (int) (random.nextGaussian() * 2000 + 10000);
            rows.add(new CasesPerMonthRow(periodName, men, women));
        }
        return rows;
    }

    @Override
    public void count(String period, Sex sex) {
    }
    // CHECKSTYLE:ON

}
