package se.inera.statistics.service.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

public final class JSONParser {

    private JSONParser() {
    }

    public static JsonNode parse(String doc) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readTree(doc);
        } catch (IOException e) {
            throw new StatisticsMalformedDocument("Could not parse document", e);
        }
    }

    public static JsonNode parse(InputStream doc) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readTree(doc);
        } catch (IOException e) {
            throw new StatisticsMalformedDocument("Could not parse document", e);
        }
    }
}
