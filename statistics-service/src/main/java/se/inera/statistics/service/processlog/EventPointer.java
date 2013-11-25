package se.inera.statistics.service.processlog;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table (name = "handelsepekare")
public class EventPointer {

    private static final int MAX_NAME_LENGTH = 50;

    @Id
    @Column(length = MAX_NAME_LENGTH)
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
