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
package se.inera.statistics.web.service.responseconverter;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import se.inera.statistics.service.report.model.KonDataRow;
import se.inera.statistics.service.report.model.KonField;
import se.inera.statistics.web.model.NamedData;

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
        List<Object> rows = ServiceUtil.getMergedSexData(row);

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
