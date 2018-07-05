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
package se.inera.statistics.service.warehouse.message;

import se.inera.statistics.hsa.model.HsaIdLakare;
import se.inera.statistics.service.report.model.Kon;

import java.time.LocalDate;

public class CountDTOAmne {
    private int count;
    private LocalDate date;
    private Kon kon;
    private MsgAmne amne;
    private String enhet;
    private int patientAge;
    private String intygTyp;
    private String dx;
    private String intygid;
    private HsaIdLakare lakareId;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Kon getKon() {
        return kon;
    }

    public void setKon(Kon kon) {
        this.kon = kon;
    }

    public MsgAmne getAmne() {
        return amne;
    }

    public void setAmne(MsgAmne amne) {
        this.amne = amne;
    }

    public void setEnhet(String enhet) {
        this.enhet = enhet;
    }

    public String getEnhet() {
        return enhet;
    }

    public void setPatientAge(int patientAge) {
        this.patientAge = patientAge;
    }

    public int getPatientAge() {
        return patientAge;
    }

    public void setIntygTyp(String intygTyp) {
        this.intygTyp = intygTyp;
    }

    public String getIntygTyp() {
        return intygTyp;
    }

    public String getDx() {
        return dx;
    }

    public void setDx(String dx) {
        this.dx = dx;
    }

    public void setIntygid(String intygid) {
        this.intygid = intygid;
    }

    public String getIntygid() {
        return intygid;
    }

    public HsaIdLakare getLakareId() {
        return lakareId;
    }

    public void setLakareId(HsaIdLakare lakareId) {
        this.lakareId = lakareId;
    }

}
