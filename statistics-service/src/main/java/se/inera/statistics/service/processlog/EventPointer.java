package se.inera.statistics.service.processlog;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class EventPointer {

    @Id
    @Column(length = 50)
    private String name;

    private long eventId;

    public EventPointer(String name, long id) {
        this.name = name;
        this.eventId = id;
    }

    /**
     * Empty constructor (as required by JPA spec).
     */
    public EventPointer() {
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
