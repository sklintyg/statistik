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
package se.inera.statistics.service.region.persistance.regionenhet;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.service.region.RegionEnhetFileDataRow;

@Component
public class RegionEnhetManager {

    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    @Transactional
    public List<RegionEnhet> getAll() {
        TypedQuery<RegionEnhet> query = manager.createQuery("SELECT l FROM RegionEnhet l", RegionEnhet.class);
        return query.getResultList();
    }

    @Transactional
    public Optional<RegionEnhet> get(Long id) {
        final RegionEnhet regionEnhet = manager.find(RegionEnhet.class, id);
        return regionEnhet == null ? Optional.empty() : Optional.of(regionEnhet);
    }

    @Transactional
    public List<RegionEnhet> getByRegionId(Long regionId) {
        TypedQuery<RegionEnhet> query = manager
                .createQuery("SELECT l FROM RegionEnhet l where l.regionId = :regionId", RegionEnhet.class)
                .setParameter("regionId", regionId);
        return query.getResultList();
    }

    @Transactional
    public void removeByRegionId(Long regionId) {
        final List<RegionEnhet> regionEnhets = getByRegionId(regionId);
        for (RegionEnhet regionEnhet : regionEnhets) {
            manager.remove(regionEnhet);
        }
    }

    @Transactional
    public void update(final Long regionId, final List<RegionEnhetFileDataRow> newData) {
        List<RegionEnhet> regionEnhets = newData.stream().map(data -> {
            final HsaIdEnhet enhetensHsaId = data.getEnhetensHsaId();
            return new RegionEnhet(regionId, enhetensHsaId, data.getListadePatienter());
        }).collect(Collectors.toList());
        removeByRegionId(regionId);
        for (RegionEnhet regionEnhet : regionEnhets) {
            manager.persist(regionEnhet);
        }
    }

}
