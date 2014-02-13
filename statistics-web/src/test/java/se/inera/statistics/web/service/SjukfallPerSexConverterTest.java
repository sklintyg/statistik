/*
 * Copyright (C) 2013 - 2014 Inera AB (http://www.inera.se)
 *
 *     This file is part of Inera Statistics (http://code.google.com/p/inera-statistics/).
 *
 *     Inera Statistics is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Inera Statistics is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU LESSER GENERAL PUBLIC LICENSE for more details.
 *
 *     You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.inera.statistics.web.service;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.web.model.NamedData;
import se.inera.statistics.web.model.SimpleDetailsData;
import se.inera.statistics.web.model.TableData;

public class SjukfallPerSexConverterTest {

    // CHECKSTYLE:OFF MagicNumber

    @Test
    public void convertTest() {
        SjukfallPerSexConverter converter = new SjukfallPerSexConverter();
        List<SimpleKonDataRow> dualSexRows = new ArrayList<>();
        dualSexRows.add(new SimpleKonDataRow("län 1", 12, 13));
        dualSexRows.add(new SimpleKonDataRow("län 2", 20, 30));
        dualSexRows.add(new SimpleKonDataRow("län 3", 5, 25));
        SimpleKonResponse<SimpleKonDataRow> casesPerMonth = new SimpleKonResponse<SimpleKonDataRow>(dualSexRows, 2);
        SimpleDetailsData result = converter.convert(casesPerMonth, new Range(1));
        TableData tableData = result.getTableData();
        assertEquals("[[Län;1, Antal sjukfall;1, Andel sjukfall för kvinnor;1, Andel sjukfall för män;1, Summering;1]]", tableData.getHeaders().toString());
        List<NamedData> rows = tableData.getRows();
        assertEquals(4, rows.size());
        assertEquals("län 1", rows.get(0).getName());
        assertEquals("län 2", rows.get(1).getName());
        assertEquals("län 3", rows.get(2).getName());
        assertEquals("Totalt", rows.get(3).getName());
        assertEquals("[25, 48% (12), 52% (13), 25]", rows.get(0).getData().toString());
        assertEquals("[50, 40% (20), 60% (30), 75]", rows.get(1).getData().toString());
        assertEquals("[30, 17% (5), 83% (25), 105]", rows.get(2).getData().toString());
        assertEquals("[105, 37, 68, ]", rows.get(3).getData().toString());
    }

    // CHECKSTYLE:ON MagicNumber

}
