package se.inera.statistics.service.processlog;

import com.fasterxml.jackson.databind.JsonNode;

class EnhetParameter {
    private final String enhet;
    private final String vardgivare;
    private final String enhetNamn;
    private final String vardgivareNamn;
    private final String lansId;
    private final String kommunId;
    private final String verksamhetsTyper;
    private final JsonNode hsaInfo;

    EnhetParameter(String enhet, String vardgivare, String enhetNamn, String vardgivareNamn, String lansId, String kommunId, String verksamhetsTyper, JsonNode hsaInfo) {
        this.enhet = enhet;
        this.vardgivare = vardgivare;
        this.enhetNamn = enhetNamn;
        this.vardgivareNamn = vardgivareNamn;
        this.lansId = lansId;
        this.kommunId = kommunId;
        this.verksamhetsTyper = verksamhetsTyper;
        this.hsaInfo = hsaInfo;
    }

    String getEnhet() {
        return enhet;
    }

    String getVardgivare() {
        return vardgivare;
    }

    String getEnhetNamn() {
        return enhetNamn;
    }

    String getVardgivareNamn() {
        return vardgivareNamn;
    }

    String getLansId() {
        return lansId;
    }

    String getKommunId() {
        return kommunId;
    }

    String getVerksamhetsTyper() {
        return verksamhetsTyper;
    }

    JsonNode getHsaInfo() {
        return hsaInfo;
    }
}
