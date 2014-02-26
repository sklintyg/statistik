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
import se.inera.statistics.service.report.api.Diagnoskapitel;
import se.inera.statistics.service.report.api.Overview;
import se.inera.statistics.service.report.api.SjukfallPerManad;
import se.inera.statistics.service.report.model.Avsnitt;
import se.inera.statistics.service.report.model.Range;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;

@RunWith(MockitoJUnitRunner.class)
public class ChartDataServiceTest {

    @Mock
    private Overview overviewMock = Mockito.mock(Overview.class);

    @Mock
    private SjukfallPerManad sjukfallPerManadMock = Mockito.mock(SjukfallPerManad.class);

    @Mock
    private se.inera.statistics.service.report.api.Diagnosgrupp diagnosgruppMock = Mockito.mock(se.inera.statistics.service.report.api.Diagnosgrupp.class);

    @Mock
    private Diagnoskapitel diagnoskapitelMock = Mockito.mock(Diagnoskapitel.class);

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
        Mockito.verify(sjukfallPerManadMock).getCasesPerMonth(anyString(), any(Range.class));
    }

    @Test
    public void getDiagnosisGroupsTest() {
        List<Avsnitt> avsnitts = chartDataService.getDiagnoskapitel();
        assertEquals(22, avsnitts.size());
        assertTrue(avsnitts.toString().contains("{\"Avsnitt\":{\"id\":\"E00-E90\", \"name\":\"Endokrina sjukdomar, nutritionsrubbningar och ämnesomsättningssjukdomar\", \"firstId\":\"E00\", \"lastId\":\"E90\"}}"));
    }

    @Test
    public void getDiagnosisGroupStatisticsTest() {
        try {
            chartDataService.getDiagnoskapitelstatistik();
        } catch (NullPointerException e) {
        }
        Mockito.verify(diagnosgruppMock).getDiagnosisGroups(anyString(), any(Range.class));
    }

    @Test
    public void getDiagnosisSubGroupStatisticsTest() {
        try {
            chartDataService.getDiagnosavsnittstatistik("testId");
        } catch (NullPointerException e) {
        }
        Mockito.verify(diagnoskapitelMock).getDiagnosisGroups(anyString(), any(Range.class), eq("testId"));
    }

    // CHECKSTYLE:ON MagicNumber
    // CHECKSTYLE:ON EmptyBlock

}
