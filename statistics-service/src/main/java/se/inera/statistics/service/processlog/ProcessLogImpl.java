package se.inera.statistics.service.processlog;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ProcessLogImpl implements ProcessLog {

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

}
