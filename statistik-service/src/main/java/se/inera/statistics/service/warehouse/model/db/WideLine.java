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
package se.inera.statistics.service.warehouse.model.db;

import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.hsa.model.HsaIdLakare;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.processlog.EventType;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = WideLine.TABLE)
public class WideLine {
    public static final String TABLE = "wideline";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String correlationId;
    private String lkf;
    private String enhet;
    private long lakarintyg;
    private EventType intygTyp;
    private String patientid;
    private int startdatum;
    private int slutdatum;
    private int kon;
    private int alder;
    private String diagnoskapitel;
    private String diagnosavsnitt;
    private String diagnoskategori;
    private String diagnoskod;
    private int sjukskrivningsgrad;
    private int lakarkon;
    private int lakaralder;
    private String lakarbefattning;
    private String vardgivareId;
    private String lakareId;

    public WideLine() {
        // Used by WidelineConverter
    }

    // CHECKSTYLE:OFF ParameterNumber
    @java.lang.SuppressWarnings("squid:S00107") // Suppress parameter number warning in Sonar
    public WideLine(long id, String correlationId, String lkf, HsaIdEnhet enhet, long lakarintyg, EventType intygTyp, String patientid,
            int startdatum, int slutdatum, int kon, int alder, String diagnoskapitel, String diagnosavsnitt, String diagnoskategori,
            String diagnoskod, int sjukskrivningsgrad, int lakarkon, int lakaralder, String lakarbefattning, HsaIdVardgivare vardgivareId,
            HsaIdLakare lakareId) {
        this.id = id;
        this.correlationId = correlationId;
        this.lkf = lkf;
        setEnhet(enhet);
        this.lakarintyg = lakarintyg;
        this.intygTyp = intygTyp;
        this.patientid = patientid;
        this.startdatum = startdatum;
        this.slutdatum = slutdatum;
        this.kon = kon;
        this.alder = alder;
        this.diagnoskapitel = diagnoskapitel;
        this.diagnosavsnitt = diagnosavsnitt;
        this.diagnoskategori = diagnoskategori;
        this.sjukskrivningsgrad = sjukskrivningsgrad;
        this.diagnoskod = diagnoskod;
        this.lakarkon = lakarkon;
        this.lakaralder = lakaralder;
        this.lakarbefattning = lakarbefattning;
        setVardgivareId(vardgivareId);
        setLakareId(lakareId);
    }
    // CHECKSTYLE:ON ParameterNumber

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getLkf() {
        return lkf;
    }

    public void setLkf(String lkf) {
        this.lkf = lkf;
    }

    public HsaIdEnhet getEnhet() {
        return enhet == null ? HsaIdEnhet.empty() : new HsaIdEnhet(enhet);
    }

    public void setEnhet(HsaIdEnhet enhet) {
        this.enhet = enhet == null ? null : enhet.getId();
    }

    public HsaIdVardgivare getVardgivareId() {
        return vardgivareId == null ? HsaIdVardgivare.empty() : new HsaIdVardgivare(vardgivareId);
    }

    public void setVardgivareId(HsaIdVardgivare vardgivareId) {
        this.vardgivareId = vardgivareId == null ? null : vardgivareId.getId();
    }

    public long getLakarintyg() {
        return lakarintyg;
    }

    public void setLakarintyg(long lakarintyg) {
        this.lakarintyg = lakarintyg;
    }

    public EventType getIntygTyp() {
        return intygTyp;
    }

    public void setIntygTyp(EventType intygTyp) {
        this.intygTyp = intygTyp;
    }

    public String getPatientid() {
        return patientid;
    }

    public void setPatientid(String patientid) {
        this.patientid = patientid;
    }

    public int getStartdatum() {
        return startdatum;
    }

    public void setStartdatum(int startdatum) {
        this.startdatum = startdatum;
    }

    public int getSlutdatum() {
        return slutdatum;
    }

    public void setSlutdatum(int slutdatum) {
        this.slutdatum = slutdatum;
    }

    public int getKon() {
        return kon;
    }

    public void setKon(int kon) {
        this.kon = kon;
    }

    public int getAlder() {
        return alder;
    }

    public void setAlder(int alder) {
        this.alder = alder;
    }

    public String getDiagnoskapitel() {
        return diagnoskapitel;
    }

    public void setDiagnoskapitel(String diagnoskapitel) {
        this.diagnoskapitel = diagnoskapitel;
    }

    public String getDiagnosavsnitt() {
        return diagnosavsnitt;
    }

    public void setDiagnosavsnitt(String diagnosavsnitt) {
        this.diagnosavsnitt = diagnosavsnitt;
    }

    public String getDiagnoskategori() {
        return diagnoskategori;
    }

    public void setDiagnoskategori(String diagnoskategori) {
        this.diagnoskategori = diagnoskategori;
    }

    public String getDiagnoskod() {
        return diagnoskod;
    }

    public void setDiagnoskod(String diagnoskod) {
        this.diagnoskod = diagnoskod;
    }

    public int getSjukskrivningsgrad() {
        return sjukskrivningsgrad;
    }

    public void setSjukskrivningsgrad(int sjukskrivningsgrad) {
        this.sjukskrivningsgrad = sjukskrivningsgrad;
    }

    public int getLakarkon() {
        return lakarkon;
    }

    public void setLakarkon(int lakarkon) {
        this.lakarkon = lakarkon;
    }

    public int getLakaralder() {
        return lakaralder;
    }

    public void setLakaralder(int lakaralder) {
        this.lakaralder = lakaralder;
    }

    public String getLakarbefattning() {
        return lakarbefattning;
    }

    public void setLakarbefattning(String lakarbefattning) {
        this.lakarbefattning = lakarbefattning;
    }

    public HsaIdLakare getLakareId() {
        return lakareId == null ? HsaIdLakare.empty() : new HsaIdLakare(lakareId);
    }

    public void setLakareId(HsaIdLakare lakareId) {
        this.lakareId = lakareId == null ? null : lakareId.getId();
    }

}
