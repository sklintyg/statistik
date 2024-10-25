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
package se.inera.statistics.service.processlog;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;
import se.inera.statistics.integration.hsa.model.HsaIdLakare;
import se.inera.statistics.integration.hsa.model.HsaIdVardgivare;

@Entity
@Table(name = Lakare.TABLE)
public class Lakare {

    public static final String TABLE = "lakare";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String vardgivareId;

    private String lakareId;

    private String tilltalsNamn;

    private String efterNamn;

    public Lakare(HsaIdVardgivare vardgivareId, HsaIdLakare lakareId, String tilltalsNamn, String efterNamn) {
        setVardgivareId(vardgivareId);
        setLakareId(lakareId);
        this.tilltalsNamn = tilltalsNamn;
        this.efterNamn = efterNamn;
    }

    Lakare() {
        //Not sure why/if this is needed
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public HsaIdVardgivare getVardgivareId() {
        return new HsaIdVardgivare(vardgivareId);
    }

    public void setVardgivareId(HsaIdVardgivare vardgivareId) {
        this.vardgivareId = vardgivareId.getId();
    }

    public HsaIdLakare getLakareId() {
        return new HsaIdLakare(lakareId);
    }

    public void setLakareId(HsaIdLakare lakareId) {
        this.lakareId = lakareId.getId();
    }

    public String getTilltalsNamn() {
        return tilltalsNamn;
    }

    public void setTilltalsNamn(String tilltalsNamn) {
        this.tilltalsNamn = tilltalsNamn;
    }

    public String getEfterNamn() {
        return efterNamn;
    }

    public void setEfterNamn(String efterNamn) {
        this.efterNamn = efterNamn;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof Lakare)) {
            return false;
        }

        final Lakare other = (Lakare) o;

        return Objects.equals(this.vardgivareId, other.vardgivareId)
            && Objects.equals(this.lakareId, other.lakareId)
            && Objects.equals(this.tilltalsNamn, other.tilltalsNamn)
            && Objects.equals(this.efterNamn, other.efterNamn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vardgivareId, lakareId, tilltalsNamn, efterNamn);
    }

}