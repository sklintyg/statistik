/**
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.landsting.persistance.landstingenhetupdate;

import se.inera.statistics.hsa.model.HsaIdUser;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

/**
 * Keeps information about last file upload action for landsting.
 */
@Entity
@Table(name = "LandstingEnhetUpdate")
public class LandstingEnhetUpdate {

    @Id
    private long landstingId;

    private String updatedByName;

    private String updatedByHsaid;

    private Timestamp timestamp;

    private String filename;

    @Enumerated(EnumType.STRING)
    private LandstingEnhetUpdateOperation operation;

    private LandstingEnhetUpdate() {
    }

    public LandstingEnhetUpdate(long landstingId, String updatedByName, HsaIdUser updatedByHsaid, Timestamp timestamp, String filename,
            LandstingEnhetUpdateOperation operation) {
        this.landstingId = landstingId;
        this.updatedByName = updatedByName;
        setUpdatedByHsaid(updatedByHsaid);
        this.timestamp = timestamp;
        this.filename = filename;
        this.operation = operation;
    }

    public long getLandstingId() {
        return landstingId;
    }

    public void setLandstingId(long landstingId) {
        this.landstingId = landstingId;
    }

    public String getUpdatedByName() {
        return updatedByName;
    }

    public void setUpdatedByName(String updatedByName) {
        this.updatedByName = updatedByName;
    }

    public String getUpdatedByHsaid() {
        return updatedByHsaid;
    }

    public void setUpdatedByHsaid(HsaIdUser updatedByHsaid) {
        this.updatedByHsaid = updatedByHsaid == null ? null : updatedByHsaid.getId();
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public LandstingEnhetUpdateOperation getOperation() {
        return operation;
    }

    public void setOperation(LandstingEnhetUpdateOperation operation) {
        this.operation = operation;
    }

}
