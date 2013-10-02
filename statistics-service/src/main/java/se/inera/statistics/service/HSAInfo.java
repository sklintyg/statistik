package se.inera.statistics.service;

import com.fasterxml.jackson.databind.JsonNode;

public class HSAInfo {
    // Enhetstyp, verksamhet, kommun, befattning, kön, ålder, specialitet*

    private String lan;

    private JsonNode jsonNode;

    public HSAInfo(String lan, JsonNode jsonNode) {
        this.lan = lan;
        this.jsonNode = jsonNode;
    }

    public String getLan() {
        return lan;
    }

    public JsonNode getJsonNode() {
        return jsonNode;
    }
}
