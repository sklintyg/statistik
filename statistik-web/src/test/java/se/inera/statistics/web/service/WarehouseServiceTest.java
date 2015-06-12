/**
 * Copyright (C) 2015 Inera AB (http://www.inera.se)
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

import com.google.common.base.Predicate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import se.inera.statistics.service.processlog.Enhet;
import se.inera.statistics.service.processlog.EnhetManager;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.warehouse.Aisle;
import se.inera.statistics.service.warehouse.SjukfallFilter;
import se.inera.statistics.service.warehouse.SjukfallUtil;
import se.inera.statistics.service.warehouse.Warehouse;
import se.inera.statistics.service.warehouse.query.CutoffUsage;
import se.inera.statistics.service.warehouse.query.DiagnosgruppQuery;
import se.inera.statistics.service.warehouse.query.OverviewQuery;
import se.inera.statistics.service.warehouse.query.SjukfallQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;

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

    @InjectMocks
    private WarehouseService warehouseService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetCasesPerEnhetIsExtendingNameWithIdIfDuplicateSTATISTIK1121() throws Exception {
        //Given
        final ArrayList<SimpleKonDataRow> rows = new ArrayList<>();
        rows.add(new SimpleKonDataRow("ABC", 0, 0, 1));
        rows.add(new SimpleKonDataRow("abc", 0, 0, 2));
        rows.add(new SimpleKonDataRow("CBA", 0, 0, 3));
        final SimpleKonResponse<SimpleKonDataRow> response = new SimpleKonResponse<>(rows);
        final Range range = new Range();
        Mockito.when(sjukfallQuery.getSjukfallPerEnhet(null, null, range.getFrom(), 1, range.getMonths(), null, CutoffUsage.DO_NOT_APPLY_CUTOFF)).thenReturn(response);

        //When
        final SimpleKonResponse<SimpleKonDataRow> casesPerEnhet = warehouseService.getCasesPerEnhet(null, null, range, null);

        //Then
        assertEquals(3, casesPerEnhet.getGroups().size());
        assertEquals("ABC 1", casesPerEnhet.getGroups().get(0));
        assertEquals("abc 2", casesPerEnhet.getGroups().get(1));
        assertEquals("CBA", casesPerEnhet.getGroups().get(2));
    }

    @Test
    public void testGetCasesPerEnhetLandstingIsExtendingNameWithIdIfDuplicateSTATISTIK1121() throws Exception {
        //Given
        final Predicate predicate = Mockito.mock(Predicate.class);
        final SjukfallFilter predicate1 = new SjukfallFilter(predicate, "testhash");
        final ArrayList<String> enheter = new ArrayList<>();
        final ArrayList<String> diagnoser = new ArrayList<>();
        final Filter filter = new Filter(predicate1, enheter, diagnoser);
        final Range range = new Range();
        final FilterSettings filterSettings = new FilterSettings(filter, range);
        final ArrayList<SimpleKonDataRow> rows = new ArrayList<>();
        rows.add(new SimpleKonDataRow("ABC", 0, 0, 1));
        rows.add(new SimpleKonDataRow("abc", 0, 0, 2));
        rows.add(new SimpleKonDataRow("CBA", 0, 0, 3));
        final SimpleKonResponse<SimpleKonDataRow> simpleKonResponse = new SimpleKonResponse<>(rows);
        Mockito.when(sjukfallQuery.getSjukfallPerEnhet(any(Aisle.class), eq(predicate1), eq(range.getFrom()), anyInt(), eq(range.getMonths()), any(Map.class), eq(CutoffUsage.APPLY_CUTOFF_PER_SEX))).thenReturn(simpleKonResponse);
        Mockito.when(enhetManager.getEnhets(enheter)).thenReturn(Arrays.asList(new Enhet("1", "vg1", "e1", "namne1", "1", "1", "")));

        //When
        final SimpleKonResponse<SimpleKonDataRow> result = warehouseService.getCasesPerEnhetLandsting(filterSettings);

        //Then
        assertEquals(3, result.getGroups().size());
        assertEquals("ABC 1", result.getGroups().get(0));
        assertEquals("abc 2", result.getGroups().get(1));
        assertEquals("CBA", result.getGroups().get(2));
    }

    @Test
    public void testGetCasesPerPatientsPerEnhetLandstingIsExtendingNameWithIdIfDuplicateSTATISTIK1121() throws Exception {
        //Given
        final Predicate predicate = Mockito.mock(Predicate.class);
        final SjukfallFilter predicate1 = new SjukfallFilter(predicate, "testhash");
        final ArrayList<String> enheter = new ArrayList<>();
        final ArrayList<String> diagnoser = new ArrayList<>();
        final Filter filter = new Filter(predicate1, enheter, diagnoser);
        final Range range = new Range();
        final FilterSettings filterSettings = new FilterSettings(filter, range);
        final ArrayList<SimpleKonDataRow> rows = new ArrayList<>();
        rows.add(new SimpleKonDataRow("ABC", 0, 0, 1));
        rows.add(new SimpleKonDataRow("abc", 0, 0, 2));
        rows.add(new SimpleKonDataRow("CBA", 0, 0, 3));
        final SimpleKonResponse<SimpleKonDataRow> simpleKonResponse = new SimpleKonResponse<>(rows);
        Mockito.when(sjukfallQuery.getSjukfallPerEnhet(any(Aisle.class), eq(predicate1), eq(range.getFrom()), anyInt(), eq(range.getMonths()), any(Map.class), eq(CutoffUsage.APPLY_CUTOFF_ON_TOTAL))).thenReturn(simpleKonResponse);
        Mockito.when(enhetManager.getEnhets(enheter)).thenReturn(Arrays.asList(new Enhet("1", "vg1", "e1", "namne1", "1", "1", "")));

        //When
        final SimpleKonResponse<SimpleKonDataRow> result = warehouseService.getCasesPerPatientsPerEnhetLandsting(filterSettings);

        //Then
        assertEquals(3, result.getGroups().size());
        assertEquals("ABC 1", result.getGroups().get(0));
        assertEquals("abc 2", result.getGroups().get(1));
        assertEquals("CBA", result.getGroups().get(2));
    }

}
