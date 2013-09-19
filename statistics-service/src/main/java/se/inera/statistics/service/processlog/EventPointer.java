package se.inera.statistics.service.processlog;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class EventPointer {

    @Id
    private String name;
    
    private long eventId;
    
    public EventPointer(String name, long id) {
        this.name = name;
        this.eventId = id;
    }
    
    public String getName() {
        return name;
    }
    
    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }
}
