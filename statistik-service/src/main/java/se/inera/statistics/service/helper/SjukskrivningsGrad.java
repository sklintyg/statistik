/**
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
 *
 * This file is part of statistik (https://github.com/sklintyg/statistik).
 *
 * statistik is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * statistik is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.service.helper;

import java.util.stream.Stream;

public enum SjukskrivningsGrad {

    /**
     * Helt nedsatt (id 1).
     */
    HELT_NEDSATT(1, "Helt nedsatt", 100),
    /**
     * Nedsatt till 3/4 (id 2).
     */
    TRE_FJARDEDEL(2, "Nedsatt med 3/4", 75),
    /**
     * Nedsatt till hälften (id 3).
     */
    HALFTEN(3, "Nedsatt med hälften", 50),
    /**
     * Nedsatt till 1/4 (id 4).
     */
    EN_FJARDEDEL(4, "Nedsatt med 1/4", 25);

    private final int id;
    private final String label;
    private final int nedsattning;

    SjukskrivningsGrad(int id, String label, int nedsattning) {
        this.id = id;
        this.label = label;
        this.nedsattning = nedsattning;
    }

    public int getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public int getNedsattning() {
        return nedsattning;
    }

    public static SjukskrivningsGrad fromId(int id) {
        return Stream.of(values())
                .filter(typ -> typ.id == id).findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

}
