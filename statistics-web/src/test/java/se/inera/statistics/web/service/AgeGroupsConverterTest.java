package se.inera.statistics.web.service;

import org.junit.Test;
import se.inera.statistics.service.report.model.AgeGroupsResponse;
import se.inera.statistics.service.report.model.AgeGroupsRow;
import se.inera.statistics.service.report.model.CasesPerMonthRow;
import se.inera.statistics.web.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class AgeGroupsConverterTest {

    @Test
    public void convertCasesPerMonthDataTest(){
        //Given
        AgeGroupsConverter converter = new AgeGroupsConverter();
        ArrayList<AgeGroupsRow> ageGroupsRows = new ArrayList<>();
        ageGroupsRows.add(new AgeGroupsRow("<20", 13, 14));
        ageGroupsRows.add(new AgeGroupsRow("20-50", 24, 15));
        ageGroupsRows.add(new AgeGroupsRow(">50", 3, 9));
        AgeGroupsResponse ageGroupsResponse = new AgeGroupsResponse(ageGroupsRows, 7);

        //When
        AgeGroupsData result = converter.convert(ageGroupsResponse);

        //Then
        TableData tableDataResult = result.getTableData();
        assertEquals("[[Åldersgrupper;1, Antal sjukfall;1, Antal kvinnor;1, Antal män;1, Summering;1]]", tableDataResult.getHeaders().toString());
        List<NamedData> rows = tableDataResult.getRows();
        assertEquals(3, rows.size());
        assertEquals("<20", rows.get(0).getName());
        assertEquals("20-50", rows.get(1).getName());
        assertEquals(">50", rows.get(2).getName());
        assertEquals("[27, 13, 14, 27]", rows.get(0).getData().toString());
        assertEquals("[39, 24, 15, 66]", rows.get(1).getData().toString());
        assertEquals("[12, 3, 9, 78]", rows.get(2).getData().toString());

        ChartData chartDataResult = result.getChartData();
        assertEquals("[<20, 20-50, >50]", chartDataResult.getCategories().toString());
        List<ChartSeries> series = chartDataResult.getSeries();
        assertEquals(2, series.size());
        assertEquals("Antal sjukskrivningar Män", series.get(0).getName());
        assertEquals("Antal sjukskrivningar Kvinnor", series.get(1).getName());
        assertEquals("[14, 15, 9]", series.get(0).getData().toString());
        assertEquals("[13, 24, 3]", series.get(1).getData().toString());

        assertEquals(7, result.getMonthsIncluded());
    }

}
