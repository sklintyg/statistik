/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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

package se.inera.statistik.tools.anonymisering.base

import groovy.util.slurpersupport.GPathResult
import groovy.xml.StreamingMarkupBuilder

class AnonymiseraTsBas {

    AnonymiseraPersonId anonymiseraPersonId;
    AnonymiseraHsaId anonymiseraHsaId;
    AnonymiseraDatum anonymiseraDatum;

    AnonymiseraTsBas(AnonymiseraPersonId anonymiseraPersonId, AnonymiseraHsaId anonymiseraHsaId,
                     AnonymiseraDatum anonymiseraDatum) {
        this.anonymiseraPersonId = anonymiseraPersonId
        this.anonymiseraHsaId = anonymiseraHsaId
        this.anonymiseraDatum = anonymiseraDatum
    }

    String anonymisera(String s) {
        def slurper = new XmlSlurper()
        slurper.keepIgnorableWhitespace = true
        def intyg = slurper.parseText(s)
        intyg.declareNamespace(
            p3: 'urn:local:se:intygstjanster:services:1',
            p2: 'urn:local:se:intygstjanster:services:types:1',
            p1: 'urn:local:se:intygstjanster:services:RegisterTSBasResponder:1')

        anonymizeCertificateXml(intyg)
        return buildOutput(s, intyg)
    }

    private String buildOutput(String s, GPathResult document) {
        def outputBuilder = new StreamingMarkupBuilder()
        outputBuilder.encoding = 'UTF-8'

        return (
            s.startsWith('<?xml') ? '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>\n' : "") +
            outputBuilder.bind {
                mkp.yield document
            }
    }

    private void anonymizeCertificateXml(def intyg) {
        anonymizePnrNode intyg.intyg.grundData.patient.personId.extension
        anonymizeNode intyg.intyg.grundData.patient.fullstandigtNamn
        anonymizeNode intyg.intyg.grundData.patient.fornamn
        anonymizeNode intyg.intyg.grundData.patient.efternamn
        anonymizeNode intyg.intyg.grundData.patient.postadress
        anonymizeNode intyg.intyg.grundData.patient.postnummer
        anonymizeNode intyg.intyg.grundData.patient.postort

        anonymizeHsaIdNode intyg.intyg.grundData.skapadAv.personId.extension
        anonymizeNode intyg.intyg.grundData.skapadAv.fullstandigtNamn
        anonymizeNode intyg.intyg.grundData.skapadAv.forskrivarkod


        anonymizeNode intyg.intyg.rorelseorganensFunktioner.rorelsebegransningBeskrivning
        anonymizeNode intyg.intyg.hjartKarlSjukdomar.riskfaktorerStrokeBeskrivning
        anonymizeNode intyg.intyg.medvetandestorning.medvetandestorningBeskrivning
        anonymizeNode intyg.intyg.alkoholNarkotikaLakemedel.lakarordineratLakemedelOchDos
        anonymizeNode intyg.intyg.sjukhusvard.sjukhusvardEllerLakarkontaktDatum
        anonymizeNode intyg.intyg.sjukhusvard.sjukhusvardEllerLakarkontaktVardinrattning
        anonymizeNode intyg.intyg.sjukhusvard.sjukhusvardEllerLakarkontaktAnledning
        anonymizeNode intyg.intyg.ovrigMedicinering.stadigvarandeMedicineringBeskrivning
        anonymizeNode intyg.intyg.ovrigKommentar
        anonymizeNode intyg.intyg.bedomning.behovAvLakareSpecialistKompetens
    }

    private void anonymizeNode(def node) {
        node?.replaceBody AnonymizeString.anonymize(node.toString())
    }

    private void anonymizePnrNode(def node) {
        node?.replaceBody anonymiseraPersonId.anonymisera(node.toString())
    }

    private void anonymizeHsaIdNode(def node) {
        node?.replaceBody anonymiseraHsaId.anonymisera(node.toString())
    }

}
