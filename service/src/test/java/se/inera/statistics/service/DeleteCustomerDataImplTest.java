/*
 * Copyright (C) 2022 Inera AB (http://www.inera.se)
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

package se.inera.statistics.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import se.inera.statistics.service.warehouse.db.DeleteCustomerDataDB;
import se.inera.statistics.service.warehouse.db.MeddelandehandelseMessagewidelineResult;

@RunWith(MockitoJUnitRunner.class)
public class DeleteCustomerDataImplTest {

    @Mock
    private DeleteCustomerDataDB deleteCustomerDataDB;

    @InjectMocks
    private DeleteCustomerDataImpl deleteCustomerData;

    @Test
    public void testDeleteCustomerDataByIntygsId() {
        List<String> intygsIdList = new ArrayList<>();
        intygsIdList.add("IntygsId1");
        intygsIdList.add("IntygsId2");
        when(deleteCustomerDataDB.deleteFromHsa(intygsIdList.get(0))).thenReturn(1);
        when(deleteCustomerDataDB.deleteFromIntygcommon(intygsIdList.get(0))).thenReturn(2);
        when(deleteCustomerDataDB.deleteFromIntyghandelse(intygsIdList.get(0))).thenReturn(3);
        when(deleteCustomerDataDB.deleteFromMeddelandehandelseAndMessagewideline(intygsIdList.get(0)))
            .thenReturn(new MeddelandehandelseMessagewidelineResult(4, 5));
        when(deleteCustomerDataDB.deleteFromWideline(intygsIdList.get(0))).thenReturn(6);
        when(deleteCustomerDataDB.deleteFromIntygsenthandelse(intygsIdList.get(0))).thenReturn(7);

        when(deleteCustomerDataDB.deleteFromHsa(intygsIdList.get(1))).thenReturn(11);
        when(deleteCustomerDataDB.deleteFromIntygcommon(intygsIdList.get(1))).thenReturn(12);
        when(deleteCustomerDataDB.deleteFromIntyghandelse(intygsIdList.get(1))).thenReturn(13);
        when(deleteCustomerDataDB.deleteFromMeddelandehandelseAndMessagewideline(intygsIdList.get(1)))
            .thenReturn(new MeddelandehandelseMessagewidelineResult(14, 15));
        when(deleteCustomerDataDB.deleteFromWideline(intygsIdList.get(1))).thenReturn(16);
        when(deleteCustomerDataDB.deleteFromIntygsenthandelse(intygsIdList.get(1))).thenReturn(17);

        List<String> result = deleteCustomerData.deleteCustomerDataByIntygsId(intygsIdList);

        verify(deleteCustomerDataDB, times(1)).deleteFromHsa(intygsIdList.get(0));
        verify(deleteCustomerDataDB, times(1)).deleteFromIntygcommon(intygsIdList.get(0));
        verify(deleteCustomerDataDB, times(1)).deleteFromIntyghandelse(intygsIdList.get(0));
        verify(deleteCustomerDataDB, times(1)).deleteFromMeddelandehandelseAndMessagewideline(intygsIdList.get(0));
        verify(deleteCustomerDataDB, times(1)).deleteFromWideline(intygsIdList.get(0));
        verify(deleteCustomerDataDB, times(1)).deleteFromIntygsenthandelse(intygsIdList.get(0));

        verify(deleteCustomerDataDB, times(1)).deleteFromHsa(intygsIdList.get(1));
        verify(deleteCustomerDataDB, times(1)).deleteFromIntygcommon(intygsIdList.get(1));
        verify(deleteCustomerDataDB, times(1)).deleteFromIntyghandelse(intygsIdList.get(1));
        verify(deleteCustomerDataDB, times(1)).deleteFromMeddelandehandelseAndMessagewideline(intygsIdList.get(1));
        verify(deleteCustomerDataDB, times(1)).deleteFromWideline(intygsIdList.get(1));
        verify(deleteCustomerDataDB, times(1)).deleteFromIntygsenthandelse(intygsIdList.get(1));

        assertEquals(result.get(0), intygsIdList.get(0));
        assertEquals(result.get(1), intygsIdList.get(1));
    }

    @Test
    public void testDeleteCustomerDataByIntygsIdExceptions() {
        List<String> intygsIdList = new ArrayList<>();
        intygsIdList.add("IntygsId1");
        intygsIdList.add("IntygsId2");
        intygsIdList.add("IntygsId3");
        intygsIdList.add("IntygsId4");
        intygsIdList.add("IntygsId5");
        intygsIdList.add("IntygsId6");
        when(deleteCustomerDataDB.deleteFromHsa(intygsIdList.get(0))).thenThrow(new RuntimeException());
        when(deleteCustomerDataDB.deleteFromIntygcommon(intygsIdList.get(1))).thenThrow(new RuntimeException());
        when(deleteCustomerDataDB.deleteFromIntyghandelse(intygsIdList.get(2))).thenThrow(new RuntimeException());
        when(deleteCustomerDataDB.deleteFromMeddelandehandelseAndMessagewideline(intygsIdList.get(3))).thenThrow(new RuntimeException());
        when(deleteCustomerDataDB.deleteFromWideline(intygsIdList.get(4))).thenThrow(new RuntimeException());
        when(deleteCustomerDataDB.deleteFromIntygsenthandelse(intygsIdList.get(5))).thenThrow(new RuntimeException());

        List<String> result = deleteCustomerData.deleteCustomerDataByIntygsId(intygsIdList);

        assertTrue(result.isEmpty());

        verify(deleteCustomerDataDB, times(1)).deleteFromHsa(intygsIdList.get(0));
        verify(deleteCustomerDataDB, times(0)).deleteFromIntygcommon(intygsIdList.get(0));
        verify(deleteCustomerDataDB, times(0)).deleteFromIntyghandelse(intygsIdList.get(0));
        verify(deleteCustomerDataDB, times(0)).deleteFromMeddelandehandelseAndMessagewideline(intygsIdList.get(0));
        verify(deleteCustomerDataDB, times(0)).deleteFromWideline(intygsIdList.get(0));
        verify(deleteCustomerDataDB, times(0)).deleteFromIntygsenthandelse(intygsIdList.get(0));

        verify(deleteCustomerDataDB, times(1)).deleteFromHsa(intygsIdList.get(1));
        verify(deleteCustomerDataDB, times(1)).deleteFromIntygcommon(intygsIdList.get(1));
        verify(deleteCustomerDataDB, times(0)).deleteFromIntyghandelse(intygsIdList.get(1));
        verify(deleteCustomerDataDB, times(0)).deleteFromMeddelandehandelseAndMessagewideline(intygsIdList.get(1));
        verify(deleteCustomerDataDB, times(0)).deleteFromWideline(intygsIdList.get(1));
        verify(deleteCustomerDataDB, times(0)).deleteFromIntygsenthandelse(intygsIdList.get(1));

        verify(deleteCustomerDataDB, times(1)).deleteFromHsa(intygsIdList.get(2));
        verify(deleteCustomerDataDB, times(1)).deleteFromIntygcommon(intygsIdList.get(2));
        verify(deleteCustomerDataDB, times(1)).deleteFromIntyghandelse(intygsIdList.get(2));
        verify(deleteCustomerDataDB, times(0)).deleteFromMeddelandehandelseAndMessagewideline(intygsIdList.get(2));
        verify(deleteCustomerDataDB, times(0)).deleteFromWideline(intygsIdList.get(2));
        verify(deleteCustomerDataDB, times(0)).deleteFromIntygsenthandelse(intygsIdList.get(2));

        verify(deleteCustomerDataDB, times(1)).deleteFromHsa(intygsIdList.get(3));
        verify(deleteCustomerDataDB, times(1)).deleteFromIntygcommon(intygsIdList.get(3));
        verify(deleteCustomerDataDB, times(1)).deleteFromIntyghandelse(intygsIdList.get(3));
        verify(deleteCustomerDataDB, times(1)).deleteFromMeddelandehandelseAndMessagewideline(intygsIdList.get(3));
        verify(deleteCustomerDataDB, times(0)).deleteFromWideline(intygsIdList.get(3));
        verify(deleteCustomerDataDB, times(0)).deleteFromIntygsenthandelse(intygsIdList.get(3));

        verify(deleteCustomerDataDB, times(1)).deleteFromHsa(intygsIdList.get(4));
        verify(deleteCustomerDataDB, times(1)).deleteFromIntygcommon(intygsIdList.get(4));
        verify(deleteCustomerDataDB, times(1)).deleteFromIntyghandelse(intygsIdList.get(4));
        verify(deleteCustomerDataDB, times(1)).deleteFromMeddelandehandelseAndMessagewideline(intygsIdList.get(4));
        verify(deleteCustomerDataDB, times(1)).deleteFromWideline(intygsIdList.get(4));
        verify(deleteCustomerDataDB, times(0)).deleteFromIntygsenthandelse(intygsIdList.get(4));

        verify(deleteCustomerDataDB, times(1)).deleteFromHsa(intygsIdList.get(5));
        verify(deleteCustomerDataDB, times(1)).deleteFromIntygcommon(intygsIdList.get(5));
        verify(deleteCustomerDataDB, times(1)).deleteFromIntyghandelse(intygsIdList.get(5));
        verify(deleteCustomerDataDB, times(1)).deleteFromMeddelandehandelseAndMessagewideline(intygsIdList.get(5));
        verify(deleteCustomerDataDB, times(1)).deleteFromWideline(intygsIdList.get(5));
        verify(deleteCustomerDataDB, times(1)).deleteFromIntygsenthandelse(intygsIdList.get(5));
    }

    @Test
    public void testDeleteCustomerDataByVardgivarId() {
        List<String> vardgivarIdList = new ArrayList<>();
        vardgivarIdList.add("VardgivarId1");
        vardgivarIdList.add("VardgivarId2");

        when(deleteCustomerDataDB.deleteFromLakare(vardgivarIdList.get(0))).thenReturn(1);
        when(deleteCustomerDataDB.deleteFromLakare(vardgivarIdList.get(1))).thenReturn(2);

        List<String> result = deleteCustomerData.deleteCustomerDataByVardgivarId(vardgivarIdList);

        assertEquals(result.get(0), vardgivarIdList.get(0));
        assertEquals(result.get(1), vardgivarIdList.get(1));

        verify(deleteCustomerDataDB, times(1)).deleteFromLakare(vardgivarIdList.get(0));
        verify(deleteCustomerDataDB, times(1)).deleteFromLakare(vardgivarIdList.get(1));
    }

    @Test
    public void testDeleteCustomerDataByVardgivarIdExceptions() {
        List<String> vardgivarIdList = new ArrayList<>();
        vardgivarIdList.add("VardgivarId1");
        vardgivarIdList.add("VardgivarId2");

        when(deleteCustomerDataDB.deleteFromLakare(vardgivarIdList.get(0))).thenThrow(new RuntimeException());
        when(deleteCustomerDataDB.deleteFromEnhet(vardgivarIdList.get(1))).thenThrow(new RuntimeException());

        List<String> result = deleteCustomerData.deleteCustomerDataByVardgivarId(vardgivarIdList);

        assertTrue(result.isEmpty());

        verify(deleteCustomerDataDB, times(1)).deleteFromLakare(vardgivarIdList.get(0));
        verify(deleteCustomerDataDB, times(0)).deleteFromEnhet(vardgivarIdList.get(0));

        verify(deleteCustomerDataDB, times(1)).deleteFromLakare(vardgivarIdList.get(1));
        verify(deleteCustomerDataDB, times(1)).deleteFromEnhet(vardgivarIdList.get(1));
    }
}