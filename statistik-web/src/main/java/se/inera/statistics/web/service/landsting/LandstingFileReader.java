/**
 * Copyright (C) 2015 Inera AB (http://www.inera.se)
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
package se.inera.statistics.web.service.landsting;

import com.google.common.math.DoubleMath;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import se.inera.statistics.service.landsting.LandstingEnhetFileDataRow;

import javax.activation.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LandstingFileReader {

    public List<LandstingEnhetFileDataRow> readExcelData(DataSource dataSource) throws LandstingEnhetFileParseException {
        final List<LandstingEnhetFileDataRow> rows = new ArrayList<>();
        try {
            InputStream fis = dataSource.getInputStream();
            Workbook workbook = null;
            if (dataSource.getName().toLowerCase().endsWith("xlsx")) {
                workbook = new XSSFWorkbook(fis);
            } else if (dataSource.getName().toLowerCase().endsWith("xls")) {
                workbook = new HSSFWorkbook(fis);
            }

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            if (rowIterator.hasNext()) {
                rowIterator.next(); //Skip title row
            }
            int rowNumber = 1;
            while (rowIterator.hasNext()) {
                rowNumber++;
                final String messagePrefix = "Row #" + rowNumber + ": ";

                String name = "";
                String id = "";
                Integer patients = null;

                Row row = rowIterator.next();
                Cell cellName = row.getCell(0);
                if (cellName != null) {
                    if (Cell.CELL_TYPE_STRING == cellName.getCellType()) {
                        name = cellName.getStringCellValue().trim();
                    } else {
                        throw new LandstingEnhetFileParseException(messagePrefix + "Wrong name cell type");
                    }
                }

                Cell cellHsaId = row.getCell(1);
                if (cellHsaId != null) {
                    if (Cell.CELL_TYPE_STRING == cellHsaId.getCellType()) {
                        id = cellHsaId.getStringCellValue().trim();
                    }
                }

                Cell cellPatients = row.getCell(2);
                if (cellPatients != null) {
                    switch (cellPatients.getCellType()) {
                        case Cell.CELL_TYPE_NUMERIC:
                            double patientsDouble = cellPatients.getNumericCellValue();
                            if (DoubleMath.isMathematicalInteger(patientsDouble)) {
                                patients = (int) patientsDouble;
                            } else {
                                throw new LandstingEnhetFileParseException(messagePrefix + "Patients cell is not an integer number: " + patientsDouble);
                            }
                            break;
                        case Cell.CELL_TYPE_STRING:
                            final String patientsString = cellPatients.getStringCellValue();
                            try {
                                patients = Integer.parseInt(patientsString);
                            } catch (NumberFormatException e) {
                                throw new LandstingEnhetFileParseException(messagePrefix + "Patients cell is not an integer number");
                            }
                            break;
                        default:
                            patients = null;
                            break;
                    }
                }

                if (!id.isEmpty()) {
                    if (!id.matches("^[a-zA-Z0-9-]+$")) {
                        throw new LandstingEnhetFileParseException(messagePrefix + "Illegal character(s) in HSA id cell");
                    }
                    if (patients != null && patients < 0) {
                        throw new LandstingEnhetFileParseException(messagePrefix + "Patients field may not be a negative number");
                    }
                    rows.add(new LandstingEnhetFileDataRow(name, id, patients));
                }
            }
        } catch (IOException e) {
            throw new LandstingEnhetFileParseException("Could not read file", e);
        }
        return rows;
    }

}
