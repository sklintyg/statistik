package se.inera.statistics.web.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import se.inera.statistics.service.report.model.CasesPerMonthRow;
import se.inera.statistics.web.model.NamedData;
import se.inera.statistics.web.model.TableData;

public class CasesPerMonthConverter {

    TableData convertCasesPerMonthData(List<CasesPerMonthRow> casesPerMonth) {
        List<NamedData> data = new ArrayList<>();
        for (CasesPerMonthRow row : casesPerMonth) {
            data.add(new NamedData(row.getPeriod(), Arrays.asList(new Integer[] {row.getFemale(), row.getMale(), row.getFemale() + row.getMale()})));
        }
        return new TableData(data, Arrays.asList(new String[] {"Period", "Antal kvinnor", "Antal män", "Summering"}));
    }

}
