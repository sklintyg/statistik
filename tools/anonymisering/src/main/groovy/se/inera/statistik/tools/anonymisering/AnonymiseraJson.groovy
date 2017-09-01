package se.inera.statistik.tools.anonymisering

import groovy.transform.InheritConstructors
import se.inera.statistik.tools.anonymisering.base.AnonymiseraJson as IntygJsonAnonymizer

@InheritConstructors
class AnonymiseraJson extends IntygJsonAnonymizer {

    void anonymizeJson(def intyg, String personId) {
        intyg.grundData.patient.personId = personId ?: anonymiseraPersonId.anonymisera(intyg.grundData.patient.personId)
        intyg.grundData.patient.anonymize('fornamn')
        intyg.grundData.patient.anonymize('efternamn')
        intyg.grundData.patient.anonymize('fullstandigtNamn')
        intyg.grundData.skapadAv.personId = anonymiseraHsaId.anonymisera(intyg.grundData.skapadAv.personId)
        intyg.grundData.skapadAv.anonymize('fullstandigtNamn')
        intyg.grundData.skapadAv.anonymize('forskrivarKod')
        if (intyg.undersokningAvPatienten) intyg.undersokningAvPatienten = anonymiseraDatum.anonymiseraDatum(intyg.undersokningAvPatienten)
        if (intyg.telefonkontaktMedPatienten) intyg.telefonkontaktMedPatienten = anonymiseraDatum.anonymiseraDatum(intyg.telefonkontaktMedPatienten)
        if (intyg.journaluppgifter) intyg.journaluppgifter = anonymiseraDatum.anonymiseraDatum(intyg.journaluppgifter)
        if (intyg.annanReferens) intyg.annanReferens = anonymiseraDatum.anonymiseraDatum(intyg.annanReferens)
        intyg.anonymize('kommentar')
        intyg.anonymize('rekommendationOvrigt')
        intyg.anonymize('atgardInomSjukvarden')
        intyg.anonymize('annanAtgard')
        intyg.anonymize('sjukdomsforlopp')
        intyg.anonymize('nuvarandeArbetsuppgifter')

        intyg.anonymize('funktionsnedsattning')
        intyg.anonymize('aktivitetsbegransning')

        intyg.anonymize('arbetsformagaPrognos')
        intyg.anonymize('namnfortydligandeOchAdress')
    }

}
