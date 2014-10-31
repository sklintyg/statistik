/*
 * Copyright (C) 2013 - 2014 Inera AB (http://www.inera.se)
 *
 *     This file is part of Inera Statistics (http://code.google.com/p/inera-statistics/).
 *
 *     Inera Statistics is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Inera Statistics is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU LESSER GENERAL PUBLIC LICENSE for more details.
 *
 *     You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.inera.statistics.service.processlog;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import se.inera.statistics.service.helper.HSAServiceHelper;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Component
public class LakareManager {

    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    @Transactional
    public void saveLakare(JsonNode hsaInfo) {
        String vardgivareId = HSAServiceHelper.getVardgivarId(hsaInfo);
        String lakareId = HSAServiceHelper.getLakareId(hsaInfo);
        String tilltalsNamn = HSAServiceHelper.getLakareTilltalsnamn(hsaInfo);
        String efterNamn = HSAServiceHelper.getLakareEfternamn(hsaInfo);

        TypedQuery<Lakare> lakareQuery = manager.createQuery("SELECT l FROM Lakare l WHERE l.lakareId = :lakareId", Lakare.class);
        List<Lakare> resultList = lakareQuery.setParameter("lakareId", lakareId).getResultList();

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

}
