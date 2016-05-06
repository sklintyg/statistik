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
package se.inera.testsupport;

import se.inera.statistics.service.processlog.EventType;

public class Intyg {
    private EventType type;
    private String data;
    private String documentId;
    private long timestamp;
    private String county;
    private String huvudenhetId;
    private String enhetName;
    private String vardgivareId;
    private String enhetId;
    private String lakareId;

    // CHECKSTYLE:OFF ParameterNumberCheck
    public Intyg(EventType type, String data, String documentId, long timestamp, String county, String huvudenhetId, String enhetName, String vgId, String enhetId, String lakareId) {
        this.type = type;
        this.data = data;
        this.documentId = documentId;
        this.timestamp = timestamp;
        this.county = county;
        this.huvudenhetId = huvudenhetId;
        this.enhetName = enhetName;
        this.vardgivareId = vgId;
        this.enhetId = enhetId;
        this.lakareId = lakareId;
    }
    // CHECKSTYLE:ON

    /**
     * For json mapper.
     */
    Intyg() { }

    public EventType getType() {
        return type;
    }

    public String getData() {
        return data;
    }

    public String getDocumentId() {
        return documentId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getCounty() {
        return county;
    }

    public String getHuvudenhetId() {
        return huvudenhetId;
    }

    public String getEnhetName() {
        return enhetName;
    }

    public String getVardgivareId() {
        return vardgivareId;
    }

    public String getEnhetId() {
        return enhetId;
    }

    public String getLakareId() {
        return lakareId;
    }

}
