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

class AnonymiseraXml {

    AnonymiseraPersonId anonymiseraPersonId;
    AnonymiseraHsaId anonymiseraHsaId;
    AnonymiseraDatum anonymiseraDatum;

    AnonymiseraXml(AnonymiseraPersonId anonymiseraPersonId, AnonymiseraHsaId anonymiseraHsaId,
                   AnonymiseraDatum anonymiseraDatum) {
        this.anonymiseraPersonId = anonymiseraPersonId
        this.anonymiseraHsaId = anonymiseraHsaId
        this.anonymiseraDatum = anonymiseraDatum
    }

    String anonymiseraIntygsXml(String s) {
        anonymiseraIntygsXml(s, null)
    }

    String anonymiseraIntygsXml(String s, String personId) {
        def slurper = new XmlSlurper()
        slurper.keepIgnorableWhitespace = true
        def intyg = slurper.parseText(s)
        intyg.declareNamespace(
            p3: 'urn:riv:clinicalprocess:healthcond:certificate:types:2',
            p2: 'urn:riv:clinicalprocess:healthcond:certificate:2',
            p1: 'urn:riv:clinicalprocess:healthcond:certificate:RegisterCertificateResponder:2')

        anonymizeCertificateXml(intyg, personId)
        return buildOutput(s, intyg)
    }

