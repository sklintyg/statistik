/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.web.service;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.service.report.util.Icd10;
import se.inera.statistics.web.model.NamedData;
import se.inera.statistics.web.model.TableData;
import se.inera.statistics.web.model.TableDataReport;
import se.inera.statistics.web.model.TableHeader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;

public class XlsxConverterTest {

    @Test
    public void testStatisticsLevelAndReportNameAndPeriodAreShownCorrect() throws Exception {
        ArrayList<String> cellTexts = addXlsxData(Report.N_SJUKSKRIVNINGSLANGD, "jan - feb");
        assertTrue(cellTexts.toString().startsWith("[Nationell statistik, Antal sjukfall fördelat på sjukskrivningslängd jan - feb"));

        cellTexts = addXlsxData(Report.V_DIAGNOSGRUPP, "2001 till 2003");
        assertTrue(cellTexts.toString().startsWith("[Verksamhetsstatistik, Antal sjukfall fördelat på diagnosgrupp 2001 till 2003"));

        cellTexts = addXlsxData(Report.L_VARDENHET, "testar");
        assertTrue(cellTexts.toString().startsWith("[Landstingsstatistik, Antal sjukfall fördelat på vårdenhet testar"));
    }

    @NotNull
    private ArrayList<String> addXlsxData(Report report, String period) {
        return addXlsxData(report, period, Arrays.asList(), null,
                Arrays.asList(new NamedData("datarad1", Arrays.asList(1, 2, 3))));
    }

    @Test
    public void testExportShouldAlwaysContainListOfEnhets() throws Exception {
        ArrayList<String> cellTexts = addXlsxDataForEnhetTest(
                Arrays.asList("TestEnhet"),
                Arrays.asList(new HsaIdEnhet("enhet1")));
        assertTrue(cellTexts.toString().contains("Sammanställning av enheter, TestEnhet"));

        cellTexts = addXlsxDataForEnhetTest(Arrays.asList("TestEnhet"), Arrays.asList());
        assertTrue(cellTexts.toString().contains("Sammanställning av enheter, TestEnhet"));

        cellTexts = addXlsxDataForEnhetTest(Arrays.asList("TestEnhet"), null);
        assertTrue(cellTexts.toString().contains("Sammanställning av enheter, TestEnhet"));
    }

    @NotNull
    private ArrayList<String> addXlsxDataForEnhetTest(List<String> testEnhet, Collection<HsaIdEnhet> enhetsfilter) {
        return addXlsxData(Report.V_DIAGNOSGRUPP, "", testEnhet, enhetsfilter,
                Arrays.asList(new NamedData("datarad1", Arrays.asList(1, 2, 3))));
    }

    @Test
    public void testAllDataRowsAreAdded() throws Exception {
        ArrayList<String> cellTexts = addXlsxData(Arrays.asList(
                new NamedData("datarad1", Arrays.asList(1, 2, 3))));
        assertTrue(cellTexts.toString().contains("datarad1, 1.0, 2.0, 3.0"));

        cellTexts = addXlsxData(Arrays.asList(
                new NamedData("datarad1", Arrays.asList(1, 2, 3)),
                new NamedData("datarad2", Arrays.asList(10, 20, 30))));
        assertTrue(cellTexts.toString().contains("datarad1, 1.0, 2.0, 3.0, datarad2, 10.0, 20.0, 30.0"));
    }

    @NotNull
    private ArrayList<String> addXlsxData(List<NamedData> dataList) {
        return addXlsxData(Report.V_DIAGNOSGRUPP, "", Arrays.asList("TestEnhet"), Arrays.asList(new HsaIdEnhet("enhet1")), dataList);
    }

    /**
     * Set up a workbook mock and return all text fields added to it
     */
    @NotNull
    private ArrayList<String> addXlsxData(Report report, String period, List<String> testEnhet, Collection<HsaIdEnhet> enhetsfilter, List<NamedData> dataList) {
        final ArrayList<String> cellTexts = new ArrayList<>();
        final Icd10 icd10 = Mockito.mock(Icd10.class);
        final XlsxConverter xlsxConverter = new XlsxConverter(icd10);
        final Workbook workbook = Mockito.mock(Workbook.class);
        final Font font = Mockito.mock(Font.class);
        Mockito.when(workbook.createFont()).thenReturn(font);
        final CellStyle cellStyle = Mockito.mock(CellStyle.class);
        Mockito.when(workbook.createCellStyle()).thenReturn(cellStyle);
        final Sheet sheet = Mockito.mock(Sheet.class);
        Mockito.when(sheet.getWorkbook()).thenReturn(workbook);
        final Row row = Mockito.mock(Row.class);
        Mockito.when(row.getSheet()).thenReturn(sheet);
        final Cell cell = Mockito.mock(Cell.class);
        Mockito.when(row.createCell(Mockito.anyInt(), Mockito.anyInt())).thenReturn(cell);
        final Answer addCellText = invocationOnMock -> {
            final Object[] arguments = invocationOnMock.getArguments();
            for (Object argument : arguments) {
                cellTexts.add(String.valueOf(argument));
            }
            return null;
        };
        Mockito.doAnswer(addCellText).when(cell).setCellValue(Mockito.anyString());
        Mockito.doAnswer(addCellText).when(cell).setCellValue(Mockito.anyDouble());
        Mockito.when(sheet.createRow(Mockito.anyInt())).thenReturn(row);
        Mockito.doReturn(sheet).when(workbook).createSheet(Mockito.anyString());
        final TableDataReport tableData = Mockito.mock(TableDataReport.class);
        final TableData data = new TableData(dataList, Arrays.asList(Arrays.asList(new TableHeader("th"))));
        Mockito.when(tableData.getTableData()).thenReturn(data);
        Mockito.when(tableData.getFilter()).thenReturn(new FilterDataResponse("", null, enhetsfilter, null, null, null));
        Mockito.when(tableData.getPeriod()).thenReturn(period);

        final FilterSelections filterSelections = new FilterSelections(false, false, false, false, false, testEnhet);
        xlsxConverter.addData(workbook, tableData, filterSelections, report);
        return cellTexts;
    }

}
