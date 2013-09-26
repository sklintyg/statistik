package se.inera.statistics.service.sjukfall;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

@Repository
public class SjukfallService {

    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    private static final int MAX_DAYS_BETWEEN_CERTIFICATES = 5;

    private static final long MILLIS_PER_DAY = 24 * 60 * 60 * 1000;

    public String register(SjukfallKey key) {
        return register(key.getPersonId(), key.getVardgivareId(), key.getStart(), key.getEnd());
    }
    public String register(String personId, String vardgivareId, Date start, Date end) {
        Sjukfall currentSjukfall = getCurrentSjukfall(personId, vardgivareId);
        if (currentSjukfall != null) {
            if (end.after(currentSjukfall.getEnd())) {
                currentSjukfall.setEnd(end);
                currentSjukfall = manager.merge(currentSjukfall);
            }
        } else {
            currentSjukfall = new Sjukfall(personId, vardgivareId, start, end);
            manager.persist(currentSjukfall);
        }

        return currentSjukfall.getId();
    }

    public int expire(Date now) {
        Query query = manager.createQuery("DELETE FROM Sjukfall s WHERE s.end < :end");
        Date lastValid = new Date(now.getTime() - MAX_DAYS_BETWEEN_CERTIFICATES * MILLIS_PER_DAY);
        query.setParameter("end", lastValid);
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
