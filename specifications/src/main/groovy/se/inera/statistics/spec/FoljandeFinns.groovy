package se.inera.statistics.spec

import se.inera.statistics.web.reports.ReportsUtil

abstract class FoljandeFinns {

    protected final ReportsUtil reportsUtil = new ReportsUtil()

    def Object findNode(parent, String nodeName) {
        return parent.find { it.name().localPart.equals(nodeName) }
    }

    def Object findNodes(parent, String nodeName) {
        return parent.findAll { it.name().localPart.equals(nodeName) }
    }

    def setLeafValue(Node node, String leafName, def value) {
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
}