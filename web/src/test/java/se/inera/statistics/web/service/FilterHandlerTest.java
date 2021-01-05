/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import com.google.common.collect.Lists;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.servlet.http.HttpServletRequest;
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
import se.inera.statistics.service.processlog.EnhetManager;
import se.inera.statistics.service.region.RegionEnhetHandler;
import se.inera.statistics.service.report.util.AgeGroup;
import se.inera.statistics.service.report.util.SjukfallsLangdGroup;
import se.inera.statistics.service.warehouse.Fact;
import se.inera.statistics.service.warehouse.FilterPredicates;
import se.inera.statistics.service.warehouse.Sjukfall;
import se.inera.statistics.service.warehouse.SjukfallUtil;
import se.inera.statistics.web.Messages;
import se.inera.statistics.web.model.LoginInfo;
import se.inera.statistics.web.model.LoginInfoVg;
import se.inera.statistics.web.model.UserSettingsDTO;
import se.inera.statistics.web.model.Verksamhet;
import se.inera.statistics.web.util.SpyableClock;

public class FilterHandlerTest {

    @Mock
    private LoginServiceUtil loginServiceUtil;

    @Mock
    private FilterHashHandler filterHashHandler;

    @Mock
    private SjukfallUtil sjukfallUtil;

    @Mock
    private RegionEnhetHandler regionEnhetHandler;

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
        // Given
        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        final String filterHash = "abc";
        final List<String> sjukskrivningslangd = Arrays.stream(SjukfallsLangdGroup.values()).map(Enum::name).collect(Collectors.toList());
        setupMocks(null, null, request, null, filterHash, null, null, sjukskrivningslangd, true, Lists.newArrayList(), null,
            Lists.newArrayList());

        // When
        final FilterSettings filter = filterHandler.getFilter(request, filterHash, 1);

