package se.inera.statistics.service.sjukfall;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.joda.time.LocalDate;
import org.springframework.stereotype.Repository;

@Repository
public class SjukfallService {

    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    private static final int MAX_DAYS_BETWEEN_CERTIFICATES = 5;

    public SjukfallInfo register(SjukfallKey key) {
        return register(key.getPersonId(), key.getVardgivareId(), key.getStart(), key.getEnd());
    }
    public SjukfallInfo register(String personId, String vardgivareId, LocalDate start, LocalDate end) {
        Sjukfall currentSjukfall = getCurrentSjukfall(personId, vardgivareId);
        LocalDate prevEnd = null;
        if (currentSjukfall != null && !currentSjukfall.getEnd().plusDays(MAX_DAYS_BETWEEN_CERTIFICATES).isBefore(start)) {
            prevEnd = currentSjukfall.getEnd();
            if (end.isAfter(prevEnd)) {
                currentSjukfall.setEnd(end);
                currentSjukfall = manager.merge(currentSjukfall);
            }
        } else {
            currentSjukfall = new Sjukfall(personId, vardgivareId, start, end);
            manager.persist(currentSjukfall);
        }

        return new SjukfallInfo(currentSjukfall.getId(), currentSjukfall.getStart(), currentSjukfall.getEnd(), prevEnd);
    }

    public int expire(LocalDate now) {
        TypedQuery<String> timeLimitQuery = manager.createQuery("SELECT MAX (s.start) FROM Sjukfall s", String.class);
        List<String> max = timeLimitQuery.getResultList();
        if (max.size() != 1) {
            return 0;
        }

        LocalDate maxDate = new LocalDate(max.get(0));

        if (maxDate.isAfter(now)) {
            maxDate = now;
        }

        Query query = manager.createQuery("DELETE FROM Sjukfall s WHERE s.end < :end");
        LocalDate lastValid = maxDate.minusDays(MAX_DAYS_BETWEEN_CERTIFICATES);
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
