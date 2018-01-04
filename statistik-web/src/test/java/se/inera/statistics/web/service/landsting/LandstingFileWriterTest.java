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
package se.inera.statistics.web.service.landsting;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.processlog.Enhet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class LandstingFileWriterTest {

    @Test
    public void testGenerateExcelFileEmptyInputAddsNothing() throws Exception {
        //Given
        final ArrayList<Enhet> enhets = new ArrayList<>();

        //When
        final ByteArrayOutputStream outputStream = new LandstingFileWriter().generateExcelFile(enhets);

        //Then
        final byte[] bytes = outputStream.toByteArray();
        final ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Workbook workbook = new XSSFWorkbook(is);
        final Sheet sheet = workbook.getSheetAt(0);
        final Row row = sheet.getRow(1);
        assertNull(row.getCell(0, Row.RETURN_BLANK_AS_NULL));
        assertNull(row.getCell(1, Row.RETURN_BLANK_AS_NULL));
        assertNull(row.getCell(2, Row.RETURN_BLANK_AS_NULL));
    }

    @Test
    public void testGenerateExcelFileRowsAreCorrectlyAdded() throws Exception {
        //Given
        final ArrayList<Enhet> enhets = new ArrayList<>();
        enhets.add(new Enhet(new HsaIdVardgivare(""), new HsaIdEnhet("id1"), "name1", "", "", ""));
        enhets.add(new Enhet(new HsaIdVardgivare(""), new HsaIdEnhet("id43"), "name fdsa wqer5", "", "", ""));
        enhets.add(new Enhet(new HsaIdVardgivare(""), new HsaIdEnhet("id6"), "farsrG", "", "", ""));
        enhets.add(new Enhet(new HsaIdVardgivare(""), new HsaIdEnhet("id123445"), "VrVRwr", "", "", ""));

        //When
        final ByteArrayOutputStream outputStream = new LandstingFileWriter().generateExcelFile(enhets);

        //Then
        final byte[] bytes = outputStream.toByteArray();
        final ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Workbook workbook = new XSSFWorkbook(is);
        final Sheet sheet = workbook.getSheetAt(0);
        for (int i = 0; i < enhets.size(); i++) {
            final Enhet enhet = enhets.get(i);
            final Row row = sheet.getRow(i + 1);
            assertEquals(enhet.getNamn(), row.getCell(0, Row.RETURN_BLANK_AS_NULL).getStringCellValue());
            assertEquals(enhet.getEnhetId().getId(), row.getCell(1, Row.RETURN_BLANK_AS_NULL).getStringCellValue());
            assertNull(row.getCell(2, Row.RETURN_BLANK_AS_NULL));
        }
    }

}
