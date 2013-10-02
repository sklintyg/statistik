package se.inera.statistics.web.service;

import org.junit.Test;
import static org.junit.Assert.*;

import se.inera.statistics.service.report.model.*;
import se.inera.statistics.web.model.DiagnosisGroupsData;
import se.inera.statistics.web.model.TableData;

import java.util.ArrayList;

public class DiagnosisGroupsConverterTest {

    @Test
    public void tableConverterTestEmptyInput(){
        DiagnosisGroupResponse resp = new DiagnosisGroupResponse(new ArrayList<DiagnosisGroup>(), new ArrayList<DiagnosisGroupRow>());
        TableData tableData = DiagnosisGroupsConverter.convertDiagnosisGroupsTableData(resp, Sex.Male);
        assertEquals("[]", tableData.getHeaders().toString());
        assertEquals("[]", tableData.getRows().toString());
    }

    @Test
    public void tableConverterTest(){
        //Given
        ArrayList<DiagnosisGroup> diagnosisGroups = new ArrayList<DiagnosisGroup>();
        diagnosisGroups.add(new DiagnosisGroup("A01-B99", "name1"));
        ArrayList<DiagnosisGroupRow> rows = new ArrayList<DiagnosisGroupRow>();
        ArrayList<DualSexField> diagnosisGroupData = new ArrayList<DualSexField>();
        diagnosisGroupData.add(new DualSexField(3, 2));
        rows.add(new DiagnosisGroupRow("period1", diagnosisGroupData));
        DiagnosisGroupResponse resp = new DiagnosisGroupResponse(diagnosisGroups, rows);

        //When
        TableData femaleTableData = DiagnosisGroupsConverter.convertDiagnosisGroupsTableData(resp, Sex.Female);
        TableData maleTableData = DiagnosisGroupsConverter.convertDiagnosisGroupsTableData(resp, Sex.Male);

        //Then
        assertEquals("[A01-B99 name1]", maleTableData.getHeaders().toString());
        assertEquals("[period1: [2]]", maleTableData.getRows().toString());
        assertEquals("[A01-B99 name1]", femaleTableData.getHeaders().toString());
        assertEquals("[period1: [3]]", femaleTableData.getRows().toString());
    }

    @Test
    public void converterTestEmpty(){
        DiagnosisGroupResponse resp = new DiagnosisGroupResponse(new ArrayList<DiagnosisGroup>(), new ArrayList<DiagnosisGroupRow>());
        DiagnosisGroupsData data = new DiagnosisGroupsConverter().convert(resp);
        assertEquals("[]", data.getFemaleChart().getHeaders().toString());
        assertEquals("[Somatiska sjukdomar (A00-E90, G00-L99, N00-N99): [], Psykiska sjukdomar (F00-F99): [], Muskuloskeletala sjukdomar (M00-M99): [], Graviditet och förlossning (O00-O99): [], Övrigt (P00-P96, Q00-Q99, S00-Y98): [], Symtomdiagnoser (R00-R99): [], Faktorer av betydelse för hälsotillståndet och för kontakter med hälso- och sjukvården (Z00-Z99): []]", data.getFemaleChart().getRows().toString());
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
        DiagnosisGroupsConverter converter = new DiagnosisGroupsConverter();
        DiagnosisGroupsData data = converter.convert(resp);

        //Then
        assertEquals("[period1]", data.getFemaleChart().getHeaders().toString());
        assertTrue(data.getFemaleChart().getRows().toString(), data.getFemaleChart().getRows().toString().contains("Somatiska sjukdomar (A00-E90, G00-L99, N00-N99): [3]"));
        assertEquals("[A00-B99 name1]", data.getFemaleTable().getHeaders().toString());
        assertEquals("[period1: [3]]", data.getFemaleTable().getRows().toString());

        assertEquals("[period1]", data.getMaleChart().getHeaders().toString());
        assertTrue(data.getMaleChart().getRows().toString(), data.getMaleChart().getRows().toString().contains("Somatiska sjukdomar (A00-E90, G00-L99, N00-N99): [2]"));
        assertEquals("[A00-B99 name1]", data.getMaleTable().getHeaders().toString());
        assertEquals("[period1: [2]]", data.getMaleTable().getRows().toString());
    }

}
