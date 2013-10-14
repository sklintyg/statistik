package se.inera.statistics.service.processlog;

import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @Enumerated
    private EventType type;

    private String data;

    private String correlationId;

    private long timestamp;

    /**
     * Empty constructor (as required by JPA spec).
     */
    public Event() {
    }

    public Event(EventType type, String data, String correlationId, long timestamp) {
        this.type = type;
        this.data = data;
        this.correlationId = correlationId;
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

    public String getCorrelationId() {
        return correlationId;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
