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
import static org.junit.Assert.assertNull;
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
import se.inera.statistics.service.warehouse.db.MeddelandehandelseMessagewidelineResultDao;

@RunWith(MockitoJUnitRunner.class)
public class DeleteCustomerDataImplTest{

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
            .thenReturn(new MeddelandehandelseMessagewidelineResultDao(4, 5));
        when(deleteCustomerDataDB.deleteFromWideline(intygsIdList.get(0))).thenReturn(6);
        when(deleteCustomerDataDB.deleteFromIntygsenthandelse(intygsIdList.get(0))).thenReturn(7);

        when(deleteCustomerDataDB.deleteFromHsa(intygsIdList.get(1))).thenReturn(11);
        when(deleteCustomerDataDB.deleteFromIntygcommon(intygsIdList.get(1))).thenReturn(12);
        when(deleteCustomerDataDB.deleteFromIntyghandelse(intygsIdList.get(1))).thenReturn(13);
        when(deleteCustomerDataDB.deleteFromMeddelandehandelseAndMessagewideline(intygsIdList.get(1)))
            .thenReturn(new MeddelandehandelseMessagewidelineResultDao(14, 15));
        when(deleteCustomerDataDB.deleteFromWideline(intygsIdList.get(1))).thenReturn(16);
        when(deleteCustomerDataDB.deleteFromIntygsenthandelse(intygsIdList.get(1))).thenReturn(17);

        List<DeleteCustomerDataByIntygsIdDao> result = deleteCustomerData.deleteCustomerDataByIntygsId(intygsIdList);

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

        assertEquals(result.get(0).getIntygsId(), intygsIdList.get(0));
        assertEquals(result.get(0).getRowsDeletedFromHsa().intValue(), 1);
        assertEquals(result.get(0).getRowsDeletedFromIntygcommon().intValue(), 2);
        assertEquals(result.get(0).getRowsDeletedFromIntyghandelse().intValue(), 3);
        assertEquals(result.get(0).getRowsDeletedFromMeddelandehandelse().intValue(), 4);
        assertEquals(result.get(0).getRowsDeleteMessagewideline().intValue(), 5);
        assertEquals(result.get(0).getRowsDeleteFromMideline().intValue(), 6);
        assertEquals(result.get(0).getRowsDeleteFromIntygsenthandelse().intValue(), 7);

