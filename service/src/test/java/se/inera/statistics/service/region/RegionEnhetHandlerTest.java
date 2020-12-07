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
package se.inera.statistics.service.region;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import se.inera.statistics.integration.hsa.model.HsaIdEnhet;
import se.inera.statistics.integration.hsa.model.HsaIdUser;
import se.inera.statistics.integration.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.region.persistance.region.Region;
import se.inera.statistics.service.region.persistance.region.RegionManager;
import se.inera.statistics.service.region.persistance.regionenhet.RegionEnhet;
import se.inera.statistics.service.region.persistance.regionenhet.RegionEnhetManager;
import se.inera.statistics.service.region.persistance.regionenhetupdate.RegionEnhetUpdateManager;
import se.inera.statistics.service.region.persistance.regionenhetupdate.RegionEnhetUpdateOperation;

public class RegionEnhetHandlerTest {

    @InjectMocks
    private RegionEnhetHandler regionEnhetHandler;

    @Mock
    private RegionManager regionManager;

    @Mock
    private RegionEnhetManager regionEnhetManager;

    @Mock
    private RegionEnhetUpdateManager regionEnhetUpdateManager;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testUpdateWhenNoRegionIsFoundExceptionIsThrownAndNoUpdateIsPerformed() throws Exception {
        //Given
        final RegionEnhetFileData data = new RegionEnhetFileData(new HsaIdVardgivare("testName"), null, "", new HsaIdUser(""), "");
        Mockito.when(regionManager.getForVg(any(HsaIdVardgivare.class))).thenReturn(Optional.empty());

        //When
        try {
            regionEnhetHandler.update(data);
            fail();
        } catch (NoRegionSetForVgException e) {
            //Expected - do nothing
        }

        //Then
        Mockito.verify(regionEnhetManager, times(0)).update(anyLong(), anyList());
        Mockito.verify(regionEnhetUpdateManager, times(0))
            .update(anyLong(), anyString(), any(HsaIdUser.class), anyString(), any(RegionEnhetUpdateOperation.class));
    }

    @Test
    public void testUpdatesAreCalledWithCorrectParams() throws Exception {
        //Given
        final ArrayList<RegionEnhetFileDataRow> rows = new ArrayList<>();
        final String userName = "testUserName";
        final String fileName = "TestFileName";
        final HsaIdUser userHsaId = new HsaIdUser("TestUserId");
        final RegionEnhetFileData data = new RegionEnhetFileData(new HsaIdVardgivare("testVgId"), rows, userName, userHsaId, fileName);
        final Region region = Mockito.mock(Region.class);
        final Long landtingsId = 5L;
        Mockito.when(region.getId()).thenReturn(landtingsId);
        Mockito.when(regionManager.getForVg(any(HsaIdVardgivare.class))).thenReturn(Optional.of(region));

        //When
        regionEnhetHandler.update(data);

        //Then
        Mockito.verify(regionEnhetManager, times(1)).update(landtingsId, rows);
        Mockito.verify(regionEnhetUpdateManager, times(1))
            .update(landtingsId, userName, userHsaId, fileName, RegionEnhetUpdateOperation.UPDATE);
    }

    @Test
    public void testClear() throws Exception {
        //Given
        final ArrayList<RegionEnhetFileDataRow> rows = new ArrayList<>();
        final String userName = "testUserName";
        final HsaIdUser userId = new HsaIdUser("TestUserId");
        final String fileName = "TestFileName";
        final HsaIdVardgivare vgId = new HsaIdVardgivare("testVgId");
        final RegionEnhetFileData data = new RegionEnhetFileData(vgId, rows, userName, userId, fileName);
        final Region region = Mockito.mock(Region.class);
        final Long landtingsId = 5L;
        Mockito.when(region.getId()).thenReturn(landtingsId);
        Mockito.when(regionManager.getForVg(any(HsaIdVardgivare.class))).thenReturn(Optional.of(region));

        //When
        regionEnhetHandler.clear(vgId, userName, userId);

        //Then
        final ArgumentCaptor<List> listArgumentCaptor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(regionEnhetManager, times(1)).update(eq(landtingsId), listArgumentCaptor.capture());
        assertEquals(0, listArgumentCaptor.getValue().size());
        Mockito.verify(regionEnhetUpdateManager, times(1)).update(landtingsId, userName, userId, "-", RegionEnhetUpdateOperation.REMOVE);
    }

