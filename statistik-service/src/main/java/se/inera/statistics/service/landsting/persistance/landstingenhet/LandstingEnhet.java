/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.landsting.persistance.landstingenhet;

import se.inera.statistics.hsa.model.HsaIdEnhet;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Contains information about which enhets that are available as landstings statistics
 * including number of signed up patients (i.e. the content of the uploaded file).
 */
@Entity
@Table(name = "LandstingEnhet")
public class LandstingEnhet {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private long landstingId;

    private String enhetensHsaId;

    private Integer listadePatienter;

    private LandstingEnhet() {
    }

    public LandstingEnhet(long landstingId, HsaIdEnhet enhetensHsaId, Integer listadePatienter) {
        this.landstingId = landstingId;
        this.enhetensHsaId = enhetensHsaId.getId();
        this.listadePatienter = listadePatienter;
    }

    public long getId() {
        return id;
    }

    public long getLandstingId() {
        return landstingId;
    }

    public HsaIdEnhet getEnhetensHsaId() {
        return new HsaIdEnhet(enhetensHsaId);
    }

    public Integer getListadePatienter() {
        return listadePatienter;
    }

}
