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
package se.inera.statistics.service.warehouse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import se.inera.statistics.service.countypopulation.CountyPopulation;
import se.inera.statistics.service.countypopulation.CountyPopulationManager;
import se.inera.statistics.service.report.model.DiagnosgruppResponse;
import se.inera.statistics.service.report.model.Icd;
import se.inera.statistics.service.report.model.KonDataRow;
import se.inera.statistics.service.report.model.KonField;
import se.inera.statistics.service.report.model.OverviewChartRowExtended;
import se.inera.statistics.service.report.model.OverviewResponse;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonResponse;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class NationellOverviewDataTest {

    @Mock
    private CountyPopulationManager countyPopulationManager;

    @InjectMocks
    private NationellOverviewData nationellOverviewData;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getOverviewUnknownDxGroupIncludedInCurrentButNotInPreviousShouldBeHandledCorrectly() {
        //Set up environment
        final CountyPopulation countyPopulation = new CountyPopulation(Collections.emptyMap(), LocalDate.now());
        Mockito.when(countyPopulationManager.getCountyPopulation(Matchers.any(Range.class))).thenReturn(countyPopulation);
        final NationellDataInfo data = new NationellDataInfo();
        data.setOverviewGenderResult(new SimpleKonResponse(null, Collections.emptyList()));
        data.setOverviewForandringResult(new SimpleKonResponse(null, Collections.emptyList()));
        data.setOverviewLanPreviousResult(new SimpleKonResponse(null, Collections.emptyList()));
        data.setOverviewLanCurrentResult(new SimpleKonResponse(null, Collections.emptyList()));

        //Given
        final KonField konField = new KonField(0, 0);
        final KonDataRow row1 = new KonDataRow("rowname1", Arrays.asList(konField));
        final KonDataRow row2 = new KonDataRow("rowname2", Arrays.asList(konField, konField));
        final List<Icd> icdTyps = Arrays.asList(new Icd("id1", "name1", 1), new Icd("id2", "name2", 2));
        data.setOverviewDiagnosgrupperResult(new DiagnosgruppResponse(null, icdTyps, Arrays.asList(row1, row2)));

        //When
        final OverviewResponse overview = nationellOverviewData.getOverview(data);

        //Then
        final List<OverviewChartRowExtended> diagnosisGroups = overview.getDiagnosisGroups();
        assertEquals(2, diagnosisGroups.size());
    }

}