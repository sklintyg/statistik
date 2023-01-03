/*
 * Copyright (C) 2023 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.service.report.model;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class Lan implements Iterable<String> {

    public static final String OVRIGT = "Okänt län";
    public static final String OVRIGT_ID = "00";
    private final Map<String, String> kodToName = new LinkedHashMap<>();

    public Lan() {
        kodToName.put("10", "Blekinge län");
        kodToName.put("20", "Dalarnas län");
        kodToName.put("13", "Hallands län");
        kodToName.put("08", "Kalmar län");
        kodToName.put("07", "Kronobergs län");
        kodToName.put("09", "Gotlands län");
        kodToName.put("21", "Gävleborgs län");
        kodToName.put("23", "Jämtlands län");
        kodToName.put("06", "Jönköpings län");
        kodToName.put("25", "Norrbottens län");
        kodToName.put("12", "Skåne län");
        kodToName.put("01", "Stockholms län");
        kodToName.put("04", "Södermanlands län");
        kodToName.put("03", "Uppsala län");
        kodToName.put("17", "Värmlands län");
        kodToName.put("24", "Västerbottens län");
        kodToName.put("22", "Västernorrlands län");
        kodToName.put("19", "Västmanlands län");
        kodToName.put("14", "Västra Götalands län");
        kodToName.put("18", "Örebro län");
        kodToName.put("05", "Östergötlands län");
        kodToName.put(OVRIGT_ID, OVRIGT);
    }

    public String getNamn(String kod) {
        String lan = kodToName.get(kod);
        return lan != null ? lan : OVRIGT;
    }

    public Map<String, String> getCodeToLanMap() {
        return Collections.unmodifiableMap(kodToName);
    }

    @Override
    public Iterator<String> iterator() {
        return kodToName.keySet().iterator();
    }
}
