/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.processlog;

import java.time.LocalDate;
import java.util.List;
import se.inera.statistics.service.helper.Patientdata;
import se.inera.statistics.service.warehouse.IntygType;

public class IntygDTO {

    // Common
    private String intygid;
    private String enhet;
    private String patientid;
    private LocalDate signeringsdatum;
    private IntygType intygtyp;
    private Patientdata patientData;

    // WideLine
    private int startdatum;
    private int slutdatum;
    private String diagnoskod;
    private String lakareId;
    private List<Arbetsnedsattning> arbetsnedsattnings;


    public String getIntygid() {
        return intygid;
    }

    public void setIntygid(String intygid) {
        this.intygid = intygid;
    }

    public String getEnhet() {
        return enhet;
    }

    public void setEnhet(String enhet) {
        this.enhet = enhet;
    }

    public String getPatientid() {
        return patientid;
    }

    public void setPatientid(String patientid) {
        this.patientid = patientid;
    }

    public LocalDate getSigneringsdatum() {
        return signeringsdatum;
    }

    public void setSigneringsdatum(LocalDate signeringsdatum) {
        this.signeringsdatum = signeringsdatum;
    }

    public IntygType getIntygtyp() {
        return intygtyp;
    }

    public void setIntygtyp(IntygType intygtyp) {
        this.intygtyp = intygtyp;
    }

    public Patientdata getPatientData() {
        return patientData;
    }

    public void setPatientData(Patientdata patientData) {
        this.patientData = patientData;
    }

    public int getStartdatum() {
        return startdatum;
    }

    public void setStartdatum(int startdatum) {
        this.startdatum = startdatum;
    }

    public int getSlutdatum() {
        return slutdatum;
    }

    public void setSlutdatum(int slutdatum) {
        this.slutdatum = slutdatum;
    }

    public String getDiagnoskod() {
        return diagnoskod;
    }

    public void setDiagnoskod(String diagnoskod) {
        this.diagnoskod = diagnoskod;
    }

    public String getLakareId() {
        return lakareId;
    }

    public void setLakareId(String lakareId) {
        this.lakareId = lakareId;
    }

    public List<Arbetsnedsattning> getArbetsnedsattnings() {
        return arbetsnedsattnings;
    }

    public void setArbetsnedsattnings(List<Arbetsnedsattning> arbetsnedsattnings) {
        this.arbetsnedsattnings = arbetsnedsattnings;
    }
}
