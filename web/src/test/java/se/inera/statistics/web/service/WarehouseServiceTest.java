/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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
package se.inera.statistics.web.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Predicate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.processlog.Enhet;
import se.inera.statistics.service.processlog.EnhetManager;
import se.inera.statistics.service.report.model.AvailableFilters;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.warehouse.Aisle;
import se.inera.statistics.service.warehouse.FilterPredicates;
import se.inera.statistics.service.warehouse.SjukfallUtil;
import se.inera.statistics.service.warehouse.Warehouse;
import se.inera.statistics.service.warehouse.query.CutoffUsage;
import se.inera.statistics.service.warehouse.query.DiagnosgruppQuery;
import se.inera.statistics.service.warehouse.query.OverviewQuery;
import se.inera.statistics.service.warehouse.query.SjukfallQuery;
import se.inera.statistics.web.util.SpyableClock;

public class WarehouseServiceTest {

    @Mock
    private Warehouse warehouse;

    @Mock
    private DiagnosgruppQuery query;

    @Mock
    private OverviewQuery overviewQuery;

    @Mock
    private SjukfallQuery sjukfallQuery;

    @Mock
    private SjukfallUtil sjukfallUtil;

    @Mock
    private EnhetManager enhetManager;

    @Spy
    private SpyableClock clock;

    @InjectMocks
    private WarehouseService warehouseService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetCasesPerEnhetIsExtendingNameWithIdIfDuplicateSTATISTIK1121() {
        //Given
        final ArrayList<SimpleKonDataRow> rows = new ArrayList<>();
        rows.add(new SimpleKonDataRow("ABC", 0, 0, 1));
        rows.add(new SimpleKonDataRow("abc", 0, 0, 2));
        rows.add(new SimpleKonDataRow("CBA", 0, 0, 3));
        final SimpleKonResponse response = new SimpleKonResponse(AvailableFilters.getForSjukfall(), rows);
        final Range range = new Range(clock);
        Mockito.when(sjukfallQuery
            .getSjukfallPerEnhet(null, null, range.getFrom(), 1, range.getNumberOfMonths(), null, CutoffUsage.DO_NOT_APPLY_CUTOFF, true))
            .thenReturn(response);

        //When
        final SimpleKonResponse casesPerEnhet = warehouseService.getCasesPerEnhet(null, null, range, null, true);

        //Then
        assertEquals(3, casesPerEnhet.getGroups().size());
        assertEquals("ABC 1", casesPerEnhet.getGroups().get(0));
        assertEquals("abc 2", casesPerEnhet.getGroups().get(1));
        assertEquals("CBA", casesPerEnhet.getGroups().get(2));
    }

    @Test
    public void testGetCasesPerEnhetRegionIsExtendingNameWithIdIfDuplicateSTATISTIK1121() {
        //Given
        final Predicate predicate = Mockito.mock(Predicate.class);
        final String testhash = "testhash";
        final FilterPredicates predicate1 = new FilterPredicates(predicate, sjukfall -> true, testhash, false);
        final ArrayList<HsaIdEnhet> enheter = new ArrayList<>();
        final ArrayList<String> diagnoser = new ArrayList<>();
        final Filter filter = new Filter(predicate1, enheter, diagnoser, null, null, testhash, null, true);
        final Range range = new Range(clock);
        final FilterSettings filterSettings = new FilterSettings(filter, range);
        final ArrayList<SimpleKonDataRow> rows = new ArrayList<>();
        rows.add(new SimpleKonDataRow("ABC", 0, 0, 1));
        rows.add(new SimpleKonDataRow("abc", 0, 0, 2));
        rows.add(new SimpleKonDataRow("CBA", 0, 0, 3));
        final SimpleKonResponse simpleKonResponse = new SimpleKonResponse(AvailableFilters.getForSjukfall(), rows);

        Mockito.doReturn(simpleKonResponse)
            .when(sjukfallQuery).getSjukfallPerEnhet(nullable(Aisle.class), eq(predicate1), eq(range.getFrom()), anyInt(),
            eq(range.getNumberOfMonths()), any(Map.class), eq(CutoffUsage.APPLY_CUTOFF_PER_SEX), eq(true));

        Mockito.doReturn(Arrays.asList(new Enhet(new HsaIdVardgivare("1"), HsaIdEnhet.empty(), "namne1", "1", "1", "", "ve1")))
            .when(enhetManager).getEnhets(enheter);

        //When
        final SimpleKonResponse result = warehouseService.getCasesPerEnhetRegion(filterSettings);

        //Then
        assertEquals(3, result.getGroups().size());
        assertEquals("ABC 1", result.getGroups().get(0));
        assertEquals("abc 2", result.getGroups().get(1));
        assertEquals("CBA", result.getGroups().get(2));
    }

    @Test
    public void testGetCasesPerPatientsPerEnhetRegionIsExtendingNameWithIdIfDuplicateSTATISTIK1121() {
        //Given
        final Predicate predicate = Mockito.mock(Predicate.class);
        final String testhash = "testhash";
        final FilterPredicates predicate1 = new FilterPredicates(predicate, sjukfall -> true, testhash, false);
        final ArrayList<HsaIdEnhet> enheter = new ArrayList<>();
        final ArrayList<String> diagnoser = new ArrayList<>();
        final Filter filter = new Filter(predicate1, enheter, diagnoser, null, null, testhash, null, true);
        final Range range = new Range(clock);
        final FilterSettings filterSettings = new FilterSettings(filter, range);
        final ArrayList<SimpleKonDataRow> rows = new ArrayList<>();
        rows.add(new SimpleKonDataRow("ABC", 0, 0, 1));
        rows.add(new SimpleKonDataRow("abc", 0, 0, 2));
        rows.add(new SimpleKonDataRow("CBA", 0, 0, 3));
        final SimpleKonResponse simpleKonResponse = new SimpleKonResponse(AvailableFilters.getForSjukfall(), rows);

        Mockito.doReturn(simpleKonResponse)
            .when(sjukfallQuery).getSjukfallPerEnhet(nullable(Aisle.class), eq(predicate1), eq(range.getFrom()), anyInt(),
            eq(range.getNumberOfMonths()), any(Map.class), eq(CutoffUsage.APPLY_CUTOFF_ON_TOTAL), eq(true));

        Mockito.doReturn(Arrays.asList(new Enhet(new HsaIdVardgivare("1"), HsaIdEnhet.empty(), "namne1", "1", "1", "", "ve1")))
            .when(enhetManager).getEnhets(enheter);

        //When
        final SimpleKonResponse result = warehouseService.getCasesPerPatientsPerEnhetRegion(filterSettings);

        //Then
        assertEquals(3, result.getGroups().size());
        assertEquals("ABC 1", result.getGroups().get(0));
        assertEquals("abc 2", result.getGroups().get(1));
        assertEquals("CBA", result.getGroups().get(2));
    }

}
