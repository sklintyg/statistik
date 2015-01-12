package se.inera.statistics.spec

import se.inera.statistics.service.report.util.Icd10
import se.inera.statistics.service.report.util.Icd10RangeType
import se.inera.statistics.web.reports.ReportsUtil
import se.inera.statistics.web.service.ReportRequestFilter

abstract class Rapport {

    String inloggadSom
    boolean inloggad
    def män
    def kvinnor
    def totalt
    boolean vårdgivarnivå
    def filterKapitel
    def filterAvsnitt
    def filterKategorier
    def filterEnheter
    def filterVerksamhetstyper

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

    void setFilterKapitel(String kapitelString) {
        if (kapitelString != null && !kapitelString.trim().isEmpty()) {
            this.filterKapitel = kapitelString.split(",")*.trim().collect{
                Icd10.icd10ToInt(it, Icd10RangeType.KAPITEL)
            }
        }
    }

    void setFilterAvsnitt(String avsnittString) {
        if (avsnittString != null && !avsnittString.trim().isEmpty()) {
            this.filterAvsnitt = avsnittString.split(",")*.trim().collect{
                Icd10.icd10ToInt(it, Icd10RangeType.AVSNITT)
            }
        }
    }

    void setFilterKategorier(String kategoriString) {
        if (kategoriString != null && !kategoriString.trim().isEmpty()) {
            this.filterKategorier = kategoriString.split(",")*.trim().collect{
                Icd10.icd10ToInt(it, Icd10RangeType.KATEGORI)
            }
        }
    }

    void setFilterEnheter(String enhetsString) {
        if (enhetsString != null && !enhetsString.trim().isEmpty()) {
            this.filterEnheter = enhetsString.split(",")*.trim()
        }
    }

    void setFilterVerksamhetstyper(String verksamhetString) {
        if (verksamhetString != null && !verksamhetString.trim().isEmpty()) {
            this.filterVerksamhetstyper = verksamhetString.split(",")*.trim()
        }
    }

    def getFilter() {
        return new ReportRequestFilter(filterKapitel, filterAvsnitt, filterKategorier, filterEnheter, filterVerksamhetstyper)
    }

    public void reset() {
        inloggadSom = null
        inloggad = false
        totalt = -1
        män = -1
        kvinnor = -1
        filterKapitel = null
        filterAvsnitt = null
        filterKategorier = null
        filterEnheter = null
        filterVerksamhetstyper = null
    }

    void setKommentar(String kommentar) {
        //Do nothing to ignore comments column
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
