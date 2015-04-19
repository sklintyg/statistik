package se.inera.testsupport;

import se.inera.statistics.service.processlog.EventType;

public class Intyg {
    private EventType type;
    private String data;
    private String documentId;
    private long timestamp;
    private String county;

    public Intyg(EventType type, String data, String documentId, long timestamp, String county) {
        this.type = type;
        this.data = data;
        this.documentId = documentId;
        this.timestamp = timestamp;
        this.county = county;
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

    public String getCounty() {
        return county;
    }

}
