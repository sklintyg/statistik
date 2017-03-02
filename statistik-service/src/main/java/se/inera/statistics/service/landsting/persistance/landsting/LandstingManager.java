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
package se.inera.statistics.service.landsting.persistance.landsting;

import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

import se.inera.statistics.hsa.model.HsaIdVardgivare;

@Component
public class LandstingManager {

    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    @Transactional
    public List<Landsting> getAll() {
        TypedQuery<Landsting> query = manager.createQuery("SELECT l FROM Landsting l", Landsting.class);
        return query.getResultList();
    }

    @Transactional
    public Optional<Landsting> get(long id) {
        final Landsting landsting = manager.find(Landsting.class, id);
        return landsting == null ? Optional.<Landsting> absent() : Optional.of(landsting);
    }

    @Transactional
    public void add(String name, HsaIdVardgivare vgId) {
        final List<Landsting> all = getAll();
        List<Long> allIds = Lists.transform(all, landsting -> landsting.getId());
        final Long maxId = Collections.max(allIds);
        final Landsting landsting = new Landsting(maxId + 1, name, vgId);
        manager.persist(landsting);
    }

    @Transactional
    public Optional<Landsting> getForVg(HsaIdVardgivare vgId) {
        TypedQuery<Landsting> query = manager.createQuery("SELECT l FROM Landsting l where l.vardgivareId = :vgId", Landsting.class)
                .setParameter("vgId", vgId.getId());
        final List<Landsting> resultList = query.getResultList();
        return resultList.isEmpty() ? Optional.<Landsting> absent() : Optional.of(resultList.get(0));
    }

}
