/**
 * Copyright (C) 2014 Inera AB (http://www.inera.se)
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

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import se.inera.statistics.service.helper.HSAServiceHelper;
import se.inera.statistics.service.warehouse.WidelineConverter;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Component
public class LakareManager {
    private static final Logger LOG = LoggerFactory.getLogger(LakareManager.class);

    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    @Transactional
    public void saveLakare(JsonNode hsaInfo) {
        String vardgivareId = HSAServiceHelper.getVardgivarId(hsaInfo);
        String lakareId = HSAServiceHelper.getLakareId(hsaInfo);
        String tilltalsNamn = HSAServiceHelper.getLakareTilltalsnamn(hsaInfo);
        String efterNamn = HSAServiceHelper.getLakareEfternamn(hsaInfo);

        if (vardgivareId == null) {
            LOG.error("Vardgivare saknas: " + hsaInfo.asText());
            return;
        }
        TypedQuery<Lakare> lakareQuery = manager.createQuery("SELECT l FROM Lakare l WHERE l.lakareId = :lakareId", Lakare.class);
        List<Lakare> resultList = lakareQuery.setParameter("lakareId", lakareId).getResultList();

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
    public List<Lakare> getLakares(String vardgivare) {
        TypedQuery<Lakare> query = manager.createQuery("SELECT l FROM Lakare l WHERE l.vardgivareId = :vardgivareId", Lakare.class).setParameter("vardgivareId", vardgivare);
        return query.getResultList();
    }

    private boolean validate(String vardgivare, String lakareId, String tilltalsNamn, String efterNamn) {
        // Utan vardgivare har vi inget uppdrag att behandla intyg, avbryt direkt
        if (vardgivare == null) {
            LOG.error("Vardgivare saknas for lakare");
            return false;
        }
        if (vardgivare.length() > WidelineConverter.MAX_LENGTH_VGID) {
            LOG.error("Vardgivare saknas for lakare");
            return false;
        }
        boolean result = checkLength(lakareId, "Lakareid", WidelineConverter.MAX_LENGTH_LAKARE_ID);
        result |= checkLength(tilltalsNamn, "Tilltalsnamn", WidelineConverter.MAX_LENGTH_TILLTALSNAMN);
        result |= checkLength(efterNamn, "Efternamn", WidelineConverter.MAX_LENGTH_EFTERNAMN);
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