    String anonymiseraMeddelandeXml(String s, String type) {
        def slurper = new XmlSlurper()
        slurper.keepIgnorableWhitespace = true
        def message = slurper.parseText(s)

        // Anonymisera meddelande
        anonymizeMessageXml(declareNamespaces(message, type))
        return buildOutput(s, message)
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

    private GPathResult declareNamespaces(GPathResult document, String type) {
        switch (type) {
            case "SENT":
                document.declareNamespace(
                    p1: 'urn:riv:clinicalprocess:healthcond:certificate:SendMessageToCareResponder:2',
                    p2: 'urn:riv:clinicalprocess:healthcond:certificate:types:3');
                break;
            default:
                break;
        }

        return document;
    }

    private void anonymizeCertificateXml(def intyg, String personId) {
        anonymizePnrNode intyg.'p1:intyg'.'p2:patient'.'p2:person-id'.'p3:extension' //= personId ?: "anonprn" //anonymiseraPersonId.anonymisera((String) intyg.'p1:intyg'.'p2:patient'.'p2:person-id'.'p3:extension')
        anonymizeNode intyg.'p1:intyg'.'p2:patient'.'p2:fornamn'
        anonymizeNode intyg.'p1:intyg'.'p2:patient'.'p2:efternamn'
        anonymizeNode intyg.'p1:intyg'.'p2:patient'.'p2:postadress'
        anonymizeNode intyg.'p1:intyg'.'p2:patient'.'p2:postnummer'
        anonymizeNode intyg.'p1:intyg'.'p2:patient'.'p2:postort'

        anonymizeHsaIdNode intyg.'p1:intyg'.'p2:skapadAv'.'p2:personal-id'.'p3:extension'
        anonymizeNode intyg.'p1:intyg'.'p2:skapadAv'.'p2:fullstandigtNamn'
        anonymizeNode intyg.'p1:intyg'.'p2:skapadAv'.'p2:forskrivarkod'

        anonymizeDateNode intyg.'p1:intyg'.'p2:svar'.'p2:delsvar'.find{ it.@id == "1.2" }
        anonymizeNode intyg.'p1:intyg'.'p2:svar'.'p2:delsvar'.find{ it.@id == "1.3" }
        anonymizeDateNode intyg.'p1:intyg'.'p2:svar'.'p2:delsvar'.find{ it.@id == "2.1" }
        anonymizeDateNode intyg.'p1:intyg'.'p2:svar'.'p2:delsvar'.find{ it.@id == "4.2" }
        anonymizeNode intyg.'p1:intyg'.'p2:svar'.'p2:delsvar'.find{ it.@id == "4.3" }
        anonymizeNode intyg.'p1:intyg'.'p2:svar'.'p2:delsvar'.find{ it.@id == "5.1" }
        anonymizeNode intyg.'p1:intyg'.'p2:svar'.'p2:delsvar'.find{ it.@id == "6.1" }
        anonymizeNode intyg.'p1:intyg'.'p2:svar'.'p2:delsvar'.find{ it.@id == "7.1" }
        anonymizeNode intyg.'p1:intyg'.'p2:svar'.'p2:delsvar'.find{ it.@id == "8.1" }
        anonymizeNode intyg.'p1:intyg'.'p2:svar'.'p2:delsvar'.find{ it.@id == "9.1" }
        anonymizeNode intyg.'p1:intyg'.'p2:svar'.'p2:delsvar'.find{ it.@id == "15.1" }
        anonymizeNode intyg.'p1:intyg'.'p2:svar'.'p2:delsvar'.find{ it.@id == "16.1" }
        anonymizeNode intyg.'p1:intyg'.'p2:svar'.'p2:delsvar'.find{ it.@id == "17.1" }
        anonymizeNode intyg.'p1:intyg'.'p2:svar'.'p2:delsvar'.find{ it.@id == "18.1" }
        anonymizeNode intyg.'p1:intyg'.'p2:svar'.'p2:delsvar'.find{ it.@id == "19.1" }
        anonymizeNode intyg.'p1:intyg'.'p2:svar'.'p2:delsvar'.find{ it.@id == "20.1" }
        anonymizeNode intyg.'p1:intyg'.'p2:svar'.'p2:delsvar'.find{ it.@id == "21.1" }
        anonymizeNode intyg.'p1:intyg'.'p2:svar'.'p2:delsvar'.find{ it.@id == "22.1" }
        anonymizeNode intyg.'p1:intyg'.'p2:svar'.'p2:delsvar'.find{ it.@id == "23.1" }
        anonymizeNode intyg.'p1:intyg'.'p2:svar'.'p2:delsvar'.find{ it.@id == "24.1" }
        anonymizeNode intyg.'p1:intyg'.'p2:svar'.'p2:delsvar'.find{ it.@id == "25.1" }
        anonymizeNode intyg.'p1:intyg'.'p2:svar'.'p2:delsvar'.find{ it.@id == "26.2" }
        anonymizeNode intyg.'p1:intyg'.'p2:svar'.'p2:delsvar'.find{ it.@id == "29.1" }
        anonymizeNode intyg.'p1:intyg'.'p2:svar'.'p2:delsvar'.find{ it.@id == "33.1" }
        anonymizeNode intyg.'p1:intyg'.'p2:svar'.'p2:delsvar'.find{ it.@id == "33.2" }
        anonymizeNode intyg.'p1:intyg'.'p2:svar'.'p2:delsvar'.find{ it.@id == "35.1" }
        anonymizeNode intyg.'p1:intyg'.'p2:svar'.'p2:delsvar'.find{ it.@id == "39.1" }
        anonymizeNode intyg.'p1:intyg'.'p2:svar'.'p2:delsvar'.find{ it.@id == "39.2" }
        anonymizeNode intyg.'p1:intyg'.'p2:svar'.'p2:delsvar'.find{ it.@id == "40.2" }
        anonymizeNode intyg.'p1:intyg'.'p2:svar'.'p2:delsvar'.find{ it.@id == "44.1" }
        anonymizeNode intyg.'p1:intyg'.'p2:svar'.'p2:delsvar'.find{ it.@id == "45.2" }
    }

    private void anonymizeMessageXml(def message) {
        anonymizePnrNode message."p1:patientPerson-id"."p2:extension"
        anonymizeNode message."p1:meddelande"
    }

    private void anonymizeNode(def node) {
        node?.replaceBody AnonymizeString.anonymize(node.toString())
    }

    private void anonymizeDateNode(def node) {
        node?.replaceBody anonymiseraDatum.anonymiseraDatum(node.toString())
    }

    private void anonymizePnrNode(def node) {
        node?.replaceBody anonymiseraPersonId.anonymisera(node.toString())
    }

    private void anonymizeHsaIdNode(def node) {
        node?.replaceBody anonymiseraHsaId.anonymisera(node.toString())
    }

}
