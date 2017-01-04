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
package se.inera.statistics.web.service.landsting;

import org.junit.Test;
import se.inera.statistics.service.landsting.LandstingEnhetFileDataRow;

import javax.activation.FileDataSource;

import java.io.File;
import java.net.URL;
import java.util.List;

import static org.junit.Assert.*;

public class LandstingFileReaderTest {

    private LandstingFileReader landstingFileReader = new LandstingFileReader();

    @Test
    public void testReadExcelData() throws Exception {
        //Given
        URL url = LandstingFileReader.class.getResource("/underlag-till-v3-vgr-listningar.xls");
        File file = new File(url.getPath());
        final FileDataSource dataSource = new FileDataSource(file);

        //When
        final List<LandstingEnhetFileDataRow> result = landstingFileReader.readExcelData(dataSource);

        //Then
        assertEquals(204, result.size());

        final LandstingEnhetFileDataRow row0 = result.get(0);
        assertEquals("SE2321000131-E000000000455", row0.getEnhetensHsaId().getId());
        assertEquals(Integer.valueOf(0), row0.getListadePatienter());

        final LandstingEnhetFileDataRow row3 = result.get(3);
        assertEquals("SE2321000131-E000000007507", row3.getEnhetensHsaId().getId());
        assertEquals(Integer.valueOf(1072), result.get(3).getListadePatienter());

        final LandstingEnhetFileDataRow row192 = result.get(192);
        assertEquals("SE2321000131-P000000015902", row192.getEnhetensHsaId().getId());
        assertEquals(null, row192.getListadePatienter());
    }

    @Test
    public void testReadExcelDataWithOneRow() throws Exception {
        //Given
        URL url = LandstingFileReader.class.getResource("/landsting-test.xls");
        File file = new File(url.getPath());
        final FileDataSource dataSource = new FileDataSource(file);

        //When
        final List<LandstingEnhetFileDataRow> result = landstingFileReader.readExcelData(dataSource);

        //Then
        assertEquals(1, result.size());

        final LandstingEnhetFileDataRow row0 = result.get(0);
        assertEquals("SE2321000131-E000000007507", row0.getEnhetensHsaId().getId());
        assertEquals(Integer.valueOf(1072), row0.getListadePatienter());
    }

    @Test
    public void testReadExcelDataWithListningarAsText() throws Exception {
        //Given
        URL url = LandstingFileReader.class.getResource("/landsting-test-text-listning.xls");
        File file = new File(url.getPath());
        final FileDataSource dataSource = new FileDataSource(file);

        //When
        final List<LandstingEnhetFileDataRow> result = landstingFileReader.readExcelData(dataSource);

        //Then
        assertEquals(2, result.size());

        final LandstingEnhetFileDataRow row0 = result.get(0);
        assertEquals("SE2321000131-E000000007507", row0.getEnhetensHsaId().getId());
        assertEquals(Integer.valueOf(1072), row0.getListadePatienter());

        final LandstingEnhetFileDataRow row1 = result.get(1);
        assertEquals("SE2321000131-E000000007508", row1.getEnhetensHsaId().getId());
        assertEquals(Integer.valueOf(1073), row1.getListadePatienter());
    }

    @Test
    public void testReadExcelDataWithErrorInPatientsFieldThrowsErrorAndShowsCorrectRowNumberInMessage() throws Exception {
        //Given
        URL url = LandstingFileReader.class.getResource("/landsting-test-med-patientfel.xls");
        File file = new File(url.getPath());
        final FileDataSource dataSource = new FileDataSource(file);

        //When
        try {
            landstingFileReader.readExcelData(dataSource);
            fail();
        } catch (LandstingEnhetFileParseException e) {
            //Then
            assertTrue(e.getMessage(), e.getMessage().startsWith("Rad 2"));
        }
    }

    @Test
    public void testReadExcelDataWithErrorInHsaIdFieldThrowsErrorAndShowsCorrectRowNumberInMessage() throws Exception {
        //Given
        URL url = LandstingFileReader.class.getResource("/landsting-hsaid-felaktiga-tecken.xls");
        File file = new File(url.getPath());
        final FileDataSource dataSource = new FileDataSource(file);

        //When
        try {
            landstingFileReader.readExcelData(dataSource);
            fail();
        } catch (LandstingEnhetFileParseException e) {
            //Then
            assertTrue(e.getMessage(), e.getMessage().startsWith("Rad 3"));
        }
    }

    @Test
    public void testReadExcelDataWithNegativeNumberOfPatientsThrowsErrorAndShowsCorrectRowNumberInMessage() throws Exception {
        //Given
        URL url = LandstingFileReader.class.getResource("/landsting-negativa-patienter.xls");
        File file = new File(url.getPath());
        final FileDataSource dataSource = new FileDataSource(file);

        //When
        try {
            landstingFileReader.readExcelData(dataSource);
            fail();
        } catch (LandstingEnhetFileParseException e) {
            //Then
            assertTrue(e.getMessage(), e.getMessage().startsWith("Rad 3"));
        }
    }

}
