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
package se.inera.statistics.service.landsting.persistance.landstingenhet;

import se.inera.statistics.hsa.model.HsaId;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

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

    public LandstingEnhet(long landstingId, HsaId enhetensHsaId, Integer listadePatienter) {
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

    public HsaId getEnhetensHsaId() {
        return new HsaId(enhetensHsaId);
    }

    public Integer getListadePatienter() {
        return listadePatienter;
    }

}
