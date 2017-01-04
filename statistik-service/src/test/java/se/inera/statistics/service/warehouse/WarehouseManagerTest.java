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
package se.inera.statistics.service.warehouse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.Matchers.any;

@RunWith(MockitoJUnitRunner.class)
public class WarehouseManagerTest {

    @Mock
    private Warehouse warehouse;

    @Mock
    private Aisle aisle;

    @Mock
    private WidelineLoader loader;

    @Mock
    private EnhetLoader enhetLoader;

    @Mock
    private SjukfallUtil sjukfallUtil;

    @InjectMocks
    private WarehouseManager manager = new WarehouseManager();

    @Test
    public void verifyOrderOfActionWhenLoadingData() {
        Mockito.when(warehouse.iterator()).thenReturn(Collections.singletonList(aisle).iterator());

        manager.loadEnhetAndWideLines();

        InOrder inOrder = Mockito.inOrder(loader, warehouse, aisle);
        inOrder.verify(loader).populateWarehouse();
        inOrder.verify(warehouse).complete(any(LocalDateTime.class));
    }
}
