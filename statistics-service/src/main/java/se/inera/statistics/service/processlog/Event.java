package se.inera.statistics.service.processlog;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Event {

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE)
    private long id;
    
    @Enumerated
    private EventType type;
    
    private String data;
    
    
    public Event(EventType type, String data) {
        this.type = type;
        this.data = data;
    }
    
    public EventType getType() {
        return type;
    }
    
    public String getData() {
        return data;
    }

    public long getId() {
        return id;
    }

}
