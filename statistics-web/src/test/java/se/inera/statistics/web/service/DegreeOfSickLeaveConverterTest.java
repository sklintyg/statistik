package se.inera.statistics.web.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import se.inera.statistics.service.report.model.DegreeOfSickLeaveResponse;
import se.inera.statistics.service.report.model.DualSexDataRow;
import se.inera.statistics.service.report.model.DualSexField;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.web.model.DualSexStatisticsData;
import se.inera.statistics.web.model.TableData;

public class DegreeOfSickLeaveConverterTest {

    @Test
    public void tableConverterTestEmptyInput() {
//        DiagnosisGroupResponse resp = new DiagnosisGroupResponse(new ArrayList<DiagnosisGroup>(), new ArrayList<DiagnosisGroupRow>());
        final DegreeOfSickLeaveResponse resp = new DegreeOfSickLeaveResponse(new ArrayList<String>(), new ArrayList<DualSexDataRow>());
        TableData tableData = DegreeOfSickLeaveConverter.convertTable(resp);
        assertEquals("[[;1], [Period;1, Summering;1]]", tableData.getHeaders().toString());
        assertEquals("[Totalt: []]", tableData.getRows().toString());
    }

    @Test
    public void tableConverterTest() {
        //Given
        ArrayList<DualSexDataRow> rows = new ArrayList<>();
        ArrayList<DualSexField> diagnosisGroupData = new ArrayList<DualSexField>();
        // CHECKSTYLE:OFF MagicNumber
        diagnosisGroupData.add(new DualSexField(3, 2));
        // CHECKSTYLE:ON MagicNumber
        rows.add(new DualSexDataRow("period1", diagnosisGroupData));
        final List<String> degreesOfSickLeave = Arrays.asList("50%");
        final DegreeOfSickLeaveResponse resp = new DegreeOfSickLeaveResponse(degreesOfSickLeave, rows);

        //When
        TableData tableData = DegreeOfSickLeaveConverter.convertTable(resp);

        //Then
        assertEquals("[[;1, 50%;2], [Period;1, Kvinnor;1, Män;1, Summering;1]]", tableData.getHeaders().toString());
        assertEquals("[period1: [3, 2, 5], Totalt: [3, 2, 5]]", tableData.getRows().toString());
    }

    @Test
    public void converterTestEmpty() {
        DegreeOfSickLeaveResponse resp = new DegreeOfSickLeaveResponse(new ArrayList<String>(), new ArrayList<DualSexDataRow>());
        DualSexStatisticsData data = new DegreeOfSickLeaveConverter().convert(resp, new Range());
        assertEquals("[]", data.getFemaleChart().getCategories().toString());
        assertEquals("[]", data.getFemaleChart().getSeries().toString());
    }

    @Test
    public void converterTest() {
        //Given
        ArrayList<DualSexDataRow> rows = new ArrayList<>();
        ArrayList<DualSexField> diagnosisGroupData = new ArrayList<DualSexField>();
        // CHECKSTYLE:OFF MagicNumber
        diagnosisGroupData.add(new DualSexField(3, 2));
        // CHECKSTYLE:ON MagicNumber
        rows.add(new DualSexDataRow("period1", diagnosisGroupData));
        final List<String> degreesOfSickLeave = Arrays.asList("50%");
        final DegreeOfSickLeaveResponse resp = new DegreeOfSickLeaveResponse(degreesOfSickLeave, rows);

        //When
        DegreeOfSickLeaveConverter converter = new DegreeOfSickLeaveConverter();
        DualSexStatisticsData data = converter.convert(resp, new Range());

        //Then
        assertEquals("[period1]", data.getFemaleChart().getCategories().toString());
        assertTrue(data.getFemaleChart().getSeries().toString(), data.getFemaleChart().getSeries().toString().contains("50%: [3]"));

        assertEquals("[period1]", data.getMaleChart().getCategories().toString());
        assertTrue(data.getMaleChart().getSeries().toString(), data.getMaleChart().getSeries().toString().contains("50%: [2]"));

        assertEquals("[[;1, 50%;2], [Period;1, Kvinnor;1, Män;1, Summering;1]]", data.getTableData().getHeaders().toString());
        assertEquals("[period1: [3, 2, 5], Totalt: [3, 2, 5]]", data.getTableData().getRows().toString());
    }

}
