package se.inera.statistics.spec

import se.inera.statistics.web.reports.ReportsUtil

abstract class Rapport {

    String inloggadSom
    boolean inloggad
    int män
    int kvinnor
    int totalt
    boolean vårdgivarnivå

    ReportsUtil reportsUtil = new ReportsUtil()

    public final void execute() {
        doLoginIfRequested()
        doExecute()
    }

    abstract void doExecute()

    int män() {
        return män
    }

    int kvinnor() {
        return kvinnor
    }

    int totalt() {
        return totalt
    }

    void setVårdgivarnivå(boolean vårdgivarnivå) {
        this.vårdgivarnivå = vårdgivarnivå
    }

    public void reset() {
        inloggadSom = null
        inloggad = false
        totalt = -1
        män = -1
        kvinnor = -1
    }

    void setKommentar(String kommentar) {
    }

    void setInloggadSom(String inloggadSom) {
        this.inloggadSom = inloggadSom
    }

    void doLoginIfRequested() {
        if (inloggadSom != null && !inloggadSom.isEmpty()) {
            reportsUtil.login(inloggadSom, vårdgivarnivå)
            inloggad = true
        }
    }

}
