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
package se.inera.statistics.service.warehouse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.processlog.EventType;
import se.inera.statistics.service.warehouse.model.db.WideLine;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class WidelineLoader {

    private static final Logger LOG = LoggerFactory.getLogger(WidelineLoader.class);

    @Autowired
    private FactConverter factConverter;

    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    public List<Fact> getFactsForVg(HsaIdVardgivare vgid) {
        LOG.info("Getting facts for vg: " + vgid);
        final TypedQuery<WideLine> query = manager.createNamedQuery("WideLine.getByVg", WideLine.class);
        query.setParameter("vgid", vgid.getId());
        query.setParameter("intygTyp", EventType.REVOKED);
        return query.getResultList().stream().map(wideLine -> factConverter.toFact(wideLine)).collect(Collectors.toList());
    }

    List<HsaIdVardgivare> getAllVgs() {
        LOG.info("Getting all vgs in db");
        final TypedQuery<String> query = manager.createNamedQuery("WideLine.getAllVgids", String.class);
        return query.getResultList().stream().map(HsaIdVardgivare::new).collect(Collectors.toList());
    }

}
