/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
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
package se.inera.testsupport.specialrapport.regionskane;

import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.Range;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class SpecialSkaneAgeComputeRow {

    private int age;
    private HsaIdEnhet enhet;
    private Range range;
    private Kon kon;

    public SpecialSkaneAgeComputeRow(int age, HsaIdEnhet enhet, Range range, Kon kon) {
        this.age = age;
        this.enhet = enhet;
        this.range = range;
        this.kon = kon;
    }

    public int getAge() {
        return age;
    }

    public HsaIdEnhet getEnhet() {
        return enhet;
    }

    public Range getRange() {
        return range;
    }

    public Kon getKon() {
        return kon;
    }

    public String getFormattedRange() {
        return getDateFormatted(range.getFrom()) + " - " + getDateFormatted(range.getTo());
    }

    private String getDateFormatted(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd", new Locale("sv")));
    }

}
