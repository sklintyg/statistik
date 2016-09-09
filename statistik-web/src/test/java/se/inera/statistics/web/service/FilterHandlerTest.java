/**
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
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
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import se.inera.auth.model.User;
import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.hsa.model.HsaIdUser;
import se.inera.statistics.service.landsting.LandstingEnhetHandler;
import se.inera.statistics.service.processlog.EnhetManager;
import se.inera.statistics.service.report.util.SjukfallsLangdGroup;
import se.inera.statistics.service.warehouse.Sjukfall;
import se.inera.statistics.service.warehouse.SjukfallFilter;
import se.inera.statistics.service.warehouse.SjukfallUtil;
import se.inera.statistics.web.model.LoginInfo;
import se.inera.statistics.web.model.Verksamhet;

import javax.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;

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
        final FilterData filterData = new FilterData(null, null, null, sjukskrivningslangd, null, null, true);
        Mockito.when(filterHashHandler.getFilterFromHash(filterHash)).thenReturn(filterData);
        final LoginInfo loginInfo = new LoginInfo(new HsaIdUser(""), "", Lists.newArrayList(), Lists.newArrayList());
        Mockito.when(loginServiceUtil.getLoginInfo()).thenReturn(loginInfo);
        Mockito.when(sjukfallUtil.createEnhetFilter(new HsaIdEnhet[0])).thenReturn(new SjukfallFilter(f -> true, s -> true, filterHash));

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
    public void testGetFilterSelectingNoneSjukfallLenghtsWillMakeAllLengthsMatch() throws Exception {
        //Given
        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        final String filterHash = "abc";
        final FilterData filterData = new FilterData(null, null, null, null, null, null, true);
        Mockito.when(filterHashHandler.getFilterFromHash(filterHash)).thenReturn(filterData);
        final LoginInfo loginInfo = new LoginInfo(new HsaIdUser(""), "", Lists.newArrayList(), Lists.newArrayList());
        Mockito.when(loginServiceUtil.getLoginInfo()).thenReturn(loginInfo);
        Mockito.when(sjukfallUtil.createEnhetFilter(new HsaIdEnhet[0])).thenReturn(new SjukfallFilter(f -> true, s -> true, filterHash));

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
        final FilterData filterData = new FilterData(null, null, null, sjukskrivningslangd, null, null, true);
        Mockito.when(filterHashHandler.getFilterFromHash(filterHash)).thenReturn(filterData);
        final LoginInfo loginInfo = new LoginInfo(new HsaIdUser(""), "", Lists.newArrayList(), Lists.newArrayList());
        Mockito.when(loginServiceUtil.getLoginInfo()).thenReturn(loginInfo);
        Mockito.when(sjukfallUtil.createEnhetFilter(new HsaIdEnhet[0])).thenReturn(new SjukfallFilter(f -> true, s -> true, filterHash));

        //When
        final FilterSettings filter = filterHandler.getFilter(request, filterHash, 1);

        //Then
        for (int days = 0; days < 1000; days++) {
            final Sjukfall sjukfall = Mockito.mock(Sjukfall.class);
            Mockito.when(sjukfall.getRealDays()).thenReturn(days);
            assertTrue(days + " days is not matching", filter.getFilter().getPredicate().getSjukfallFilter().apply(sjukfall));
        }
    }

}
