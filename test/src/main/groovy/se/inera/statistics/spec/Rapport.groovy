/*
 * Copyright (C) 2022 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.spec
import se.inera.statistics.service.report.util.AgeGroup
import se.inera.statistics.service.report.util.Icd10
import se.inera.statistics.service.report.util.Icd10RangeType
import se.inera.statistics.service.report.util.SjukfallsLangdGroup
import se.inera.statistics.service.warehouse.IntygType
import se.inera.statistics.web.error.Message
import se.inera.statistics.web.reports.ReportsUtil
import se.inera.statistics.web.service.dto.FilterData
import se.inera.statistics.web.service.dto.ResponseKeys

abstract class Rapport {

    String inloggadSom
    String valdVg
    def loginInfo
    boolean inloggad
    def män
    def kvinnor
    def gruppTotalt
    def totalt
    def färg
    Boolean markerad
    boolean vårdgivarnivå
    def filterKapitel
    def filterAvsnitt
    def filterKategorier
    def filterKod
    def filterEnheter
    def filterSjukskrivningslängd
    def filterIntygstyp
    def filterÅldersgrupp
    def filterStartdatum
    def filterSlutdatum
    def meddelande
    def allaDiagnosfilterValda
    def allaEnhetsfilterValda
    def allaSjukskrivningslängdfilterValda
    def allaÅldersgruppfilterValda
    def enhetsfilterlista
    def sjukskrivningslangdfilterlista
    def åldersgruppfilterlista

    ReportsUtil reportsUtil = new ReportsUtil()

    public final void execute() {
        doLoginIfRequested()
        doExecute()
    }

    public final void executeWithReport(report) {
        setMeddelande(report.messages)
        allaDiagnosfilterValda = report[ResponseKeys.ALL_AVAILABLE_DXS_SELECTED_IN_FILTER]
        allaEnhetsfilterValda = report[ResponseKeys.ALL_AVAILABLE_ENHETS_SELECTED_IN_FILTER]
        allaSjukskrivningslängdfilterValda = report[ResponseKeys.ALL_AVAILABLE_SJUKSKRIVNINGSLANGDS_SELECTED_IN_FILTER]
        allaÅldersgruppfilterValda = report[ResponseKeys.ALL_AVAILABLE_AGEGROUPS_SELECTED_IN_FILTER]
        enhetsfilterlista = report[ResponseKeys.FILTERED_ENHETS]
        sjukskrivningslangdfilterlista = report?.filter?.sjukskrivningslangd
        åldersgruppfilterlista = report?.filter?.aldersgrupp
    }

    abstract void doExecute()

    def män() {
        return män
    }

    def kvinnor() {
        return kvinnor
    }

    def gruppTotalt() {
        return gruppTotalt
    }

    def totalt() {
        return totalt
    }

    def färg() {
        return färg
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

    def allaÅldersgruppfilterValda() {
        return allaÅldersgruppfilterValda
    }

    def enhetsfilterlista() {
        return enhetsfilterlista
    }

    def sjukskrivningslangdfilterlista() {
        return sjukskrivningslangdfilterlista
    }

    def åldersgruppfilterlista() {
        return åldersgruppfilterlista
    }

    def markerad() {
        if (markerad == null) {
            return null
        }
        return markerad ? "ja" : "nej"
    }

    def vårdgivarnamn() {
        return loginInfo?.vgs?.find{ it.hsaId.equalsIgnoreCase(vg)}?.name
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

    void setFilterKod(String kodString) {
        if (kodString != null && !kodString.trim().isEmpty()) {
            this.filterKod = kodString.split(",")*.trim().collect{
                String.valueOf(Icd10.icd10ToInt(it, Icd10RangeType.KOD))
            }
        }
    }

    void setFilterEnheter(String enhetsString) {
        if (enhetsString != null && !enhetsString.trim().isEmpty()) {
            this.filterEnheter = enhetsString.split(",")*.trim()
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

    void setFilterÅldersgrupp(String aldersgruppString) {
        if (aldersgruppString != null && !aldersgruppString.trim().isEmpty()) {
            this.filterÅldersgrupp = aldersgruppString.split(",")*.trim().collect {
                def optionalGroup = AgeGroup.getByName(it)
                optionalGroup.present ? optionalGroup.get().name() : it;
            }
        }
    }

    void setFilterIntygstyp(String intygstypString) {
        if (intygstypString != null && !intygstypString.trim().isEmpty()) {
            this.filterIntygstyp = intygstypString.split(",")*.trim().collect {
                def optionalType = IntygType.parseStringOptional(it);
                optionalType.present ? optionalType.get().name() : it;
            }
        }
    }

    void setMeddelande(Object meddelanden) {
        if (meddelanden) {
            if (meddelanden instanceof Map<String, Message>) {
                setMeddelandeMap(meddelanden)
            } else if (meddelanden instanceof List<Message>) {
                setMeddelandeList(meddelanden)
            } else {
                this.meddelande = null;
            }
        } else {
            this.meddelande = null;
        }
    }

    void setMeddelandeMap(Map<String, Message> meddelanden) {
        this.meddelande = meddelanden.get("message", null);
    }

    void setMeddelandeList(List<Message> meddelanden) {
        this.meddelande = meddelanden.findAll { it != null }.collect { it.message }.toList().join(',')
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
        if (filterKod != null) {
            diagnoser.addAll(filterKod)
        }
        return new FilterData(diagnoser, filterEnheter, filterSjukskrivningslängd, filterÅldersgrupp, filterIntygstyp, filterStartdatum, filterSlutdatum, filterStartdatum == null || filterSlutdatum == null)
    }

    public void reset() {
        inloggadSom = null
        valdVg = null
        loginInfo = null
        inloggad = false
        totalt = -1
        färg = null
        män = -1
        kvinnor = -1
        markerad = null
        filterKapitel = null
        filterAvsnitt = null
        filterKategorier = null
        filterKod = null
        filterEnheter = null
        filterSjukskrivningslängd = null
        filterIntygstyp = null
        filterÅldersgrupp = null
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
