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
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.web.model.NamedData;
import se.inera.statistics.web.model.SimpleDetailsData;
import se.inera.statistics.web.model.TableData;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SjukfallPerSexConverterTest {

    // CHECKSTYLE:OFF MagicNumber

    @Test
    public void convertTest() {
        SjukfallPerSexConverter converter = new SjukfallPerSexConverter();
        List<SimpleKonDataRow> dualSexRows = new ArrayList<>();
        dualSexRows.add(new SimpleKonDataRow("län 1", 12, 13));
        dualSexRows.add(new SimpleKonDataRow("län 2", 20, 30));
        dualSexRows.add(new SimpleKonDataRow("län 3", 5, 25));
        SimpleKonResponse<SimpleKonDataRow> casesPerMonth = new SimpleKonResponse<SimpleKonDataRow>(dualSexRows);
        SimpleDetailsData result = converter.convert(casesPerMonth, Range.createForLastMonthsExcludingCurrent(1, Clock.systemDefaultZone()));
        TableData tableData = result.getTableData();
        assertEquals("[[Län;1, Antal sjukfall totalt;1, Andel sjukfall för kvinnor;1, Andel sjukfall för män;1]]", tableData.getHeaders().toString());
        List<NamedData> rows = tableData.getRows();
        assertEquals(3, rows.size());
        assertEquals("län 1", rows.get(0).getName());
        assertEquals("län 2", rows.get(1).getName());
        assertEquals("län 3", rows.get(2).getName());
        assertEquals("[25, 48 % (12), 52 % (13)]", rows.get(0).getData().toString());
        assertEquals("[50, 40 % (20), 60 % (30)]", rows.get(1).getData().toString());
        assertEquals("[30, 17 % (5), 83 % (25)]", rows.get(2).getData().toString());
    }

    // CHECKSTYLE:ON MagicNumber

}
