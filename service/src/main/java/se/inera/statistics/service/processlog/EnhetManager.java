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
package se.inera.statistics.service.processlog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.collect.Collections2;

import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.hsa.model.HsaIdVardgivare;

@Component
public class EnhetManager {

    private static final Logger LOG = LoggerFactory.getLogger(EnhetManager.class);

    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    public List<Enhet> getEnhets(Collection<HsaIdEnhet> enhetIds) {
        if (enhetIds == null || enhetIds.isEmpty()) {
            return Collections.emptyList();
        }
        final Query query = manager.createQuery("SELECT e FROM Enhet e WHERE e.enhetId IN :enhetids");
        query.setParameter("enhetids", Collections2.transform(enhetIds, hsaId -> hsaId.getId()));
        final List resultList = query.getResultList();
        return getEnhetsFromResultList(resultList);
    }

    public List<Enhet> getAllEnhetsForVardgivareId(HsaIdVardgivare vgId) {
        final Query query = manager.createQuery("SELECT e FROM Enhet e WHERE e.vardgivareId = :vgId");
        query.setParameter("vgId", vgId.getId());
        final List resultList = query.getResultList();
        return getEnhetsFromResultList(resultList);
    }

    private List<Enhet> getEnhetsFromResultList(List resultList) {
        if (resultList == null) {
            return Collections.emptyList();
        }
        final List<Enhet> enhets = new ArrayList<>();
        for (Object o : resultList) {
            if (o instanceof Enhet) {
                enhets.add((Enhet) o);
            } else {
                LOG.error("Wrong enhet type found");
            }
        }
        return enhets;
    }

}
