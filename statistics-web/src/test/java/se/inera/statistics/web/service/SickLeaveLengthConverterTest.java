package se.inera.statistics.web.service;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import se.inera.statistics.service.report.model.SickLeaveLengthResponse;
import se.inera.statistics.service.report.model.SickLeaveLengthRow;
import se.inera.statistics.web.model.ChartData;
import se.inera.statistics.web.model.ChartSeries;
import se.inera.statistics.web.model.NamedData;
import se.inera.statistics.web.model.SickLeaveLengthData;
import se.inera.statistics.web.model.TableData;

public class SickLeaveLengthConverterTest {

    // CHECKSTYLE:OFF MagicNumber

    @Test
    public void convertCasesPerMonthDataTest() {
        //Given
        SickLeaveLengthConverter converter = new SickLeaveLengthConverter();
        ArrayList<SickLeaveLengthRow> sickLeaveLengthRows = new ArrayList<>();
        sickLeaveLengthRows.add(new SickLeaveLengthRow(null, "< 20 dagar", 3, 13, 14));
        sickLeaveLengthRows.add(new SickLeaveLengthRow(null, "20-50 dagar", 3, 24, 15));
        sickLeaveLengthRows.add(new SickLeaveLengthRow(null, "> 50 dagar", 3, 3, 9));
        SickLeaveLengthResponse sickLeaveLengthResponse = new SickLeaveLengthResponse(sickLeaveLengthRows, 7);

        //When
        SickLeaveLengthData result = converter.convert(sickLeaveLengthResponse);

        //Then
        TableData tableDataResult = result.getTableData();
        assertEquals("[[Sjukskrivningslängd;1, Antal sjukfall;1, Antal sjukfall Kvinnor;1, Antal sjukfall Män;1, Summering;1]]", tableDataResult.getHeaders().toString());
        List<NamedData> rows = tableDataResult.getRows();
        assertEquals(3, rows.size());
        assertEquals("< 20 dagar", rows.get(0).getName());
        assertEquals("20-50 dagar", rows.get(1).getName());
        assertEquals("> 50 dagar", rows.get(2).getName());
        assertEquals("[27, 13, 14, 27]", rows.get(0).getData().toString());
        assertEquals("[39, 24, 15, 66]", rows.get(1).getData().toString());
        assertEquals("[12, 3, 9, 78]", rows.get(2).getData().toString());

        ChartData chartDataResult = result.getChartData();
        assertEquals("[< 20 dagar, 20-50 dagar, > 50 dagar]", chartDataResult.getCategories().toString());
        List<ChartSeries> series = chartDataResult.getSeries();
        assertEquals(2, series.size());
        assertEquals("Antal sjukfall Män", series.get(0).getName());
        assertEquals("Antal sjukfall Kvinnor", series.get(1).getName());
        assertEquals("[14, 15, 9]", series.get(0).getData().toString());
        assertEquals("[13, 24, 3]", series.get(1).getData().toString());

        assertEquals(7, result.getMonthsIncluded());
    }

    // CHECKSTYLE:ON MagicNumber
}
