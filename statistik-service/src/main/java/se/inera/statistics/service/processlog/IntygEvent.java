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
package se.inera.statistics.service.processlog;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "intyghandelse")
public class IntygEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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
        final boolean isRegisterCertificateXmlFormat = intyg != null && intyg.matches("(?s)^.*<[^>]*RegisterCertificate.*>.*$");
        return isRegisterCertificateXmlFormat ? IntygFormat.REGISTER_CERTIFICATE : IntygFormat.REGISTER_MEDICAL_CERTIFICATE;
    }

}
