package se.inera.statistics.web.service;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Test;

import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleDualSexDataRow;
import se.inera.statistics.service.report.model.SimpleDualSexResponse;
import se.inera.statistics.web.model.CasesPerCountyData;
import se.inera.statistics.web.model.ChartData;
import se.inera.statistics.web.model.ChartSeries;
import se.inera.statistics.web.model.NamedData;
import se.inera.statistics.web.model.TableData;

public class CasesPerCountyConverterTest {

    // CHECKSTYLE:OFF MagicNumber

    @Test
    public void convertCasesPerCountyDataTest() {
        //Given
        ArrayList<SimpleDualSexDataRow> perCountyRows1 = new ArrayList<>();
        perCountyRows1.add(new SimpleDualSexDataRow("<20", 13, 14));
        perCountyRows1.add(new SimpleDualSexDataRow("20-50", 24, 15));
        perCountyRows1.add(new SimpleDualSexDataRow(">50", 3, 9));
        SimpleDualSexResponse<SimpleDualSexDataRow> ageGroupsResponseNew = new SimpleDualSexResponse<>(perCountyRows1, 1);

        ArrayList<SimpleDualSexDataRow> perCountyRowsOld = new ArrayList<>();
        perCountyRowsOld.add(new SimpleDualSexDataRow("<20", 3, 4));
        perCountyRowsOld.add(new SimpleDualSexDataRow("20-50", 4, 5));
        perCountyRowsOld.add(new SimpleDualSexDataRow(">50", 2, 8));
        SimpleDualSexResponse<SimpleDualSexDataRow> ageGroupsResponseOld = new SimpleDualSexResponse<>(perCountyRowsOld, 2);

        LocalDate fromOld = new LocalDate(2013, 2, 1);
        LocalDate toOld = new LocalDate(2013, 4, 1);
        LocalDate fromNew = new LocalDate(2013, 5, 1);
        LocalDate toNew = new LocalDate(2013, 7, 1);

        CasesPerCountyConverter converter = new CasesPerCountyConverter(ageGroupsResponseNew, ageGroupsResponseOld, new Range(fromNew, toNew), new Range(fromOld, toOld));

        //When
        CasesPerCountyData result = converter.convert();

        //Then
        TableData tableDataResult = result.getTableData();
        assertEquals("[[;1, feb-apr 2013;4, maj-jul 2013;4, ;1], [Län;1, Antal sjukfall;1, Antal kvinnor;1, Antal män;1, Summering;1, Antal sjukfall;1, Antal kvinnor;1, Antal män;1, Summering;1]]", tableDataResult.getHeaders().toString());
        List<NamedData> rows = tableDataResult.getRows();
        assertEquals(4, rows.size());
        assertEquals("<20", rows.get(0).getName());
        assertEquals("20-50", rows.get(1).getName());
        assertEquals(">50", rows.get(2).getName());
        assertEquals("Totalt", rows.get(3).getName());
        assertEquals("[7, 3, 4, 7, 27, 13, 14, 27]", rows.get(0).getData().toString());
        assertEquals("[9, 4, 5, 16, 39, 24, 15, 66]", rows.get(1).getData().toString());
        assertEquals("[10, 2, 8, 26, 12, 3, 9, 78]", rows.get(2).getData().toString());
        assertEquals("[26, 9, 17, , 78, 40, 38]", rows.get(3).getData().toString());

        ChartData chartDataResult = result.getChartData();
        assertEquals("[<20, 20-50, >50]", chartDataResult.getCategories().toString());
        List<ChartSeries> series = chartDataResult.getSeries();
        assertEquals(4, series.size());
        assertEquals("Sjukfall feb-apr 2013 kvinnor", series.get(0).getName());
        assertEquals("Sjukfall feb-apr 2013 män", series.get(1).getName());
        assertEquals("Sjukfall maj-jul 2013 kvinnor", series.get(2).getName());
        assertEquals("Sjukfall maj-jul 2013 män", series.get(3).getName());
        assertEquals("[3, 4, 2]", series.get(0).getData().toString());
        assertEquals("[4, 5, 8]", series.get(1).getData().toString());
        assertEquals("[13, 24, 3]", series.get(2).getData().toString());
        assertEquals("[14, 15, 9]", series.get(3).getData().toString());

        assertEquals(6, result.getMonthsIncluded());
    }

    // CHECKSTYLE:ON MagicNumber
}
