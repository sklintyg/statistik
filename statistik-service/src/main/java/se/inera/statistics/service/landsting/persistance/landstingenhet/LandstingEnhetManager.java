/*
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.landsting.persistance.landstingenhet;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.service.landsting.LandstingEnhetFileDataRow;

@Component
public class LandstingEnhetManager {

    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    @Transactional
    public List<LandstingEnhet> getAll() {
        TypedQuery<LandstingEnhet> query = manager.createQuery("SELECT l FROM LandstingEnhet l", LandstingEnhet.class);
        return query.getResultList();
    }

    @Transactional
    public Optional<LandstingEnhet> get(Long id) {
        final LandstingEnhet landstingEnhet = manager.find(LandstingEnhet.class, id);
        return landstingEnhet == null ? Optional.<LandstingEnhet> empty() : Optional.of(landstingEnhet);
    }

    @Transactional
    public List<LandstingEnhet> getByLandstingId(Long landstingId) {
        TypedQuery<LandstingEnhet> query = manager
                .createQuery("SELECT l FROM LandstingEnhet l where l.landstingId = :landstingId", LandstingEnhet.class)
                .setParameter("landstingId", landstingId);
        return query.getResultList();
    }

    @Transactional
    public void removeByLandstingId(Long landstingId) {
        final List<LandstingEnhet> landstingEnhets = getByLandstingId(landstingId);
        for (LandstingEnhet landstingEnhet : landstingEnhets) {
            manager.remove(landstingEnhet);
        }
    }

    @Transactional
    public void update(final Long landstingId, final List<LandstingEnhetFileDataRow> newData) {
        List<LandstingEnhet> landstingEnhets = newData.stream().map(data -> {
            final HsaIdEnhet enhetensHsaId = data.getEnhetensHsaId();
            return new LandstingEnhet(landstingId, enhetensHsaId, data.getListadePatienter());
        }).collect(Collectors.toList());
        removeByLandstingId(landstingId);
        for (LandstingEnhet landstingEnhet : landstingEnhets) {
            manager.persist(landstingEnhet);
        }
    }

}
