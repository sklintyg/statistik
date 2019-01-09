/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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
import java.util.regex.Pattern;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AnonymiseraPersonId {

    private static final String PERSON_NUMBER_WITHOUT_DASH_REGEX = "[0-9]{12}";

    public static final int DAY_INDEX = 6;
    public static final int END_OF_BIRTHDATE = 8;
    private static final int SAMORDNING_OFFSET = 6;
    public static final int BIRTHDAY_RANGE = 1000;
    public static final int SEX_INDEX = 11;

    private Random random = new Random();

    private final Map<String, String> actualToAnonymized = Collections.synchronizedMap(new HashMap<String, String>());
    private final Set<String> anonymizedSet = Collections.synchronizedSet(new HashSet<String>());

    public String anonymisera(String patientIdIn) {
        String patientId = normalisera(patientIdIn);
        String anonymized = actualToAnonymized.get(patientId);
        if (anonymized == null) {
            anonymized = getUniqueRandomPersonid(patientId);
        }
        return anonymized;
    }

    String normalisera(String personnr) {
        if (Pattern.matches(PERSON_NUMBER_WITHOUT_DASH_REGEX, personnr)) {
            final int endOfDateIndex = 8;
            return personnr.substring(0, endOfDateIndex) + "-" + personnr.substring(endOfDateIndex);
        } else {
            return personnr;
        }
    }

    private String getUniqueRandomPersonid(String nummer) {
        String anonymized;
        try {
            do {
                anonymized = newRandomPersonid(nummer);
            } while (anonymizedSet.contains(anonymized) || nummer.equals(anonymized));
        } catch (Exception ee) {
            System.err.println("Unrecognized personid " + nummer);
            anonymized = nummer;
        }
        anonymizedSet.add(anonymized);
        actualToAnonymized.put(nummer, anonymized);
        return anonymized;
    }

    // CHECKSTYLE:OFF MagicNumber
    private String newRandomPersonid(String nummer) {
        LocalDate date;
        boolean samordning = false;
        try {
            date = LocalDate.parse(nummer.substring(0, END_OF_BIRTHDATE), DateTimeFormatter.BASIC_ISO_DATE);
        } catch (Exception e) {
            StringBuilder b = new StringBuilder(nummer.substring(0, END_OF_BIRTHDATE));
            b.setCharAt(DAY_INDEX, (char) (b.charAt(DAY_INDEX) - SAMORDNING_OFFSET));
            date = LocalDate.parse(b.toString(), DateTimeFormatter.BASIC_ISO_DATE);
            samordning = true;
        }
        int days = random.nextInt(BIRTHDAY_RANGE) - BIRTHDAY_RANGE / 2;
        if (days == 0) {
            days = BIRTHDAY_RANGE / 2;
        }
        date = date.plusDays(days);
        int extension = random.nextInt(998);
        // Fix sex if needed
        if (((int) (nummer.charAt(SEX_INDEX) - '0') % 2) != extension % 2) {
            extension += 1;
        }
        String suffix = String.format("%1$03d", extension);
        String prefix = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        // Make samordning if needed
        if (samordning) {
            StringBuilder b = new StringBuilder(prefix);
            b.setCharAt(DAY_INDEX, (char) (prefix.charAt(DAY_INDEX) + DAY_INDEX));
            prefix = b.toString();
        }

        return prefix + "-" + suffix + kontrollSiffra(prefix.substring(2) + suffix);
    }
    // CHECKSTYLE:ON MagicNumber

    // CHECKSTYLE:OFF MagicNumber
    // Beräkning av kontrollsiffra enligt Luhn-algoritmen (http://sv.wikipedia.org/wiki/Luhn-algoritmen)
    int kontrollSiffra(String s) {
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            int d = (s.charAt(i) - '0') * (i % 2 == 0 ? 2 : 1);
            sum += d / 10 + d % 10;
        }
        return (10 - (sum % 10)) % 10;
    }
    // CHECKSTYLE:ON MagicNumber
}
