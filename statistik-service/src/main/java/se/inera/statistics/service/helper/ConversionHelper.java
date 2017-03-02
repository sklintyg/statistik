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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.inera.statistics.service.report.model.Kon;

import java.time.LocalDate;
import java.time.MonthDay;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class ConversionHelper {
    private static final Logger LOG = LoggerFactory.getLogger(ConversionHelper.class);

    private static final int UNKNOWN = 0;
    static final int DATE_PART_OF_PERSON_ID = 8;
    private static final int DAY_PART_OF_DATE_PART = 6;
    private static final int MONTH_PART_OF_DATE_PART = 4;
    private static final int SAMORDNINGSNUMMER_DAY_CONSTANT = 60;
    private static final DateTimeFormatter MONTHDAY_FORMATTER = DateTimeFormatter.ofPattern("MMdd");
    public static final int NO_AGE = -1;

    private ConversionHelper() {
    }

    // CHECKSTYLE:OFF MagicNumber
    public static long patientIdToInt(String id) {
        final String onlyNumbers = id.replaceAll("[\\D]", "");
        return Long.parseLong(onlyNumbers);
    }

    public static String patientIdToString(long id) {
        final String onlyNumberString = String.valueOf(id);
        return onlyNumberString.replaceFirst("(........)(....)", "$1-$2");
    }

    protected static String extractKon(String personId) {
        return personId.charAt(DocumentHelper.SEX_DIGIT) % 2 == 0 ? Kon.FEMALE.toString() : Kon.MALE.toString();
    }

    protected static int extractAlder(String personId, LocalDate start) {
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
                        + (MONTHDAY_FORMATTER.format(MonthDay.of(month, day - SAMORDNINGSNUMMER_DAY_CONSTANT)));
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
            try {
                return Integer.parseInt(lkf.substring(0, length));
            } catch (NumberFormatException e) {
                LOG.warn("Could not parse LKF: " + lkf);
                return UNKNOWN;
            }
        }
    }
    // CHECKSTYLE:ON MagicNumber

}
