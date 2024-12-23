/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.statistics.service.user.UserSelection;
import se.inera.statistics.service.user.UserSelectionManager;
import se.inera.statistics.web.service.dto.FilterData;

@ExtendWith(MockitoExtension.class)
public class FilterHashHandlerTest {

    @Mock
    private UserSelectionManager userSelectionManager;

    @InjectMocks
    private FilterHashHandler handler = new FilterHashHandler();

    @Test
    public void willSkipSaveOnNonJsonData() {
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

    @Test
    public void testGetFilterFromHashUnparsableData() throws Exception {
        Mockito.when(userSelectionManager.find(anyString())).thenReturn(new UserSelection("mykey", "UnparsableData"));
        assertThrows(RuntimeException.class, () -> handler.getFilterFromHash(""));
    }

    @Test
    public void testGetFilterFromHashDataNotFound() throws Exception {
        Mockito.when(userSelectionManager.find(anyString())).thenReturn(null);
        assertThrows(RuntimeException.class, () -> handler.getFilterFromHash(""));
    }

}