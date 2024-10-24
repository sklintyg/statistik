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
package se.inera.statistics.service.processlog;

import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import se.inera.statistics.fileservice.HsaUnit;
import se.inera.statistics.integration.hsa.model.HsaIdEnhet;
import se.inera.statistics.integration.hsa.model.HsaIdVardgivare;

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

    public List<Enhet> getAllVardenhetsForVardgivareId(HsaIdVardgivare vgId) {
        final Query query = manager.createQuery("SELECT e FROM Enhet e WHERE e.vardgivareId = :vgId");
        query.setParameter("vgId", vgId.getId());
        final List resultList = query.getResultList();
        return getEnhetsFromResultList(resultList);
    }

    @Transactional
    public int updateName(List<HsaUnit> hsaUnits) {
        return hsaUnits.stream().reduce(0, (partialUpdateResultCount, hsaUnit) -> {
            final Query query = manager.createQuery(
                "UPDATE Enhet e SET e.namn = :namn WHERE e.enhetId = :id AND e.namn <> :namn");
            final String name = hsaUnit.getName();
            query.setParameter("namn", name);
            final String hsaid = hsaUnit.getHsaIdentity();
            query.setParameter("id", hsaid);
            final int updateResultCount = query.executeUpdate();
            if (updateResultCount > 0) {
                LOG.info(String.format("Id: %s, Namn: %s", hsaid, name));
            }
            return partialUpdateResultCount + updateResultCount;

        }, Integer::sum);
    }

    private List<Enhet> getEnhetsFromResultList(List resultList) {
        if (resultList == null) {
            return Collections.emptyList();
        }
        final List<Enhet> enhets = new ArrayList<>();
        for (Object o : resultList) {
            if (o instanceof Enhet) {
                final Enhet enhet = (Enhet) o;
                if (enhet.isVardenhet()) {
                    enhets.add(enhet);
                }
            } else {
                LOG.error("Wrong enhet type found");
            }
        }
        return ImmutableList.copyOf(enhets);
    }

}