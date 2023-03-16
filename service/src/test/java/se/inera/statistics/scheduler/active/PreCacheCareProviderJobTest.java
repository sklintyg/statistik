/*
 * Copyright (C) 2023 Inera AB (http://www.inera.se)
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

package se.inera.statistics.scheduler.active;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import se.inera.statistics.integration.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.warehouse.Warehouse;

class PreCacheCareProviderJobTest {

    private Warehouse warehouse = mock(Warehouse.class);
    private PreCacheCareProviderJob preCacheCareProviderJob;

    @Test
    void shallNotPrecacheAnyCareProviders() {
        preCacheCareProviderJob = new PreCacheCareProviderJob(warehouse, Collections.emptyList());
        preCacheCareProviderJob.run();
        verifyNoInteractions(warehouse);
    }

    @Test
    void shallPrecacheOneCareProvider() {
        final var careProviderIds = List.of("ID1");
        preCacheCareProviderJob = new PreCacheCareProviderJob(warehouse, careProviderIds);
        preCacheCareProviderJob.run();
        verify(warehouse, times(1)).get(new HsaIdVardgivare("ID1"));
        verifyNoMoreInteractions(warehouse);
    }

    @Test
    void shallPrecacheMultipleCareProviders() {
        final var careProviderIds = List.of("ID1", "ID2", "ID3");
        preCacheCareProviderJob = new PreCacheCareProviderJob(warehouse, careProviderIds);
        preCacheCareProviderJob.run();
        verify(warehouse, times(1)).get(new HsaIdVardgivare("ID1"));
        verify(warehouse, times(1)).get(new HsaIdVardgivare("ID2"));
        verify(warehouse, times(1)).get(new HsaIdVardgivare("ID3"));
        verifyNoMoreInteractions(warehouse);
    }

    @Test
    void shallSkipEmptyCareProviders() {
        final var careProviderIds = List.of("ID1", "", "ID3");
        preCacheCareProviderJob = new PreCacheCareProviderJob(warehouse, careProviderIds);
        preCacheCareProviderJob.run();
        verify(warehouse, times(1)).get(new HsaIdVardgivare("ID1"));
        verify(warehouse, times(1)).get(new HsaIdVardgivare("ID3"));
        verifyNoMoreInteractions(warehouse);
    }
}