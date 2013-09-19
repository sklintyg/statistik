package se.inera.statistics.service.processlog;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ProcessLogImpl implements ProcessLog {

    private static final String PROCESSED_HSA = "PROCESSED_HSA";
    @PersistenceContext(unitName="IneraStatisticsLog")
    EntityManager manager;
    
    @Override
    @Transactional
    public long store(EventType type, String data) {
        Event event = new Event(type, data);
        manager.persist(event);
        return event.getId();
    }

    @Transactional
    public Event get(long id) {
        return manager.find(Event.class, id);
    }

    @Transactional
    public Event getPending() {
        long lastEventId = getLastId();
        TypedQuery<Event> allQuery = manager.createQuery("SELECT e from Event e WHERE e.id > :lastId ORDER BY e.id ASC", Event.class);
        allQuery.setParameter("lastId", lastEventId);
        
        List<Event> found = allQuery.getResultList();
        if (found.isEmpty()) {
            return null;
        } else {
            return found.get(0);
        }
    }

    private long getLastId() {
        TypedQuery<EventPointer> allQuery = getPointerQuery();
        List<EventPointer> resultList = allQuery.getResultList();
        if (resultList.isEmpty()) {
            return Long.MIN_VALUE;
        } else {
            return resultList.get(0).getEventId();
        }
    }

    protected void confirm(long id) {
        TypedQuery<EventPointer> allQuery = getPointerQuery();
        List<EventPointer> resultList = allQuery.getResultList();
        EventPointer pointer = null;
        if (resultList.isEmpty()) {
            pointer = new EventPointer(PROCESSED_HSA, id);
        } else {
            pointer = resultList.get(0);
            pointer.setEventId(id);
        }
        manager.persist(pointer);
    }

    private TypedQuery<EventPointer> getPointerQuery() {
        TypedQuery<EventPointer> allQuery = manager.createQuery("SELECT ep from EventPointer ep WHERE ep.name = :name", EventPointer.class);
        allQuery.setParameter("name", PROCESSED_HSA);
        return allQuery;
    }
}
