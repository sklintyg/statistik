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
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = WideLine.TABLE)
public class WideLine {
    public static final String TABLE = "wideline";

    @Id
    private int id;
    private int lan;
    private int kommun;
    private int forsamling;
    private int enhet;
    private int lakarintyg;
    private String patient;
    private int kalenderperiod;
    private int kon;
    private int alder;
    private String diagnoskapitel;
    private String diagnosavsnitt;
    private String diagnoskategori;
    private int sjukskrivningsgrad;
    private int sjukskrivningslangd;
    private int lakarkon;
    private int lakaralder;
    private int lakarbefattning;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLan() {
        return lan;
    }

    public void setLan(int lan) {
        this.lan = lan;
    }

    public int getKommun() {
        return kommun;
    }

    public void setKommun(int kommun) {
        this.kommun = kommun;
    }

    public int getForsamling() {
        return forsamling;
    }

    public void setForsamling(int forsamling) {
        this.forsamling = forsamling;
    }

    public int getEnhet() {
        return enhet;
    }

    public void setEnhet(int enhet) {
        this.enhet = enhet;
    }

    public int getLakarintyg() {
        return lakarintyg;
    }

    public void setLakarintyg(int lakarintyg) {
        this.lakarintyg = lakarintyg;
    }

    public String getPatient() {
        return patient;
    }

    public void setPatient(String patient) {
        this.patient = patient;
    }

    public int getKalenderperiod() {
        return kalenderperiod;
    }

    public void setKalenderperiod(int kalenderperiod) {
        this.kalenderperiod = kalenderperiod;
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

    public int getSjukskrivningslangd() {
        return sjukskrivningslangd;
    }

    public void setSjukskrivningslangd(int sjukskrivningslangd) {
        this.sjukskrivningslangd = sjukskrivningslangd;
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

    public int getLakarbefattning() {
        return lakarbefattning;
    }

    public void setLakarbefattning(int lakarbefattning) {
        this.lakarbefattning = lakarbefattning;
    }
}
