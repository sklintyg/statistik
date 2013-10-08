package se.inera.statistics.web.service;

import org.junit.Test;
import se.inera.statistics.service.report.model.*;
import se.inera.statistics.web.model.DualSexStatisticsData;
import se.inera.statistics.web.model.TableData;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DiagnosisSubGroupsConverterTest {

    @Test
    public void converterTestEmpty(){
        DiagnosisGroupResponse resp = new DiagnosisGroupResponse(new ArrayList<DiagnosisGroup>(), new ArrayList<DiagnosisGroupRow>());
        DualSexStatisticsData data = new DiagnosisSubGroupsConverter().convert(resp);
        assertEquals("[]", data.getFemaleChart().getCategories().toString());
        assertEquals("[]", data.getFemaleChart().getSeries().toString());
    }

    @Test
    public void converterTest(){
        //Given
        ArrayList<DiagnosisGroup> diagnosisGroups = new ArrayList<DiagnosisGroup>();
        diagnosisGroups.add(new DiagnosisGroup("A00-B99", "name1"));
        ArrayList<DiagnosisGroupRow> rows = new ArrayList<DiagnosisGroupRow>();
        ArrayList<DualSexField> diagnosisGroupData = new ArrayList<DualSexField>();
        diagnosisGroupData.add(new DualSexField(3, 2));
        rows.add(new DiagnosisGroupRow("period1", diagnosisGroupData));
        DiagnosisGroupResponse resp = new DiagnosisGroupResponse(diagnosisGroups, rows);

        //When
        DiagnosisSubGroupsConverter converter = new DiagnosisSubGroupsConverter();
        DualSexStatisticsData data = converter.convert(resp);

        //Then
        assertEquals("[period1]", data.getFemaleChart().getCategories().toString());
        assertEquals("[A00-B99 name1: [3]]", data.getFemaleChart().getSeries().toString());

        assertEquals("[period1]", data.getMaleChart().getCategories().toString());
        assertEquals("[A00-B99 name1: [2]]", data.getMaleChart().getSeries().toString());

        assertEquals("[[;1, A00-B99 name1;2], [Period;1, Kvinnor;1, Män;1, Summering;1]]", data.getTableData().getHeaders().toString());
        assertEquals("[period1: [3, 2, 5]]", data.getTableData().getRows().toString());
    }

}
