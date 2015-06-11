package se.inera.testsupport;

import se.inera.statistics.service.processlog.EventType;

public class Intyg {
    private EventType type;
    private String data;
    private String documentId;
    private long timestamp;
    private String county;
    private String huvudenhetId;
    private String enhetName;


    public Intyg(EventType type, String data, String documentId, long timestamp, String county, String huvudenhetId, String enhetName) {
        this.type = type;
        this.data = data;
        this.documentId = documentId;
        this.timestamp = timestamp;
        this.county = county;
        this.huvudenhetId = huvudenhetId;
        this.enhetName = enhetName;
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

    public String getHuvudenhetId() {
        return huvudenhetId;
    }

    public String getEnhetName() {
        return enhetName;
    }

}
