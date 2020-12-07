/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.warehouse;

import java.io.Serializable;
import java.util.Objects;
import se.inera.statistics.integration.hsa.model.HsaIdLakare;
import se.inera.statistics.service.report.model.Kon;

public class Lakare implements Serializable {

    private static final long serialVersionUID = -3030152743446943570L;
    private HsaIdLakare id;
    private Kon kon;
    private int age;
    private int[] befattnings;

    public Lakare(HsaIdLakare lakareId, Kon kon, int age, int[] befattnings) {
        this.id = lakareId;
        this.kon = kon;
        this.age = age;
        this.befattnings = befattnings;
    }

    public HsaIdLakare getId() {
        return id;
    }

    public Kon getKon() {
        return kon;
    }

    public int getAge() {
        return age;
    }

    public int[] getBefattnings() {
        return befattnings;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof Lakare)) {
            return false;
        }
        Lakare lakare = (Lakare) o;
        return Objects.equals(id, lakare.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
