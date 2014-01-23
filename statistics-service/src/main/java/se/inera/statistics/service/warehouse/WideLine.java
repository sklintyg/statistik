package se.inera.statistics.service.warehouse;

import com.fasterxml.jackson.databind.JsonNode;
import se.inera.statistics.service.helper.DocumentHelper;

public class WideLine {
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

    public WideLine(int lan, int kommun, int forsamling, int enhet, int lakarintyg, int patient, int kalenderperiod, int kon, int alder, int diagnoskapitel, int diagnosavsnitt, int diagnoskategori, int sjukskrivningsgrad, int sjukskrivningslangd, int lakarkon, int lakaralder, int lakarbefattning) {
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

    public WideLine(JsonNode node) {
    }


    @Override
    public String toString() {
        return "WideLine{" +
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
}
