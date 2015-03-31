package se.inera.statistics.service

import groovy.json.JsonSlurper
import se.inera.statistics.service.helper.DocumentHelper.IntygVersion

import static se.inera.statistics.service.helper.DocumentHelper.IntygVersion.VERSION1
import static se.inera.statistics.service.helper.DocumentHelper.IntygVersion.VERSION2

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

    static String readTemplateAsString(IntygVersion version) {
        if (version == VERSION1) {
            def doc = new InputStreamReader(this.getClass().getResourceAsStream("/json/fk7263_M_template.json"))
            doc.text
        } else if (version == VERSION2) {
            def doc = new InputStreamReader(this.getClass().getResourceAsStream("/json/maximalt-fk7263-internal.json"))
            doc.text
        } else {
            return null
        }
    }

    static String readHSASample() {
        readHSASample("hsa_example")
    }

    static String readHSASample(name) {
        def doc = new InputStreamReader(this.getClass().getResourceAsStream("/json/${name}.json"))
        doc.text
    }
}
