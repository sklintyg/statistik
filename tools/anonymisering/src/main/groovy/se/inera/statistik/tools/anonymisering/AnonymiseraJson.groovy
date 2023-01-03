/*
 * Copyright (C) 2023 Inera AB (http://www.inera.se)
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
