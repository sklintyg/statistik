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
package se.inera.statistics.service.processlog;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import se.inera.statistics.hsa.model.HsaIdLakare;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.helper.HSAServiceHelper;
import se.inera.statistics.service.hsa.HsaInfo;
import se.inera.statistics.service.warehouse.WidelineConverter;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

@Component
public class LakareManager {
    private static final Logger LOG = LoggerFactory.getLogger(LakareManager.class);

    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    @Transactional
    public void saveLakare(HsaInfo hsaInfo) {
        HsaIdVardgivare vardgivareId = HSAServiceHelper.getVardgivarId(hsaInfo);
        HsaIdLakare lakareId = HSAServiceHelper.getLakareId(hsaInfo);
        String tilltalsNamn = HSAServiceHelper.getLakareTilltalsnamn(hsaInfo);
        String efterNamn = HSAServiceHelper.getLakareEfternamn(hsaInfo);

        if (vardgivareId == null || vardgivareId.isEmpty()) {
            LOG.error("Vardgivare saknas: " + hsaInfo.toString());
            return;
        }
        if (lakareId == null || lakareId.isEmpty()) {
            LOG.error("Läkare saknas: " + hsaInfo.toString());
            return;
        }
        TypedQuery<Lakare> lakareQuery = manager.createQuery("SELECT l FROM Lakare l WHERE l.lakareId = :lakareId", Lakare.class);
        List<Lakare> resultList = lakareQuery.setParameter("lakareId", lakareId.getId()).getResultList();

        if (validate(vardgivareId, lakareId, tilltalsNamn, efterNamn)) {
            if (resultList.isEmpty()) {
                manager.persist(new Lakare(vardgivareId, lakareId, tilltalsNamn, efterNamn));
            } else {
                Lakare updatedLakare = resultList.get(0);
                updatedLakare.setVardgivareId(vardgivareId);
                updatedLakare.setLakareId(lakareId);
                updatedLakare.setTilltalsNamn(tilltalsNamn);
                updatedLakare.setEfterNamn(efterNamn);
                manager.merge(updatedLakare);
            }
        }
    }

    @Transactional
    public List<Lakare> getAllLakares() {
        TypedQuery<Lakare> query = manager.createQuery("SELECT l FROM Lakare l", Lakare.class);
        return query.getResultList();
    }

    @Transactional
    public List<Lakare> getAllSpecifiedLakares(Collection<HsaIdLakare> lakares) {
        if (lakares == null || lakares.isEmpty()) {
            return Collections.emptyList();
        }
        TypedQuery<Lakare> query = manager.createQuery("SELECT l FROM Lakare l WHERE l.lakareId IN :hsaIds", Lakare.class);
        query.setParameter("hsaIds", Collections2.transform(lakares, new Function<HsaIdLakare, String>() {
            @Override
            public String apply(HsaIdLakare hsaId) {
                return hsaId.getId();
            }
        }));
        return query.getResultList();
    }

    @Transactional
    public List<Lakare> getLakares(HsaIdVardgivare vardgivare) {
        TypedQuery<Lakare> query = manager.createQuery("SELECT l FROM Lakare l WHERE l.vardgivareId = :vardgivareId", Lakare.class).setParameter("vardgivareId", vardgivare.getId());
        return query.getResultList();
    }

    private boolean validate(HsaIdVardgivare vardgivare, HsaIdLakare lakareId, String tilltalsNamn, String efterNamn) {
        // Utan vardgivare har vi inget uppdrag att behandla intyg, avbryt direkt
        if (vardgivare == null || vardgivare.isEmpty()) {
            LOG.error("Vardgivare saknas for lakare");
            return false;
        }
        if (vardgivare.getId().length() > WidelineConverter.MAX_LENGTH_VGID) {
            LOG.error("Vardgivare id ogiltigt for lakare");
            return false;
        }
        if (lakareId == null || lakareId.isEmpty()) {
            LOG.error("LakareId saknas for lakare");
            return false;
        }
        boolean result = checkLength(lakareId.getId(), "Lakareid", WidelineConverter.MAX_LENGTH_LAKARE_ID);
        result &= checkLength(tilltalsNamn, "Tilltalsnamn", WidelineConverter.MAX_LENGTH_TILLTALSNAMN);
        result &= checkLength(efterNamn, "Efternamn", WidelineConverter.MAX_LENGTH_EFTERNAMN);
        return result;
    }

    private boolean checkLength(String field, String name, int max) {
        if (field == null || field.length() > max) {
            LOG.error(name + " saknas eller ar ogiltigt for lakare");
            return false;
        }
        return true;
    }

}
