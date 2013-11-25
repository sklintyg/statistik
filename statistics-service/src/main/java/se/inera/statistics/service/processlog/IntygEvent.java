package se.inera.statistics.service.processlog;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "intyghandelse")
public class IntygEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Enumerated
    private EventType type;

    private String data;

    private String correlationId;

    private long timestamp;

    /**
     * Empty constructor (as required by JPA spec).
     */
    public IntygEvent() {
    }

    public IntygEvent(EventType type, String data, String correlationId, long timestamp) {
        this.type = type;
        this.data = data;
        this.correlationId = correlationId;
        this.timestamp = timestamp;
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
