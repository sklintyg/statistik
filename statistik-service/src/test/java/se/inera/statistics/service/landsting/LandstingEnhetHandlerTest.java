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
package se.inera.statistics.service.landsting;

import com.google.common.base.Optional;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import se.inera.statistics.service.landsting.persistance.landsting.Landsting;
import se.inera.statistics.service.landsting.persistance.landsting.LandstingManager;
import se.inera.statistics.service.landsting.persistance.landstingenhet.LandstingEnhet;
import se.inera.statistics.service.landsting.persistance.landstingenhet.LandstingEnhetManager;
import se.inera.statistics.service.landsting.persistance.landstingenhetupdate.LandstingEnhetUpdateManager;
import se.inera.statistics.service.landsting.persistance.landstingenhetupdate.LandstingEnhetUpdateOperation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;

public class LandstingEnhetHandlerTest {

    @InjectMocks
    private LandstingEnhetHandler landstingEnhetHandler;

    @Mock
    private LandstingManager landstingManager;

    @Mock
    private LandstingEnhetManager landstingEnhetManager;

    @Mock
    private LandstingEnhetUpdateManager landstingEnhetUpdateManager;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testUpdateWhenNoLandstingIsFoundExceptionIsThrownAndNoUpdateIsPerformed() throws Exception {
        //Given
        final LandstingEnhetFileData data = new LandstingEnhetFileData("testName", null, "", "", "");
        Mockito.when(landstingManager.getForVg(anyString())).thenReturn(Optional.<Landsting>absent());

        //When
        try {
            landstingEnhetHandler.update(data);
            fail();
        } catch (NoLandstingSetForVgException e) {
            //Expected - do nothing
        }

        //Then
        Mockito.verify(landstingEnhetManager, times(0)).update(anyLong(), anyListOf(LandstingEnhetFileDataRow.class));
        Mockito.verify(landstingEnhetUpdateManager, times(0)).update(anyLong(), anyString(), anyString(), anyString(), any(LandstingEnhetUpdateOperation.class));
    }

    @Test
    public void testUpdatesAreCalledWithCorrectParams() throws Exception {
        //Given
        final ArrayList<LandstingEnhetFileDataRow> rows = new ArrayList<>();
        final String userName = "testUserName";
        final String userId = "TestUserId";
        final String fileName = "TestFileName";
        final LandstingEnhetFileData data = new LandstingEnhetFileData("testVgId", rows, userName, userId, fileName);
        final Landsting landsting = Mockito.mock(Landsting.class);
        final Long landtingsId = 5L;
        Mockito.when(landsting.getId()).thenReturn(landtingsId);
        Mockito.when(landstingManager.getForVg(anyString())).thenReturn(Optional.of(landsting));

        //When
        landstingEnhetHandler.update(data);

        //Then
        Mockito.verify(landstingEnhetManager, times(1)).update(landtingsId, rows);
        Mockito.verify(landstingEnhetUpdateManager, times(1)).update(landtingsId, userName, userId, fileName, LandstingEnhetUpdateOperation.Update);
    }

    @Test
    public void testIllegalCharactersAreRemovedFromFilenameBeforeDatabaseUpdate() throws Exception {
        //Given
        final ArrayList<LandstingEnhetFileDataRow> rows = new ArrayList<>();
        final String fileName = "TestFile<Name.xls";
        final LandstingEnhetFileData data = new LandstingEnhetFileData("testVgId", null, "", "", fileName);
        final Landsting landsting = Mockito.mock(Landsting.class);
        final Long landtingsId = 5L;
        Mockito.when(landsting.getId()).thenReturn(landtingsId);
        Mockito.when(landstingManager.getForVg(anyString())).thenReturn(Optional.of(landsting));

        //When
        landstingEnhetHandler.update(data);

        //Then
        Mockito.verify(landstingEnhetUpdateManager, times(1)).update(anyLong(), anyString(), anyString(), eq("TestFile_Name.xls"), any(LandstingEnhetUpdateOperation.class));
    }

    @Test
    public void testGetLandstingsVardgivareStatusWhenVgidNotConnectedToAnyLandsting() throws Exception {
        //Given
        final String vgid = "testvgid";
        Mockito.when(landstingManager.getForVg(vgid)).thenReturn(Optional.<Landsting>absent());

        //When
        final LandstingsVardgivareStatus status = landstingEnhetHandler.getLandstingsVardgivareStatus(vgid);

        //Then
        assertEquals(LandstingsVardgivareStatus.NO_LANDSTINGSVARDGIVARE, status);
    }

    @Test
    public void testGetLandstingsVardgivareStatusWhenVgidIsConnectedToALandstingButHasNoLandstingsenhetsConnected() throws Exception {
        //Given
        final String vgid = "testvgid";
        final Landsting landsting = Mockito.mock(Landsting.class);
        final long landstingsid = 4L;
        Mockito.when(landsting.getId()).thenReturn(landstingsid);
        final List<LandstingEnhet> landstingEnhets = Collections.emptyList();
        Mockito.when(landstingEnhetManager.getByLandstingId(landstingsid)).thenReturn(landstingEnhets);
        Mockito.when(landstingManager.getForVg(vgid)).thenReturn(Optional.of(landsting));

        //When
        final LandstingsVardgivareStatus status = landstingEnhetHandler.getLandstingsVardgivareStatus(vgid);

        //Then
        assertEquals(LandstingsVardgivareStatus.LANDSTINGSVARDGIVARE_WITHOUT_UPLOAD, status);
    }

    @Test
    public void testGetLandstingsVardgivareStatusWhenVgidIsConnectedToALandstingAndHasLandstingsenhetsConnected() throws Exception {
        //Given
        final String vgid = "testvgid";
        final Landsting landsting = Mockito.mock(Landsting.class);
        final long landstingsid = 4L;
        Mockito.when(landsting.getId()).thenReturn(landstingsid);
        final List<LandstingEnhet> landstingEnhets = Arrays.asList(new LandstingEnhet(1L, "", 2));
        Mockito.when(landstingEnhetManager.getByLandstingId(landstingsid)).thenReturn(landstingEnhets);
        Mockito.when(landstingManager.getForVg(vgid)).thenReturn(Optional.of(landsting));

        //When
        final LandstingsVardgivareStatus status = landstingEnhetHandler.getLandstingsVardgivareStatus(vgid);

        //Then
        assertEquals(LandstingsVardgivareStatus.LANDSTINGSVARDGIVARE_WITH_UPLOAD, status);
    }

}
