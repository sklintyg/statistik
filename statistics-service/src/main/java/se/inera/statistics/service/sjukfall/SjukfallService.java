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

package se.inera.statistics.service.sjukfall;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class SjukfallService {

    private static final Logger LOG = LoggerFactory.getLogger(SjukfallService.class);

    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    private static final int MAX_DAYS_BETWEEN_CERTIFICATES = 5;

    private LocalDate cutOff = new LocalDate("1970-01-01");

    public SjukfallInfo register(SjukfallKey key) {
        return register(key.getPersonId(), key.getVardgivareId(), key.getStart(), key.getEnd());
    }
    public SjukfallInfo register(String personId, String vardgivareId, LocalDate start, LocalDate end) {
        Sjukfall currentSjukfall = getCurrentSjukfall(personId, vardgivareId);
        LocalDate prevEnd = null;
        if (existsActiveSjukfall(start, currentSjukfall)) {
            prevEnd = currentSjukfall.getEnd();
            if (end.isAfter(prevEnd)) {
                currentSjukfall.setEnd(end);
                currentSjukfall = manager.merge(currentSjukfall);
            }
        } else {
            currentSjukfall = new Sjukfall(personId, vardgivareId, start, end);
            manager.persist(currentSjukfall);
        }
        checkExpiry(start);
        return new SjukfallInfo(currentSjukfall.getId(), currentSjukfall.getStart(), currentSjukfall.getEnd(), prevEnd);
    }
    private boolean existsActiveSjukfall(LocalDate start, Sjukfall existingSjukfall) {
        return existingSjukfall != null && !existingSjukfall.getEnd().plusDays(MAX_DAYS_BETWEEN_CERTIFICATES + 1).isBefore(start);
    }

    private void checkExpiry(LocalDate start) {
        if (cutOff.isBefore(start)) {
            int expired = expire(start);
            LOG.info("Expire sjukfall with cutoff {}, expired {}", start, expired);
            cutOff = start;
        }
    }

    public int expire(LocalDate now) {
        Query query = manager.createQuery("DELETE FROM Sjukfall s WHERE s.end < :end");
        LocalDate lastValid = now.minusDays(MAX_DAYS_BETWEEN_CERTIFICATES);
        query.setParameter("end", lastValid.toString());
        return query.executeUpdate();
    }

    private Sjukfall getCurrentSjukfall(String personId, String vardgivareId) {
        TypedQuery<Sjukfall> query = manager.createNamedQuery("SjukfallByPersonIdAndVardgivareId",  Sjukfall.class);
        query.setParameter("personId", personId);
        query.setParameter("vardgivareId", vardgivareId);
        List<Sjukfall> resultList = query.getResultList();
        return resultList.isEmpty() ? null : resultList.get(0);
    }

}
