package se.inera.statistics.service.hsa;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Component
public class HSAServiceImpl implements HSAService {
    private JsonNodeFactory factory = JsonNodeFactory.instance;

    @Override
    public JsonNode getHSAInfo(HSAKey key) {
        ObjectNode root = factory.objectNode();
        root.put("enhetsnamn", "Enhetens namn");
        root.put("organisationsnummer", "Enhetens organisationsnummer");
        root.put("organisationsnamn", "Organisationsnamn");
        root.put("enhetstyp", asList("02"));
        root.put("vardgivartillhorighet", "vardgivar hsaid");
        root.put("agarform", "Landsting/Region");
        root.put("startdatum", "");
        root.put("slutdatum", "");
        root.put("geografi", createGeografiskIndelning());
        root.put("hos-personal", createHosPersonal());
        return root;
    }

    private JsonNode createHosPersonal() {
        ObjectNode root = factory.objectNode();
        root.put("placeholder", "value");
        return root;
    }

    private JsonNode createGeografiskIndelning() {
        ObjectNode root = factory.objectNode();
        root.put("koordinater", "nagonsortskoordinat");
        root.put("plats", "Plats");
        root.put("kommundelskod", "0");
        root.put("kommundelsnamn", "Centrum");
        root.put("kommun", createKommun());
        root.put("lan", createLan());
        return root;
    }

    private JsonNode createLan() {
        ObjectNode root = factory.objectNode();
        root.put("kod", "14");
        root.put("namn", "Västra Götalands lan");
        return root;
    }

    private JsonNode createKommun() {
        ObjectNode root = factory.objectNode();
        root.put("kod", "80");
        root.put("namn", "Göteborg");
        return root;
    }

    private JsonNode asList(String...items) {
        ArrayNode container = factory.arrayNode();
        for (String item: items) {
            container.add(item);
        }
        return container;
    }
}
