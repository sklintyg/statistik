/*
 * Copyright (C) 2023 Inera AB (http://www.inera.se)
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

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import se.inera.statistics.service.report.model.AvailableFilters;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.web.model.NamedData;
import se.inera.statistics.web.model.SimpleDetailsData;
import se.inera.statistics.web.model.TableData;
import se.inera.statistics.web.service.dto.Filter;
import se.inera.statistics.web.service.dto.FilterSettings;

public class SimpleMultiDualSexConverterTest {

    // CHECKSTYLE:OFF MagicNumber

    @Test
    public void convertTest() {
        SimpleDualSexConverter converter = new PeriodConverter();
        List<SimpleKonDataRow> dualSexRows = new ArrayList<>();
        dualSexRows.add(new SimpleKonDataRow("jan 12", 12, 13));
        dualSexRows.add(new SimpleKonDataRow("feb 12", 20, 30));
        dualSexRows.add(new SimpleKonDataRow("mar 12", 5, 25));
        SimpleKonResponse casesPerMonth = new SimpleKonResponse(AvailableFilters.getForNationell(), dualSexRows);
        final FilterSettings filterSettings = new FilterSettings(Filter.empty(),
            Range.createForLastMonthsExcludingCurrent(1, Clock.systemDefaultZone()));
        SimpleDetailsData result = converter.convert(casesPerMonth, filterSettings);
        TableData tableData = result.getTableData();
        assertEquals("[[Period;1, Antal sjukfall totalt;1, Antal sjukfall för kvinnor;1, Antal sjukfall för män;1]]",
            tableData.getHeaders().toString());
        List<NamedData> rows = tableData.getRows();
        assertEquals(3, rows.size());
        assertEquals("jan 12", rows.get(0).getName());
        assertEquals("feb 12", rows.get(1).getName());
        assertEquals("mar 12", rows.get(2).getName());
        assertEquals("[25, 12, 13]", rows.get(0).getData().toString());
        assertEquals("[50, 20, 30]", rows.get(1).getData().toString());
        assertEquals("[30, 5, 25]", rows.get(2).getData().toString());
    }

    // CHECKSTYLE:ON MagicNumber

}
