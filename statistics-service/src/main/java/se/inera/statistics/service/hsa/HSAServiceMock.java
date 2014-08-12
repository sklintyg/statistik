/*
 * Copyright (C) 2013 - 2014 Inera AB (http://www.inera.se)
 *
 *     This file is part of Inera Statistics (http://code.google.com/p/inera-statistics/).
 *
 *     Inera Statistics is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Inera Statistics is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU LESSER GENERAL PUBLIC LICENSE for more details.
 *
 *     You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
@Profile({"dev", "mockhsa" })
@Primary
public class HSAServiceMock implements HSAService {
    private static final int POSITIVE_MASK = 0x7fffffff;

    private JsonNodeFactory factory = JsonNodeFactory.instance;

    private static final Lan LAN = new Lan();
    private static final List<String> LAN_CODES;

    static {
        LAN_CODES = new ArrayList<>();
        for (String kod : LAN) {
            LAN_CODES.add(kod);
        }
    }

    @Override
    public JsonNode getHSAInfo(HSAKey key) {
        ObjectNode root = factory.objectNode();
        root.put("enhet", createEnhet(key));
        root.put("huvudenhet", createEnhet(key));
        root.put("vardgivare", createVardgivare(key));
        root.put("personal", createPersonal());
        return root;
    }

    private JsonNode createVardgivare(HSAKey key) {
        ObjectNode root = factory.objectNode();
        root.put("id", (JsonNode) null);
        root.put("orgnr", (JsonNode) null);
        root.put("namn", (JsonNode) null);
        root.put("startdatum", (JsonNode) null);
        root.put("slutdatum", (JsonNode) null);
        root.put("arkiverad", (JsonNode) null);
        return root;
    }

    public JsonNode createEnhet(HSAKey key) {
        ObjectNode root = factory.objectNode();
        root.put("id", "Enhetens organisationsnummer");
        root.put("namn", "Enhetens namn");
        root.put("enhetstyp", asList("02"));
        root.put("agarform", asList("Landsting/Region"));
        root.put("startdatum", "");
        root.put("slutdatum", "");
        root.put("arkiverad", (JsonNode) null);
        root.put("organisationsnamn", "Organisationsnamn");
        root.put("verksamhet", (JsonNode) null);
        root.put("vardform", (JsonNode) null);
        root.put("geografi", createGeografiskIndelning(key));
        return root;
    }

    private JsonNode createPersonal() {
        ObjectNode root = factory.objectNode();
        root.put("id", (JsonNode) null);
        root.put("efternamn", (JsonNode) null);
        root.put("tilltalsnamn", (JsonNode) null);
        root.put("initial", (JsonNode) null);
        root.put("kon", (JsonNode) null);
        root.put("alder", (JsonNode) null);
        root.put("befattning", (JsonNode) null);
        root.put("specialitet", (JsonNode) null);
        root.put("yrkesgrupp", (JsonNode) null);
        root.put("skyddad", (JsonNode) null);
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
        int keyIndex = key != null && key.getVardgivareId() != null ? key.getVardgivareId().hashCode() & POSITIVE_MASK : 0;
        return LAN_CODES.get(keyIndex % LAN_CODES.size());
    }

    private String createKommun() {
        return "80";
    }

    private JsonNode asList(String... items) {
        ArrayNode container = factory.arrayNode();
        for (String item : items) {
            container.add(item);
        }
        return container;
    }
}
