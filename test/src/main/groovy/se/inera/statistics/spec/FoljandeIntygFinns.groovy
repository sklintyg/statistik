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
package se.inera.statistics.spec

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import se.inera.statistics.service.helper.SjukskrivningsGrad
import se.inera.statistics.service.processlog.EventType
import se.inera.statistics.web.reports.ReportsUtil
import se.inera.testsupport.Intyg

import java.text.SimpleDateFormat

class FoljandeIntygFinns extends FoljandeFinns {

    private static int intygIdCounter = 1;

    def personnr
    def diagnoskod
    def start
    def slut
    def enhet
    String enhetsnamn
    def huvudenhet
    def vardgivare
    def arbetsförmåga
    String arbetsförmåga2
    def start2
    def slut2
    def läkare
    def län
    def kommun
    def intygid
    String händelsetyp
    String intygformat
    String intygstyp
    String funktionsnedsättning
    String aktivitetsbegränsning
    def signeringstid
    def intygdata

    public void setKommentar(String kommentar) {}

    void setEnhet(enhet) {
        this.enhet = "UTANENHETSID".equalsIgnoreCase(enhet) ? null : enhet
        this.vardgivare = reportsUtil.getVardgivareForEnhet(enhet, ReportsUtil.VARDGIVARE)
    }

    void setDiagnoskod(kod) {
        this.diagnoskod = "NULLDIAGNOSKOD".equalsIgnoreCase(kod) ? null : kod
    }

