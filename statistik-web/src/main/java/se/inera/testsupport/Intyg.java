package se.inera.testsupport;

import se.inera.statistics.service.processlog.EventType;
import se.inera.statistics.service.processlog.IntygFormat;

public class Intyg {
    private EventType type;
    private String data;
    private String documentId;
    private long timestamp;
    private String county;
    private String huvudenhetId;
    private String enhetName;
    private String vardgivareId;
    private String enhetId;
    private String lakareId;
    private IntygFormat intygFormat;

    // CHECKSTYLE:OFF ParameterNumberCheck
    public Intyg(EventType type, String data, String documentId, long timestamp, String county, String huvudenhetId, String enhetName, String vgId, String enhetId, String lakareId, IntygFormat intygFormat) {
        this.type = type;
        this.data = data;
        this.documentId = documentId;
        this.timestamp = timestamp;
        this.county = county;
        this.huvudenhetId = huvudenhetId;
        this.enhetName = enhetName;
        this.vardgivareId = vgId;
        this.enhetId = enhetId;
        this.lakareId = lakareId;
        this.intygFormat = intygFormat;
    }
    // CHECKSTYLE:ON

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

    public String getVardgivareId() {
        return vardgivareId;
    }

    public String getEnhetId() {
        return enhetId;
    }

    public String getLakareId() {
        return lakareId;
    }

    public IntygFormat getIntygFormat() {
        return intygFormat;
    }

}
