package se.inera.statistics.service.hsa;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import se.inera.statistics.service.report.model.Lan;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Component
public class HSAServiceImpl implements HSAService {
    private JsonNodeFactory factory = JsonNodeFactory.instance;

    private static final Lan LAN = new Lan();
    private static final List<String> LAN_CODES;
    
    static {
        LAN_CODES = new ArrayList<>();
        for (String kod: LAN) {
            LAN_CODES.add(kod);
        }
    }
    
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
        root.put("geografi", createGeografiskIndelning(key));
        root.put("hos-personal", createHosPersonal());
        return root;
    }

    private JsonNode createHosPersonal() {
        ObjectNode root = factory.objectNode();
        root.put("placeholder", "value");
        return root;
    }

    private JsonNode createGeografiskIndelning(HSAKey key) {
        ObjectNode root = factory.objectNode();
        root.put("koordinater", "nagonsortskoordinat");
        root.put("plats", "Plats");
        root.put("kommundelskod", "0");
        root.put("kommundelsnamn", "Centrum");
        root.put("kommun", createKommun());
        root.put("lan", createLan(key));
        return root;
    }

    private JsonNode createLan(HSAKey key) {
        ObjectNode root = factory.objectNode();
        String kod = LAN_CODES.get(key.hashCode() % LAN_CODES.size());
        root.put("kod", kod);
        root.put("namn", LAN.getNamn(kod));
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
