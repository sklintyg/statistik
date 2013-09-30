package se.inera.statistics.service.report.api;

import java.util.List;

import se.inera.statistics.service.report.model.CasesPerMonthRow;
import se.inera.statistics.service.report.model.Sex;

public interface CasesPerMonth {

    List<CasesPerMonthRow> getCasesPerMonth();

    void count(String period, Sex sex);
}
