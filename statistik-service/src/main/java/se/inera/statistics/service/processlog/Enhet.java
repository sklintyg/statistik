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
package se.inera.statistics.service.processlog;

import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.report.model.Kommun;
import se.inera.statistics.service.report.model.VerksamhetsTyp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import java.util.Comparator;

@Entity
@Table(name = Enhet.TABLE)
@NamedQueries({
        @NamedQuery(name = "Enhet.getByVg", query = "SELECT e FROM Enhet e WHERE e.vardgivareId = :vgid"),
        @NamedQuery(name = "Enhet.getByEnhetids", query = "SELECT e FROM Enhet e WHERE e.enhetId IN :enhetids")
})
public class Enhet {

    public static final String TABLE = "enhet";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String vardgivareId;

    private String enhetId;

    private String namn;

    private String lansId;

    private String kommunId;

    private String verksamhetsTyper;

    Enhet() {
        //Not sure why/if this is needed
    }

    public Enhet(HsaIdVardgivare vardgivareId, HsaIdEnhet enhetId, String namn, String lansId, String kommunId, String verksamhetsTyper) {
        setVardgivareId(vardgivareId);
        setEnhetId(enhetId);
        this.namn = namn;
        this.lansId = lansId;
        this.kommunId = kommunId;
        this.verksamhetsTyper = verksamhetsTyper;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public HsaIdVardgivare getVardgivareId() {
        return new HsaIdVardgivare(vardgivareId);
    }

    public void setVardgivareId(HsaIdVardgivare vardgivareId) {
        this.vardgivareId = vardgivareId.getId();
    }

    public HsaIdEnhet getEnhetId() {
        return new HsaIdEnhet(enhetId);
    }

    public void setEnhetId(HsaIdEnhet enhetId) {
        this.enhetId = enhetId.getId();
    }

    public String getNamn() {
        return namn;
    }

    public String getLansId() {
        return lansId;
    }

    public void setLansId(String lansId) {
        this.lansId = lansId;
    }

    public String getKommunId() {
        final int length = kommunId != null ? kommunId.length() : 0;
        if (length < 2) {
            return Kommun.OVRIGT_ID.substring(2);
        }
        return kommunId.substring(length - 2, length);
    }

    public void setKommunId(String kommunId) {
        this.kommunId = kommunId;
    }

    public String getVerksamhetsTyper() {
        if (verksamhetsTyper == null || verksamhetsTyper.isEmpty()) {
            return VerksamhetsTyp.OVRIGT_ID;
        }
        return verksamhetsTyper;
    }

    public void setVerksamhetsTyper(String verksamhetsTyper) {
        this.verksamhetsTyper = verksamhetsTyper;
    }

    public static Comparator<Enhet> byEnhetId() {
        return (enhet1, enhet2) -> enhet1.enhetId.compareTo(enhet2.enhetId);
    }

}
