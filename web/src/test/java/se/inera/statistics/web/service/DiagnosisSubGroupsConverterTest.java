package se.inera.statistics.web.service;

import org.junit.Test;
import se.inera.statistics.service.report.model.DiagnosgruppResponse;
import se.inera.statistics.service.report.model.KonDataRow;
import se.inera.statistics.service.report.model.KonField;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class DiagnosisSubGroupsConverterTest {

    @Test
    public void testGetTopColumnIndexesAllAreIncluded() throws Exception {
        //Given
        ArrayList<KonDataRow> rows = new ArrayList<>();
        ArrayList<KonField> data = new ArrayList<>();
        data.add(new KonField(1, 2));
        data.add(new KonField(2, 1));
        data.add(new KonField(2, 2));
        rows.add(new KonDataRow("", data));
        DiagnosgruppResponse response = new DiagnosgruppResponse(null, rows);

        //When
        List<Integer> result = new DiagnosisSubGroupsConverter().getTopColumnIndexes(response);

        //Then
        assertEquals(3, result.size());
    }

    @Test
    public void testGetTopColumnIndexesCorrectOrder() throws Exception {
        //Given
        ArrayList<KonDataRow> rows = new ArrayList<>();
        ArrayList<KonField> data = new ArrayList<>();
        data.add(new KonField(1, 2));
        data.add(new KonField(2, 0));
        data.add(new KonField(2, 2));
        rows.add(new KonDataRow("", data));
        DiagnosgruppResponse response = new DiagnosgruppResponse(null, rows);

        //When
        List<Integer> result = new DiagnosisSubGroupsConverter().getTopColumnIndexes(response);

        //Then
        assertEquals(2, result.get(0).intValue());
        assertEquals(0, result.get(1).intValue());
        assertEquals(1, result.get(2).intValue());
    }

    @Test
    public void testGetTopColumnIndexesRowWithZeroIsExcluded() throws Exception {
        //Given
        ArrayList<KonDataRow> rows = new ArrayList<>();
        ArrayList<KonField> data = new ArrayList<>();
        data.add(new KonField(1, 2));
        data.add(new KonField(0, 0));
        data.add(new KonField(2, 2));
        rows.add(new KonDataRow("", data));
        DiagnosgruppResponse response = new DiagnosgruppResponse(null, rows);

        //When
        List<Integer> result = new DiagnosisSubGroupsConverter().getTopColumnIndexes(response);

        //Then
        assertEquals(2, result.size());
        assertEquals(2, result.get(0).intValue());
        assertEquals(0, result.get(1).intValue());
    }

}
