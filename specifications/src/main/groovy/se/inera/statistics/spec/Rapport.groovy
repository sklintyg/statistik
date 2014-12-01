package se.inera.statistics.spec

import se.inera.statistics.web.reports.ReportsUtil

abstract class Rapport {

    String inloggadSom;
    boolean inloggad;
    int män
    int kvinnor
    boolean vårdgivarnivå

    ReportsUtil reportsUtil = new ReportsUtil()

    int män() {
        return män
    }

    int kvinnor() {
        return kvinnor
    }

    void setVårdgivarnivå(boolean vårdgivarnivå) {
        this.vårdgivarnivå = vårdgivarnivå
    }

    public void reset() {
        inloggad = false
    }

    void setKommentar(String kommentar) {
    }

    void setInloggadSom(String inloggadSom) {
        if (inloggadSom != null && !inloggadSom.isEmpty()) {
            inloggad = true
            reportsUtil.login(inloggadSom, vårdgivarnivå)
            this.inloggadSom = inloggadSom
        }
    }

}
