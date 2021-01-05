/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
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

import com.fasterxml.jackson.databind.JsonNode;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.hsa.model.HsaIdLakare;

public class Fact implements Serializable {

    public static final String HEADING = "lan;kommun;forsamling;enhet;lakarintyg;patient;startdatum;kon;alder;diagnoskapitel;"
        + "diagnosavsnitt;diagnoskategori;sjukskrivningsgrad;sjukskrivningslangd;lakarkon;lakaralder;lakarbefattning";

    static final Comparator<Fact> TIME_ORDER = Comparator.comparingInt(Fact::getStartdatum);

    private long id;
    private int lan;
    private int kommun;
    private int forsamling;
    private HsaIdEnhet vardenhet;
    private HsaIdEnhet enhet;
    private long lakarintyg;
    private long patient;
    private int startdatum;
    private int slutdatum;
    private int kon;
    private int alder;
    private int diagnoskapitel;
    private int diagnosavsnitt;
    private int diagnoskategori;
    private int diagnoskod;
    private int sjukskrivningsgrad;
    private int lakarkon;
    private int lakaralder;
    private int[] lakarbefattnings;
    private HsaIdLakare lakarid;

    // CHECKSTYLE:OFF ParameterNumber
    @java.lang.SuppressWarnings("squid:S00107") // Suppress parameter number warning in Sonar
    public Fact(long id, int lan, int kommun, int forsamling, HsaIdEnhet vardenhet, HsaIdEnhet enhet, long lakarintyg, long patient,
                int startdatum, int slutdatum, int kon, int alder, int diagnoskapitel, int diagnosavsnitt, int diagnoskategori,
                int diagnoskod, int sjukskrivningsgrad, int lakarkon, int lakaralder, int[] lakarbefattnings, HsaIdLakare lakarid) {
        this.id = id;
        this.lan = lan;
        this.kommun = kommun;
        this.forsamling = forsamling;
        this.vardenhet = vardenhet;
        this.enhet = enhet;
        this.lakarintyg = lakarintyg;
        this.patient = patient;
        this.startdatum = startdatum;
        this.kon = kon;
        this.alder = alder;
        this.diagnoskapitel = diagnoskapitel;
        this.diagnosavsnitt = diagnosavsnitt;
        this.diagnoskategori = diagnoskategori;
        this.diagnoskod = diagnoskod;
        this.sjukskrivningsgrad = sjukskrivningsgrad;
        this.slutdatum = slutdatum;
        this.lakarkon = lakarkon;
        this.lakaralder = lakaralder;
        this.lakarbefattnings = lakarbefattnings;
        this.lakarid = lakarid;
    }
    // CHECKSTYLE:ON ParameterNumber

    public Fact(JsonNode node) {
    }

    public long getId() {
        return id;
    }

    public long getLakarintyg() {
        return lakarintyg;
    }

    public long getPatient() {
        return patient;
    }

    public int getStartdatum() {
        return startdatum;
    }

    public int getSlutdatum() {
        return slutdatum;
    }

    public int getSjukskrivningsgrad() {
        return sjukskrivningsgrad;
    }

    public HsaIdEnhet getVardenhet() {
        return vardenhet;
    }

    public HsaIdEnhet getUnderenhet() {
        return enhet;
    }

    public int getForsamling() {
        return forsamling;
    }

    public int getKommun() {
        return kommun;
    }

    public int getLan() {
        return lan;
    }

    public int getDiagnoskategori() {
        return diagnoskategori;
    }

    public int getDiagnosavsnitt() {
        return diagnosavsnitt;
    }

    public int getDiagnoskapitel() {
        return diagnoskapitel;
    }

    public int getDiagnoskod() {
        return diagnoskod;
    }

    public int[] getLakarbefattnings() {
        return lakarbefattnings;
    }

    public int getAlder() {
        return alder;
    }

    public int getLakaralder() {
        return lakaralder;
    }

    public int getLakarkon() {
        return lakarkon;
    }

    public int getKon() {
        return kon;
    }

    public HsaIdLakare getLakarid() {
        return lakarid;
    }

    @Override
    public String toString() {
        return "Fact{"
            + "lan=" + lan
            + ", kommun=" + kommun
            + ", forsamling=" + forsamling
            + ", enhet=" + vardenhet
            + ", lakarintyg=" + lakarintyg
            + ", patient=" + patient
            + ", startdatum=" + WidelineConverter.toDate(startdatum) + " (" + startdatum + ")"
            + ", slutdatum=" + WidelineConverter.toDate(slutdatum) + " (" + slutdatum + ")"
            + ", kon=" + kon
            + ", alder=" + alder
            + ", diagnoskapitel=" + diagnoskapitel
            + ", diagnosavsnitt=" + diagnosavsnitt
            + ", diagnoskategori=" + diagnoskategori
            + ", sjukskrivningsgrad=" + sjukskrivningsgrad
            + ", lakarkon=" + lakarkon
            + ", lakaralder=" + lakaralder
            + ", lakarbefattnings=" + Arrays.toString(lakarbefattnings)
            + ", lakarid=" + lakarid
            + '}';
    }

    /**
     * @param c delimiter
     * @return CSV line including a terminating newline character
     */
    public String toCSVString(char c) {
        StringBuilder sb = new StringBuilder();
        sb.append(lan).append(c)
            .append(kommun).append(c)
            .append(forsamling).append(c)
            .append(vardenhet).append(c)
            .append(lakarintyg).append(c)
            .append(patient).append(c)
            .append(startdatum).append(c)
            .append(slutdatum).append(c)
            .append(alder).append(c)
            .append(diagnoskapitel).append(c)
            .append(diagnosavsnitt).append(c)
            .append(diagnoskategori).append(c)
            .append(diagnoskod).append(c)
            .append(sjukskrivningsgrad).append(c)
            .append(lakarkon).append(c)
            .append(lakaralder).append(c)
            .append(Arrays.toString(lakarbefattnings)).append(c)
            .append(lakarid).append('\n');
        return sb.toString();
    }

    public int getSjukskrivningslangd() {
        return slutdatum - startdatum + 1;
    }


}
