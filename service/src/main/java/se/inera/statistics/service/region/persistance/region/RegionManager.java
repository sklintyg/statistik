/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.region.persistance.region;

import com.google.common.collect.Lists;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import se.inera.statistics.integration.hsa.model.HsaIdVardgivare;

@Component
public class RegionManager {

    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    @Transactional
    public List<Region> getAll() {
        TypedQuery<Region> query = manager.createQuery("SELECT l FROM Region l", Region.class);
        return query.getResultList();
    }

    @Transactional
    public Optional<Region> get(long id) {
        final Region region = manager.find(Region.class, id);
        return region == null ? Optional.<Region>empty() : Optional.of(region);
    }

    @Transactional
    public void add(String name, HsaIdVardgivare vgId) {
        final List<Region> all = getAll();
        List<Long> allIds = Lists.transform(all, region -> region.getId());
        final Long maxId = Collections.max(allIds);
        final Region region = new Region(maxId + 1, name, vgId);
        manager.persist(region);
    }

    @Transactional
    public Optional<Region> getForVg(HsaIdVardgivare vgId) {
        TypedQuery<Region> query = manager.createQuery("SELECT l FROM Region l where l.vardgivareId = :vgId", Region.class)
            .setParameter("vgId", vgId.getId());
        final List<Region> resultList = query.getResultList();
        return resultList.isEmpty() ? Optional.<Region>empty() : Optional.of(resultList.get(0));
    }

}