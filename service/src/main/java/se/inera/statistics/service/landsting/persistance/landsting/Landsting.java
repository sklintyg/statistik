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
package se.inera.statistics.service.landsting.persistance.landsting;

import se.inera.statistics.hsa.model.HsaIdVardgivare;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Contains all landsting, i.e. which VGs that have access to landstings statistics.
 */
@Entity
@Table(name = "Landsting")
public class Landsting {

    @Id
    private long id;

    private String namn;

    private String vardgivareId;

    private Landsting() {
    }

    Landsting(long id, String namn, HsaIdVardgivare vardgivareId) {
        this.id = id;
        this.namn = namn;
        this.vardgivareId = vardgivareId.getId();
    }

    public long getId() {
        return id;
    }

    public String getNamn() {
        return namn;
    }

    public HsaIdVardgivare getVardgivareId() {
        return new HsaIdVardgivare(vardgivareId);
    }

}