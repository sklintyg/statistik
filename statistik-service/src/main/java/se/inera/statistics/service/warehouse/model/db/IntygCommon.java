/**
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.warehouse.model.db;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = IntygCommon.TABLE)
public class IntygCommon {
    public static final String TABLE = "intygcommon";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String intygid;
    private String enhet;
    private String patientid;
    private int signeringsdatum;
    private String intygtyp;
    private String vardgivareId;
    private int kon;

    /**
     * @param id
     * @param intygid
     * @param patientid
     * @param signeringsdatum
     * @param intygtyp
     * @param enhet
     * @param vardgivareId
     */
    // FIXME: Checkstyle warning
    // CHECKSTYLE:OFF ParameterNumber
    public IntygCommon(long id, String intygid, String patientid, int signeringsdatum, String intygtyp, String enhet, String vardgivareId, int kon) {
        this.id = id;
        this.intygid = intygid;
        this.patientid = patientid;
        this.kon = kon;
        this.signeringsdatum = signeringsdatum;
        this.intygtyp = intygtyp;
        this.enhet = enhet;
        this.vardgivareId = vardgivareId;
    }
    // CHECKSTYLE:ON ParameterNumber

    public IntygCommon() {
        // Used by IntygCommonConverter
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIntygid() {
        return intygid;
    }

    public void setIntygid(String intygid) {
        this.intygid = intygid;
    }

    public String getPatientid() {
        return patientid;
    }

    public void setPatientid(String patientid) {
        this.patientid = patientid;
    }

    public int getSigneringsdatum() {
        return signeringsdatum;
    }

    public void setSigneringsdatum(int signeringsdatum) {
        this.signeringsdatum = signeringsdatum;
    }

    public String getIntygtyp() {
        return intygtyp;
    }

    public void setIntygtyp(String intygtyp) {
        this.intygtyp = intygtyp;
    }

    public String getEnhet() {
        return enhet;
    }

    public void setEnhet(String enhet) {
        this.enhet = enhet;
    }

    public String getVardgivareId() {
        return vardgivareId;
    }

    public void setVardgivareId(String vardgivareId) {
        this.vardgivareId = vardgivareId;
    }

    public int getKon() {
        return kon;
    }

    public void setKon(int kon) {
        this.kon = kon;
    }

}