        assertEquals(result.get(1).getIntygsId(), intygsIdList.get(1));
        assertEquals(result.get(1).getRowsDeletedFromHsa().intValue(), 11);
        assertEquals(result.get(1).getRowsDeletedFromIntygcommon().intValue(), 12);
        assertEquals(result.get(1).getRowsDeletedFromIntyghandelse().intValue(), 13);
        assertEquals(result.get(1).getRowsDeletedFromMeddelandehandelse().intValue(), 14);
        assertEquals(result.get(1).getRowsDeleteMessagewideline().intValue(), 15);
        assertEquals(result.get(1).getRowsDeleteFromMideline().intValue(), 16);
        assertEquals(result.get(1).getRowsDeleteFromIntygsenthandelse().intValue(), 17);
    }

    @Test
    public void testDeleteCustomerDataByIntygsIdIgnoreExceptions() {
        List<String> intygsIdList = new ArrayList<>();
        intygsIdList.add("IntygsId1");
        when(deleteCustomerDataDB.deleteFromHsa(intygsIdList.get(0))).thenThrow(new RuntimeException());
        when(deleteCustomerDataDB.deleteFromIntygcommon(intygsIdList.get(0))).thenThrow(new RuntimeException());
        when(deleteCustomerDataDB.deleteFromIntyghandelse(intygsIdList.get(0))).thenThrow(new RuntimeException());
        when(deleteCustomerDataDB.deleteFromMeddelandehandelseAndMessagewideline(intygsIdList.get(0))).thenThrow(new RuntimeException());
        when(deleteCustomerDataDB.deleteFromWideline(intygsIdList.get(0))).thenThrow(new RuntimeException());
        when(deleteCustomerDataDB.deleteFromIntygsenthandelse(intygsIdList.get(0))).thenThrow(new RuntimeException());

        List<DeleteCustomerDataByIntygsIdDao> result = deleteCustomerData.deleteCustomerDataByIntygsId(intygsIdList);

        verify(deleteCustomerDataDB, times(1)).deleteFromHsa(intygsIdList.get(0));
        verify(deleteCustomerDataDB, times(1)).deleteFromIntygcommon(intygsIdList.get(0));
        verify(deleteCustomerDataDB, times(1)).deleteFromIntyghandelse(intygsIdList.get(0));
        verify(deleteCustomerDataDB, times(1)).deleteFromMeddelandehandelseAndMessagewideline(intygsIdList.get(0));
        verify(deleteCustomerDataDB, times(1)).deleteFromWideline(intygsIdList.get(0));
        verify(deleteCustomerDataDB, times(1)).deleteFromIntygsenthandelse(intygsIdList.get(0));

        assertEquals(result.get(0).getIntygsId(), intygsIdList.get(0));
        assertNull(result.get(0).getRowsDeletedFromHsa());
        assertNull(result.get(0).getRowsDeletedFromIntygcommon());
        assertNull(result.get(0).getRowsDeletedFromIntyghandelse());
        assertNull(result.get(0).getRowsDeletedFromMeddelandehandelse());
        assertNull(result.get(0).getRowsDeleteMessagewideline());
        assertNull(result.get(0).getRowsDeleteFromMideline());
        assertNull(result.get(0).getRowsDeleteFromIntygsenthandelse());
    }

    @Test
    public void testDeleteCustomerDataByEnhetsId() {
        List<String> enhetsIdList = new ArrayList<>();
        enhetsIdList.add("EnhetsId1");
        enhetsIdList.add("EnhetsId2");

        when(deleteCustomerDataDB.deleteFromEnhet(enhetsIdList.get(0))).thenReturn(1);
        when(deleteCustomerDataDB.deleteFromEnhet(enhetsIdList.get(1))).thenReturn(2);

        List<DeletedEnhetDao> result = deleteCustomerData.deleteCustomerDataByEnhetsId(enhetsIdList);

        verify(deleteCustomerDataDB, times(1)).deleteFromEnhet(enhetsIdList.get(0));
        verify(deleteCustomerDataDB, times(1)).deleteFromEnhet(enhetsIdList.get(1));

        assertEquals(result.get(0).getEnhetsId(), enhetsIdList.get(0));
        assertEquals(result.get(0).getRowsDeletedFromEnhet().intValue(), 1);

        assertEquals(result.get(1).getEnhetsId(), enhetsIdList.get(1));
        assertEquals(result.get(1).getRowsDeletedFromEnhet().intValue(), 2);
    }

    @Test
    public void testDeleteCustomerDataByEnhetsIdIgnoreExceptions() {
        List<String> enhetsIdList = new ArrayList<>();
        enhetsIdList.add("EnhetsId1");

        when(deleteCustomerDataDB.deleteFromEnhet(enhetsIdList.get(0))).thenThrow(new RuntimeException());

        List<DeletedEnhetDao> result = deleteCustomerData.deleteCustomerDataByEnhetsId(enhetsIdList);

        verify(deleteCustomerDataDB, times(1)).deleteFromEnhet(enhetsIdList.get(0));

        assertEquals(result.get(0).getEnhetsId(), enhetsIdList.get(0));
        assertNull(result.get(0).getRowsDeletedFromEnhet());
    }

    @Test
    public void testDeleteCustomerDataByVardgivarId() {
        List<String> vardgivarIdList = new ArrayList<>();
        vardgivarIdList.add("VardgivarId1");
        vardgivarIdList.add("VardgivarId2");

        when(deleteCustomerDataDB.deleteFromLakare(vardgivarIdList.get(0))).thenReturn(1);
        when(deleteCustomerDataDB.deleteFromLakare(vardgivarIdList.get(1))).thenReturn(2);

        List<DeletedVardgivare> result = deleteCustomerData.deleteCustomerDataByVardgivarId(vardgivarIdList);

        verify(deleteCustomerDataDB, times(1)).deleteFromLakare(vardgivarIdList.get(0));
        verify(deleteCustomerDataDB, times(1)).deleteFromLakare(vardgivarIdList.get(1));

        assertEquals(result.get(0).getVardgivareId(), vardgivarIdList.get(0));
        assertEquals(result.get(0).getRowsDeletedFromLakare().intValue(), 1);

        assertEquals(result.get(1).getVardgivareId(), vardgivarIdList.get(1));
        assertEquals(result.get(1).getRowsDeletedFromLakare().intValue(), 2);
    }

    @Test
    public void testDeleteCustomerDataByVardgivarIdIgnoreExceptions() {
        List<String> vardgivarIdList = new ArrayList<>();
        vardgivarIdList.add("VardgivarId1");

        when(deleteCustomerDataDB.deleteFromLakare(vardgivarIdList.get(0))).thenThrow(new RuntimeException());

        List<DeletedVardgivare> result = deleteCustomerData.deleteCustomerDataByVardgivarId(vardgivarIdList);

        verify(deleteCustomerDataDB, times(1)).deleteFromLakare(vardgivarIdList.get(0));

        assertEquals(result.get(0).getVardgivareId(), vardgivarIdList.get(0));
        assertNull(result.get(0).getRowsDeletedFromLakare());
    }
}