        // Then
        for (int days = 0; days < 1000; days++) {
            final Sjukfall sjukfall = Mockito.mock(Sjukfall.class);
            Mockito.when(sjukfall.getRealDays()).thenReturn(days);
            assertTrue(days + " days is not matching", filter.getFilter().getPredicate().getSjukfallFilter().test(sjukfall));
        }
    }

    @Test
    public void testGetFilterSelectingAllAgeGroupsWillMakeAllAgesMatch() throws Exception {
        // Given
        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        final String filterHash = "abc";
        final List<String> ageGroups = Arrays.stream(AgeGroup.values()).map(Enum::name).collect(Collectors.toList());
        setupMocks(null, null, request, null, filterHash, null, null, null, true, Lists.newArrayList(), ageGroups, Lists.newArrayList());

        // When
        final FilterSettings filter = filterHandler.getFilter(request, filterHash, 1);

        // Then
        for (int days = 0; days < 200; days++) {
            final Fact fact = Mockito.mock(Fact.class);
            Mockito.when(fact.getAlder()).thenReturn(days);
            assertTrue(days + " days is not matching", filter.getFilter().getPredicate().getIntygFilter().test(fact));
        }
    }

    @Test
    public void testGetFilterSelectingNoneSjukfallLenghtsWillMakeAllLengthsMatch() throws Exception {
        // Given
        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        final String filterHash = "abc";
        setupMocks(null, null, request, null, filterHash, null, null, null, true, Lists.newArrayList(), null, Lists.newArrayList());

        // When
        final FilterSettings filter = filterHandler.getFilter(request, filterHash, 1);

        // Then
        for (int days = 0; days < 1000; days++) {
            final Sjukfall sjukfall = Mockito.mock(Sjukfall.class);
            Mockito.when(sjukfall.getRealDays()).thenReturn(days);
            assertTrue(days + " days is not matching", filter.getFilter().getPredicate().getSjukfallFilter().test(sjukfall));
        }
    }

    @Test
    public void testGetFilterNonExistingSjukfallsLengthWillBeIgnored() throws Exception {
        // Given
        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        final String filterHash = "abc";
        final List<String> sjukskrivningslangd = Stream
            .concat(Arrays.stream(SjukfallsLangdGroup.values()).map(Enum::name), Stream.of("EjGiltigLangd"))
            .collect(Collectors.toList());
        setupMocks(null, null, request, null, filterHash, null, null, sjukskrivningslangd, true, Lists.newArrayList(), null,
            Lists.newArrayList());

        // When
        final FilterSettings filter = filterHandler.getFilter(request, filterHash, 1);

        // Then
        for (int days = 0; days < 1000; days++) {
            final Sjukfall sjukfall = Mockito.mock(Sjukfall.class);
            Mockito.when(sjukfall.getRealDays()).thenReturn(days);
            assertTrue(days + " days is not matching", filter.getFilter().getPredicate().getSjukfallFilter().test(sjukfall));
        }
    }

    @Test
    public void testGetFilterDateBeforeFirstAndAfterToday() {
        // Given
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        String filterHash = "abc";
        setupMocks("2013-09-01", LocalDate.now().plusMonths(2).toString(), request, null, filterHash, null, null, null, false,
            Lists.newArrayList(), null, Lists.newArrayList());

        // When
        FilterSettings filter = filterHandler.getFilter(request, filterHash, 1);

        // Then
        LocalDate expectedFromDate = LocalDate.of(2013, 10, 1);
        LocalDate expectedToDate = LocalDate.now().plusMonths(1).withDayOfMonth(1).minusDays(1);

        assertEquals(expectedFromDate, filter.getRange().getFrom());
        assertEquals(expectedToDate, filter.getRange().getTo());
        assertNotNull(filter.getMessage());
    }

    @Test
    public void testGetFilterPredicatesFilterIncludesEnhetsIntyg3486() {
        // Given
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        final HsaIdVardgivare vgid = new HsaIdVardgivare("vgid");
        String filterHash = "abc1";
        final String fromDate = "2013-09-01";
        final String toDate = LocalDate.now().plusMonths(2).toString();
        final List<String> enheter = Arrays.asList("E1", "E2", "E3");
        final List<Verksamhet> businesses = Arrays.asList(createVerksamhet("E1", vgid), createVerksamhet("E2", vgid),
            createVerksamhet("E3", vgid));
        final ArrayList<LoginInfoVg> loginInfoVgs = Lists.newArrayList();

        setupMocks(fromDate, toDate, request, vgid, filterHash, null, enheter, null, false, businesses, null, loginInfoVgs);

        // When
        FilterSettings filter1 = filterHandler.getFilter(request, filterHash, 1);

        // Given
        setupFilterHashHandlerMock("2013-09-01", LocalDate.now().plusMonths(2).toString(), filterHash, null, Arrays.asList("E1", "E3"),
            null, false, null);

        // When
        FilterSettings filter2 = filterHandler.getFilter(request, filterHash, 1);

        // Then
        final String hash1 = filter1.getFilter().getPredicate().getHash();
        final String hash2 = filter2.getFilter().getPredicate().getHash();
        assertNotEquals(hash1, hash2);
    }

    @Test
    public void testGetFilterStartAndEndDateOutOfScopeIntyg4472() {
        // Given
        final String fromDate = "2013-09-01";
        final String toDate = LocalDate.now().plusMonths(2).toString();

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        final HsaIdVardgivare vgid = new HsaIdVardgivare("vgid");

        String filterHash = "abc1";
        final List<String> diagnoser = null;
        final List<String> enheter = Arrays.asList("E1", "E2", "E3");
        final List<Verksamhet> businesses = Arrays.asList(createVerksamhet("E1", vgid), createVerksamhet("E2", vgid),
            createVerksamhet("E3", vgid));
        final ArrayList<LoginInfoVg> loginInfoVgs = Lists.newArrayList();

        setupMocks(fromDate, toDate, request, vgid, filterHash, diagnoser, enheter, null, false, businesses, null, loginInfoVgs);

        // When
        FilterSettings filter1 = filterHandler.getFilter(request, filterHash, 1);

        // Then
        final String nowMonth = LocalDate.now().toString().substring(0, 7);
        assertEquals("Det finns ingen statistik innan 2013-10 och ingen efter " + nowMonth + ", visar "
            + "statistik mellan 2013-10 och " + nowMonth + ".", filter1.getMessage().getMessage());
    }

    @Test
    public void testGetFilterStartAndEndDateTooEarlyIntygfv11329() {
        // Given
        final String fromDate = "2013-08-01";
        final String toDate = "2013-09-01";

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        final HsaIdVardgivare vgid = new HsaIdVardgivare("vgid");

        String filterHash = "abc1";
        final List<String> diagnoser = null;
        final List<String> enheter = Arrays.asList("E1", "E2", "E3");
        final List<Verksamhet> businesses = Arrays.asList(createVerksamhet("E1", vgid), createVerksamhet("E2", vgid),
            createVerksamhet("E3", vgid));
        final ArrayList<LoginInfoVg> loginInfoVgs = Lists.newArrayList();

        setupMocks(fromDate, toDate, request, vgid, filterHash, diagnoser, enheter, null, false, businesses, null, loginInfoVgs);

        // When
        FilterSettings filter1 = filterHandler.getFilter(request, filterHash, 1);

        // Then
        assertEquals(Messages.ST_F_FI_004.getText(), filter1.getMessage().getMessage());
    }

    @Test
    public void testGetFilterStartAndEndDateTooLateIntygfv11329() {
        // Given
        final String fromDate = LocalDate.now().plusMonths(1).toString();
        final String toDate = LocalDate.now().plusMonths(2).toString();

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        final HsaIdVardgivare vgid = new HsaIdVardgivare("vgid");

        String filterHash = "abc1";
        final List<String> diagnoser = null;
        final List<String> enheter = Arrays.asList("E1", "E2", "E3");
        final List<Verksamhet> businesses = Arrays.asList(createVerksamhet("E1", vgid), createVerksamhet("E2", vgid),
            createVerksamhet("E3", vgid));
        final ArrayList<LoginInfoVg> loginInfoVgs = Lists.newArrayList();

        setupMocks(fromDate, toDate, request, vgid, filterHash, diagnoser, enheter, null, false, businesses, null, loginInfoVgs);

        // When
        FilterSettings filter1 = filterHandler.getFilter(request, filterHash, 1);

        // Then
        assertEquals(Messages.ST_F_FI_009.getText(), filter1.getMessage().getMessage());
    }

    private void setupMocks(String fromDate, String toDate, HttpServletRequest request, HsaIdVardgivare vgid, String filterHash,
        List<String> diagnoser, List<String> enheter, List<String> sjukskrivningslangd, boolean useDefaultPeriod,
        List<Verksamhet> businesses, List<String> aldersgrupp, ArrayList<LoginInfoVg> loginInfoVgs) {
        setupFilterHashHandlerMock(fromDate, toDate, filterHash, diagnoser, enheter, sjukskrivningslangd, useDefaultPeriod, aldersgrupp);
        setupLoginServiceMock(request, vgid, businesses, loginInfoVgs);
        setupSjukfallUtilMock(filterHash);
    }

    private void setupLoginServiceMock(HttpServletRequest request, HsaIdVardgivare vgid, List<Verksamhet> businesses,
        ArrayList<LoginInfoVg> loginInfoVgs) {
        LoginInfo loginInfo = new LoginInfo(new HsaIdUser(""), "", businesses, loginInfoVgs, new UserSettingsDTO(), "FAKE");
        Mockito.when(loginServiceUtil.getLoginInfo()).thenReturn(loginInfo);
        Mockito.when(loginServiceUtil.getSelectedVgIdForLoggedInUser(request)).thenReturn(vgid);
    }

    private void setupSjukfallUtilMock(String filterHash) {
        Mockito.when(sjukfallUtil.createEnhetFilter(any()))
            .thenReturn(new FilterPredicates(f -> true, s -> true, filterHash, false));
    }

    private void setupFilterHashHandlerMock(String fromDate, String toDate, String filterHash, List<String> diagnoser, List<String> enheter,
        List<String> sjukskrivningslangd, boolean useDefaultPeriod, List<String> aldersgrupp) {
        FilterData filterData = new FilterData(diagnoser, enheter, sjukskrivningslangd, aldersgrupp, null, fromDate, toDate,
            useDefaultPeriod);
        Mockito.when(filterHashHandler.getFilterFromHash(filterHash)).thenReturn(filterData);
    }

    private Verksamhet createVerksamhet(String hsaId, HsaIdVardgivare vgid) {
        return new Verksamhet(new HsaIdEnhet(hsaId), hsaId, vgid, "", "", "", "", "", Collections.EMPTY_SET, null);
    }

}
