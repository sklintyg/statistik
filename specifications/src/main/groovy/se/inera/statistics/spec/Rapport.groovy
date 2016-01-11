package se.inera.statistics.spec

import se.inera.statistics.service.report.util.Icd10
import se.inera.statistics.service.report.util.Icd10RangeType
import se.inera.statistics.web.reports.ReportsUtil
import se.inera.statistics.web.service.FilterData
import se.inera.statistics.web.service.ResponseHandler

abstract class Rapport {

    String inloggadSom
    boolean inloggad
    def män
    def kvinnor
    def totalt
    Boolean markerad
    boolean vårdgivarnivå
    def filterKapitel
    def filterAvsnitt
    def filterKategorier
    def filterEnheter
    def filterVerksamhetstyper
    def filterStartdatum
    def filterSlutdatum
    def meddelande
    def allaDiagnosfilterValda
    def allaEnhetsfilterValda

    ReportsUtil reportsUtil = new ReportsUtil()

    public final void execute() {
        doLoginIfRequested()
        doExecute()
    }

    public final void executeWithReport(report) {
        meddelande = report.message
        allaDiagnosfilterValda = report[ResponseHandler.ALL_AVAILABLE_DXS_SELECTED_IN_FILTER]
        allaEnhetsfilterValda = report[ResponseHandler.ALL_AVAILABLE_ENHETS_SELECTED_IN_FILTER]
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

    def meddelande() {
        return meddelande
    }

    def allaDiagnosfilterValda() {
        return allaDiagnosfilterValda
    }

    def allaEnhetsfilterValda() {
        return allaEnhetsfilterValda
    }

    def markerad() {
        if (markerad == null) {
            return null
        }
        return markerad ? "ja" : "nej"
    }

    void setVårdgivarnivå(boolean vårdgivarnivå) {
        this.vårdgivarnivå = vårdgivarnivå
    }

    void setFilterKapitel(String kapitelString) {
        if (kapitelString != null && !kapitelString.trim().isEmpty()) {
            this.filterKapitel = kapitelString.split(",")*.trim().collect{
                String.valueOf(Icd10.icd10ToInt(it, Icd10RangeType.KAPITEL))
            }
        }
    }

    void setFilterAvsnitt(String avsnittString) {
        if (avsnittString != null && !avsnittString.trim().isEmpty()) {
            this.filterAvsnitt = avsnittString.split(",")*.trim().collect{
                String.valueOf(Icd10.icd10ToInt(it, Icd10RangeType.AVSNITT))
            }
        }
    }

    void setFilterKategorier(String kategoriString) {
        if (kategoriString != null && !kategoriString.trim().isEmpty()) {
            this.filterKategorier = kategoriString.split(",")*.trim().collect{
                String.valueOf(Icd10.icd10ToInt(it, Icd10RangeType.KATEGORI))
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
        def diagnoser = new ArrayList<String>();
        if (filterKapitel != null) {
            diagnoser.addAll(filterKapitel)
        }
        if (filterAvsnitt != null) {
            diagnoser.addAll(filterAvsnitt)
        }
        if (filterKategorier != null) {
            diagnoser.addAll(filterKategorier)
        }
        return new FilterData(diagnoser, filterEnheter, filterVerksamhetstyper, filterStartdatum, filterSlutdatum, filterStartdatum == null || filterSlutdatum == null)
    }

    public void reset() {
        inloggadSom = null
        inloggad = false
        totalt = -1
        män = -1
        kvinnor = -1
        markerad = null
        filterKapitel = null
        filterAvsnitt = null
        filterKategorier = null
        filterEnheter = null
        filterVerksamhetstyper = null
        filterStartdatum = null
        filterSlutdatum = null
        meddelande = null
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

    def getVerksamhetsoversikt() {
        if (inloggad) {
            return reportsUtil.getVerksamhetsoversikt(filter);
        }
        return reportsUtil.getNationalOverview();
    }

}
