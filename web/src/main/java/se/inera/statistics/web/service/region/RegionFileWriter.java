/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.inera.statistics.service.processlog.Enhet;

public class RegionFileWriter {

    private static final Logger LOG = LoggerFactory.getLogger(RegionFileWriter.class);

    public ByteArrayOutputStream generateExcelFile(List<Enhet> enhets) throws RegionFileGenerationException {
        try {
            final InputStream stream = RegionFileWriter.class.getResourceAsStream("/MALL_regionsstatistik.xlsx");
            Workbook workbook = new XSSFWorkbook(stream);
            Sheet sheet = workbook.getSheetAt(0);
            final int startRow = 1;
            for (int i = 0; i < enhets.size(); i++) {
                final Enhet enhet = enhets.get(i);
                final Row row = sheet.createRow(i + startRow);
                row.createCell(0, CellType.STRING).setCellValue(enhet.getNamn());
                row.createCell(1, CellType.STRING).setCellValue(enhet.getEnhetId().getId());
            }
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();
            return outputStream;
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            throw new RegionFileGenerationException();
        }
    }

}
