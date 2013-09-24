package se.inera.statistics.service

import groovy.json.JsonSlurper
import org.junit.Test
import static org.junit.Assert.*

class ReadJSONTest {
    @Test
    void read_fk7263_M_template() {
        def slurper = new JsonSlurper()
        def doc = new InputStreamReader(this.getClass().getResourceAsStream("/json/fk7263_M_template.json"))

        def result = slurper.parse(doc)

        println result
        assertEquals "2011-01-24", result.validFromDate
        assertEquals "74964007", result.referenser[0].referenstyp.code

    }
}
