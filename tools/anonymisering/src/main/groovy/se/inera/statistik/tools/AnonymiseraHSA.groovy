package se.inera.statistik.tools

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovy.sql.Sql
import org.apache.commons.dbcp2.BasicDataSource
import se.inera.statistik.tools.anonymisering.base.AnonymiseraHsaId
import se.inera.statistik.tools.anonymisering.base.AnonymizeString

import java.util.concurrent.atomic.AtomicInteger

class AnonymiseraHSA {

    private BasicDataSource dataSource;

    AnonymiseraHSA(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    void anonymize(AnonymiseraHsaId anonymiseraHsaId) {
        final AtomicInteger count = new AtomicInteger(0)
        final AtomicInteger errorCount = new AtomicInteger(0)

        println "Start anonymization of HSA personnel"
        long start = System.currentTimeMillis()

        def query = '''
        SELECT id 
        FROM hsa
        '''

        def sql = new Sql(dataSource)
        def hsaIds = sql.rows(query)
        println "${hsaIds.size()} HSA personnel found to anonymize"
        sql.close()

        def output = hsaIds.collect {
            StringBuffer result = new StringBuffer()
            sql = new Sql(dataSource)

            def id = it.id
            try {
                def intyg = sql.firstRow('SELECT data FROM hsa WHERE id = :id' , [id : id])
                String jsonDoc = intyg.data
                String anonymiseradJson = anonymizeHsaJson(jsonDoc, anonymiseraHsaId)
                sql.executeUpdate('UPDATE hsa SET data = :document WHERE id = :id',
                    [document: anonymiseradJson, id: id])

                int current = count.addAndGet(1)
                if (current % 10000 == 0) {
                    println "${current} HSA anonymized in ${(int)((System.currentTimeMillis()-start) / 1000)} seconds"
                }
            } catch (Throwable t) {
                t.printStackTrace()

                result << "Anonymization of HSA personnel ${id} failed: ${t}"
                errorCount.incrementAndGet()
            } finally {
                sql.close()
            }
            result.toString()
        }

        long end = System.currentTimeMillis()
        output.each {line ->
            if (line) println line
        }

        println "Done! ${count} HSA personnel anonymized with ${errorCount} errors in ${(int)((end-start) / 1000)} seconds"
    }

    private String anonymizeHsaJson(def s, def anonymiseraHsaId) {
        def hsa = new JsonSlurper().parseText(s)
        if (hsa.personal) {
            hsa.personal.id = anonymiseraHsaId.anonymisera(hsa.personal.id)
            if (hsa.personal.tilltalsnamn) {
                hsa.personal.tilltalsnamn = AnonymizeString.anonymize(hsa.personal.tilltalsnamn)
            }
            if (hsa.personal.efternamn) {
                hsa.personal.efternamn = AnonymizeString.anonymize(hsa.personal.efternamn)
            }
        }
        JsonBuilder builder = new JsonBuilder(hsa)
        return builder.toString()
    }

}



