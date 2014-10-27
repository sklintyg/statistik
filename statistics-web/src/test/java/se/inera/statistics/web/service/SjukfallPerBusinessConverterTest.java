package se.inera.statistics.web.service;

import org.junit.Test;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.web.model.NamedData;
import se.inera.statistics.web.model.SimpleDetailsData;
import se.inera.statistics.web.model.TableData;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SjukfallPerBusinessConverterTest {

    // CHECKSTYLE:OFF MagicNumber

    @Test
    public void convertTest() {
        SjukfallPerBusinessConverter converter = new SjukfallPerBusinessConverter();
        List<SimpleKonDataRow> businessRows = new ArrayList<>();
        businessRows.add(new SimpleKonDataRow("enhet1", 12, 13));
        businessRows.add(new SimpleKonDataRow("enhet2", 20, 30));
        businessRows.add(new SimpleKonDataRow("enhet3", 5, 25));
        SimpleKonResponse<SimpleKonDataRow> casesPerUnit = new SimpleKonResponse<>(businessRows, 2);
        SimpleDetailsData result = converter.convert(casesPerUnit, new Range(1));
        TableData tableData = result.getTableData();
        assertEquals("[[Vårdenhet;1, Antal sjukfall totalt;1, Antal sjukfall för kvinnor;1, Antal sjukfall för män;1]]", tableData.getHeaders().toString());
        List<NamedData> rows = tableData.getRows();
        assertEquals(3, rows.size());
        assertEquals("enhet1", rows.get(0).getName());
        assertEquals("enhet2", rows.get(1).getName());
        assertEquals("enhet3", rows.get(2).getName());
        assertEquals("[25, 12, 13]", rows.get(0).getData().toString());
        assertEquals("[50, 20, 30]", rows.get(1).getData().toString());
        assertEquals("[30, 5, 25]", rows.get(2).getData().toString());
    }

    // CHECKSTYLE:ON MagicNumber

}
