package se.inera.statistics.web.service;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import se.inera.statistics.service.report.model.SimpleDualSexDataRow;
import se.inera.statistics.service.report.model.SimpleDualSexResponse;
import se.inera.statistics.web.model.NamedData;
import se.inera.statistics.web.model.SimpleDetailsData;
import se.inera.statistics.web.model.TableData;

public class SimpleDualSexConverterTest {

    // CHECKSTYLE:OFF MagicNumber

    @Test
    public void convertTest() {
        SimpleDualSexConverter converter = new SimpleDualSexConverter();
        List<SimpleDualSexDataRow> dualSexRows = new ArrayList<>();
        dualSexRows.add(new SimpleDualSexDataRow("jan 12", 12, 13));
        dualSexRows.add(new SimpleDualSexDataRow("feb 12", 20, 30));
        dualSexRows.add(new SimpleDualSexDataRow("mar 12", 5, 25));
        SimpleDualSexResponse<SimpleDualSexDataRow> casesPerMonth = new SimpleDualSexResponse<SimpleDualSexDataRow>(dualSexRows, 2);
        SimpleDetailsData result = converter.convert(casesPerMonth);
        TableData tableData = result.getTableData();
        assertEquals("[[Period;1, Antal sjukfall;1, Antal kvinnor;1, Antal män;1, Summering;1]]", tableData.getHeaders().toString());
        List<NamedData> rows = tableData.getRows();
        assertEquals(3, rows.size());
        assertEquals("jan 12", rows.get(0).getName());
        assertEquals("feb 12", rows.get(1).getName());
        assertEquals("mar 12", rows.get(2).getName());
        assertEquals("[25, 12, 13, 25]", rows.get(0).getData().toString());
        assertEquals("[50, 20, 30, 75]", rows.get(1).getData().toString());
        assertEquals("[30, 5, 25, 105]", rows.get(2).getData().toString());
    }

    // CHECKSTYLE:ON MagicNumber

}
