package se.inera.statistics.web.service;

import org.junit.Test;
import static org.junit.Assert.*;
import se.inera.statistics.service.report.model.CasesPerMonthRow;
import se.inera.statistics.web.model.NamedData;
import se.inera.statistics.web.model.TableData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CasesPerMonthConverterTest {

    @Test
    public void convertCasesPerMonthDataTest(){
        CasesPerMonthConverter converter = new CasesPerMonthConverter();
        ArrayList<CasesPerMonthRow> casesPerMonth = new ArrayList<CasesPerMonthRow>();
        casesPerMonth.add(new CasesPerMonthRow("jan 12", 12, 13));
        casesPerMonth.add(new CasesPerMonthRow("feb 12", 20, 30));
        casesPerMonth.add(new CasesPerMonthRow("mar 12", 5, 25));
        TableData tableData = converter.convertCasesPerMonthData(casesPerMonth);
        assertEquals("[[Antal sjukfall;1, Antal kvinnor;1, Antal män;1, Summering;1]]", tableData.getHeaders().toString());
        List<NamedData> rows = tableData.getRows();
        assertEquals(3, rows.size());
        assertEquals("jan 12", rows.get(0).getName());
        assertEquals("feb 12", rows.get(1).getName());
        assertEquals("mar 12", rows.get(2).getName());
        assertEquals("[25, 12, 13, 25]", rows.get(0).getData().toString());
        assertEquals("[50, 20, 30, 75]", rows.get(1).getData().toString());
        assertEquals("[30, 5, 25, 105]", rows.get(2).getData().toString());
    }

}
