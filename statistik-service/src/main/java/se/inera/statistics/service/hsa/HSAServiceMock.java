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
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import se.inera.statistics.service.report.model.Kommun;
import se.inera.statistics.service.report.model.Lan;
import se.inera.statistics.service.report.model.VerksamhetsTyp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@Profile({"dev", "mockhsa" })
@Primary
public class HSAServiceMock implements HSAService, HsaDataInjectable {
    private static final int POSITIVE_MASK = 0x7fffffff;
    public static final int VERKSAMHET_MODULO = 7;

    private JsonNodeFactory factory = JsonNodeFactory.instance;

    private static final Lan LAN = new Lan();
    private static final List<String> LAN_CODES;
    private static final Kommun KOMMUN = new Kommun();
    private static final String[] TILLTALS_NAMN = new String[] {"Abdullah", "Beata", "Cecilia", "David", "Egil", "Fredrika", "Gustave", "Henning", "Ibrahim", "José", "Kone", "Lazlo", "My", "Natasha", "Orhan", "Pawel", "Rebecca", "Sirkka", "Tuula", "Urban", "Vieux", "Åsa"};
    private static final String[] EFTER_NAMN = new String[] {"Andersson", "Bardot", "Cohen", "Derrida", "En", "Flod", "Gran", "Holmberg", "Isaac", "Juhanen", "Karlsson", "Lazar", "Manard", "Nadal", "Olrik", "Pettersson", "Rawls", "Sadat", "Tot", "Uddhammar", "Wedén", "Åsgren", "Örn"};
    private static final List<String> KOMMUN_CODES;
    private static final VerksamhetsTyp VERKSAMHET = new VerksamhetsTyp();
    private static final List<String> VERKSAMHET_CODES;
    private final Map<String, JsonNode> personals = new HashMap<>();

    static {
        LAN_CODES = new ArrayList<>();
        for (String kod : LAN) {
            LAN_CODES.add(kod);
        }
        KOMMUN_CODES = new ArrayList<>();
        for (String kod : KOMMUN) {
            KOMMUN_CODES.add(kod);
        }
        VERKSAMHET_CODES = new ArrayList<>();
        for (String kod : VERKSAMHET) {
            VERKSAMHET_CODES.add(kod);
        }
    }

    @Override
    public JsonNode getHSAInfo(HSAKey key) {
        ObjectNode root = factory.objectNode();
        root.put("enhet", createEnhet(key));
        root.put("huvudenhet", createEnhet(key));
        root.put("vardgivare", createVardgivare(key));
        root.put("personal", getOrCreatePersonal(key));
        return root;
    }

    private JsonNode getOrCreatePersonal(HSAKey key) {
        if (personals.containsKey(key.getLakareId())) {
            return personals.get(key.getLakareId());
        }
        return createPersonal(key);
    }

    private JsonNode createVardgivare(HSAKey key) {
        ObjectNode root = factory.objectNode();
        root.put("id", key.getVardgivareId());
        root.put("orgnr", (JsonNode) null);
        root.put("namn", "vardgivarnamn");
        root.put("startdatum", (JsonNode) null);
        root.put("slutdatum", (JsonNode) null);
        root.put("arkiverad", (JsonNode) null);
        return root;
    }

    public JsonNode createEnhet(HSAKey key) {
        ObjectNode root = factory.objectNode();
        root.put("id", key.getEnhetId());
        root.put("namn", getEnhetsNamn(key.getEnhetId()));
        root.put("enhetstyp", asList("02"));
        root.put("agarform", asList("Landsting/Region"));
        root.put("startdatum", "");
        root.put("slutdatum", "");
        root.put("arkiverad", (JsonNode) null);
        root.put("organisationsnamn", "Organisationsnamn");
        root.put("verksamhet", (JsonNode) null);
        root.put("vardform", (JsonNode) null);
        root.put("geografi", createGeografiskIndelning(key));
        root.put("verksamhet", asList(createVerksamhet(key)));
        return root;
    }

    private String getEnhetsNamn(String enhetId) {
        if (enhetId.startsWith("vg1-")) {
            String suffix = enhetId.substring(enhetId.lastIndexOf('-') + 1);
            return "Verksamhet " + suffix;
        }
        return enhetId;
    }

