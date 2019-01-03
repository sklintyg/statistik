/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.landsting.persistance.landstingenhetupdate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import se.inera.statistics.hsa.model.HsaIdUser;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.Timestamp;
import java.time.Clock;
import java.util.Optional;

@Component
public class LandstingEnhetUpdateManager {

    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    @Autowired
    private Clock clock;

    @Transactional
    public Optional<LandstingEnhetUpdate> getByLandstingId(long landstingId) {
        final LandstingEnhetUpdate landstingEnhetUpdate = manager.find(LandstingEnhetUpdate.class, landstingId);
        return landstingEnhetUpdate == null ? Optional.<LandstingEnhetUpdate> empty() : Optional.of(landstingEnhetUpdate);
    }

    @Transactional
    public void update(long landstingId, String updatedByName, HsaIdUser updatedByHsaid, String filename,
            LandstingEnhetUpdateOperation operation) {
        final Optional<LandstingEnhetUpdate> existing = getByLandstingId(landstingId);
        existing.ifPresent(landstingEnhetUpdate -> manager.remove(landstingEnhetUpdate));
        final LandstingEnhetUpdate landstingEnhetUpdate = new LandstingEnhetUpdate(landstingId, updatedByName, updatedByHsaid,
                new Timestamp(clock.millis()), filename, operation);
        manager.persist(landstingEnhetUpdate);
    }

}
