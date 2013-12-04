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
        if (currentSjukfall != null && !currentSjukfall.getEnd().plusDays(MAX_DAYS_BETWEEN_CERTIFICATES + 1).isBefore(start)) {
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
        int count = query.executeUpdate();
        return count;
    }

    private Sjukfall getCurrentSjukfall(String personId, String vardgivareId) {
        TypedQuery<Sjukfall> query = manager.createNamedQuery("SjukfallByPersonIdAndVardgivareId",  Sjukfall.class);
        query.setParameter("personId", personId);
        query.setParameter("vardgivareId", vardgivareId);
        List<Sjukfall> resultList = query.getResultList();
        return resultList.isEmpty() ? null : resultList.get(0);
    }

}
