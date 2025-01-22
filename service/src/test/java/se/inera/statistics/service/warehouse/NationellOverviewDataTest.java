/*
 * Copyright (C) 2025 Inera AB (http://www.inera.se)
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

import static junit.framework.TestCase.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;

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
        when(countyPopulationManager.getCountyPopulation(nullable(Range.class))).thenReturn(countyPopulation);
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

    @Test
    public void shouldCalculateSickLeavePerThousand() {
        final var expectedSickleavePerThousand = 9000; // 9 per thousand * 1000 = 9000

        final var county = "01";
        final var countyName = "Stockholm";
        final NationellDataInfo data = getDefaultOverviewData();
        data.setOverviewLanPreviousResult(getOverviewLanResult(4500, 3500, county, countyName));
        data.setOverviewLanCurrentResult(getOverviewLanResult(5000, 4000, county, countyName));
        setupCountyPopulation(550000, 450000, county);

        final OverviewResponse overview = nationellOverviewData.getOverview(data);

        final var overviewPerCounty = overview.getPerCounty().get(0);
        assertEquals(expectedSickleavePerThousand, overviewPerCounty.getQuantity());
    }

    @Test
    public void shouldCalculateSickLeaveDifferenceInPercent() {
        final var expectedSickleaveDifference = 12; // 8 / thousand -> 9 / thousand == 12.5 percent ~ 12 percent

        final var county = "01";
        final var countyName = "Stockholm";
        final NationellDataInfo data = getDefaultOverviewData();
        data.setOverviewLanPreviousResult(getOverviewLanResult(4500, 3500, county, countyName));
        data.setOverviewLanCurrentResult(getOverviewLanResult(5000, 4000, county, countyName));
        setupCountyPopulation(550000, 450000, county);

        final OverviewResponse overview = nationellOverviewData.getOverview(data);

        final var overviewPerCounty = overview.getPerCounty().get(0);
        assertEquals(expectedSickleaveDifference, overviewPerCounty.getAlternation());
    }

    @Test
    public void shouldHandleSituationIfPopulationIsZero() {
        final var expectedSickleavePerThousand = 0; // 9 per thousand * 1000 = 9000

        final var county = "01";
        final var countyName = "Stockholm";
        final NationellDataInfo data = getDefaultOverviewData();
        data.setOverviewLanPreviousResult(getOverviewLanResult(4500, 3500, county, countyName));
        data.setOverviewLanCurrentResult(getOverviewLanResult(5000, 4000, county, countyName));
        setupCountyPopulation(0, 0, county);

        final OverviewResponse overview = nationellOverviewData.getOverview(data);

        final var overviewPerCounty = overview.getPerCounty().get(0);
        assertEquals(expectedSickleavePerThousand, overviewPerCounty.getQuantity());
    }

    @Test
    public void shouldReturnTopFiveCountiesBasedOnSickLeavesPerThousand() {
        final NationellDataInfo data = getDefaultOverviewData();
        final var previousKonDataRows = new ArrayList<SimpleKonDataRow>(10);
        previousKonDataRows.add(getLanKonRowData(6, 6, "01", "county01"));
        previousKonDataRows.add(getLanKonRowData(5, 5, "02", "county02"));
        previousKonDataRows.add(getLanKonRowData(4, 4, "03", "county03"));
        previousKonDataRows.add(getLanKonRowData(3, 3, "04", "county04"));
        previousKonDataRows.add(getLanKonRowData(2, 2, "05", "county05"));
        previousKonDataRows.add(getLanKonRowData(1, 1, "06", "county06"));

        final var currentKonDataRows = new ArrayList<SimpleKonDataRow>(10);
        currentKonDataRows.add(getLanKonRowData(1, 1, "01", "county01"));
        currentKonDataRows.add(getLanKonRowData(2, 2, "02", "county02"));
        currentKonDataRows.add(getLanKonRowData(3, 3, "03", "county03"));
        currentKonDataRows.add(getLanKonRowData(4, 4, "04", "county04"));
        currentKonDataRows.add(getLanKonRowData(5, 5, "05", "county05"));
        currentKonDataRows.add(getLanKonRowData(6, 6, "06", "county06"));

        data.setOverviewLanPreviousResult(new SimpleKonResponse(null, previousKonDataRows));
        data.setOverviewLanCurrentResult(new SimpleKonResponse(null, currentKonDataRows));
        setupCountyPopulation();

        final OverviewResponse overview = nationellOverviewData.getOverview(data);

        assertEquals(5, overview.getPerCounty().size());
        assertEquals("county06", overview.getPerCounty().get(0).getName());
        assertEquals("county05", overview.getPerCounty().get(1).getName());
        assertEquals("county04", overview.getPerCounty().get(2).getName());
        assertEquals("county03", overview.getPerCounty().get(3).getName());
        assertEquals("county02", overview.getPerCounty().get(4).getName());
    }

    private NationellDataInfo getDefaultOverviewData() {
        final NationellDataInfo data = new NationellDataInfo();
        data.setOverviewGenderResult(new SimpleKonResponse(null, Collections.emptyList()));
        data.setOverviewForandringResult(new SimpleKonResponse(null, Collections.emptyList()));
        return data;
    }

    private SimpleKonResponse getOverviewLanResult(int female, int male, String county, String rowName) {
        final var currentSimpleKonDataRow = getLanKonRowData(female, male, county, rowName);
        return new SimpleKonResponse(null, Collections.singletonList(currentSimpleKonDataRow));
    }

    private SimpleKonDataRow getLanKonRowData(int female, int male, String county, String rowName) {
        final var currentKonField = new KonField(female, male, county);
        return new SimpleKonDataRow(rowName, currentKonField, county);
    }


    private void setupCountyPopulation(int female, int male, String county) {
        final var countyPopulationMap = new HashMap<String, KonField>();
        countyPopulationMap.put(county, new KonField(female, male));
        final CountyPopulation countyPopulation = new CountyPopulation(countyPopulationMap, LocalDate.now());
        when(countyPopulationManager.getCountyPopulation(nullable(Range.class))).thenReturn(countyPopulation);
    }

    private void setupCountyPopulation() {
        final var mockMap = mock(Map.class);
        when(mockMap.get(anyString())).thenReturn(new KonField(10, 10));
        final CountyPopulation countyPopulation = new CountyPopulation(mockMap, LocalDate.now());
        when(countyPopulationManager.getCountyPopulation(nullable(Range.class))).thenReturn(countyPopulation);
    }
}
