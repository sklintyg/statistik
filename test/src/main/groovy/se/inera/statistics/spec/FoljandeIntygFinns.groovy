/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
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
        signeringstid = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(DagensDatum.currentDate)
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
                return executeForNewJsonFormat()
            case ~/^(?i)LUSE$/:
                return executeForXmlFormatSit('/luse.xml', "LUSE")
            case ~/^(?i)LUAE_NA$/:
                return executeForXmlFormatSit('/luae_na.xml', "LUAE_NA")
            case ~/^(?i)LUAE_FS$/:
                return executeForXmlFormatSit('/luae_fs.xml', "LUAE_FS")
            case ~/^(?i)LISJP$/:
                return executeForXmlFormat('/lisjp.xml', "LISJP")
            case ~/^(?i)FK7263SIT$/:
                return executeForXmlFormat('/fk7263sit.xml', "fk7263")
            case ~/^(?i)felaktigt.*$/:
                return executeForIllegalIntygFormat()
            case ~/^(?i)DB$/:
                return executeForXmlFormatRegisterCertificate('/db.xml', "DB")
            case ~/^(?i)DOI$/:
                return executeForXmlFormatRegisterCertificate('/doi.xml', "DOI")
            case ~/^(?i)AF00213$/:
                return executeForXmlFormatRegisterCertificate('/af00213.xml', "AF00213")
            case ~/^(?i)AF00251$/:
                return executeForXmlFormatRegisterCertificate('/af00251.xml', "AF00251")
            case ~/^(?i)TSTRK1007$/:
                return executeForXmlFormatRegisterCertificate('/tstrk1007.xml', "TSTRK1007")
            case ~/^(?i)TSTRK1007-oldformat$/:
                return executeForXmlFormatTS('/ts-bas.xml', "ts-bas")
            case ~/^(?i)TSTRK1031-v2$/:
                return executeForXmlFormatTS('/ts-diabetes.xml', "ts-diabetes")
            case ~/^(?i)TSTRK1031-v3$/:
                return executeForXmlFormatRegisterCertificate('/tstrk1031.xml', "TSTRK1031")
            case ~/^(?i)TSTRK1009$/:
                return executeForXmlFormatRegisterCertificate('/tstrk1009.xml', "TSTRK1009")
            case ~/^(?i)TSTRK1062$/:
                return executeForXmlFormatRegisterCertificate('/tstrk1062.xml', "TSTRK1062")
            case ~/^(?i)AG1-14/:
                return executeForXmlFormatRegisterCertificateWithDX('/ag114.xml', "AG1-14", "4", "4.2")
            case ~/^(?i)AG7804/:
                return executeForXmlFormatRegisterCertificateWithDX('/ag7804.xml', "AG7804", "6", "6.2")
            default:
                throw new RuntimeException("Unknown intyg format requested: " + intygformat)
        }
    }

    private String executeForIllegalIntygFormat() {
        return "This intyg will not be possible to parse"
    }

    private String executeForXmlFormatRegisterCertificate(String filepath, String defaultIntygstyp) {
        Node result = handleGeneralRegisterCertificate(filepath, defaultIntygstyp)

        def builder = groovy.xml.XmlUtil.serialize(result)
        return builder.toString()
    }

    private String executeForXmlFormatRegisterCertificateWithDX(String filepath, String defaultIntygstyp, String dxId, String dxDelSvarId) {
        Node result = handleGeneralRegisterCertificate(filepath, defaultIntygstyp)

        def intyg = result.value()[0]

        def svarNodes = findNodes(intyg, "svar")
        setDx(intyg, svarNodes, dxId, dxDelSvarId)

        def builder = groovy.xml.XmlUtil.serialize(result)
        return builder.toString()
    }

    private String executeForXmlFormatSit(String filepath, String defaultIntygstyp) {
        Node result = handleGeneralSit(filepath, defaultIntygstyp)

        def builder = groovy.xml.XmlUtil.serialize(result)
        return builder.toString()
    }

    private String executeForXmlFormatTS(String filepath, String defaultIntygstyp) {
        Node result = readXmlFormat(filepath)
        def intyg = result.value()[0]

        def grundDataNode = findNode(intyg, "grundData")

        def patientNode = findNode(grundDataNode, "patient")

        def patientPersonIdNode = findNode(patientNode, "personId")
        setExtension(patientPersonIdNode, personnr)
        setLeafValue(intyg, "intygsTyp", intygstyp != null ? intygstyp : defaultIntygstyp)

        setLeafValue(grundDataNode, "signeringsTidstampel", signeringstid)

        def skapadAvNode = findNode(grundDataNode, "skapadAv")
        setExtension(findNode(skapadAvNode, "personId"), läkare)

        def skapadAvEnhetNode = findNode(skapadAvNode, "vardenhet")
        setExtension(findNode(skapadAvEnhetNode, "enhetsId"), enhet)

        def skapadAvEnhetVgNode = findNode(skapadAvEnhetNode, "vardgivare")
        setExtension(findNode(skapadAvEnhetVgNode, "vardgivarid"), vardgivare)

        def builder = groovy.xml.XmlUtil.serialize(result)
        return builder.toString()
    }

    private Node readXmlFormat(String filepath) {
        def slurper = new XmlParser(false, true)
        String intygString = getClass().getResource(filepath).getText('UTF-8')
        return slurper.parseText(intygString)
    }

    private Node handleGeneralRegisterCertificate(String filepath, String defaultIntygstyp) {
        Node result = readXmlFormat(filepath)
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

    private Node handleGeneralSit(String filepath, String defaultIntygstyp) {
        def result = handleGeneralRegisterCertificate(filepath, defaultIntygstyp)

        def intyg = result.value()[0]

        def svarNodes = findNodes(intyg, "svar")
        setDx(intyg, svarNodes, "6", "6.2")

        return result
    }

    private String executeForXmlFormat(String filepath, String defaultIntygstyp) {
        Node result = handleGeneralSit(filepath, defaultIntygstyp)
        def intyg = result.value()[0]

        def svarNodes = findNodes(intyg, "svar")
        setDx(intyg, svarNodes, "6", "6.2")

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

    private void setDx(intyg, svarNodes, String svarId, String delsvarId) {
        def svar = svarNodes.find { it.@id == svarId }
        if ("UTANDIAGNOSKOD".equalsIgnoreCase(diagnoskod)) {
            if (svar != null) {
                intyg.remove(svar)
            }
        } else {
            def dxCodeSvarNode = svar
            def dxCodeNode = dxCodeSvarNode.find { it.@id == delsvarId }.value()[0]
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
