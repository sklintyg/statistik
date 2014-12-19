package se.inera.statistics.spec

import se.inera.statistics.web.reports.ReportsUtil

abstract class Rapport {

    String inloggadSom
    boolean inloggad
    def män
    def kvinnor
    def totalt
    boolean vårdgivarnivå
    protected kapitel = []
    protected avsnitt = []
    protected kategorier = []
    protected enheter = []
    protected verksamhetstyper = []

    ReportsUtil reportsUtil = new ReportsUtil()

    public final void execute() {
        doLoginIfRequested()
        doExecute()
    }

    abstract void doExecute()

    def män() {
        return män
    }

    def kvinnor() {
        return kvinnor
    }

    def totalt() {
        return totalt
    }

    void setVårdgivarnivå(boolean vårdgivarnivå) {
        this.vårdgivarnivå = vårdgivarnivå
    }

    void setKapitel(String kapitelString) {
        if (kapitelString != null) {
            this.kapitel = kapitelString.tokenize(',')*.trim()
        }
    }

    void setAvsnitt(String avsnittString) {
        if (avsnittString != null) {
            this.avsnitt = avsnittString.tokenize(',')*.trim()
        }
    }

    void setKategorier(String kategoriString) {
        if (kategoriString != null) {
            this.kategorier = kategoriString.tokenize(',')*.trim()
        }
    }

    void setEnheter(String enhetsString) {
        if (enhetsString != null) {
            this.enheter = enhetsString.tokenize(',')*.trim()
        }
    }

    void setVerksamhetstyper(String verksamhetString) {
        if (verksamhetString != null) {
            this.verksamhetstyper = verksamhetString.tokenize(',')*.trim()
        }
    }

    public void reset() {
        inloggadSom = null
        inloggad = false
        totalt = -1
        män = -1
        kvinnor = -1
        kapitel = []
        avsnitt = []
        kategorier = []
        enheter = []
        verksamhetstyper = []
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
