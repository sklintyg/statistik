package se.inera.statistics.service

import groovy.json.JsonSlurper

import org.junit.Test

import static org.junit.Assert.*

class ReadJSONTest {
    @Test
    void read_fk7263_M_template() {
        def result = JSONSource.readTemplate()

        println result
        assertEquals "2011-01-24", result.validFromDate
        assertEquals "74964007", result.referenser[0].referenstyp.code

    }

    @Test
    void publish_fk7263_document_to_queue() {
        def doc = JSONSource.readTemplate()

    }

    Object mutateDoc(Object doc) {

    }
}