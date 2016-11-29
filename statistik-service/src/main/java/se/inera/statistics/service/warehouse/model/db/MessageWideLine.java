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

import se.inera.intyg.common.support.common.enumerations.PartKod;
import se.inera.statistics.service.processlog.message.MessageEventType;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


@Entity
@Table(name = "messagewideline")
public class MessageWideLine {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private long logId;
    private String meddelandeId;
    private String intygsId;
    @Enumerated(EnumType.STRING)
    private MessageEventType meddelandeTyp;
    private String patientid;
    private LocalDate skickatDate;
    private LocalTime skickatTidpunkt;
    @Enumerated(EnumType.STRING)
    private PartKod skickatAv;
    private String amneCode;
    private int kon;
    private int alder;
    private String enhet;
    private String vardgivareid;

    public MessageWideLine() {
        // Used by WidelineConverter
    }

    // CHECKSTYLE:OFF ParameterNumber
    @SuppressWarnings("squid:S00107") // Suppress parameter number warning in Sonar
    public MessageWideLine(long id, long logId, String meddelandeId, String intygsId, MessageEventType meddelandeTyp, String patientid, LocalDateTime skickatTidpunkt, PartKod skickatAv, String amneCode, int kon, int alder, String enhet, String vardgivareid) {
        this.id = id;
        this.logId = logId;
        this.meddelandeId = meddelandeId;
        this.intygsId = intygsId;
        this.meddelandeTyp = meddelandeTyp;
        this.patientid = patientid;
        this.skickatTidpunkt = skickatTidpunkt.toLocalTime();
        this.skickatDate = skickatTidpunkt.toLocalDate();
        this.skickatAv = skickatAv;
        this.amneCode = amneCode;
        this.kon = kon;
        this.alder = alder;
        this.enhet = enhet;
        this.vardgivareid = vardgivareid;
    }
    // CHECKSTYLE:ON ParameterNumber

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getLogId() {
        return logId;
    }

    public void setLogId(long logId) {
        this.logId = logId;
    }

    public String getMeddelandeId() {
        return meddelandeId;
    }

    public void setMeddelandeId(String meddelandeId) {
        this.meddelandeId = meddelandeId;
    }

    public String getIntygsId() {
        return intygsId;
    }

    public void setIntygsId(String intygsId) {
        this.intygsId = intygsId;
    }

    public MessageEventType getMeddelandeTyp() {
        return meddelandeTyp;
    }

    public void setMeddelandeTyp(MessageEventType meddelandeTyp) {
        this.meddelandeTyp = meddelandeTyp;
    }

    public String getPatientid() {
        return patientid;
    }

    public void setPatientid(String patientid) {
        this.patientid = patientid;
    }

    public LocalDate getSkickatDate() {
        return skickatDate;
    }

    public void setSkickatDate(LocalDate skickatDate) {
        this.skickatDate = skickatDate;
    }

    public LocalTime getSkickatTidpunkt() {
        return skickatTidpunkt;
    }

    public void setSkickatTidpunkt(LocalTime skickatTidpunkt) {
        this.skickatTidpunkt = skickatTidpunkt;
    }

    public PartKod getSkickatAv() {
        return skickatAv;
    }

    public void setSkickatAv(PartKod skickatAv) {
        this.skickatAv = skickatAv;
    }

    public String getAmneCode() {
        return amneCode;
    }

    public void setAmneCode(String amneCode) {
        this.amneCode = amneCode;
    }

    public int getKon() {
        return kon;
    }

    public void setKon(int kon) {
        this.kon = kon;
    }

    public int getAlder() {
        return alder;
    }

    public void setAlder(int alder) {
        this.alder = alder;
    }

    public String getEnhet() {
        return enhet;
    }

    public void setEnhet(String enhet) {
        this.enhet = enhet;
    }

    public String getVardgivareid() {
        return vardgivareid;
    }

    public void setVardgivareid(String vardgivareid) {
        this.vardgivareid = vardgivareid;
    }
}
