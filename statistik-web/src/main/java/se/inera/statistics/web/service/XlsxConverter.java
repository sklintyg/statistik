/**
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
 *
 * This file is part of statistik (https://github.com/sklintyg/statistik).
 *
 * statistik is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * statistik is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.web.service;

import com.google.common.annotations.VisibleForTesting;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.inera.statistics.service.report.util.Icd10;
import se.inera.statistics.web.model.DiagnosisSubGroupStatisticsData;
import se.inera.statistics.web.model.NamedData;
import se.inera.statistics.web.model.TableData;
import se.inera.statistics.web.model.TableDataReport;
import se.inera.statistics.web.model.TableHeader;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

final class XlsxConverter {
    private static final Logger LOG = LoggerFactory.getLogger(XlsxConverter.class);

    private final Icd10 icd10;
    private final String dataSheetName = "tabell";

    XlsxConverter(Icd10 icd10) {
        this.icd10 = icd10;
    }

    Response getXlsxResponse(final TableDataReport tableData, final String fileName, FilterSelections filterSelections, Report report) {
        try {
            final ByteArrayOutputStream generatedFile = generateExcelFile(tableData, filterSelections, report);
            return Response.ok(generatedFile.toByteArray(), MediaType.APPLICATION_OCTET_STREAM)
                    .header("Content-Disposition", "attachment; filename=" + fileName).build();
        } catch (XslxFileGenerationException e) {
            LOG.debug("Xlsx file generation failed", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Could not generate xlsx file").build();
        }
    }

    private ByteArrayOutputStream generateExcelFile(TableDataReport tableData, FilterSelections filterSelections,
                                                    Report report) throws XslxFileGenerationException {
        try (Workbook workbook = new XSSFWorkbook()) {
            addData(workbook, tableData, filterSelections, report);
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream;
        } catch (IOException e) {
            LOG.error("Failed to generate xlsx export", e);
            throw new XslxFileGenerationException();
        }
    }

    @NotNull
    private CellStyle getBoldStyle(Workbook workbook) {
        CellStyle boldStyle = workbook.createCellStyle();
        Font boldFont = workbook.createFont();
        boldFont.setBold(true);
        boldStyle.setFont(boldFont);
        return boldStyle;
    }

    @NotNull
    private CellStyle getHlinkStyle(Workbook workbook) {
        CellStyle hlinkStyle = workbook.createCellStyle();
        Font hlinkFont = workbook.createFont();
        hlinkFont.setUnderline(Font.U_SINGLE);
        hlinkFont.setColor(IndexedColors.BLUE.getIndex());
        hlinkStyle.setFont(hlinkFont);
        return hlinkStyle;
    }

    @VisibleForTesting
    void addData(Workbook workbook, TableDataReport tableData, FilterSelections filterSelections, Report report) {
        Sheet sheet = workbook.createSheet(dataSheetName);
        int currentRow = 0;
        currentRow = addReportHeader(tableData, report, sheet, currentRow);
        sheet.createRow(currentRow++);
        currentRow = addFilters(tableData.getFilter(), filterSelections, sheet, currentRow);
        sheet.createRow(currentRow++);
        currentRow = addDataTable(tableData.getTableData(), sheet, currentRow);
        LOG.info(currentRow + " rows added to xlsx-export");
    }

    private int addFilters(FilterDataResponse filter, FilterSelections filterSelections, Sheet dataSheet, int startRow) {
        final List<String> enheter = filterSelections.getEnhetNames();
        final List<String> dxs = filterSelections.isAllAvailableDxsSelectedInFilter()
                ? Collections.emptyList() : filter.getDiagnoser();
        final List<String> sjukskrivningslangds = filterSelections.isAllAvailableSjukskrivningslangdsSelectedInFilter()
                ? Collections.emptyList() : filter.getSjukskrivningslangd();
        final List<String> aldersgrupps = filterSelections.isAllAvailableAgeGroupsSelectedInFilter()
                ? Collections.emptyList() : filter.getAldersgrupp();

        final boolean useSeparateSheetForFilters = isUseSeparateSheetForFilters(enheter, dxs, sjukskrivningslangds, aldersgrupps);
        String urvalSheetName = "urval";
        final Sheet sheet = useSeparateSheetForFilters ? dataSheet.getWorkbook().createSheet(urvalSheetName) : dataSheet;
        int currentRow = useSeparateSheetForFilters ? 0 : startRow;

        if (useSeparateSheetForFilters) {
            addLink(sheet, "Se tabell", currentRow++, "'" + dataSheetName + "'!A1");
            sheet.createRow(currentRow++);
        }
        currentRow = addFilter(sheet, currentRow, enheter, "Sammanställning av enheter");
        currentRow = addFilter(sheet, currentRow, getDxNames(dxs), "Sammanställning av diagnosfilter");
        currentRow = addFilter(sheet, currentRow, sjukskrivningslangds, "Sammanställning av sjukskrivningslängdsfilter");
        currentRow = addFilter(sheet, currentRow, aldersgrupps, "Sammanställning av åldersgruppsfilter");

        int currentRowDataSheet = useSeparateSheetForFilters ? startRow : currentRow;
        if (useSeparateSheetForFilters) {
            addLink(dataSheet, "Se urval", currentRowDataSheet++, "'" + urvalSheetName + "'!A1");
        }

        return currentRowDataSheet;
    }

    private List<String> getDxNames(List<String> dxs) {
        return dxs == null ? null : dxs.stream().map(icdNumId -> {
                final Icd10.Id icd = icd10.findIcd10FromNumericId(Integer.parseInt(icdNumId));
                return (icd.getVisibleId() + " " + icd.getName()).trim();
            }).collect(Collectors.toList());
    }

    private boolean isUseSeparateSheetForFilters(List<String> enheter, List<String> dxs,
                                                 List<String> sjukskrivningslangds, List<String> aldersgrupps) {
        final int totalFilters = (enheter == null ? 0 : enheter.size())
                + (dxs == null ? 0 : dxs.size())
                + (sjukskrivningslangds == null ? 0 : sjukskrivningslangds.size())
                + (aldersgrupps == null ? 0 : aldersgrupps.size());
        final int maxFiltersOnDataSheet = 8;
        return totalFilters > maxFiltersOnDataSheet;
    }

    private void addLink(Sheet sheet, String text, int rowToCreate, String address) {
        final Row row = sheet.createRow(rowToCreate);
        final Cell cell = row.createCell(0, Cell.CELL_TYPE_STRING);
        cell.setCellValue(text);
        Hyperlink link = sheet.getWorkbook().getCreationHelper().createHyperlink(Hyperlink.LINK_DOCUMENT);
        link.setAddress(address);
        cell.setHyperlink(link);
        cell.setCellStyle(getHlinkStyle(sheet.getWorkbook()));
    }

    private int addFilter(Sheet sheet, int row, List<String> filters, String title) {
        int currentRow = row;
        if (filters != null && !filters.isEmpty()) {
            addTitle(sheet, currentRow++, title);
            for (String filter : filters) {
                addValue(sheet.createRow(currentRow++), 0, filter);
            }
            sheet.createRow(currentRow++);
        }
        return currentRow;
    }

    private void addTitle(Sheet sheet, int row, String text) {
        addTitle(sheet.createRow(row), 0, text);
    }

    private void addTitle(Row row, int column, String text) {
        final Cell cell = row.createCell(column, Cell.CELL_TYPE_STRING);
        cell.setCellStyle(getBoldStyle(row.getSheet().getWorkbook()));
        cell.setCellValue(text);
    }

    private void addValue(Row row, int column, Object value) {
        final boolean isNumber = value instanceof Number;
        if (isNumber) {
            final Cell cell = row.createCell(column, Cell.CELL_TYPE_NUMERIC);
            cell.setCellValue(((Number) value).doubleValue());
        } else {
            final Cell cell = row.createCell(column, Cell.CELL_TYPE_STRING);
            cell.setCellValue(String.valueOf(value));
        }
    }

    private int addReportHeader(TableDataReport tableData, Report report, Sheet sheet, int startRow) {
        int currentRow = startRow;
        addTitle(sheet, currentRow++, report.getStatisticsLevel().getText());
        final String titleExtras = tableData instanceof DiagnosisSubGroupStatisticsData
                ? " " + ((DiagnosisSubGroupStatisticsData) tableData).getDxGroup() : "";
        addTitle(sheet, currentRow++, report.getLongName() + titleExtras + " " + tableData.getPeriod());
        return currentRow;
    }

    private int addDataTable(TableData tableData, Sheet sheet, int startRow) {
        int currentRow = startRow;
        for (List<TableHeader> list : tableData.getHeaders()) {
            int currentCol = 0;
            final Row row = sheet.createRow(currentRow++);
            for (TableHeader tableHeader : list) {
                for (int i = 0; i < tableHeader.getColspan(); i++) {
                    addTitle(row, currentCol++, tableHeader.getText());
                }
            }
        }
        for (NamedData namedData : tableData.getRows()) {
            int currentCol = 0;
            final Row row = sheet.createRow(currentRow++);
            addValue(row, currentCol++, namedData.getName());
            for (Object value : namedData.getData()) {
                addValue(row, currentCol++, value);
            }
        }
        return currentRow;
    }

}