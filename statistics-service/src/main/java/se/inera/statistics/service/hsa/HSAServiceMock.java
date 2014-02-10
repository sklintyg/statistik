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

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import se.inera.statistics.service.report.model.Lan;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Component
@Profile("dev")
@Primary
public class HSAServiceMock implements HSAService {
    private static final int POSITIVE_MASK = 0x7fffffff;

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
        int keyIndex = key != null && key.getVardgivareId() != null ? key.getVardgivareId().hashCode() & POSITIVE_MASK : 0;
        String kod = LAN_CODES.get(keyIndex % LAN_CODES.size());
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
