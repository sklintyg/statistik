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
package se.inera.statistics.service.processlog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Collection;
import java.util.List;

@Component
public class EnhetManager {

    private static final Logger LOG = LoggerFactory.getLogger(EnhetManager.class);

    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    public List<Enhet> getEnhets(Collection<String> enhetIds) {
        final Query query = manager.createQuery("SELECT e FROM Enhet e WHERE e.enhetId IN :enhetids");
        query.setParameter("enhetids", enhetIds);
        return query.getResultList();
    }

    public List<Enhet> getAllEnhetsForVardgivareId(String vgId) {
        final Query query = manager.createQuery("SELECT e FROM Enhet e WHERE e.vardgivareId IN :vgId");
        query.setParameter("vgId", vgId);
        return query.getResultList();
    }

}
