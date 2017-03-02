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
package se.inera.statistics.service.landsting.persistance.landstingenhetupdate;

import com.google.common.base.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import se.inera.statistics.hsa.model.HsaIdUser;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.Timestamp;

@Component
public class LandstingEnhetUpdateManager {

    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    @Transactional
    public Optional<LandstingEnhetUpdate> getByLandstingId(long landstingId) {
        final LandstingEnhetUpdate landstingEnhetUpdate = manager.find(LandstingEnhetUpdate.class, landstingId);
        return landstingEnhetUpdate == null ? Optional.<LandstingEnhetUpdate> absent() : Optional.of(landstingEnhetUpdate);
    }

    @Transactional
    public void update(long landstingId, String updatedByName, HsaIdUser updatedByHsaid, String filename,
            LandstingEnhetUpdateOperation operation) {
        final Optional<LandstingEnhetUpdate> existing = getByLandstingId(landstingId);
        if (existing.isPresent()) {
            manager.remove(existing.get());
        }
        final LandstingEnhetUpdate landstingEnhetUpdate = new LandstingEnhetUpdate(landstingId, updatedByName, updatedByHsaid,
                new Timestamp(System.currentTimeMillis()), filename, operation);
        manager.persist(landstingEnhetUpdate);
    }

}
