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
package se.inera.statistics.service.region.persistance.regionenhet;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import se.inera.statistics.integration.hsa.model.HsaIdEnhet;

/**
 * Contains information about which enhets that are available as regions statistics
 * including number of signed up patients (i.e. the content of the uploaded file).
 */
@Entity
@Table(name = "LandstingEnhet")
public class RegionEnhet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "landstingId")
    private long regionId;

    private String enhetensHsaId;

    private Integer listadePatienter;

    private RegionEnhet() {
    }

    public RegionEnhet(long regionId, HsaIdEnhet enhetensHsaId, Integer listadePatienter) {
        this.regionId = regionId;
        this.enhetensHsaId = enhetensHsaId.getId();
        this.listadePatienter = listadePatienter;
    }

    public long getId() {
        return id;
    }

    public long getRegionId() {
        return regionId;
    }

    public HsaIdEnhet getEnhetensHsaId() {
        return new HsaIdEnhet(enhetensHsaId);
    }

    public Integer getListadePatienter() {
        return listadePatienter;
    }

}