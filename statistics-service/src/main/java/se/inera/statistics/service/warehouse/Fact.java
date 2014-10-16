package se.inera.statistics.service.warehouse;

import org.joda.time.LocalDate;

import com.fasterxml.jackson.databind.JsonNode;

public class Fact {
    public static final String HEADING = "lan;kommun;forsamling;enhet;lakarintyg;patient;startdatum;kon;alder;diagnoskapitel;"
            + "diagnosavsnitt;diagnoskategori;sjukskrivningsgrad;sjukskrivningslangd;lakarkon;lakaralder;lakarbefattning";

    private static final LocalDate ERA = new LocalDate("2000-01-01");

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
    private int lakarbefattning;

    // CHECKSTYLE:OFF ParameterNumber
    public Fact(int lan, int kommun, int forsamling, int enhet, long lakarintyg, int patient, int startdatum, int kon, int alder, int diagnoskapitel, int diagnosavsnitt, int diagnoskategori, int sjukskrivningsgrad, int sjukskrivningslangd, int lakarkon, int lakaralder, int lakarbefattning) {
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
        this.lakarbefattning = lakarbefattning;
    }
    // CHECKSTYLE:ON ParameterNumber

    public Fact(JsonNode node) {
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

    public int getLakarbefattning() {
        return lakarbefattning;
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
                + ", lakarbefattning=" + lakarbefattning
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
                .append(lakarbefattning).append('\n');
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
        private int lakarbefattning = -1;

        public Fact build() {
            if (lan == -1 || kommun == -1 || forsamling == -1 || enhet == -1 || lakarintyg == -1 || patient == -1 ||
                    startdatum == -1 || kon == -1 || alder == -1 || diagnoskapitel == -1 || diagnosavsnitt == -1 ||
                    diagnoskategori == -1 || sjukskrivningsgrad == -1 || sjukskrivningslangd == -1 || lakarkon == -1 ||
                    lakaralder == -1 || lakarbefattning == -1) {
                throw new RuntimeException("unitialized values");
            }
            return new Fact(lan, kommun, forsamling, enhet, lakarintyg, patient, startdatum, kon, alder, diagnoskapitel,
                    diagnosavsnitt, diagnoskategori, sjukskrivningsgrad, sjukskrivningslangd, lakarkon,
                    lakaralder, lakarbefattning);
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

        public FactBuilder withKon(int kon) {
            this.kon = kon;
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

        public FactBuilder withLakarkon(int lakarkon) {
            this.lakarkon = lakarkon;
            return this;
        }

        public FactBuilder withLakaralder(int lakaralder) {
            this.lakaralder = lakaralder;
            return this;
        }

        public FactBuilder withLakarbefatttning(int lakarbefattning) {
            this.lakarbefattning = lakarbefattning;
            return this;
        }
    }

    public static FactBuilder aFact() {
        return new FactBuilder();
    }

}
