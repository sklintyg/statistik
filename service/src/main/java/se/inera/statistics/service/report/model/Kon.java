/*
 * Copyright (C) 2025 Inera AB (http://www.inera.se)
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

public enum Kon {

    MALE(1), FEMALE(2), UNKNOWN(0);

    private final int numberRepresentation;

    Kon(int numberRepresentation) {
        this.numberRepresentation = numberRepresentation;
    }

    public int getNumberRepresentation() {
        return numberRepresentation;
    }

    public static Kon byNumberRepresentation(int number) {
        for (Kon kon : values()) {
            if (kon.numberRepresentation == number) {
                return kon;
            }
        }
        throw new IllegalArgumentException("Unknown number for Kon: " + number);
    }

    public static Kon parse(String konString) {
        for (Kon kon : values()) {
            if (kon.name().equalsIgnoreCase(konString)) {
                return kon;
            }
        }
        throw new IllegalArgumentException("Unknown name for Kon: " + konString);
    }

}
