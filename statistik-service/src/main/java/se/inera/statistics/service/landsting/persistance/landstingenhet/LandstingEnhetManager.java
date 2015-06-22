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
package se.inera.statistics.service.landsting.persistance.landstingenhet;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.service.landsting.LandstingEnhetFileDataRow;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

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
    public Optional<LandstingEnhet> get(long id) {
        final LandstingEnhet landstingEnhet = manager.find(LandstingEnhet.class, id);
        return landstingEnhet == null ? Optional.<LandstingEnhet>absent() : Optional.of(landstingEnhet);
    }

    @Transactional
    public List<LandstingEnhet> getByLandstingId(long landstingId) {
        TypedQuery<LandstingEnhet> query = manager.createQuery("SELECT l FROM LandstingEnhet l where l.landstingId = :landstingId", LandstingEnhet.class).setParameter("landstingId", landstingId);
        return query.getResultList();
    }

    @Transactional
    public void removeByLandstingId(long landstingId) {
        final List<LandstingEnhet> landstingEnhets = getByLandstingId(landstingId);
        for (LandstingEnhet landstingEnhet : landstingEnhets) {
            manager.remove(landstingEnhet);
        }
    }

    @Transactional
    public void update(final long landstingId, final List<LandstingEnhetFileDataRow> newData) {
        List<LandstingEnhet> landstingEnhets = Lists.transform(newData, new Function<LandstingEnhetFileDataRow, LandstingEnhet>() {
            @Override
            public LandstingEnhet apply(LandstingEnhetFileDataRow data) {
                final HsaIdEnhet enhetensHsaId = data.getEnhetensHsaId();
                return new LandstingEnhet(landstingId, enhetensHsaId, data.getListadePatienter());
            }
        });
        removeByLandstingId(landstingId);
        for (LandstingEnhet landstingEnhet : landstingEnhets) {
            manager.persist(landstingEnhet);
        }
    }



}