    public void reset() {
        personnr = "19790407-1295"
        diagnoskod = "A01"
        vardgivare = ReportsUtil.VARDGIVARE
        arbetsförmåga = 0
        arbetsförmåga2 = ""
        läkare = "Personal HSA-ID"
        län = null
        kommun = null
        händelsetyp = EventType.CREATED.name()
        intygid = intygIdCounter++
        intygformat = "FK7263SIT"
        huvudenhet = null
        intygstyp = null
        enhetsnamn = null
        funktionsnedsättning = "Default funktionsnedsattning"
        aktivitetsbegränsning = "Default arbetsbegransning"
        signeringstid = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date());
        start = "2013-08-20"
        slut = "2016-12-09"
        intygdata = null
    }

    public void execute() {
        if (huvudenhet == null || huvudenhet.trim().isEmpty()) {
            huvudenhet = enhet;
        }
        def finalIntygDataString = getIntygDataString()
        Intyg intyg = new Intyg(EventType.valueOf(händelsetyp), finalIntygDataString, String.valueOf(intygid), System.currentTimeMillis(),
                län, kommun, huvudenhet, enhetsnamn, vardgivare, enhet, läkare)
        reportsUtil.insertIntyg(intyg)
    }

    Object getIntygDataString() {
        if (intygdata != null) {
            return intygdata;
        }
        switch (intygformat) {
            case ~/^(?i)NyttJson$/:
                return executeForNewJsonFormat();
            case ~/^(?i)LISU$/:
                return executeForXmlFormat('/lisu.xml', "LISU");
            case ~/^(?i)LUSE$/:
                return executeForXmlFormatGeneral('/luse.xml', "LUSE");
            case ~/^(?i)LUAE_NA$/:
                return executeForXmlFormatGeneral('/luae_na.xml', "LUAE_NA")
            case ~/^(?i)LUAE_FS$/:
                return executeForXmlFormatGeneral('/luae_fs.xml', "LUAE_FS");
            case ~/^(?i)LISJP$/:
                return executeForXmlFormat('/lisjp.xml', "LISJP");
            case ~/^(?i)FK7263SIT$/:
                return executeForXmlFormat('/fk7263sit.xml', "fk7263");
            case ~/^(?i)felaktigt.*$/:
                return executeForIllegalIntygFormat();
            case ~/^(?i)DB$/:
                return executeForXmlFormatRegisterMedical('/db.xml', "DB");
            case ~/^(?i)DOI$/:
                return executeForXmlFormatRegisterMedical('/doi.xml', "DOI");
            default:
                throw new RuntimeException("Unknown intyg format requested")
        }
    }

    private String executeForIllegalIntygFormat() {
        return "This intyg will not be possible to parse"
    }

    private String executeForXmlFormatRegisterMedical(String filepath, String defaultIntygstyp) {
        Node result = handleGeneralRegisterMedical(filepath, defaultIntygstyp)

        def builder = groovy.xml.XmlUtil.serialize(result)
        return builder.toString()
    }

    private Node handleGeneralRegisterMedical(String filepath, String defaultIntygstyp) {
        def slurper = new XmlParser(false, true)
        String intygString = getClass().getResource(filepath).getText('UTF-8')
        def result = slurper.parseText(intygString)
        def intyg = result.value()[0]

        def patientNode = findNode(intyg, "patient")

        def patientPersonIdNode = findNode(patientNode, "person-id")
        setExtension(patientPersonIdNode, personnr)
        setLeafValue(findNode(intyg, "typ"), "code", intygstyp != null ? intygstyp : defaultIntygstyp)

        setLeafValue(intyg, "signeringstidpunkt", signeringstid)

        def skapadAvNode = findNode(intyg, "skapadAv")
        setExtension(findNode(skapadAvNode, "personal-id"), läkare)

        def skapadAvEnhetNode = findNode(skapadAvNode, "enhet")
        setExtension(findNode(skapadAvEnhetNode, "enhets-id"), enhet)

        def skapadAvEnhetVgNode = findNode(skapadAvEnhetNode, "vardgivare")
        setExtension(findNode(skapadAvEnhetVgNode, "vardgivare-id"), vardgivare)

        return result
    }

    private String executeForXmlFormatGeneral(String filepath, String defaultIntygstyp) {
        Node result = handleGeneralSit(filepath, defaultIntygstyp)

        def builder = groovy.xml.XmlUtil.serialize(result)
        return builder.toString()
    }

    private Node handleGeneralSit(String filepath, String defaultIntygstyp) {
        def result = handleGeneralRegisterMedical(filepath, defaultIntygstyp)

        def intyg = result.value()[0]

        def svarNodes = findNodes(intyg, "svar")
        setDx(intyg, svarNodes)

        return result
    }

    private String executeForXmlFormat(String filepath, String defaultIntygstyp) {
        Node result = handleGeneralSit(filepath, defaultIntygstyp)
        def intyg = result.value()[0]

        def svarNodes = findNodes(intyg, "svar")
        setDx(intyg, svarNodes)

        def funktionsnedsattningNode = svarNodes.find{ it.@id=="35" }
        def funktionsnedsattningCodeNode = funktionsnedsattningNode.value().find{it.@id=="35.1"}
        funktionsnedsattningCodeNode.setValue(funktionsnedsättning)

        def aktivitetsbegransningNode = svarNodes.find{ it.@id=="17" }
        def aktivitetsbegransningCodeNode = aktivitetsbegransningNode.value().find{it.@id=="17.1"}
        aktivitetsbegransningCodeNode.setValue(aktivitetsbegränsning)

        def arbetsformagaNode = svarNodes.find{ it.@id=="32" }
        if (arbetsformagaNode != null){
            if (Integer.valueOf(arbetsförmåga) > 0 || (!arbetsförmåga2.isEmpty() && Integer.valueOf(arbetsförmåga2) > 0)) {
                Node node33 = intyg.appendNode("svar", simpleMap("id", "33", "xmlns", "urn:riv:clinicalprocess:healthcond:certificate:2"));
                node33.appendNode("delsvar", simpleMap("id", "33.1"), "false");
            }

            def arbetsformagaCodeNode = arbetsformagaNode.value().find{it.@id=="32.1"}.value()[0]
            setLeafValue(arbetsformagaCodeNode, "code", SjukskrivningsGrad.fromId((Integer.valueOf(arbetsförmåga) / 25).intValue() + 1));
            def arbetsformagaPeriodNode = arbetsformagaNode.value().find{it.@id=="32.2"}.value()[0]
            setLeafValue(arbetsformagaPeriodNode, "start", start)
            setLeafValue(arbetsformagaPeriodNode, "end", slut)

            if (!arbetsförmåga2.isEmpty()) {
                def arbetsformagaNode2 = arbetsformagaNode.clone();
                intyg.append(arbetsformagaNode2);
                def arbetsformagaCodeNode2 = arbetsformagaNode2.value().find{it.@id=="32.1"}.value()[0]
                setLeafValue(arbetsformagaCodeNode2, "code", SjukskrivningsGrad.fromId((Integer.valueOf(arbetsförmåga2) / 25).intValue() + 1));
                def arbetsformagaPeriodNode2 = arbetsformagaNode2.value().find{it.@id=="32.2"}.value()[0]
                setLeafValue(arbetsformagaPeriodNode2, "start", start2)
                setLeafValue(arbetsformagaPeriodNode2, "end", slut2)
            }
        }

        def builder = groovy.xml.XmlUtil.serialize(result)
        return builder.toString()
    }

    private void setDx(intyg, svarNodes) {
        def svar6 = svarNodes.find { it.@id == "6" }
        if ("UTANDIAGNOSKOD".equalsIgnoreCase(diagnoskod)) {
            if (svar6 != null) {
                intyg.remove(svar6)
            }
        } else {
            def dxCodeSvarNode = svar6
            def dxCodeNode = dxCodeSvarNode.find { it.@id == "6.2" }.value()[0]
            setLeafValue(dxCodeNode, "code", diagnoskod)
        }
    }

    private String executeForNewJsonFormat() {
        def slurper = new JsonSlurper()
        String intygString = getClass().getResource('/maximalt-fk7263-internal.json').getText('UTF-8')
        def result = slurper.parseText(intygString)

        result.grundData.signeringsdatum = signeringstid + ".000"

        result.grundData.patient.personId = personnr
        result.typ = intygstyp != null ? intygstyp : "fk7263"
        result.grundData.skapadAv.personId = läkare
        result.grundData.skapadAv.vardenhet.enhetsid = enhet
        result.grundData.skapadAv.vardenhet.vardgivare.vardgivarid = vardgivare

        if ("UTANDIAGNOSKOD".equalsIgnoreCase(diagnoskod)) {
            result.remove("diagnosKod")
        } else {
            result.diagnosKod = diagnoskod
        }

        result.funktionsnedsattning = funktionsnedsättning
        result.aktivitetsbegransning = aktivitetsbegränsning

        result["nedsattMed" + (100 - Integer.valueOf(arbetsförmåga))] = [
            from: start,
            tom: slut
        ]

        if (!arbetsförmåga2.isEmpty()) {
            result["nedsattMed" + (100 - Integer.valueOf(arbetsförmåga2))] = [
                from: start2,
                tom: slut2
            ]
        }

        def builder = new JsonBuilder(result)
        return builder.toString()
    }

    public void endTable() {
        reportsUtil.processIntyg()
    }
}