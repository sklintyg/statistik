/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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


import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlSlurper
import groovy.xml.slurpersupport.GPathResult

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
        def slurper = new XmlSlurper()
        slurper.keepIgnorableWhitespace = true
        def intyg = slurper.parseText(s)
        intyg.declareNamespace(
                p3: 'urn:riv:clinicalprocess:healthcond:certificate:types:2',
                p2: 'urn:riv:clinicalprocess:healthcond:certificate:2',
                p1: 'urn:riv:clinicalprocess:healthcond:certificate:RegisterCertificateResponder:2')

        anonymizeCertificateXml(intyg)
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

    private void anonymizeCertificateXml(def intyg) {
        anonymizePnrNode intyg.intyg.patient.'person-id'.extension
        anonymizeNode intyg.intyg.patient.fornamn
        anonymizeNode intyg.intyg.patient.efternamn
        anonymizeNode intyg.intyg.patient.postadress
        anonymizeNode intyg.intyg.patient.postnummer
        anonymizeNode intyg.intyg.patient.postort

        anonymizeHsaIdNode intyg.intyg.skapadAv.'personal-id'.extension
        anonymizeNode intyg.intyg.skapadAv.fullstandigtNamn
        anonymizeNode intyg.intyg.skapadAv.forskrivarkod

        def allDelsvars = intyg.intyg.svar.delsvar.collect { it.@id }
        def dateDelsvars = []
        def delsvarsNotToAnonymize = []

        def fkIntyg = ['fk7263', 'luse', 'lisjp', 'luae_fs', 'luae_na']

        if (fkIntyg.contains(intyg.intyg.typ.code.toString().toLowerCase())) {
            dateDelsvars = ["1.2", "2.1", "4.2"]
            delsvarsNotToAnonymize = ["1.1",
                                      "3.1",
                                      "4.1",
                                      "6.2", "6.3", "6.4",
                                      "26.1",
                                      "27.1",
                                      "28.1",
                                      "32.1", "32.2",
                                      "33.1",
                                      "34.1",
                                      "40.1",
                                      "45.1"]
        }

        def delsvarsToAnonymize = allDelsvars.minus(dateDelsvars).minus(delsvarsNotToAnonymize)

        dateDelsvars.each { delsvarid ->
            intyg.intyg.svar.delsvar.findAll { it.@id == delsvarid }.each { node ->
                anonymizeDateNode node
            }
        }
        delsvarsToAnonymize.each { delsvarid ->
            intyg.intyg.svar.delsvar.findAll { it.@id == delsvarid }.each { node ->
                anonymizeNode node
            }
        }
    }

    private void anonymizeMessageXml(def message) {
        anonymizePnrNode message."patientPerson-id".extension
        anonymizeNode message.meddelande
        anonymizeNode message.komplettering.text
        anonymizeNode message.skickatAv.kontaktInfo
    }

    private void anonymizeNode(def node) {
        String nodeBody = node.toString()

        // Skip boolean values
        if (nodeBody == "false" || nodeBody == "true") {
            return
        }

        // Skip codeSystem
        if (node.cv != "") {
            return
        }

        node?.replaceBody AnonymizeString.anonymize(nodeBody)
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