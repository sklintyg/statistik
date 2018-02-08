/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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
package se.inera.statistik.tools.anonymisering.base;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Collections;

public class AnonymiseraHsaId {

    private final Random random = new Random();
    private final Map<String, String> actualToAnonymized = Collections.synchronizedMap(new HashMap<String, String>());
    private final Set<String> anonymizedSet = Collections.synchronizedSet(new HashSet<String>());

    public String anonymisera(String hsaId) {
        String anonymized = actualToAnonymized.get(hsaId);
        if (anonymized == null) {
            anonymized = getUniqueRandomHsaId(hsaId);
        }
        return anonymized;
    }

    private String getUniqueRandomHsaId(String hsaId) {
        String anonymized;
        do {
            anonymized = newRandomHsaId();
        } while (anonymizedSet.contains(anonymized) || hsaId.equals(anonymized));
        anonymizedSet.add(anonymized);
        actualToAnonymized.put(hsaId, anonymized);
        return anonymized;
    }

    // CHECKSTYLE:OFF MagicNumber
    private String newRandomHsaId() {
        int number = random.nextInt(1000000000);
        return "SE" + number;
    }
    // CHECKSTYLE:ON MagicNumber

}
