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
        EventPointer pointer = getPointerQuery();
        if (pointer == null) {
            return Long.MIN_VALUE;
        } else {
            return pointer.getEventId();
        }
    }

    protected void confirm(long id) {
        EventPointer pointer = getPointerQuery();
        if (pointer == null) {
            pointer = new EventPointer(PROCESSED_HSA, id);
        } else {
            pointer.setEventId(id);
        }
        manager.persist(pointer);
    }

    private EventPointer getPointerQuery() {
        return manager.find(EventPointer.class, PROCESSED_HSA);
    }
}
