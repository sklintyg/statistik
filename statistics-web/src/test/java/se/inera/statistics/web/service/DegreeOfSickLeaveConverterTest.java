package se.inera.statistics.web.service;

import org.junit.Test;

import static org.junit.Assert.*;
import se.inera.statistics.service.report.model.*;
import se.inera.statistics.web.model.DualSexStatisticsData;
import se.inera.statistics.web.model.TableData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DegreeOfSickLeaveConverterTest {

    @Test
    public void tableConverterTestEmptyInput(){
//        DiagnosisGroupResponse resp = new DiagnosisGroupResponse(new ArrayList<DiagnosisGroup>(), new ArrayList<DiagnosisGroupRow>());
        final DegreeOfSickLeaveResponse resp = new DegreeOfSickLeaveResponse(new ArrayList<String>(), new ArrayList<DegreeOFSickLeaveRow>());
        TableData tableData = DegreeOfSickLeaveConverter.convertTable(resp);
        assertEquals("[[;1], [Period;1, Summering;1]]", tableData.getHeaders().toString());
        assertEquals("[]", tableData.getRows().toString());
    }

    @Test
    public void tableConverterTest(){
        //Given
        ArrayList<DegreeOFSickLeaveRow> rows = new ArrayList<>();
        ArrayList<DualSexField> diagnosisGroupData = new ArrayList<DualSexField>();
        diagnosisGroupData.add(new DualSexField(3, 2));
        rows.add(new DegreeOFSickLeaveRow("period1", diagnosisGroupData));
        final List<String> degreesOfSickLeave = Arrays.asList("50%");
        final DegreeOfSickLeaveResponse resp = new DegreeOfSickLeaveResponse(degreesOfSickLeave, rows);

        //When
        TableData tableData = DegreeOfSickLeaveConverter.convertTable(resp);

        //Then
        assertEquals("[[;1, 50%;2], [Period;1, Kvinnor;1, Män;1, Summering;1]]", tableData.getHeaders().toString());
        assertEquals("[period1: [3, 2, 5]]", tableData.getRows().toString());
    }

    @Test
    public void converterTestEmpty(){
        DegreeOfSickLeaveResponse resp = new DegreeOfSickLeaveResponse(new ArrayList<String>(), new ArrayList<DegreeOFSickLeaveRow>());
        DualSexStatisticsData data = new DegreeOfSickLeaveConverter().convert(resp);
        assertEquals("[]", data.getFemaleChart().getCategories().toString());
        assertEquals("[]", data.getFemaleChart().getSeries().toString());
    }

    @Test
    public void converterTest(){
        //Given
        ArrayList<DegreeOFSickLeaveRow> rows = new ArrayList<>();
        ArrayList<DualSexField> diagnosisGroupData = new ArrayList<DualSexField>();
        diagnosisGroupData.add(new DualSexField(3, 2));
        rows.add(new DegreeOFSickLeaveRow("period1", diagnosisGroupData));
        final List<String> degreesOfSickLeave = Arrays.asList("50%");
        final DegreeOfSickLeaveResponse resp = new DegreeOfSickLeaveResponse(degreesOfSickLeave, rows);

        //When
        DegreeOfSickLeaveConverter converter = new DegreeOfSickLeaveConverter();
        DualSexStatisticsData data = converter.convert(resp);

        //Then
        assertEquals("[period1]", data.getFemaleChart().getCategories().toString());
        assertTrue(data.getFemaleChart().getSeries().toString(), data.getFemaleChart().getSeries().toString().contains("50%: [3]"));

        assertEquals("[period1]", data.getMaleChart().getCategories().toString());
        assertTrue(data.getMaleChart().getSeries().toString(), data.getMaleChart().getSeries().toString().contains("50%: [2]"));

        assertEquals("[[;1, 50%;2], [Period;1, Kvinnor;1, Män;1, Summering;1]]", data.getTableData().getHeaders().toString());
        assertEquals("[period1: [3, 2, 5]]", data.getTableData().getRows().toString());
    }

}
