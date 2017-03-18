package se.inera.statistics.service
/**
 * Created with IntelliJ IDEA.
 * User: inera
 * Date: 9/24/13
 * Time: 2:48 PM
 * To change this template use File | Settings | File Templates.
 */
class JSONSource {

    static String readTemplateAsString() {
        def doc = new InputStreamReader(this.getClass().getResourceAsStream("/json/maximalt-fk7263-internal.json"))
        doc.text
    }

    static String readHSASample() {
        readHSASample("hsa_example")
    }

    static String readHSASample(name) {
        def doc = new InputStreamReader(this.getClass().getResourceAsStream("/json/${name}.json"))
        doc.text
    }
}
