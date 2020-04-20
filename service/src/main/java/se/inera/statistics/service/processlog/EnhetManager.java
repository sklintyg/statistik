/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import se.inera.ifv.hsawsresponder.v3._31._1.ListGetHsaUnitsResponseType;
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

    public List<Enhet> getAllVardenhetsForVardgivareId(HsaIdVardgivare vgId) {
        final Query query = manager.createQuery("SELECT e FROM Enhet e WHERE e.vardgivareId = :vgId");
        query.setParameter("vgId", vgId.getId());
        final List resultList = query.getResultList();
        return getEnhetsFromResultList(resultList);
    }

    @Transactional
    public int updateName(ListGetHsaUnitsResponseType.HsaUnits hsaUnits) {
        return hsaUnits.getHsaUnit().stream().reduce(0, (integer, hsaUnit) -> {
            final Query q = manager.createNativeQuery("UPDATE enhet SET namn = :namn WHERE enhetId = :id AND namn <> :namn");
            final String name = hsaUnit.getName();
            q.setParameter("namn", name);
            final String hsaid = hsaUnit.getHsaIdentity();
            q.setParameter("id", hsaid);
            final int updated = q.executeUpdate();
            if (updated > 0) {
                LOG.info(String.format("Id: %s, Namn: %s", hsaid, name));
            }
            return updated;

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
