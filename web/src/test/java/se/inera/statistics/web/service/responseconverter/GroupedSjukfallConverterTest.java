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
package se.inera.statistics.web.service.responseconverter;

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
import se.inera.statistics.web.service.Filter;
import se.inera.statistics.web.service.FilterSettings;

import static org.junit.Assert.assertEquals;

public class GroupedSjukfallConverterTest {

    // CHECKSTYLE:OFF MagicNumber

    @Test
    public void convertTest() {
        GroupedSjukfallConverter converter = new GroupedSjukfallConverter("Vårdenhet");
        List<SimpleKonDataRow> businessRows = new ArrayList<>();
        businessRows.add(new SimpleKonDataRow("enhet1", 12, 13));
        businessRows.add(new SimpleKonDataRow("enhet2", 20, 30));
        businessRows.add(new SimpleKonDataRow("enhet3", 5, 25));
        SimpleKonResponse casesPerUnit = new SimpleKonResponse(AvailableFilters.getForSjukfall(), businessRows);
        final FilterSettings filterSettings = new FilterSettings(Filter.empty(), Range.createForLastMonthsExcludingCurrent(1, Clock.systemDefaultZone()));
        SimpleDetailsData result = converter.convert(casesPerUnit, filterSettings);
        TableData tableData = result.getTableData();
        assertEquals("[[Vårdenhet;1, Antal sjukfall totalt;1, Antal sjukfall för kvinnor;1, Antal sjukfall för män;1]]", tableData.getHeaders().toString());
        List<NamedData> rows = tableData.getRows();
        assertEquals(3, rows.size());
        assertEquals("enhet1", rows.get(0).getName());
        assertEquals("enhet2", rows.get(1).getName());
        assertEquals("enhet3", rows.get(2).getName());
        assertEquals("[25, 12, 13]", rows.get(0).getData().toString());
        assertEquals("[50, 20, 30]", rows.get(1).getData().toString());
        assertEquals("[30, 5, 25]", rows.get(2).getData().toString());
    }

    // CHECKSTYLE:ON MagicNumber

}
