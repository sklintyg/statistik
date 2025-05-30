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
package se.inera.statistics.service.processlog;

import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = IntygEvent.TABLE)
public class IntygEvent {

    public static final String TABLE = "intyghandelse";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated
    private EventType type;

    private String data;

    private String correlationId;

    private long timestamp;

    /**
     * Empty constructor (as required by JPA spec).
     */
    public IntygEvent() {
        // Empty constructor (as required by JPA spec).
    }

    public IntygEvent(EventType type, String data, String correlationId, long timestamp) {
        this.type = type;
        this.data = data;
        this.correlationId = correlationId;
        this.timestamp = timestamp;
    }

    public EventType getType() {
        return type;
    }

    public String getData() {
        return data;
    }

    public long getId() {
        return id;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Transient
    public IntygFormat getFormat() {
        return getIntygFormat(data);
    }

    public static IntygFormat getIntygFormat(String intyg) {
        if (intyg == null) {
            return IntygFormat.REGISTER_MEDICAL_CERTIFICATE;
        }

        final String trimmedIntyg = intyg.trim();
        if (trimmedIntyg.endsWith("RegisterCertificate>")) {
            return IntygFormat.REGISTER_CERTIFICATE;
        }

        if (trimmedIntyg.endsWith("RegisterTSBas>")) {
            return IntygFormat.REGISTER_TS_BAS;
        }

        if (trimmedIntyg.endsWith("RegisterTSDiabetes>")) {
            return IntygFormat.REGISTER_TS_DIABETES;
        }

        return IntygFormat.REGISTER_MEDICAL_CERTIFICATE;
    }

}
