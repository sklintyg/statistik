/*
 * Copyright (C) 2025 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.warehouse;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import se.inera.statistics.integration.hsa.model.HsaIdAny;
import se.inera.statistics.integration.hsa.model.HsaIdEnhet;
import se.inera.statistics.integration.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.processlog.Enhet;

@Component
public class EnhetLoader {

    private static final Logger LOG = LoggerFactory.getLogger(EnhetLoader.class);

    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    List<Enhet> getAllEnhetsForVg(HsaIdVardgivare vg) {
        LOG.info("Getting enhets for vg: " + vg);
        final TypedQuery<Enhet> query = manager.createNamedQuery("Enhet.getByVg", Enhet.class);
        query.setParameter("vgid", vg.getId());
        return query.getResultList();
    }

    List<Enhet> getAllEnhetsForVardenhet(HsaIdEnhet vardenhet) {
        LOG.info("Getting enhets for vardenhet: " + vardenhet);
        final TypedQuery<Enhet> query = manager.createNamedQuery("Enhet.getByVardenhetid", Enhet.class);
        query.setParameter("veid", vardenhet.getId());
        return query.getResultList();
    }

    List<Enhet> getEnhets(Collection<HsaIdEnhet> enhetIds) {
        if (enhetIds == null || enhetIds.isEmpty()) {
            return Collections.emptyList();
        }

        final TypedQuery<Enhet> query = manager.createNamedQuery("Enhet.getByEnhetids", Enhet.class);
        final List<String> stringEnhetids = enhetIds.stream().map(HsaIdAny::getId).collect(Collectors.toList());
        LOG.info("Getting enhets for ids: " + String.join(", ", stringEnhetids));
        query.setParameter("enhetids", stringEnhetids);
        return query.getResultList();
    }

    Enhet getEnhet(HsaIdEnhet enhetId) {
        if (enhetId == null) {
            return null;
        }

        final TypedQuery<Enhet> query = manager.createNamedQuery("Enhet.getByEnhetid", Enhet.class);
        LOG.info("Getting enhet for id: " + enhetId.getId());
        query.setParameter("enhetid", enhetId.getId());
        List<Enhet> resultList = query.getResultList();
        return resultList.isEmpty() ? null : resultList.get(0);
    }

}
