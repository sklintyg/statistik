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
}
