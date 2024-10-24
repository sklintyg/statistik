/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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
package se.inera.statistics.web.service.region;

import com.google.common.math.DoubleMath;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import jakarta.activation.DataSource;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import se.inera.statistics.integration.hsa.model.HsaIdEnhet;
import se.inera.statistics.service.region.RegionEnhetFileDataRow;

public class RegionFileReader {

    public List<RegionEnhetFileDataRow> readExcelData(DataSource dataSource) throws RegionEnhetFileParseException {
        final List<RegionEnhetFileDataRow> rows = new ArrayList<>();
        final HashSet<HsaIdEnhet> addedEnhetIds = new HashSet<>();
        try {
            InputStream fis = dataSource.getInputStream();
            Workbook workbook;
            if (dataSource.getName().toLowerCase().endsWith("xlsx")) {
                workbook = new XSSFWorkbook(fis);
            } else if (dataSource.getName().toLowerCase().endsWith("xls")) {
                workbook = new HSSFWorkbook(fis);
            } else {
                throw new RegionEnhetFileParseException("Felaktigt filformat");
            }

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            if (rowIterator.hasNext()) {
                rowIterator.next(); // Skip title row
            }
            int rowNumber = 1;
            while (rowIterator.hasNext()) {
                rowNumber++;
                final String messagePrefix = "Rad " + rowNumber + ": ";

                Row row = rowIterator.next();
                final HsaIdEnhet id = getEnhetHsaId(row);
                Integer patients = getListedPatients(messagePrefix, row);

                if (!id.getId().isEmpty()) {
                    if (!addedEnhetIds.add(id)) {
                        throw new RegionEnhetFileParseException(messagePrefix + "Vårdenheten förekommer mer än en gång");
                    }
                    if (!id.getId().matches("^[a-zA-Z0-9-]+$")) {
                        throw new RegionEnhetFileParseException(messagePrefix + "Kolumn “HSA-id” innehåller otillåtna tecken");
                    }
                    if (patients != null && patients < 0) {
                        throw new RegionEnhetFileParseException(
                            messagePrefix + "Kolumn “Antal listade patienter” innehåller ett negativt tal");
                    }
                    rows.add(new RegionEnhetFileDataRow(id, patients));
                }
            }
        } catch (IOException e) {
            throw new RegionEnhetFileParseException("Kunde inte läsa fil", e);
        }
        return rows;
    }

    private Integer getListedPatients(String messagePrefix, Row row) throws RegionEnhetFileParseException {
        Integer patients = null;
        Cell cellPatients = row.getCell(2);
        if (cellPatients != null) {
            switch (cellPatients.getCellType()) {
                case NUMERIC:
                    double patientsDouble = cellPatients.getNumericCellValue();
                    if (DoubleMath.isMathematicalInteger(patientsDouble)) {
                        patients = (int) patientsDouble;
                    } else {
                        throw new RegionEnhetFileParseException(
                            messagePrefix + "Kolumn “Antal listade patienter” innehåller inte ett heltal: " + patientsDouble);
                    }
                    break;
                case STRING:
                    final String patientsString = cellPatients.getStringCellValue();
                    try {
                        patients = Integer.parseInt(patientsString);
                    } catch (NumberFormatException e) {
                        throw new RegionEnhetFileParseException(
                            messagePrefix + "Kolumn “Antal listade patienter” innehåller inte ett heltal");
                    }
                    break;
                default:
                    patients = null;
                    break;
            }
        }
        return patients;
    }

    private HsaIdEnhet getEnhetHsaId(Row row) {
        Cell cellHsaId = row.getCell(1);
        if (cellHsaId != null) {
            if (CellType.STRING == cellHsaId.getCellType()) {
                final String cellValue = cellHsaId.getStringCellValue().trim();
                return new HsaIdEnhet(cellValue);
            }
        }
        return new HsaIdEnhet("");
    }

}