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

import org.joda.time.LocalDate;

import com.fasterxml.jackson.databind.JsonNode;
import se.inera.statistics.service.report.model.Kon;

import java.util.Comparator;

public class Fact {
    public static final String HEADING = "lan;kommun;forsamling;enhet;lakarintyg;patient;startdatum;kon;alder;diagnoskapitel;"
            + "diagnosavsnitt;diagnoskategori;sjukskrivningsgrad;sjukskrivningslangd;lakarkon;lakaralder;lakarbefattning";

    public static final Comparator<Fact> TIME_ORDER = new Comparator<Fact>() {
        @Override
        public int compare(Fact f1, Fact f2) {
            return f1.getStartdatum() - f2.getStartdatum();
        }
    };

    private static final LocalDate ERA = new LocalDate("2000-01-01");

    private int lan;
    private int kommun;
    private int forsamling;
    private int enhet;
    private long lakarintyg;
    private long patient;
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

    // CHECKSTYLE:OFF ParameterNumber
    public Fact(int lan, int kommun, int forsamling, int enhet, long lakarintyg, long patient, int startdatum, int kon, int alder, int diagnoskapitel, int diagnosavsnitt, int diagnoskategori, int sjukskrivningsgrad, int sjukskrivningslangd, int lakarkon, int lakaralder, int[] lakarbefattnings, int lakarid) {
        this.lan = lan;
        this.kommun = kommun;
        this.forsamling = forsamling;
        this.enhet = enhet;
        this.lakarintyg = lakarintyg;
        this.patient = patient;
        this.startdatum = startdatum;
        this.kon = kon;
        this.alder = alder;
        this.diagnoskapitel = diagnoskapitel;
        this.diagnosavsnitt = diagnosavsnitt;
        this.diagnoskategori = diagnoskategori;
        this.sjukskrivningsgrad = sjukskrivningsgrad;
        this.sjukskrivningslangd = sjukskrivningslangd;
        this.lakarkon = lakarkon;
        this.lakaralder = lakaralder;
        this.lakarbefattnings = lakarbefattnings;
        this.lakarid = lakarid;
    }
    // CHECKSTYLE:ON ParameterNumber

