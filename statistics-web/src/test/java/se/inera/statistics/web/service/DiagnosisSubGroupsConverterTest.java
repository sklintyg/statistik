package se.inera.statistics.web.service;

import org.junit.Test;
import se.inera.statistics.service.report.model.*;
import se.inera.statistics.web.model.DiagnosisGroupsData;
import se.inera.statistics.web.model.TableData;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DiagnosisSubGroupsConverterTest {

    @Test
    public void converterTestEmpty(){
        DiagnosisGroupResponse resp = new DiagnosisGroupResponse(new ArrayList<DiagnosisGroup>(), new ArrayList<DiagnosisGroupRow>());
        DiagnosisGroupsData data = new DiagnosisSubGroupsConverter().convert(resp);
        assertEquals("[]", data.getFemaleChart().getHeaders().toString());
        assertEquals("[]", data.getFemaleChart().getRows().toString());
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
        DiagnosisGroupsData data = converter.convert(resp);

        //Then
        assertEquals("[period1]", data.getFemaleChart().getHeaders().toString());
        assertEquals("[A00-B99 name1: [3]]", data.getFemaleChart().getRows().toString());
        assertEquals("[A00-B99 name1]", data.getFemaleTable().getHeaders().toString());
        assertEquals("[period1: [3]]", data.getFemaleTable().getRows().toString());

        assertEquals("[period1]", data.getMaleChart().getHeaders().toString());
        assertEquals("[A00-B99 name1: [2]]", data.getMaleChart().getRows().toString());
        assertEquals("[A00-B99 name1]", data.getMaleTable().getHeaders().toString());
        assertEquals("[period1: [2]]", data.getMaleTable().getRows().toString());
    }

}
