package se.inera.statistics.service.warehouse;

import org.joda.time.Days;
import org.joda.time.LocalDate;

import com.fasterxml.jackson.databind.JsonNode;

import se.inera.statistics.service.helper.DocumentHelper;
import se.inera.statistics.service.helper.HSAServiceHelper;

public class Fact {
    public static final String HEADING = "lan;kommun;forsamling;enhet;lakarintyg;patient;kalenderperiod;kon;alder;diagnoskapitel;"
            + "diagnosavsnitt;diagnoskategori;sjukskrivningsgrad;sjukskrivningslangd;lakarkon;lakaralder;lakarbefattning";

    private static final LocalDate ERA = new LocalDate("2000-01-01");
    int lan;
    int kommun;
    int forsamling;
    int enhet;
    int lakarintyg;
    int patient;
    int kalenderperiod;
    int kon;
    int alder;
    int diagnoskapitel;
    int diagnosavsnitt;
    int diagnoskategori;
    int sjukskrivningsgrad;
    int sjukskrivningslangd;
    int lakarkon;
    int lakaralder;
    int lakarbefattning;

    public Fact(int lan, int kommun, int forsamling, int enhet, int lakarintyg, int patient, int kalenderperiod, int kon, int alder, int diagnoskapitel, int diagnosavsnitt, int diagnoskategori, int sjukskrivningsgrad, int sjukskrivningslangd, int lakarkon, int lakaralder, int lakarbefattning) {
        this.lan = lan;
        this.kommun = kommun;
        this.forsamling = forsamling;
        this.enhet = enhet;
        this.lakarintyg = lakarintyg;
        this.patient = patient;
        this.kalenderperiod = kalenderperiod;
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

    public Fact(JsonNode node) {
    }


    public int getPatient() {
        return patient;
    }

    public int getKalenderperiod() {
        return kalenderperiod;
    }

    public int getSjukskrivningsgrad() {
        return sjukskrivningsgrad;
    }

    @Override
    public String toString() {
        return "Fact{" +
                "lan=" + lan +
                ", kommun=" + kommun +
                ", forsamling=" + forsamling +
                ", enhet=" + enhet +
                ", lakarintyg=" + lakarintyg +
                ", patient=" + patient +
                ", kalenderperiod=" + kalenderperiod +
                ", kon=" + kon +
                ", alder=" + alder +
                ", diagnoskapitel=" + diagnoskapitel +
                ", diagnosavsnitt=" + diagnosavsnitt +
                ", diagnoskategori=" + diagnoskategori +
                ", sjukskrivningsgrad=" + sjukskrivningsgrad +
                ", sjukskrivningslangd=" + sjukskrivningslangd +
                ", lakarkon=" + lakarkon +
                ", lakaralder=" + lakaralder +
                ", lakarbefattning=" + lakarbefattning +
                '}';
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
                .append(kalenderperiod).append(c)
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
}
