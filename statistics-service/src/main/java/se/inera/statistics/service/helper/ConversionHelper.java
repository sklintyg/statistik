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

package se.inera.statistics.service.helper;

import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.format.ISODateTimeFormat;

import java.util.HashMap;
import java.util.Map;

public final class ConversionHelper {

    public static final int UNKNOWN = 0;
    private static IdMap<String> enhetsMap = new IdMap<>();

    private ConversionHelper() {
    }

    public static int patientIdToInt(String id) {
        return Integer.parseInt(id.substring(2, 8)) * 1000 + Integer.parseInt(id.substring(9, 12)) + (1_000_000_000 * (Integer.parseInt(id.substring(0, 2)) - 19));
    }

    /**
     * @param id id
     * @return personnummer med 0 som kontrollsiffra
     */
    public static String patientIdToString(int id) {
        int centuryFactor = (19 + (id < 1_000_000_000 ? 0 : 1));
        int idWoCentury = id - (id < 1_000_000_000 ? 0 : id - 1_000_000_000);
        String century = "" + centuryFactor;
        String date = "" + (idWoCentury / 1000);
        String seq = "" + (idWoCentury % 1000);
        return century + date + '-' + seq + '0';
    }

    public static int getEnhetAndRemember(String id) {
        return enhetsMap.getId(id);
    }

    protected static String extractKon(String personId) {
        return personId.charAt(DocumentHelper.SEX_DIGIT) % 2 == 0 ? "kvinna" : "man";
    }

    protected static int extractAlder(String personId, LocalDate start) {
        LocalDate birthDate = ISODateTimeFormat.basicDate().parseLocalDate(personId.substring(0, DocumentHelper.DATE_PART_OF_PERSON_ID));
        LocalDate referenceDate = new LocalDate(start);
        Period period = new Period(birthDate, referenceDate);
        return period.getYears();
    }

    public static int extractLan(String lkf) {
            return extractLKF(lkf, 2);
    }

    public static int extractKommun(String lkf) {
        return extractLKF(lkf, 4);
    }

    public static int extractForsamling(String lkf) {
        return extractLKF(lkf, 6);
    }

    private static int extractLKF(String lkf, int length) {
        if (lkf.length() < length) {
            return UNKNOWN;
        } else {
            return Integer.parseInt(lkf.substring(0, length));
        }
    }

    private static class IdMap<T> {
        private final Map<T, Integer> map = new HashMap<>();

        public synchronized Integer getId(T key) {
            Integer id = map.get(key);
            if (id == null) {
                id = map.size() + 1;
                map.put(key, id);
            }
            return id;
        }
    }
}
