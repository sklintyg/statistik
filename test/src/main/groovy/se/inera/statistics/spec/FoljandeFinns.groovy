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

    Map simpleMap(Object key, Object value) {
        def map = new HashMap()
        map.put(key, value);
        return map;
    }

    Map simpleMap(Object key, Object value, Object key2, Object value2) {
        def map = new HashMap()
        map.put(key, value);
        map.put(key2, value2);
        return map;
    }

}
