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
package se.inera.testsupport.fkrapport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import se.inera.statistics.integration.hsa.model.HsaIdEnhet;
import se.inera.statistics.integration.hsa.model.HsaIdLakare;
import se.inera.statistics.integration.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.util.Icd10;
import se.inera.statistics.service.warehouse.Aisle;
import se.inera.statistics.service.warehouse.Fact;

/**
 * Created by marced on 2016-11-08.
 */
public class FkReportCreatorTest {

    @Mock
    private Icd10 icd10 = new Icd10();

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);

        final Icd10.Kapitel kapitel = new Icd10.Kapitel("F00-F99", "a");
        final Icd10.Avsnitt avsnitt = new Icd10.Avsnitt("F00-F99", "aa", kapitel);
        final Icd10.Kategori kategoriF32 = new Icd10.Kategori("F32", "aaa", avsnitt);
        final Icd10.Kategori kategoriF33 = new Icd10.Kategori("F33", "aaa", avsnitt);

        when(icd10.getKategori(eq("F32"))).thenReturn(kategoriF32);
        when(icd10.getKategori(eq("F33"))).thenReturn(kategoriF33);

    }

    @Test
    public void testDistributeFactRows() throws Exception {
        // Arrange
        final Clock clock = Clock.fixed(Instant.parse("2016-05-11T10:15:30.00Z"), ZoneId.systemDefault());
        final Iterator<Aisle> aisles = Collections.emptyIterator();

        final FkReportCreator fkReportCreator = new FkReportCreator(aisles, new Icd10(), null, clock);

        List<FkFactRow> fkFactRows = new ArrayList<>();

        // Should not be used (no females specified in resultrows
        fkFactRows.add(new FkFactRow("F32", Kon.FEMALE, "10", 991));

        // should match "Alla", "F32" and "F32+"
        fkFactRows.add(new FkFactRow("F32", Kon.MALE, "10", 2));

        // should match "Alla", "F32+" and "F320"
        fkFactRows.add(new FkFactRow("F320", Kon.MALE, "10", 3));

        // should match "Alla", "F32+" and "F321"
        fkFactRows.add(new FkFactRow("F321", Kon.MALE, "10", 4));

        // should match "Alla", "F32+"
        fkFactRows.add(new FkFactRow("F3273", Kon.MALE, "10", 5));

        // should only match "Alla" (not F33 with lansId 17)
        fkFactRows.add(new FkFactRow("F33", Kon.MALE, "10", 10));

        // should match "Alla" since it's a unknown landId
        fkFactRows.add(new FkFactRow("F33", Kon.MALE, "17", 992));

        List<FkReportDataRow> fkReportDataRows = new ArrayList<>();
        fkReportDataRows.add(new FkReportDataRow("Alla", "(.*)", Kon.MALE, "10"));
        fkReportDataRows.add(new FkReportDataRow("F32+", "F32(.*)", Kon.MALE, "10"));
        fkReportDataRows.add(new FkReportDataRow("F320", "F320", Kon.MALE, "10"));
        fkReportDataRows.add(new FkReportDataRow("F321", "F321", Kon.MALE, "10"));
        fkReportDataRows.add(new FkReportDataRow("F33", "F33", Kon.MALE, "17"));
        fkReportDataRows.add(new FkReportDataRow("F34", "F34", Kon.MALE, "10"));

        // Act
        final List<FkReportDataRow> result = fkReportCreator.distributeFactRows(fkFactRows, fkReportDataRows);

        // Assert
        assertEquals(6, result.size());
        assertEquals("Alla", result.get(0).getDiagnos());
        assertEquals(5, result.get(0).getAntal());
        assertEquals(4.8, result.get(0).getMedel());
        assertEquals(4.0, result.get(0).getMedian());

        assertEquals("F32+", result.get(1).getDiagnos());
        assertEquals(4, result.get(1).getAntal());
        assertEquals(3.5, result.get(1).getMedel());
        assertEquals(3.5, result.get(1).getMedian());

        assertEquals("F320", result.get(2).getDiagnos());
        assertEquals(1, result.get(2).getAntal());
        assertEquals(3.0, result.get(2).getMedel());
        assertEquals(3.0, result.get(2).getMedian());

        assertEquals("F321", result.get(3).getDiagnos());
        assertEquals(1, result.get(3).getAntal());
        assertEquals(4.0, result.get(3).getMedel());
        assertEquals(4.0, result.get(3).getMedian());

        assertEquals("F33", result.get(4).getDiagnos());
        assertEquals(1, result.get(4).getAntal());
        assertEquals(992.0, result.get(4).getMedel());
        assertEquals(992.0, result.get(4).getMedian());

        assertEquals("F34", result.get(5).getDiagnos());
        assertEquals(0, result.get(5).getAntal());
        assertEquals(0.0, result.get(5).getMedel());
        assertEquals(0.0, result.get(5).getMedian());

    }

    @Test
    public void testCreateResultRowsForDiagnoses() throws Exception {
        // Arrange
        final Clock clock = Clock.fixed(Instant.parse("2016-05-11T10:15:30.00Z"), ZoneId.systemDefault());
        final Iterator<Aisle> aisles = Collections.emptyIterator();

        List<String> codes = Arrays.asList("F32", "F33");
        final FkReportCreator fkReportCreator = new FkReportCreator(aisles, icd10, codes, clock);

        final List<FkReportDataRow> rows = fkReportCreator.createResultRowsForDiagnoses();

        int nrLan = 22;
        int nrKon = 3;
        int rowsPerCode = nrKon * nrLan;

        int expectedNrRows = (1 * nrLan * nrKon) + (codes.size() * 2 * rowsPerCode);

        // Assert
        assertEquals(expectedNrRows, rows.size());
        assertEquals("Alla", rows.get(0).getDiagnos());
        assertEquals("F32", rows.get(rowsPerCode).getDiagnos());
        assertEquals("F33+", rows.get(expectedNrRows - 1).getDiagnos());
    }

    @Test
    public void testGetRealDaysForIntyg() throws Exception {
        //Given
        final ArrayList<Fact> facts = new ArrayList<>();
        facts.add(createFact(10, 15, 10));
        facts.add(createFact(20, 25, 15));
        facts.add(createFact(30, 35, 10));
        facts.add(createFact(40, 45, 60));

        //When
        final Aisle aisle = new Aisle(new HsaIdVardgivare("vg1"), facts);
        final int realDaysForIntyg = FkReportCreator.getRealDaysForIntyg(10, aisle);

        //Then
        assertEquals(12, realDaysForIntyg);
    }

    private Fact createFact(int startdatum, int slutdatum, int lakarintyg) {
        return new Fact(1L, 1, 1, 1, new HsaIdEnhet("1"), new HsaIdEnhet("1"), lakarintyg, 1, startdatum, slutdatum, 1, 1, 1, 1, 1, 1, 1, 1,
            1, new int[0],
            new HsaIdLakare("1"));
    }

}