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
package se.inera.statistics.service.warehouse.model.db;

import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import se.inera.statistics.service.processlog.EventType;
import se.inera.statistics.service.warehouse.IntygType;

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
    private LocalDate signeringsdatum;
    @Enumerated(EnumType.STRING) private IntygType intygtyp;
    private String vardgivareId;
    private int kon;
    private EventType eventType;
    private String dx;
    private boolean sentToFk;
    private String lakareId;
    private boolean active;

    // CHECKSTYLE:OFF ParameterNumber
    public IntygCommon(String intygid, String patientid, LocalDate signeringsdatum, IntygType intygtyp, String enhet, String vardgivareId,
                       int kon, EventType eventType, String dx, boolean sentToFk, String lakareId) {
        this.intygid = intygid;
        this.patientid = patientid;
        this.kon = kon;
        this.signeringsdatum = signeringsdatum;
        this.intygtyp = intygtyp;
        this.enhet = enhet;
        this.vardgivareId = vardgivareId;
        this.eventType = eventType;
        this.dx = dx;
        this.sentToFk = sentToFk;
        this.lakareId = lakareId;
        this.active = !EventType.REVOKED.equals(eventType);
    }
    // CHECKSTYLE:ON ParameterNumber

    private IntygCommon() {
        // Must exist for an entity
    }

    public long getId() {
        return id;
    }

    public String getIntygid() {
        return intygid;
    }

    public String getPatientid() {
        return patientid;
    }

    public LocalDate getSigneringsdatum() {
        return signeringsdatum;
    }

    public IntygType getIntygtyp() {
        return intygtyp;
    }

    public String getEnhet() {
        return enhet;
    }

    public String getVardgivareId() {
        return vardgivareId;
    }

    public int getKon() {
        return kon;
    }

    public EventType getEventType() {
        return eventType;
    }

    public String getDx() {
        return dx;
    }

    public void setDx(String dx) {
        this.dx = dx;
    }

    public boolean isSentToFk() {
        return sentToFk;
    }

    public void setSentToFk(boolean sentToFk) {
        this.sentToFk = sentToFk;
    }

    public String getLakareId() {
        return lakareId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
