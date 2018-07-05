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
package se.inera.statistics.web.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import se.inera.statistics.service.user.UserSelection;
import se.inera.statistics.service.user.UserSelectionManager;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;

@RunWith(MockitoJUnitRunner.class)
public class FilterHashHandlerTest {

    @Mock
    private UserSelectionManager userSelectionManager;

    @InjectMocks
    private FilterHashHandler handler = new FilterHashHandler();

    @Test
    public void willSkipSaveOnNonJsonData() throws Exception {
        try {
            handler.getHash("not json");
            fail();
        } catch (Exception e) {
            //Do nothing
        }
        Mockito.verify(userSelectionManager, never()).register(anyString(), anyString());
    }

    @Test
    public void willSaveOnJsonData() throws Exception {
        String hash = handler.getHash("{}");

        Mockito.verify(userSelectionManager, atLeastOnce()).register(anyString(), anyString());

        assertEquals("99914b932bd37a50b983c5e7c90ae93b", hash);
    }

    @Test
    public void testGetFilterFromHash() throws Exception {
        //Given
        Mockito.when(userSelectionManager.find(anyString())).thenReturn(new UserSelection("mykey", "{\"fromDate\": 1234}"));

        //When
        final FilterData filterFromHash = handler.getFilterFromHash("");

        //Then
        assertEquals("1234", filterFromHash.getFromDate());
        assertEquals(null, filterFromHash.getToDate());
    }

    @Test (expected = RuntimeException.class)
    public void testGetFilterFromHashUnparsableData() throws Exception {
        //Given
        Mockito.when(userSelectionManager.find(anyString())).thenReturn(new UserSelection("mykey", "UnparsableData"));

        //When
        final FilterData filterFromHash = handler.getFilterFromHash("");

        //Then Exception is thrown
    }

    @Test (expected = RuntimeException.class)
    public void testGetFilterFromHashDataNotFound() throws Exception {
        //Given
        Mockito.when(userSelectionManager.find(anyString())).thenReturn(null);

        //When
        final FilterData filterFromHash = handler.getFilterFromHash("");

        //Then Exception is thrown
    }

}
