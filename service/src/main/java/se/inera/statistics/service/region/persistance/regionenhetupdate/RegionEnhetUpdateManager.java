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
package se.inera.statistics.service.region.persistance.regionenhetupdate;

import java.sql.Timestamp;
import java.time.Clock;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import se.inera.statistics.hsa.model.HsaIdUser;

@Component
public class RegionEnhetUpdateManager {

    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    @Autowired
    private Clock clock;

    @Transactional
    public Optional<RegionEnhetUpdate> getByRegionId(long regionId) {
        final RegionEnhetUpdate regionEnhetUpdate = manager.find(RegionEnhetUpdate.class, regionId);
        return regionEnhetUpdate == null ? Optional.<RegionEnhetUpdate> empty() : Optional.of(regionEnhetUpdate);
    }

    @Transactional
    public void update(long regionId, String updatedByName, HsaIdUser updatedByHsaid, String filename,
                       RegionEnhetUpdateOperation operation) {
        final Optional<RegionEnhetUpdate> existing = getByRegionId(regionId);
        existing.ifPresent(regionEnhetUpdate -> manager.remove(regionEnhetUpdate));
        final RegionEnhetUpdate regionEnhetUpdate = new RegionEnhetUpdate(regionId, updatedByName, updatedByHsaid,
                new Timestamp(clock.millis()), filename, operation);
        manager.persist(regionEnhetUpdate);
    }

}
