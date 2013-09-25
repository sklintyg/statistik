package se.inera.statistics.service.processlog;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "intygevent")
public class IntygEvent extends Event {

    public IntygEvent() {
    }

    public IntygEvent(EventType type, String data, String correlationId, long timestamp) {
        super(type, data, correlationId, timestamp);
    }

}
