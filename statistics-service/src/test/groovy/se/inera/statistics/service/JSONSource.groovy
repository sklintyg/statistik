package se.inera.statistics.service

import groovy.json.JsonSlurper

/**
 * Created with IntelliJ IDEA.
 * User: inera
 * Date: 9/24/13
 * Time: 2:48 PM
 * To change this template use File | Settings | File Templates.
 */
class JSONSource {
    static def readTemplate() {
        def slurper = new JsonSlurper()
        def doc = new InputStreamReader(this.getClass().getResourceAsStream("/json/fk7263_M_template.json"))

        def result = slurper.parse(doc)
        result
    }

    static String readTemplateAsString() {
        def doc = new InputStreamReader(this.getClass().getResourceAsStream("/json/fk7263_M_template.json"))
        doc.text
    }

}
