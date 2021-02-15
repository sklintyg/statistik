/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.region.persistance.regionenhetupdate;

import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import se.inera.statistics.integration.hsa.model.HsaIdUser;

/**
 * Keeps information about last file upload action for region.
 */
@Entity
@Table(name = "LandstingEnhetUpdate")
public class RegionEnhetUpdate {

    @Id
    @Column(name = "landstingId")
    private long regionId;

    private String updatedByName;

    private String updatedByHsaid;

    private Timestamp timestamp;

    private String filename;

    @Enumerated(EnumType.STRING)
    private RegionEnhetUpdateOperation operation;

    private RegionEnhetUpdate() {
    }

    public RegionEnhetUpdate(long regionId, String updatedByName, HsaIdUser updatedByHsaid, Timestamp timestamp, String filename,
        RegionEnhetUpdateOperation operation) {
        this.regionId = regionId;
        this.updatedByName = updatedByName;
        setUpdatedByHsaid(updatedByHsaid);
        this.timestamp = timestamp;
        this.filename = filename;
        this.operation = operation;
    }

    public long getRegionId() {
        return regionId;
    }

    public void setRegionId(long regionId) {
        this.regionId = regionId;
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

    public RegionEnhetUpdateOperation getOperation() {
        return operation;
    }

    public void setOperation(RegionEnhetUpdateOperation operation) {
        this.operation = operation;
    }

}
