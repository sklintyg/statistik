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

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import se.inera.statistics.service.report.model.OverviewChartRowExtended;
import se.inera.statistics.service.report.util.DiagnosisGroup;

public class DiagnosisGroupsConverterTest {

    @Test
    public void testConvertList() throws Exception {
        List<OverviewChartRowExtended> rows = new ArrayList<>();
        rows.add(new OverviewChartRowExtended("180108190", 20, 0, null));
        rows.add(new OverviewChartRowExtended("230108230", 35, 10, null));
        rows.add(new OverviewChartRowExtended("300108300", 10, 0, null));
        rows.add(new OverviewChartRowExtended("320108320", 5, 0, null));
        rows.add(new OverviewChartRowExtended("360108370", 1, 0, null));
        rows.add(new OverviewChartRowExtended("350108350", 1, 10, null));
        rows.add(new OverviewChartRowExtended("430108430", 1, 0, null));

        List<OverviewChartRowExtended> expectedList = new ArrayList<>();
        expectedList.add(new OverviewChartRowExtended(DiagnosisGroup.A00_B99.getName(), 20, 0, DiagnosisGroup.A00_B99.getColor()));
        expectedList.add(new OverviewChartRowExtended(DiagnosisGroup.F00_F99.getName(), 35, 40, DiagnosisGroup.F00_F99.getColor()));
        expectedList.add(new OverviewChartRowExtended(DiagnosisGroup.M00_M99.getName(), 10, 0, DiagnosisGroup.M00_M99.getColor()));
        expectedList.add(new OverviewChartRowExtended(DiagnosisGroup.O00_O99.getName(), 5, 0, DiagnosisGroup.O00_O99.getColor()));
        expectedList.add(new OverviewChartRowExtended(DiagnosisGroup.P00_P96.getName(), 1, 0, DiagnosisGroup.P00_P96.getColor()));
        expectedList.add(
            new OverviewChartRowExtended(DiagnosisGroupsConverter.DIAGNOS_REST_NAME, 2, -125, DiagnosisGroupsConverter.DIAGNOS_REST_COLOR));

        List<OverviewChartRowExtended> convertedList = new DiagnosisGroupsConverter().convert(rows);

        assertEquals(expectedList, convertedList);
    }

}
