/**
 * Copyright (C) 2015 Inera AB (http://www.inera.se)
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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "enhet")
public class Enhet implements Comparable<Enhet> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String vardgivareId;

    private String vardgivareNamn;

    private String enhetId;

    private String namn;

    private String lansId;

    private String kommunId;

    private String verksamhetsTyper;

    public Enhet() {
    }

    public long getId() {
        return id;
    }

    public Enhet(String vardgivareId, String vardgivareNamn, String enhetId, String namn, String lansId, String kommunId, String verksamhetsTyper) {
        this.vardgivareId = vardgivareId;
        this.vardgivareNamn = vardgivareNamn;
        this.enhetId = enhetId;
        this.namn = namn;
        this.lansId = lansId;
        this.kommunId = kommunId;
        this.verksamhetsTyper = verksamhetsTyper;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getVardgivareId() {
        return vardgivareId;
    }

    public void setVardgivareId(String vardgivareId) {
        this.vardgivareId = vardgivareId;
    }

    public String getVardgivareNamn() {
        return vardgivareNamn;
    }

    public void setVardgivareNamn(String namn) {
        this.vardgivareNamn = namn;
    }

    public String getEnhetId() {
        return enhetId;
    }

    public void setEnhetId(String enhetId) {
        this.enhetId = enhetId;
    }

    public String getNamn() {
        return namn;
    }

    public void setNamn(String namn) {
        this.namn = namn;
    }

    public String getLansId() {
        return lansId;
    }

    public void setLansId(String lansId) {
        this.lansId = lansId;
    }

    public String getKommunId() {
        return kommunId;
    }

    public void setKommunId(String kommunId) {
        this.kommunId = kommunId;
    }

    public String getVerksamhetsTyper() {
        return verksamhetsTyper;
    }

    public void setVerksamhetsTyper(String verksamhetsTyper) {
        this.verksamhetsTyper = verksamhetsTyper;
    }

    @Override
    public int compareTo(Enhet enhet) {
        return enhetId.compareTo(enhet.getEnhetId());
    }
}
