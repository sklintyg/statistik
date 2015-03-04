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
package se.inera.statistics.service.warehouse;

import se.inera.statistics.service.processlog.Enhet;
import se.inera.statistics.service.processlog.Lakare;

public class BiExportData {

    private int lan;
    private int kommun;
    private int forsamling;
    private int enhet;
    private long lakarintyg;
    private int patient;
    private int startdatum;
    private int kon;
    private int alder;
    private int diagnoskapitel;
    private int diagnosavsnitt;
    private int diagnoskategori;
    private int sjukskrivningsgrad;
    private int sjukskrivningslangd;
    private int lakarkon;
    private int lakaralder;
    private int[] lakarbefattnings;
    private int lakarid;

    private String vardgivareId;
    private String vardgivareNamn;
    private String enhetId;
    private String enhetNamn;
    private String lansId;
    private String kommunId;
    private String verksamhetsTyper;

    private String lakareVardgivareId;
    private String lakareId;
    private String lakareTilltalsNamn;
    private String lakareEfterNamn;


    public BiExportData(final Fact fact, final Enhet enhet, final Lakare lakare) {
        lan = fact.getLan();
        kommun = fact.getKommun();
        forsamling = fact.getForsamling();
        this.enhet = fact.getEnhet();
        lakarintyg = fact.getLakarintyg();
        patient = fact.getPatient();
        startdatum = fact.getStartdatum();
        kon = fact.getLakarkon();
        alder = fact.getAlder();
        diagnoskapitel = fact.getDiagnoskapitel();
        diagnosavsnitt = fact.getDiagnosavsnitt();
        diagnoskategori = fact.getDiagnoskategori();
        sjukskrivningsgrad = fact.getSjukskrivningsgrad();
        sjukskrivningslangd = fact.getSjukskrivningslangd();
        lakarkon = fact.getLakarkon();
        lakaralder = fact.getLakaralder();
        lakarbefattnings = fact.getLakarbefattnings();
        lakarid = fact.getLakarid();

        vardgivareId = enhet.getVardgivareId();
        vardgivareNamn = enhet.getVardgivareNamn();
        enhetId = enhet.getEnhetId();
        enhetNamn = enhet.getNamn();
        lansId = enhet.getLansId();
        kommunId = enhet.getKommunId();
        verksamhetsTyper = enhet.getVerksamhetsTyper();

        lakareVardgivareId = lakare.getVardgivareId();
        lakareId = lakare.getLakareId();
        lakareTilltalsNamn = lakare.getTilltalsNamn();
        lakareEfterNamn = lakare.getEfterNamn();
    }

    BiExportData() {
    }

    public int getLan() {
        return lan;
    }

    public int getKommun() {
        return kommun;
    }

    public int getForsamling() {
        return forsamling;
    }

    public int getEnhet() {
        return enhet;
    }

    public long getLakarintyg() {
        return lakarintyg;
    }

    public int getPatient() {
        return patient;
    }

    public int getStartdatum() {
        return startdatum;
    }

    public int getKon() {
        return kon;
    }

    public int getAlder() {
        return alder;
    }

    public int getDiagnoskapitel() {
        return diagnoskapitel;
    }

    public int getDiagnosavsnitt() {
        return diagnosavsnitt;
    }

    public int getDiagnoskategori() {
        return diagnoskategori;
    }

    public int getSjukskrivningsgrad() {
        return sjukskrivningsgrad;
    }

    public int getSjukskrivningslangd() {
        return sjukskrivningslangd;
    }

    public int getLakarkon() {
        return lakarkon;
    }

    public int getLakaralder() {
        return lakaralder;
    }

    public int[] getLakarbefattnings() {
        return lakarbefattnings;
    }

    public int getLakarid() {
        return lakarid;
    }

    public String getVardgivareId() {
        return vardgivareId;
    }

    public String getVardgivareNamn() {
        return vardgivareNamn;
    }

    public String getEnhetId() {
        return enhetId;
    }

    public String getEnhetNamn() {
        return enhetNamn;
    }

    public String getLansId() {
        return lansId;
    }

    public String getKommunId() {
        return kommunId;
    }

    public String getVerksamhetsTyper() {
        return verksamhetsTyper;
    }

    public String getLakareVardgivareId() {
        return lakareVardgivareId;
    }

    public String getLakareId() {
        return lakareId;
    }

    public String getLakareTilltalsNamn() {
        return lakareTilltalsNamn;
    }

    public String getLakareEfterNamn() {
        return lakareEfterNamn;
    }

}
