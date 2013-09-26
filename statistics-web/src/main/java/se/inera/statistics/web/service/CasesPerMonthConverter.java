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
        int accumulatedSum = 0;
        for (CasesPerMonthRow row : casesPerMonth) {
            int rowSum = row.getFemale() + row.getMale();
            accumulatedSum += rowSum;
            data.add(new NamedData(row.getPeriod(), Arrays.asList(new Integer[] {rowSum, row.getFemale(), row.getMale(), accumulatedSum})));
        }
        return new TableData(data, Arrays.asList(new String[] {"Antal sjukfall", "Antal kvinnor", "Antal män", "Summering"}));
    }

}
