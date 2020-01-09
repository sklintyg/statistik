/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.helper;

import java.time.LocalDate;
import java.time.MonthDay;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.inera.statistics.service.report.model.Kon;

public final class ConversionHelper {

    private static final Logger LOG = LoggerFactory.getLogger(ConversionHelper.class);

    private static final int UNKNOWN = 0;
    private static final int DATE_PART_OF_PERSON_ID = 8;
    private static final int DAY_PART_OF_DATE_PART = 6;
    private static final int MONTH_PART_OF_DATE_PART = 4;
    private static final int SAMORDNINGSNUMMER_DAY_CONSTANT = 60;
    private static final DateTimeFormatter MONTHDAY_FORMATTER = DateTimeFormatter.ofPattern("MMdd");
    private static final int SEX_DIGIT = 11;
    public static final int NO_AGE = -1;

    private ConversionHelper() {
    }

    // CHECKSTYLE:OFF MagicNumber
    public static long patientIdToInt(String id) {
        return patientIdToLong(id);
    }

    private static long patientIdToLong(String id) {
        final char from = '0';
        final char to = '9';
        long nr = 0;
        long j = 1;
        for (int i = id.length() - 1; i >= 0; i--) {
            final char ch = id.charAt(i);
            if (ch >= from && ch <= to) {
                nr += (ch - from) * j;
                j = j * 10;
            }
        }
        return nr;
    }

    public static String patientIdToString(long id) {
        final String onlyNumberString = String.valueOf(id);
        return onlyNumberString.replaceFirst("(........)(....)", "$1-$2");
    }

    public static String getUnifiedPersonId(String personIdRaw1) {
        // "replaceAll" below is a fancy "trim" that will also remove non breaking spaces
        String safePersonId = personIdRaw1 != null ? personIdRaw1.replaceAll("(^\\h*)|(\\h*$)", "") : "";
        if (safePersonId.matches("[0-9]{8}-[0-9]{4}")) {
            return safePersonId;
        } else if (safePersonId.matches("[0-9]{12}")) {
            return safePersonId.substring(0, DATE_PART_OF_PERSON_ID) + "-"
                + safePersonId.substring(DATE_PART_OF_PERSON_ID);
        } else {
            throw new PersonIdParseException("Failed to parse person id");
        }
    }

    public static String extractKon(String personId) {
        return personId.charAt(SEX_DIGIT) % 2 == 0 ? Kon.FEMALE.toString() : Kon.MALE.toString();
    }

    /**
     * Same as extractAlder() method but will return NO_AGE instead of throwing exception when personId can not be parsed.
     */
    public static int extractAlderSafe(String personId, LocalDate start) {
        try {
            return extractAlder(personId, start);
        } catch (IllegalArgumentException ignore) {
            return NO_AGE;
        }
    }

    public static int extractAlder(String personId, LocalDate start) {
        LocalDate birthDate = null;
        int age;
        if (personId == null || personId.length() < DATE_PART_OF_PERSON_ID) {
            throw new IllegalArgumentException("Personnummer cannot be parsed as a date: " + personId);
        }
        try {
            String dateString = personId.substring(0, DATE_PART_OF_PERSON_ID);
            int day = Integer.parseInt(dateString.substring(DAY_PART_OF_DATE_PART));
            int month = Integer.parseInt(dateString.substring(MONTH_PART_OF_DATE_PART, DAY_PART_OF_DATE_PART));

            if (day > SAMORDNINGSNUMMER_DAY_CONSTANT) {
                dateString = dateString.substring(0, MONTH_PART_OF_DATE_PART)
                    + MONTHDAY_FORMATTER.format(MonthDay.of(month, day - SAMORDNINGSNUMMER_DAY_CONSTANT));
            }
            birthDate = LocalDate.from(DateTimeFormatter.BASIC_ISO_DATE.parse(dateString));
            LocalDate referenceDate = LocalDate.from(start);
            Period period = Period.between(birthDate, referenceDate);
            age = period.getYears();
        } catch (NumberFormatException | DateTimeParseException e) {
            LOG.debug("Personnummer cannot be parsed as a date, adjusting for samordningsnummer did not help", e);
            throw new IllegalArgumentException(
                "Personnummer cannot be parsed as a date, adjusting for samordningsnummer did not help: " + personId);
        }
        return age;
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
            final Integer parsed = parseInt(lkf.substring(0, length));
            if (parsed != null) {
                return parsed;
            } else {
                LOG.warn("Could not parse LKF: " + lkf);
                return UNKNOWN;
            }
        }
    }
    // CHECKSTYLE:ON MagicNumber

    /**
     * This is an edited copy of the built in Integer.parseInt() method that will return null instead of
     * throwing an exception when parsing failed. This gives us better performance.
     */
    public static Integer parseInt(String s) {
        final int radix = 10;

        if (s == null) {
            return null;
        }

        int result = 0;
        boolean negative = false;
        int i = 0, len = s.length();
        int limit = -Integer.MAX_VALUE;
        int multmin;
        int digit;

        if (len > 0) {
            char firstChar = s.charAt(0);
            if (firstChar < '0') { // Possible leading "+" or "-"
                if (firstChar == '-') {
                    negative = true;
                    limit = Integer.MIN_VALUE;
                } else if (firstChar != '+') {
                    return null;
                }

                if (len == 1) { // Cannot have lone "+" or "-"
                    return null;
                }
                i++;
            }
            multmin = limit / radix;
            while (i < len) {
                // Accumulating negatively avoids surprises near MAX_VALUE
                digit = Character.digit(s.charAt(i++), radix);
                if (digit < 0) {
                    return null;
                }
                if (result < multmin) {
                    return null;
                }
                result *= radix;
                if (result < limit + digit) {
                    return null;
                }
                result -= digit;
            }
        } else {
            return null;
        }
        return negative ? result : -result;
    }

}
