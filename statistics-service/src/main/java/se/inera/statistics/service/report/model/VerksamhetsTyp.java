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

package se.inera.statistics.service.report.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class VerksamhetsTyp {

    public static final String OVRIGT_ID = "00";
    public static final String OVRIGT = "Okänd verksamhet";

    private final Map<String, String> kodToName = new LinkedHashMap<>();

    public VerksamhetsTyp() {
        kodToName.put("10", "Barn- och ungdomsverksamhet");
        kodToName.put("11", "Medicinsk verksamhet");
        kodToName.put("12", "Laboratorieverksamhet");
        kodToName.put("13", "Opererande verksamhet");
        kodToName.put("14", "Övrig medicinsk verksamhet");
        kodToName.put("15", "Primärvårdsverksamhet");
        kodToName.put("16", "Psykatrisk verksamhet");
        kodToName.put("17", "Radiologisk verksamhet");
        kodToName.put("18", "Tandvårdsverksamhet");
        kodToName.put("20", "Övrig medicinsk serviceverksamhet");
        kodToName.put("23", "Socialtjänstverksamhet");
    }

    public String getGruppId(String id) {
        return id != null ? id.substring(0, 2) : OVRIGT_ID;
    }

    public String getNamn(String kod) {
        String verksamhet = kodToName.get(kod);
        return verksamhet != null ? verksamhet : OVRIGT;
    }

}
