package se.inera.statistics.service.helper;

import com.fasterxml.jackson.databind.JsonNode;

public final class TestData {
    private Object replyObject;
    private JsonNode jsonNode;

    TestData(Object replyObject, JsonNode jsonNode) {
        this.replyObject = replyObject;
        this.jsonNode = jsonNode;
    }

    public Object getReplyObject() {
        return replyObject;
    }

    public JsonNode getJsonNode() {
        return jsonNode;
    }
}
