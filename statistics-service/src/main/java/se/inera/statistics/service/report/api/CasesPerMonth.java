package se.inera.statistics.service.report.api;

import java.util.List;

import se.inera.statistics.service.report.model.CasesPerMonthRow;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.Sex;

public interface CasesPerMonth {

    List<CasesPerMonthRow> getCasesPerMonth(Range range);

    void count(String period, Sex sex);
}
