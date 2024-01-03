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
package se.inera.statistics.spec

import se.inera.statistics.service.processlog.message.MessageEventType
import se.inera.testsupport.Meddelande

import java.text.SimpleDateFormat

class FoljandeMeddelandenFinns extends FoljandeFinns {

    private static int meddelandeIdCounter = 1

    def personnr
    def skickat
    def exaktmeddelandeid
    def händelsetyp
    def intygsid
    def ämne
    def frågeid //Only applicable for Komplettering-type

    public void reset() {
        personnr = "19790407-1295"
        händelsetyp = MessageEventType.SENT.name()
        exaktmeddelandeid = meddelandeIdCounter++
        ämne = "KOMPLT"
        skickat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date());
        frågeid = null;
    }

    public void execute() {
        def finalMeddelandeDataString = getMeddelandeDataString()
        Meddelande meddelande = new Meddelande(MessageEventType.valueOf(händelsetyp), finalMeddelandeDataString, String.valueOf(exaktmeddelandeid), System.currentTimeMillis())
        reportsUtil.insertMeddelande(meddelande)
    }

    Object getMeddelandeDataString() {
        return executeForXmlFormatGeneral('/sendMessageToCare.xml');
    }

    private String executeForXmlFormatGeneral(String filepath) {
        def slurper = new groovy.xml.XmlParser(false, true)
        String intygString = getClass().getResource(filepath).getText('UTF-8')
        def result = slurper.parseText(intygString)
        def meddelande = result.value()

        def patientNode = findNode(meddelande, "patientPerson-id")
        setExtension(patientNode, personnr)

        def intygsNode = findNode(meddelande, "intygs-id")
        setExtension(intygsNode, intygsid)

        setValue(findNode(meddelande, "skickatTidpunkt"), skickat)
        setValue(findNode(meddelande, "meddelande-id"), exaktmeddelandeid)
        setLeafValue(findNode(meddelande, "amne"), "code", ämne)

        if (frågeid != null) {
            Node komplNode = result.appendNode("komplettering");
            komplNode.appendNode("frage-id", frågeid);
            komplNode.appendNode("instans", 1);
        }

        def builder = groovy.xml.XmlUtil.serialize(result)
        return builder.toString()
    }

    public void endTable() {
        reportsUtil.processMeddelande()
    }
}
