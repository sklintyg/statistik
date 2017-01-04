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

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.hsa.model.HsaIdUser;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.landsting.LandstingEnhetHandler;
import se.inera.statistics.service.processlog.EnhetManager;
import se.inera.statistics.service.report.util.AgeGroup;
import se.inera.statistics.service.report.util.SjukfallsLangdGroup;
import se.inera.statistics.service.warehouse.Fact;
import se.inera.statistics.service.warehouse.FilterPredicates;
import se.inera.statistics.service.warehouse.Sjukfall;
import se.inera.statistics.service.warehouse.SjukfallUtil;
import se.inera.statistics.web.model.LoginInfo;
import se.inera.statistics.web.model.Verksamhet;
import se.inera.statistics.web.util.SpyableClock;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class FilterHandlerTest {

    @Mock
    private LoginServiceUtil loginServiceUtil;

    @Mock
    private FilterHashHandler filterHashHandler;

    @Mock
    private SjukfallUtil sjukfallUtil;

    @Mock
    private LandstingEnhetHandler landstingEnhetHandler;

    @Mock
    private EnhetManager enhetManager;

    @Spy
    private SpyableClock clock = new SpyableClock();

    @InjectMocks
    private FilterHandler filterHandler;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetFilterSelectingAllSjukfallLenghtsWillMakeAllLengthsMatch() throws Exception {
        //Given
        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        final String filterHash = "abc";
        final List<String> sjukskrivningslangd = Arrays.stream(SjukfallsLangdGroup.values()).map(Enum::name).collect(Collectors.toList());
        final FilterData filterData = new FilterData(null, null, null, sjukskrivningslangd, null, null, null, true);
        Mockito.when(filterHashHandler.getFilterFromHash(filterHash)).thenReturn(filterData);
        final LoginInfo loginInfo = new LoginInfo(new HsaIdUser(""), "", Lists.newArrayList(), Lists.newArrayList());
        Mockito.when(loginServiceUtil.getLoginInfo()).thenReturn(loginInfo);
        Mockito.when(sjukfallUtil.createEnhetFilter(new HsaIdEnhet[0])).thenReturn(new FilterPredicates(f -> true, s -> true, filterHash, false));

        //When
        final FilterSettings filter = filterHandler.getFilter(request, filterHash, 1);

        //Then
        for (int days = 0; days < 1000; days++) {
            final Sjukfall sjukfall = Mockito.mock(Sjukfall.class);
            Mockito.when(sjukfall.getRealDays()).thenReturn(days);
            assertTrue(days + " days is not matching", filter.getFilter().getPredicate().getSjukfallFilter().apply(sjukfall));
        }
    }

    @Test
    public void testGetFilterSelectingAllAgeGroupsWillMakeAllAgesMatch() throws Exception {
        //Given
        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        final String filterHash = "abc";
        final List<String> ageGroups = Arrays.stream(AgeGroup.values()).map(Enum::name).collect(Collectors.toList());
        final FilterData filterData = new FilterData(null, null, null, null, ageGroups, null, null, true);
        Mockito.when(filterHashHandler.getFilterFromHash(filterHash)).thenReturn(filterData);
        final LoginInfo loginInfo = new LoginInfo(new HsaIdUser(""), "", Lists.newArrayList(), Lists.newArrayList());
        Mockito.when(loginServiceUtil.getLoginInfo()).thenReturn(loginInfo);
        Mockito.when(sjukfallUtil.createEnhetFilter(new HsaIdEnhet[0])).thenReturn(new FilterPredicates(f -> true, s -> true, filterHash, false));

        //When
        final FilterSettings filter = filterHandler.getFilter(request, filterHash, 1);

        //Then
        for (int days = 0; days < 200; days++) {
            final Fact fact = Mockito.mock(Fact.class);
            Mockito.when(fact.getAlder()).thenReturn(days);
            assertTrue(days + " days is not matching", filter.getFilter().getPredicate().getIntygFilter().apply(fact));
        }
    }

    @Test
    public void testGetFilterSelectingNoneSjukfallLenghtsWillMakeAllLengthsMatch() throws Exception {
        //Given
        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        final String filterHash = "abc";
        final FilterData filterData = new FilterData(null, null, null, null, null, null, null, true);
        Mockito.when(filterHashHandler.getFilterFromHash(filterHash)).thenReturn(filterData);
        final LoginInfo loginInfo = new LoginInfo(new HsaIdUser(""), "", Lists.newArrayList(), Lists.newArrayList());
        Mockito.when(loginServiceUtil.getLoginInfo()).thenReturn(loginInfo);
        Mockito.when(sjukfallUtil.createEnhetFilter(new HsaIdEnhet[0])).thenReturn(new FilterPredicates(f -> true, s -> true, filterHash, false));

        //When
        final FilterSettings filter = filterHandler.getFilter(request, filterHash, 1);

        //Then
        for (int days = 0; days < 1000; days++) {
            final Sjukfall sjukfall = Mockito.mock(Sjukfall.class);
            Mockito.when(sjukfall.getRealDays()).thenReturn(days);
            assertTrue(days + " days is not matching", filter.getFilter().getPredicate().getSjukfallFilter().apply(sjukfall));
        }
    }

    @Test
    public void testGetFilterNonExistingSjukfallsLengthWillBeIgnored() throws Exception {
        //Given
        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        final String filterHash = "abc";
        final List<String> sjukskrivningslangd = Stream.concat(Arrays.stream(SjukfallsLangdGroup.values()).map(Enum::name), Stream.of("EjGiltigLangd")).collect(Collectors.toList());
        final FilterData filterData = new FilterData(null, null, null, sjukskrivningslangd, null, null, null, true);
        Mockito.when(filterHashHandler.getFilterFromHash(filterHash)).thenReturn(filterData);
        final LoginInfo loginInfo = new LoginInfo(new HsaIdUser(""), "", Lists.newArrayList(), Lists.newArrayList());
        Mockito.when(loginServiceUtil.getLoginInfo()).thenReturn(loginInfo);
        Mockito.when(sjukfallUtil.createEnhetFilter(new HsaIdEnhet[0])).thenReturn(new FilterPredicates(f -> true, s -> true, filterHash, false));

        //When
        final FilterSettings filter = filterHandler.getFilter(request, filterHash, 1);

        //Then
        for (int days = 0; days < 1000; days++) {
            final Sjukfall sjukfall = Mockito.mock(Sjukfall.class);
            Mockito.when(sjukfall.getRealDays()).thenReturn(days);
            assertTrue(days + " days is not matching", filter.getFilter().getPredicate().getSjukfallFilter().apply(sjukfall));
        }
    }

    @Test
    public void testGetFilterDateBeforeFirstAndAfterToday() {
        //Given
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        String filterHash = "abc";
        FilterData filterData = new FilterData(null, null, null, null, null, "2013-09-01", LocalDate.now().plusMonths(2).toString(), false);
        Mockito.when(filterHashHandler.getFilterFromHash(filterHash)).thenReturn(filterData);
        LoginInfo loginInfo = new LoginInfo(new HsaIdUser(""), "", Lists.newArrayList(), Lists.newArrayList());
        Mockito.when(loginServiceUtil.getLoginInfo()).thenReturn(loginInfo);
        Mockito.when(sjukfallUtil.createEnhetFilter(new HsaIdEnhet[0])).thenReturn(new FilterPredicates(f -> true, s -> true, filterHash));

        //When
        FilterSettings filter = filterHandler.getFilter(request, filterHash, 1);

        //Then
        LocalDate expectedFromDate = LocalDate.of(2013, 10, 1);
        LocalDate expectedToDate = LocalDate.now().plusMonths(1).withDayOfMonth(1).minusDays(1);

        assertEquals(expectedFromDate, filter.getRange().getFrom());
        assertEquals(expectedToDate, filter.getRange().getTo());
        assertNotNull(filter.getMessage());
    }

}
