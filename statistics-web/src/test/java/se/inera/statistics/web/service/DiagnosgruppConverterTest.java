package se.inera.statistics.web.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import se.inera.statistics.service.report.model.DiagnosisGroup;
import se.inera.statistics.service.report.model.DiagnosisGroupResponse;
import se.inera.statistics.service.report.model.DualSexDataRow;
import se.inera.statistics.service.report.model.DualSexField;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.web.model.DualSexStatisticsData;
import se.inera.statistics.web.model.TableData;

public class DiagnosgruppConverterTest {

    @Test
    public void tableConverterTestEmptyInput() {
        DiagnosisGroupResponse resp = new DiagnosisGroupResponse(new ArrayList<DiagnosisGroup>(), new ArrayList<DualSexDataRow>());
        TableData tableData = DiagnosisGroupsConverter.convertTable(resp);
        assertEquals("[[;1, ;1, ;1], [Period;1, Antal sjukfall;1, Summering;1]]", tableData.getHeaders().toString());
        assertEquals("[Totalt: []]", tableData.getRows().toString());
    }

    @Test
    public void tableConverterTest() {
        //Given
        ArrayList<DiagnosisGroup> diagnosisGroups = new ArrayList<DiagnosisGroup>();
        diagnosisGroups.add(new DiagnosisGroup("A01-B99", "name1"));
        ArrayList<DualSexDataRow> rows = new ArrayList<DualSexDataRow>();
        ArrayList<DualSexField> diagnosisGroupData = new ArrayList<DualSexField>();
        // CHECKSTYLE:OFF MagicNumber
        diagnosisGroupData.add(new DualSexField(3, 2));
        // CHECKSTYLE:ON MagicNumber
        rows.add(new DualSexDataRow("period1", diagnosisGroupData));
        DiagnosisGroupResponse resp = new DiagnosisGroupResponse(diagnosisGroups, rows);

        //When
        TableData tableData = DiagnosisGroupsConverter.convertTable(resp);

        //Then
        assertEquals("[[;1, ;1, A01-B99 name1;2, ;1], [Period;1, Antal sjukfall;1, Kvinnor;1, Män;1, Summering;1]]", tableData.getHeaders().toString());
        assertEquals("[period1: [5, 3, 2, 5], Totalt: [5, 3, 2]]", tableData.getRows().toString());
    }

    @Test
    public void converterTestEmpty() {
        DiagnosisGroupResponse resp = new DiagnosisGroupResponse(new ArrayList<DiagnosisGroup>(), new ArrayList<DualSexDataRow>());
        DualSexStatisticsData data = new DiagnosisGroupsConverter().convert(resp, new Range());
        assertEquals("[]", data.getFemaleChart().getCategories().toString());
        assertEquals("[Somatiska sjukdomar (A00-E90, G00-L99, N00-N99): [], Psykiska sjukdomar (F00-F99): [], Muskuloskeletala sjukdomar (M00-M99): [], Graviditet och förlossning (O00-O99): [], Övrigt (P00-P96, Q00-Q99, S00-Y98): [], Symtomdiagnoser (R00-R99): [], Faktorer av betydelse för hälsotillståndet och för kontakter med hälso- och sjukvården (Z00-Z99): []]", data.getFemaleChart().getSeries().toString());
    }

    @Test
    public void converterTest() {
        //Given
        ArrayList<DiagnosisGroup> diagnosisGroups = new ArrayList<DiagnosisGroup>();
        diagnosisGroups.add(new DiagnosisGroup("A00-B99", "name1"));
        ArrayList<DualSexDataRow> rows = new ArrayList<DualSexDataRow>();
        ArrayList<DualSexField> diagnosisGroupData = new ArrayList<DualSexField>();
        // CHECKSTYLE:OFF MagicNumber
        diagnosisGroupData.add(new DualSexField(3, 2));
        // CHECKSTYLE:ON MagicNumber
        rows.add(new DualSexDataRow("period1", diagnosisGroupData));
        DiagnosisGroupResponse resp = new DiagnosisGroupResponse(diagnosisGroups, rows);

        //When
        DiagnosisGroupsConverter converter = new DiagnosisGroupsConverter();
        DualSexStatisticsData data = converter.convert(resp, new Range());

        //Then
        assertEquals("[period1]", data.getFemaleChart().getCategories().toString());
        assertTrue(data.getFemaleChart().getSeries().toString(), data.getFemaleChart().getSeries().toString().contains("Somatiska sjukdomar (A00-E90, G00-L99, N00-N99): [3]"));

        assertEquals("[period1]", data.getMaleChart().getCategories().toString());
        assertTrue(data.getMaleChart().getSeries().toString(), data.getMaleChart().getSeries().toString().contains("Somatiska sjukdomar (A00-E90, G00-L99, N00-N99): [2]"));

        assertEquals("[[;1, ;1, A00-B99 name1;2, ;1], [Period;1, Antal sjukfall;1, Kvinnor;1, Män;1, Summering;1]]", data.getTableData().getHeaders().toString());
        assertEquals("[period1: [5, 3, 2, 5], Totalt: [5, 3, 2]]", data.getTableData().getRows().toString());
    }

}
