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
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import se.inera.statistics.integration.hsa.model.HsaIdEnhet;
import se.inera.statistics.service.report.model.AvailableFilters;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.web.model.ChartCategory;
import se.inera.statistics.web.model.ChartData;
import se.inera.statistics.web.model.NamedData;
import se.inera.statistics.web.model.SimpleDetailsData;
import se.inera.statistics.web.model.TableData;
import se.inera.statistics.web.service.dto.Filter;
import se.inera.statistics.web.service.dto.FilterSettings;

public class GroupedSjukfallWithRegionSortingConverterTest {
    // CHECKSTYLE:OFF MagicNumber

    @Test
    public void convertTest() {
        //Given
        GroupedSjukfallWithRegionSortingConverter converter = new GroupedSjukfallWithRegionSortingConverter("Vårdenhet",
            Collections.<HsaIdEnhet>emptyList());
        List<SimpleKonDataRow> businessRows = new ArrayList<>();
        businessRows.add(new SimpleKonDataRow("enhet1", 12, 13));
        businessRows.add(new SimpleKonDataRow("enhet2", 20, 30));
        businessRows.add(new SimpleKonDataRow("enhet3", 5, 25));
        SimpleKonResponse casesPerUnit = new SimpleKonResponse(AvailableFilters.getForSjukfall(), businessRows);
        final FilterSettings filterSettings = new FilterSettings(Filter.empty(),
            Range.createForLastMonthsExcludingCurrent(1, Clock.systemDefaultZone()));

        //When
        SimpleDetailsData result = converter.convert(casesPerUnit, filterSettings);

        //Then
        //STATISTIK-1034: Table sorted by name
        TableData tableData = result.getTableData();
        assertEquals("[[Vårdenhet;1, Antal sjukfall totalt;1, Antal sjukfall för kvinnor;1, Antal sjukfall för män;1]]",
            tableData.getHeaders().toString());
        List<NamedData> tableRows = tableData.getRows();
        assertEquals(3, tableRows.size());
        assertEquals("enhet1", tableRows.get(0).getName());
        assertEquals("enhet2", tableRows.get(1).getName());
        assertEquals("enhet3", tableRows.get(2).getName());
        assertEquals("[25, 12, 13]", tableRows.get(0).getData().toString());
        assertEquals("[50, 20, 30]", tableRows.get(1).getData().toString());
        assertEquals("[30, 5, 25]", tableRows.get(2).getData().toString());

        //STATISTIK-1034: Chart sorted by highest bar
        ChartData chartData = result.getChartData();
        final List<ChartCategory> categories = chartData.getCategories();
        assertEquals(3, categories.size());
        assertEquals("enhet2", categories.get(0).getName());
        assertEquals("enhet3", categories.get(1).getName());
        assertEquals("enhet1", categories.get(2).getName());
    }

    // CHECKSTYLE:ON MagicNumber
}