    private JsonNode createPersonal(HSAKey key) {
        ObjectNode root = factory.objectNode();
        root.put("id", key.getLakareId());
        root.put("initial", (JsonNode) null);
        root.put("kon", (JsonNode) null);
        root.put("alder", (JsonNode) null);
        root.put("befattning", (JsonNode) null);
        root.put("specialitet", (JsonNode) null);
        root.put("yrkesgrupp", (JsonNode) null);
        root.put("skyddad", (JsonNode) null);
        root.put("tilltalsnamn", getTilltalsnamn(key));
        root.put("efternamn", getEfternamn(key));
        return root;
    }

    public ObjectNode createPersonal(String id, HsaKon kon, int age, int befattning) {
        ObjectNode root = factory.objectNode();
        root.put("id", id);
        root.put("initial", (JsonNode) null);
        root.put("kon", kon.getHsaRepresantation());
        root.put("alder", age);
        root.put("befattning", befattning);
        root.put("specialitet", (JsonNode) null);
        root.put("yrkesgrupp", (JsonNode) null);
        root.put("skyddad", (JsonNode) null);
        root.put("tilltalsnamn", getTilltalsnamn(id));
        root.put("efternamn", getEfternamn(id));
        return root;
    }

    private String getTilltalsnamn(Object key) {
        if (key == null) {
            return null;
        }
        int index = key.toString().hashCode() & POSITIVE_MASK;
        return TILLTALS_NAMN[index % TILLTALS_NAMN.length ];
    }

    private String getEfternamn(Object key) {
        if (key == null) {
            return null;
        }
        int index = key.toString().hashCode() & POSITIVE_MASK;
        return EFTER_NAMN[index % EFTER_NAMN.length ];
    }

    private JsonNode createGeografiskIndelning(HSAKey key) {
        ObjectNode root = factory.objectNode();
        root.put("koordinat", "nagonsortskoordinat");
        root.put("plats", "Plats");
        root.put("kommundelskod", "0");
        root.put("kommundelsnamn", "Centrum");
        String lan = createLan(key);
        root.put("lan", lan);
        root.put("kommun", createKommun(key, lan));
        return root;
    }

    private String createLan(HSAKey key) {
        int keyIndex = key != null && key.getVardgivareId() != null ? key.getVardgivareId().hashCode() & POSITIVE_MASK : 0;
        return LAN_CODES.get(keyIndex % LAN_CODES.size());
    }

    private String createKommun(HSAKey key, final String lan) {
        int keyIndex = key != null && key.getEnhetId() != null ? key.getEnhetId().hashCode() & POSITIVE_MASK : 0;
        List<String> relevantKommuns = FluentIterable.from(KOMMUN_CODES).filter(new Predicate<String>() {
            @Override
            public boolean apply(String s) {
                return s.startsWith(lan) || s.equals(Kommun.OVRIGT_ID);
            }
        }).toList();
        return relevantKommuns.get(keyIndex % relevantKommuns.size());
    }

    private String[] createVerksamhet(HSAKey key) {
        if (key == null || key.getVardgivareId() == null) {
            return new String[] {VerksamhetsTyp.OVRIGT_ID};
        }
        Set<String> returnSet = new HashSet<>();
        int numberOfVerksamhet = (key.getEnhetId().hashCode() & POSITIVE_MASK) % VERKSAMHET_MODULO;
        int i = 0;
        while (returnSet.size() < numberOfVerksamhet) {
            int index = ((key.getVardgivareId() + key.getEnhetId() + i).hashCode()) & POSITIVE_MASK;
            returnSet.add(VERKSAMHET_CODES.get(index % VERKSAMHET_CODES.size()));
            i++;
        }
        String[] returnArray = returnSet.toArray(new String[returnSet.size()]);
        Arrays.sort(returnArray);
        return returnArray;
    }

    private JsonNode asList(String... items) {
        ArrayNode container = factory.arrayNode();
        for (String item : items) {
            container.add(item);
        }
        return container;
    }

    @Override
    public void addPersonal(String id, HsaKon kon, int age, int befattning) {
        final ObjectNode personal = createPersonal(id, kon, age, befattning);
        personals.put(id, personal);
    }

}
