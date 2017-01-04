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

import org.junit.Test;
import se.inera.statistics.service.report.model.KonDataRow;
import se.inera.statistics.service.report.model.KonField;
import se.inera.statistics.web.model.NamedData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ServiceUtilTest {


    @Test
    public void testGetRowSumEmpty() {
        // Arrange
        List<KonField> data = new ArrayList<>();
        KonDataRow row = new KonDataRow("Test", data);

        // Act
        int sum = ServiceUtil.getRowSum(row);

        // Assert
        assertEquals(0, sum);
    }

    @Test
    public void testGetRowSum() {
        // Arrange
        List<KonField> data = new ArrayList<>();
        data.add(new KonField(1, 5));
        data.add(new KonField(0, 0));
        data.add(new KonField(10, 0));
        KonDataRow row = new KonDataRow("Test", data);

        // Act
        int sum = ServiceUtil.getRowSum(row);

        // Assert
        assertEquals(16, sum);
    }

    @Test
    public void testGetMergedSexData() {
        // Arrange
        List<KonField> data = new ArrayList<>();
        data.add(new KonField(1, 5));
        data.add(new KonField(0, 0));
        data.add(new KonField(10, 0));
        KonDataRow row = new KonDataRow("Test", data);

        List<Integer> expected = Arrays.asList(6, 1, 5, 0, 0, 0, 10, 10, 0);

        // Act
        List<Integer> rows = ServiceUtil.getMergedSexData(row);

        // Assert
        assertEquals(expected, rows);
    }

    @Test
    public void testGetSumRow() {
        // Arrange
        List<NamedData> data = new ArrayList<>();
        data.add(new NamedData("Test", Arrays.asList(0, 1)));
        data.add(new NamedData("Test", Arrays.asList(0, 5)));
        data.add(new NamedData("Test", Arrays.asList(0, 1)));
        data.add(new NamedData("Test", Arrays.asList(0, 3)));

        List<Integer> sumData = Arrays.asList(0, 10);
        NamedData expected = new NamedData("Totalt", sumData);

        // Act
        NamedData row = ServiceUtil.getSumRow(data, true);

        // Assert
        assertEquals(expected, row);
    }
}
