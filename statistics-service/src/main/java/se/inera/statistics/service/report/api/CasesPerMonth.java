package se.inera.statistics.service.report.api;

import java.util.List;

import se.inera.statistics.service.report.model.CasesPerMonthRow;

public interface CasesPerMonth {

    List<CasesPerMonthRow> getCasesPerMonth();

}