    public Fact(JsonNode node) {
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

    public int getSjukskrivningsgrad() {
        return sjukskrivningsgrad;
    }

    public int getEnhet() {
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

    public int getLakarid() {
        return lakarid;
    }

    @Override
    public String toString() {
        return "Fact{"
                + "lan=" + lan
                + ", kommun=" + kommun
                + ", forsamling=" + forsamling
                + ", enhet=" + enhet
                + ", lakarintyg=" + lakarintyg
                + ", patient=" + patient
                + ", startdatum=" + startdatum
                + ", kon=" + kon
                + ", alder=" + alder
                + ", diagnoskapitel=" + diagnoskapitel
                + ", diagnosavsnitt=" + diagnosavsnitt
                + ", diagnoskategori=" + diagnoskategori
                + ", sjukskrivningsgrad=" + sjukskrivningsgrad
                + ", sjukskrivningslangd=" + sjukskrivningslangd
                + ", lakarkon=" + lakarkon
                + ", lakaralder=" + lakaralder
                + ", lakarbefattnings=" + lakarbefattnings
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
                .append(enhet).append(c)
                .append(lakarintyg).append(c)
                .append(patient).append(c)
                .append(startdatum).append(c)
                .append(alder).append(c)
                .append(diagnoskapitel).append(c)
                .append(diagnosavsnitt).append(c)
                .append(diagnoskategori).append(c)
                .append(sjukskrivningsgrad).append(c)
                .append(sjukskrivningslangd).append(c)
                .append(lakarkon).append(c)
                .append(lakaralder).append(c)
                .append(lakarbefattnings).append(c)
                .append(lakarid).append('\n');
        return sb.toString();
    }

    public int getSjukskrivningslangd() {
        return sjukskrivningslangd;
    }

    public static class FactBuilder {
        private int lan = -1;
        private int kommun = -1;
        private int forsamling = -1;
        private int enhet = -1;
        private long lakarintyg = -1;
        private int patient = -1;
        private int startdatum = -1;
        private int kon = -1;
        private int alder = -1;
        private int diagnoskapitel = -1;
        private int diagnosavsnitt = -1;
        private int diagnoskategori = -1;
        private int sjukskrivningsgrad = -1;
        private int sjukskrivningslangd = -1;
        private int lakarkon = -1;
        private int lakaralder = -1;
        private int[] lakarbefattnings = null;
        private int lakarid = -1;

        public Fact build() {
            if (lan == -1 || kommun == -1 || forsamling == -1 || enhet == -1 || lakarintyg == -1 || patient == -1
                    || startdatum == -1 || kon == -1 || alder == -1 || diagnoskapitel == -1 || diagnosavsnitt == -1
                    || diagnoskategori == -1 || sjukskrivningsgrad == -1 || sjukskrivningslangd == -1 || lakarkon == -1
                    || lakaralder == -1 || lakarbefattnings == null || lakarid == -1) {
                throw new RuntimeException("unitialized values");
            }
            return new Fact(lan, kommun, forsamling, enhet, lakarintyg, patient, startdatum, kon, alder, diagnoskapitel,
                    diagnosavsnitt, diagnoskategori, sjukskrivningsgrad, sjukskrivningslangd, lakarkon,
                    lakaralder, lakarbefattnings, lakarid);
        }

        public FactBuilder withLan(int lan) {
            this.lan = lan;
            return this;
        }

        public FactBuilder withKommun(int kommun) {
            this.kommun = kommun;
            return this;
        }

        public FactBuilder withForsamling(int forsamling) {
            this.forsamling = forsamling;
            return this;
        }

        public FactBuilder withEnhet(int enhet) {
            this.enhet = enhet;
            return this;
        }

        public FactBuilder withLakarintyg(long lakarintyg) {
            this.lakarintyg = lakarintyg;
            return this;
        }

        public FactBuilder withPatient(int patient) {
            this.patient = patient;
            return this;
        }

        public FactBuilder withStartdatum(int startdatum) {
            this.startdatum = startdatum;
            return this;
        }

        public FactBuilder withKon(Kon kon) {
            this.kon = kon == null ? Kon.Unknown.getNumberRepresentation() : kon.getNumberRepresentation();
            return this;
        }

        public FactBuilder withAlder(int alder) {
            this.alder = alder;
            return this;
        }

        public FactBuilder withDiagnoskapitel(int diagnoskapitel) {
            this.diagnoskapitel = diagnoskapitel;
            return this;
        }

        public FactBuilder withDiagnosavsnitt(int diagnosavsnitt) {
            this.diagnosavsnitt = diagnosavsnitt;
            return this;
        }

        public FactBuilder withDiagnoskategori(int diagnoskategori) {
            this.diagnoskategori = diagnoskategori;
            return this;
        }

        public FactBuilder withSjukskrivningsgrad(int sjukskrivningsgrad) {
            this.sjukskrivningsgrad = sjukskrivningsgrad;
            return this;
        }

        public FactBuilder withSjukskrivningslangd(int sjukskrivningslangd) {
            this.sjukskrivningslangd = sjukskrivningslangd;
            return this;
        }

        public FactBuilder withLakarkon(Kon lakarkon) {
            this.lakarkon = lakarkon == null ? Kon.Unknown.getNumberRepresentation() : lakarkon.getNumberRepresentation();
            return this;
        }

        public FactBuilder withLakaralder(int lakaralder) {
            this.lakaralder = lakaralder;
            return this;
        }

        public FactBuilder withLakarbefattning(int[] lakarbefattnings) {
            this.lakarbefattnings = lakarbefattnings;
            return this;
        }

        public FactBuilder withLakarid(int lakarid) {
            this.lakarid = lakarid;
            return this;
        }
    }

    public static FactBuilder aFact() {
        return new FactBuilder();
    }

}
