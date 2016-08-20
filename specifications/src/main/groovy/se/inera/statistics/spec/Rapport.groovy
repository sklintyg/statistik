package se.inera.statistics.spec

import se.inera.statistics.service.report.util.Icd10
import se.inera.statistics.service.report.util.Icd10RangeType
import se.inera.statistics.service.report.util.SjukfallsLangdGroup
import se.inera.statistics.web.reports.ReportsUtil
import se.inera.statistics.web.service.FilterData
import se.inera.statistics.web.service.ResponseHandler

abstract class Rapport {

    String inloggadSom
    String valdVg
    def loginInfo
    boolean inloggad
    def män
    def kvinnor
    def könTotalt
    def totalt
    Boolean markerad
    boolean vårdgivarnivå
    def filterKapitel
    def filterAvsnitt
    def filterKategorier
    def filterEnheter
    def filterVerksamhetstyper
    def filterSjukskrivningslängd
    def filterStartdatum
    def filterSlutdatum
    def meddelande
    def allaDiagnosfilterValda
    def allaEnhetsfilterValda
    def allaSjukskrivningslängdfilterValda
    def enhetsfilterlista
    def sjukskrivningslangdfilterlista

    ReportsUtil reportsUtil = new ReportsUtil()

    public final void execute() {
        doLoginIfRequested()
        doExecute()
    }

    public final void executeWithReport(report) {
        meddelande = report.message
        allaDiagnosfilterValda = report[ResponseHandler.ALL_AVAILABLE_DXS_SELECTED_IN_FILTER]
        allaEnhetsfilterValda = report[ResponseHandler.ALL_AVAILABLE_ENHETS_SELECTED_IN_FILTER]
        allaSjukskrivningslängdfilterValda = report[ResponseHandler.ALL_AVAILABLE_SJUKSKRIVNINGSLANGDS_SELECTED_IN_FILTER]
        enhetsfilterlista = report[ResponseHandler.FILTERED_ENHETS]
        sjukskrivningslangdfilterlista = report.filter.sjukskrivningslangd
    }

    abstract void doExecute()

    def män() {
        return män
    }

    def kvinnor() {
        return kvinnor
    }

    def könTotalt() {
        return könTotalt
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

    def allaSjukskrivningslängdfilterValda() {
        return allaSjukskrivningslängdfilterValda
    }

    def enhetsfilterlista() {
        return enhetsfilterlista
    }

    def sjukskrivningslangdfilterlista() {
        return sjukskrivningslangdfilterlista
    }

    def markerad() {
        if (markerad == null) {
            return null
        }
        return markerad ? "ja" : "nej"
    }

    def vårdgivarnamn() {
        return loginInfo?.vgs?.get(0).name
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

    void setFilterSjukskrivningslängd(String sjukskrivningslängdString) {
        if (sjukskrivningslängdString != null && !sjukskrivningslängdString.trim().isEmpty()) {
            this.filterSjukskrivningslängd = sjukskrivningslängdString.split(",")*.trim().collect {
                def optionalGroup = SjukfallsLangdGroup.getByName(it)
                optionalGroup.present ? optionalGroup.get().name() : it;
            }
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
        return new FilterData(diagnoser, filterEnheter, filterVerksamhetstyper, filterSjukskrivningslängd, filterStartdatum, filterSlutdatum, filterStartdatum == null || filterSlutdatum == null)
    }

    public void reset() {
        inloggadSom = null
        valdVg = null
        loginInfo = null
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
        filterSjukskrivningslängd = null
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

    def getVg() {
        return valdVg != null ? valdVg : reportsUtil.getVardgivareForUser(inloggadSom)
    }

    void setVg(String vg) {
        this.valdVg = vg
    }

    void doLoginIfRequested() {
        if (inloggadSom != null && !inloggadSom.isEmpty()) {
            reportsUtil.login(inloggadSom, vårdgivarnivå)
            inloggad = true
            loginInfo = reportsUtil.getLoginInfo()
        }
    }

    def getVerksamhetsoversikt() {
        if (inloggad) {
            return reportsUtil.getVerksamhetsoversikt(vg, filter);
        }
        return reportsUtil.getNationalOverview();
    }

}
