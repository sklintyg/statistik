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
package se.inera.testsupport.specialrapport.regionskane;

import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.warehouse.Sjukfall;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class SpecialSkaneComputeRow {

    private String dx;
    private HsaIdEnhet enhet;
    private LocalDate range;
    private Kon kon;
    private int sjukskrivningsgrad;
    private Sjukfall sjukfall;
    private long patient;

    public SpecialSkaneComputeRow(String dx, HsaIdEnhet enhet, LocalDate range, Kon kon,
                                  int sjukskrivningsgrad, Sjukfall sjukfall, long patient) {
        this.dx = dx;
        this.enhet = enhet;
        this.range = range;
        this.kon = kon;
        this.sjukskrivningsgrad = sjukskrivningsgrad;
        this.sjukfall = sjukfall;
        this.patient = patient;
    }

    public String getDx() {
        return dx;
    }

    public HsaIdEnhet getEnhet() {
        return enhet;
    }

    public LocalDate getRange() {
        return range;
    }

    public Kon getKon() {
        return kon;
    }

    public Sjukfall getSjukfall() {
        return sjukfall;
    }

    public int getSjukskrivningsgrad() {
        return sjukskrivningsgrad;
    }

    public long getPatient() {
        return patient;
    }

    public String getFormattedDate() {
        return range.format(DateTimeFormatter.ofPattern("MMM yyyy", new Locale("sv")));
    }

}
