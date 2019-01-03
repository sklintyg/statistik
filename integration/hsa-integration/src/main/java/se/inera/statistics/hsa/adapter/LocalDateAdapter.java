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
package se.inera.statistics.hsa.adapter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Adapter for converting XML Schema types to Java dates and vice versa.
 *
 * @author andreaskaltenbach
 */
public final class LocalDateAdapter {

    private static final String ISO_DATE_PATTERN = "YYYY-MM-dd";
    private static final DateTimeFormatter ISO_DATE_FORMATTER = DateTimeFormatter.ofPattern(ISO_DATE_PATTERN);
    private static final String ISO_DATE_TIME_PATTERN = "YYYY-MM-dd'T'HH:mm:ss";
    private static final DateTimeFormatter ISO_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(ISO_DATE_TIME_PATTERN);

    private static final int DATE_END_INDEX = 10;
    private static final String TIMEZONE_PATTERN = "\\+.*";

    private LocalDateAdapter() {
    }

    /**
     * Converts an xs:date to a LocalDate.
     */
    public static LocalDate parseDate(String dateString) {
        if (dateString.length() > DATE_END_INDEX) {
            return LocalDate.parse(dateString.substring(0, DATE_END_INDEX));
        } else {
            return LocalDate.parse(dateString);
        }
    }

    /**
     * Converts an xs:datetime to a LocalDateTime.
     */
    public static LocalDateTime parseDateTime(String dateString) {

        // crop timezone information ('+...')
        return LocalDateTime.parse(dateString.replaceAll(TIMEZONE_PATTERN, ""));
    }

    /**
     * Converts an intyg:common-model:1:date to a LocalDate.
     */
    public static LocalDate parseIsoDate(String dateString) {
        return LocalDate.parse(dateString);
    }

    /**
     * Converts an intyg:common-model:1:dateTime to a LocalDateTime.
     */
    public static LocalDateTime parseIsoDateTime(String dateString) {
        return LocalDateTime.parse(dateString);
    }

    /**
     * Converts a LocalDateTime to an xs:datetime.
     */
    public static String printDateTime(LocalDateTime dateTime) {
        return dateTime.toString();
    }

    /**
     * Converts a LocalDate to an xs:date.
     */
    public static String printDate(LocalDate date) {
        return date.toString();
    }

    /**
     * Converts a LocalDateTime to an intyg:common-model:1:date.
     */
    public static String printIsoDateTime(LocalDateTime dateTime) {
        return dateTime.format(ISO_DATE_TIME_FORMATTER);
    }

    /**
     * Converts a LocalDate to an intyg:common-model:1:dateTime.
     */
    public static String printIsoDate(LocalDate date) {
        return date.format(ISO_DATE_FORMATTER);
    }
}
