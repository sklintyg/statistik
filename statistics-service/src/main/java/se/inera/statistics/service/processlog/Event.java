package se.inera.statistics.service.processlog;

import javax.persistence.*;

@MappedSuperclass
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @Enumerated
    private EventType type;

    private String data;

    /**
     * Empty constructor (as required by JPA spec)
     */
    public Event() {

    }

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
