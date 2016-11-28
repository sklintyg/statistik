package se.inera.statistics.spec

import se.inera.statistics.service.processlog.message.MessageEventType
import se.inera.statistics.web.reports.ReportsUtil
import se.inera.testsupport.Meddelande

class FoljandeMeddelandenFinns {

    protected final ReportsUtil reportsUtil = new ReportsUtil()

    private static int meddelandeIdCounter = 1

    def personnr
    def skickat
    def exaktmeddelandeid
    def händelsetyp
    def intygsid

    public void reset() {
        personnr = "19790407-1295"
        händelsetyp = MessageEventType.SENT.name()
        exaktmeddelandeid = meddelandeIdCounter++
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
        def slurper = new XmlParser(false, true)
        String intygString = getClass().getResource(filepath).getText('UTF-8')
        def result = slurper.parseText(intygString)
        def meddelande = result.value()

        def patientNode = findNode(meddelande, "patientPerson-id")
        setExtension(patientNode, personnr)

        def intygsNode = findNode(meddelande, "intygs-id")
        setExtension(intygsNode, intygsid)

        setValue(findNode(meddelande, "skickatTidpunkt"), skickat)
        setValue(findNode(meddelande, "meddelande-id"), exaktmeddelandeid)

        def builder = groovy.xml.XmlUtil.serialize(result)
        return builder.toString()
    }

    private Object findNode(parent, String nodeName) {
        println nodeName

        return parent.find { it.name().localPart.equals(nodeName) }
    }

    def setLeafValue(Node node, String leafName, def value) {
        println leafName
        def leafNode = node.value().find {
            def localpart = it.name().localPart
            leafName.equalsIgnoreCase(localpart)
        }
        leafNode.setValue(value)
    }

    def setValue(Node node, def value) {
        node.setValue(value)
    }

    def setExtension(Node node, def value) {
        setLeafValue(node, "extension", value)
    }

    public void endTable() {
        reportsUtil.processMeddelande()
    }
}
