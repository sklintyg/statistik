package se.inera.statistics.service.hsa;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import se.inera.statistics.service.report.model.Lan;

import java.util.ArrayList;
import java.util.List;

@Component
@Profile("dev")
@Primary
public class HSAServiceMock implements HSAService {
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
        root.put("enhet", createEnhet(key));
        return root;
    }

    public JsonNode createEnhet(HSAKey key) {
        ObjectNode root = factory.objectNode();
        root.put("enhetsnamn", "Enhetens namn");
        root.put("id", "Enhetens organisationsnummer");
        root.put("organisationsnamn", "Organisationsnamn");
        root.put("enhetstyp", asList("02"));
        root.put("vardgivartillhorighet", "vardgivar hsaid");
        root.put("agarform", asList("Landsting/Region"));
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
        root.put("koordinat", "nagonsortskoordinat");
        root.put("plats", "Plats");
        root.put("kommundelskod", "0");
        root.put("kommundelsnamn", "Centrum");
        root.put("kommun", createKommun());
        root.put("lan", createLan(key));
        return root;
    }

    private String createLan(HSAKey key) {
        int keyIndex = key != null && key.getVardgivareId() != null ? Math.abs(key.getVardgivareId().hashCode()) : 0;
        String kod = LAN_CODES.get(keyIndex % LAN_CODES.size());
        return kod;
    }

    private String createKommun() {
        return "80";
    }

    private JsonNode asList(String...items) {
        ArrayNode container = factory.arrayNode();
        for (String item: items) {
            container.add(item);
        }
        return container;
    }
}
