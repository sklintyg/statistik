package se.inera.statistics.spec;

import se.inera.statistics.service.processlog.EventType;

//TODO To be removed and instead use the common object from web module
public class Intyg {
    private EventType type;
    private String data;
    private String documentId;
    private long timestamp;

    public Intyg(EventType type, String data, String documentId, long timestamp) {
        this.type = type;
        this.data = data;
        this.documentId = documentId;
        this.timestamp = timestamp;
    }

    /**
     * For json mapper.
     */
    Intyg() { }

    public EventType getType() {
        return type;
    }

    public String getData() {
        return data;
    }

    public String getDocumentId() {
        return documentId;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
