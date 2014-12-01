package se.inera.statistics.spec

import se.inera.statistics.web.reports.ReportsUtil

abstract class Rapport {

    private String inloggadSom;
    boolean inloggad;
    int män
    int kvinnor
    boolean vårdgivarnivå

    int män() {
        return män
    }

    int kvinnor() {
        return kvinnor
    }

    void setVårdgivarnivå(boolean vårdgivarnivå) {
        this.vårdgivarnivå = vårdgivarnivå
    }

    ReportsUtil reportsUtil = new ReportsUtil()

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
