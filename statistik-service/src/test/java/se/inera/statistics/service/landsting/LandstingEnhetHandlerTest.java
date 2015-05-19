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
import org.springframework.beans.factory.annotation.Autowired;
import se.inera.statistics.service.landsting.persistance.landsting.Landsting;
import se.inera.statistics.service.landsting.persistance.landsting.LandstingManager;
import se.inera.statistics.service.landsting.persistance.landstingenhet.LandstingEnhetManager;

import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;

public class LandstingEnhetHandlerTest {

    @InjectMocks
    private LandstingEnhetHandler landstingEnhetHandler;

    @Mock
    private LandstingManager landstingManager;

    @Mock
    private LandstingEnhetManager landstingEnhetManager;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testUpdateWhenNoLandstingIsFoundExceptionIsThrownAndNoUpdateIsPerformed() throws Exception {
        //Given
        final LandstingEnhetFileData data = new LandstingEnhetFileData("testName", null);
        Mockito.when(landstingManager.getForVg(anyString())).thenReturn(Optional.<Landsting>absent());

        //When
        try {
            landstingEnhetHandler.update(data);
            fail();
        } catch (NoLandstingSetForVgException e) {
            //Expected - do nothing
        }

        //Then
        Mockito.verify(landstingEnhetManager, times(0)).update(anyLong(), anyList());
    }

    @Test
    public void testUpdateIsCalledWithCorrectParams() throws Exception {
        //Given
        final ArrayList<LandstingEnhetFileDataRow> rows = new ArrayList<>();
        final LandstingEnhetFileData data = new LandstingEnhetFileData("testVgId", rows);
        final Landsting landsting = Mockito.mock(Landsting.class);
        final Long landtingsId = new Long(5);
        Mockito.when(landsting.getId()).thenReturn(landtingsId);
        Mockito.when(landstingManager.getForVg(anyString())).thenReturn(Optional.of(landsting));

        //When
        landstingEnhetHandler.update(data);

        //Then
        Mockito.verify(landstingEnhetManager, times(1)).update(landtingsId, rows);
    }

}
