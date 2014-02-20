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
package se.inera.statistics.service.warehouse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class SjukfallUtil {

    private SjukfallUtil() {
    }

    public static Collection<Sjukfall> calculateSjukfall(Aisle aisle) {
        Collection<Sjukfall> sjukfalls = new ArrayList<>();
        Map<Integer, Sjukfall> active = new HashMap<>();
        for (WideLine line : aisle) {
            // prune active
            int key = line.patient;
            Sjukfall sjukfall = active.get(key);

            if (sjukfall == null) {
                sjukfall = new Sjukfall(line);
                active.put(key, sjukfall);
            } else {
                Sjukfall nextSjukfall = sjukfall.join(line);
                if (nextSjukfall != sjukfall) {
                    sjukfalls.add(sjukfall);
                    active.put(key, nextSjukfall);
                }
            }
        }
        for (Sjukfall sjukfall : active.values()) {
            sjukfalls.add(sjukfall);
        }
        return sjukfalls;
    }

}
