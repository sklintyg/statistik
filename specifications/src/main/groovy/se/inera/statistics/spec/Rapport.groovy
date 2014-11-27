package se.inera.statistics.spec

import se.inera.statistics.web.reports.ReportsUtil

abstract class Rapport {

    private String inloggadSom;
    boolean inloggad;
    int män
    int kvinnor

    int män() {
        return män
    }

    int kvinnor() {
        return kvinnor
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
            if (this.inloggadSom != inloggadSom) {
                reportsUtil.login(inloggadSom)
                this.inloggadSom = inloggadSom
            }
        }
    }

}
