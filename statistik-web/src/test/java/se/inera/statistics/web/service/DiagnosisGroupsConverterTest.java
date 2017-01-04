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
import se.inera.statistics.service.report.model.OverviewChartRowExtended;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class DiagnosisGroupsConverterTest {

    @Test
    public void testConvertList() throws Exception {
        List<OverviewChartRowExtended> rows = new ArrayList<>();
        rows.add(new OverviewChartRowExtended("180108190", 20, 0));
        rows.add(new OverviewChartRowExtended("230108230", 35, 10));
        rows.add(new OverviewChartRowExtended("300108300", 10, 0));
        rows.add(new OverviewChartRowExtended("320108320", 5, 0));
        rows.add(new OverviewChartRowExtended("360108370", 1, 0));
        rows.add(new OverviewChartRowExtended("350108350", 1, 10));
        rows.add(new OverviewChartRowExtended("430108430", 1, 0));


        List<OverviewChartRowExtended> expectedList = new ArrayList<>();
        expectedList.add(new OverviewChartRowExtended("F00-F99 Psykiska sjukdomar", 35, 40));
        expectedList.add(new OverviewChartRowExtended("A00-E90, G00-L99, N00-N99 Somatiska sjukdomar", 20, 0));
        expectedList.add(new OverviewChartRowExtended("M00-M99 Muskuloskeletala sjukdomar", 10, 0));
        expectedList.add(new OverviewChartRowExtended("O00-O99 Graviditet och förlossning", 5, 0));
        expectedList.add(new OverviewChartRowExtended(DiagnosisGroupsConverter.DIAGNOS_REST_NAME, 3, -143));

        List<OverviewChartRowExtended> convertedList = new DiagnosisGroupsConverter().convert(rows);

        assertEquals(expectedList, convertedList);
    }

}
