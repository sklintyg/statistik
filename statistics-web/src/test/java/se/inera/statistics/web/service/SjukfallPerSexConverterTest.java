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

public class SjukfallPerSexConverterTest {

    // CHECKSTYLE:OFF MagicNumber

    @Test
    public void convertTest() {
        SjukfallPerSexConverter converter = new SjukfallPerSexConverter();
        List<SimpleDualSexDataRow> dualSexRows = new ArrayList<>();
        dualSexRows.add(new SimpleDualSexDataRow("län 1", 12, 13));
        dualSexRows.add(new SimpleDualSexDataRow("län 2", 20, 30));
        dualSexRows.add(new SimpleDualSexDataRow("län 3", 5, 25));
        SimpleDualSexResponse<SimpleDualSexDataRow> casesPerMonth = new SimpleDualSexResponse<SimpleDualSexDataRow>(dualSexRows, 2);
        SimpleDetailsData result = converter.convert(casesPerMonth);
        TableData tableData = result.getTableData();
        assertEquals("[[Län;1, Antal sjukfall;1, Andel kvinnor;1, Andel män;1, Summering;1]]", tableData.getHeaders().toString());
        List<NamedData> rows = tableData.getRows();
        assertEquals(3, rows.size());
        assertEquals("län 1", rows.get(0).getName());
        assertEquals("län 2", rows.get(1).getName());
        assertEquals("län 3", rows.get(2).getName());
        assertEquals("[25, 48% (12), 52% (13), 25]", rows.get(0).getData().toString());
        assertEquals("[50, 40% (20), 60% (30), 75]", rows.get(1).getData().toString());
        assertEquals("[30, 17% (5), 83% (25), 105]", rows.get(2).getData().toString());
    }

    // CHECKSTYLE:ON MagicNumber

}
