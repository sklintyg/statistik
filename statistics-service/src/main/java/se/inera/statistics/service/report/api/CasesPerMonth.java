package se.inera.statistics.service.report.api;

import java.util.List;

import se.inera.statistics.service.report.model.CasesPerMonthRow;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.Sex;

public interface CasesPerMonth {
    String HSA_NATIONELL = "nationell";

    List<CasesPerMonthRow> getCasesPerMonth(String hsaId, Range range);

    void count(String hsaId, String period, Sex sex);
}
