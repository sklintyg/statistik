/*
 * Copyright (C) 2013 - 2014 Inera AB (http://www.inera.se)
 *
 *     This file is part of Inera Statistics (http://code.google.com/p/inera-statistics/).
 *
 *     Inera Statistics is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Inera Statistics is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU LESSER GENERAL PUBLIC LICENSE for more details.
 *
 *     You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.inera.statistics.service.warehouse.model.db;

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

    private String lkf;
    private String enhet;
    private long lakarintyg;
    private String patientid;
    private int startdatum;
    private int slutdatum;
    private int kon;
    private int alder;
    private String diagnoskapitel;
    private String diagnosavsnitt;
    private String diagnoskategori;
    private int sjukskrivningsgrad;
    private int lakarkon;
    private int lakaralder;
    private String lakarbefattning;
    private String vardgivareId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLkf() {
        return lkf;
    }

    public void setLkf(String lkf) {
        this.lkf = lkf;
    }

    public String getEnhet() {
        return enhet;
    }

    public void setEnhet(String enhet) {
        this.enhet = enhet;
    }

    public String getVardgivareId() {
        return vardgivareId;
    }

    public void setVardgivareId(String vardgivareId) {
        this.vardgivareId = vardgivareId;
    }

    public long getLakarintyg() {
        return lakarintyg;
    }

    public void setLakarintyg(long lakarintyg) {
        this.lakarintyg = lakarintyg;
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
}