    @Test
    public void testIllegalCharactersAreRemovedFromFilenameBeforeDatabaseUpdate() throws Exception {
        //Given
        final ArrayList<RegionEnhetFileDataRow> rows = null;
        final String fileName = "TestFile<Name.xls";
        final RegionEnhetFileData data = new RegionEnhetFileData(new HsaIdVardgivare("testVgId"), rows, "", new HsaIdUser(""), fileName);
        final Region region = Mockito.mock(Region.class);
        final Long landtingsId = 5L;
        Mockito.when(region.getId()).thenReturn(landtingsId);
        Mockito.when(regionManager.getForVg(any(HsaIdVardgivare.class))).thenReturn(Optional.of(region));

        //When
        regionEnhetHandler.update(data);

        //Then
        Mockito.verify(regionEnhetUpdateManager, times(1))
            .update(anyLong(), anyString(), any(HsaIdUser.class), eq("TestFile_Name.xls"), any(RegionEnhetUpdateOperation.class));
    }

    @Test
    public void testGetRegionsVardgivareStatusWhenVgidNotConnectedToAnyRegion() throws Exception {
        //Given
        final HsaIdVardgivare vgid = new HsaIdVardgivare("testvgid");
        Mockito.when(regionManager.getForVg(vgid)).thenReturn(Optional.empty());

        //When
        final RegionsVardgivareStatus status = regionEnhetHandler.getRegionsVardgivareStatus(vgid);

        //Then
        assertEquals(RegionsVardgivareStatus.NO_REGIONSVARDGIVARE, status);
    }

    @Test
    public void testGetRegionsVardgivareStatusWhenVgidIsConnectedToARegionButHasNoRegionsenhetsConnected() throws Exception {
        //Given
        final HsaIdVardgivare vgid = new HsaIdVardgivare("testvgid");
        final Region region = Mockito.mock(Region.class);
        final long regionId = 4L;
        Mockito.when(region.getId()).thenReturn(regionId);
        final List<RegionEnhet> regionEnhets = Collections.emptyList();
        Mockito.when(regionEnhetManager.getByRegionId(regionId)).thenReturn(regionEnhets);
        Mockito.when(regionManager.getForVg(vgid)).thenReturn(Optional.of(region));

        //When
        final RegionsVardgivareStatus status = regionEnhetHandler.getRegionsVardgivareStatus(vgid);

        //Then
        assertEquals(RegionsVardgivareStatus.REGIONSVARDGIVARE_WITHOUT_UPLOAD, status);
    }

    @Test
    public void testGetRegionsVardgivareStatusWhenVgidIsConnectedToARegionAndHasRegionsenhetsConnected() throws Exception {
        //Given
        final HsaIdVardgivare vgid = new HsaIdVardgivare("testvgid");
        final Region region = Mockito.mock(Region.class);
        final long regionId = 4L;
        Mockito.when(region.getId()).thenReturn(regionId);
        final List<RegionEnhet> regionEnhets = Collections.singletonList(new RegionEnhet(1L, new HsaIdEnhet(""), 2));
        Mockito.when(regionEnhetManager.getByRegionId(regionId)).thenReturn(regionEnhets);
        Mockito.when(regionManager.getForVg(vgid)).thenReturn(Optional.of(region));

        //When
        final RegionsVardgivareStatus status = regionEnhetHandler.getRegionsVardgivareStatus(vgid);

        //Then
        assertEquals(RegionsVardgivareStatus.REGIONSVARDGIVARE_WITH_UPLOAD, status);
    }

}
