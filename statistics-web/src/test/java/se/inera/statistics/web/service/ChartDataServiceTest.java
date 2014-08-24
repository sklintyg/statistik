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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import se.inera.statistics.service.report.model.Avsnitt;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.util.Icd10;
import se.inera.statistics.service.warehouse.NationellData;
import se.inera.statistics.service.warehouse.NationellOverviewData;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

@RunWith(MockitoJUnitRunner.class)
public class ChartDataServiceTest {

    @Mock
    private NationellOverviewData overviewMock = Mockito.mock(NationellOverviewData.class);

    @Mock
    private NationellData nationellData = Mockito.mock(NationellData.class);

    @Mock
    private Icd10 icd10;

    @InjectMocks
    private ChartDataService chartDataService = new ChartDataService();

    // CHECKSTYLE:OFF EmptyBlock
    // CHECKSTYLE:OFF MagicNumber

    @Test
    public void getOverviewDataTest() {
        try {
            chartDataService.getOverviewData();
        } catch (NullPointerException e) {
        }
        Mockito.verify(overviewMock).getOverview(any(Range.class));
    }

    @Test
    public void getNumberOfCasesPerMonthTest() {
        try {
            chartDataService.getNumberOfCasesPerMonth();
        } catch (NullPointerException e) {
        }
        Mockito.verify(nationellData).getCasesPerMonth(any(Range.class));
    }

    @Test
    public void getDiagnosisGroupsTest() {
        Mockito.when(icd10.getKapitel()).thenReturn(Arrays.asList(new Icd10.Kapitel("A00-B99", "Vissa infektionssjukdomar och parasitsjukdomar"), new Icd10.Kapitel("C00-D48", "Tumörer")));
        List<ChartDataService.Kapitel> kapitel = chartDataService.getDiagnoskapitel();
        assertEquals(2, kapitel.size());
    }

    @Test
    public void getDiagnosisGroupStatisticsTest() {
        try {
            chartDataService.getDiagnoskapitelstatistik();
        } catch (NullPointerException e) {
        }
        Mockito.verify(nationellData).getDiagnosgrupper(any(Range.class));
    }

    @Test
    public void getDiagnosisSubGroupStatisticsTest() {
        try {
            chartDataService.getDiagnosavsnittstatistik("testId");
        } catch (NullPointerException e) {
        }
        Mockito.verify(nationellData).getDiagnosavsnitt(any(Range.class), eq("testId"));
    }

    // CHECKSTYLE:ON MagicNumber
    // CHECKSTYLE:ON EmptyBlock

}
