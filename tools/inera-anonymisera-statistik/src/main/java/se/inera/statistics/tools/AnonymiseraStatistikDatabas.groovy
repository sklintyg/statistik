package se.inera.statistics.tools

import groovy.sql.Sql
import groovyx.gpars.GParsPool

import java.util.concurrent.atomic.AtomicInteger

import org.apache.commons.dbcp2.BasicDataSource

import se.inera.certificate.tools.anonymisering.AnonymiseraDatum;
import se.inera.certificate.tools.anonymisering.AnonymiseraHsaId;
import se.inera.certificate.tools.anonymisering.AnonymiseraJson;
import se.inera.certificate.tools.anonymisering.AnonymiseraPersonId;

class AnonymiseraStatistikDatabas {

    static void main(String[] args) {
        println "Starting anonymization"
        
        int numberOfThreads = args.length > 0 ? args[0] : 5
        long start = System.currentTimeMillis()
        AnonymiseraPersonId anonymiseraPersonId = new AnonymiseraPersonId()
        AnonymiseraHsaId anonymiseraHsaId = new AnonymiseraHsaId()
        AnonymiseraDatum anonymiseraDatum = new AnonymiseraDatum()
        AnonymiseraJson anonymiseraJson = new AnonymiseraJson(anonymiseraHsaId, anonymiseraDatum, anonymiseraPersonId)
        def props = new Properties()
        new File("dataSource.properties").withInputStream {
          stream -> props.load(stream)
        }
        def config = new ConfigSlurper().parse(props)
        BasicDataSource dataSource =
            new BasicDataSource(driverClassName: config.dataSource.driver, url: config.dataSource.url,
                                username: config.dataSource.username, password: config.dataSource.password,
                                initialSize: numberOfThreads, maxTotal: numberOfThreads)
        def bootstrapSql = new Sql(dataSource)
        def certificateIds = bootstrapSql.rows("select correlationId from intyghandelse")
        def personIds = bootstrapSql.rows("select id, personId from intyghandelse")
        bootstrapSql.close()
        println "${certificateIds.size()} certificates found to anonymize"
        println "${personIds.size()} sjukfall found to anonymize"
        final AtomicInteger count = new AtomicInteger(0)
        final AtomicInteger errorCount = new AtomicInteger(0)
        def output
        GParsPool.withPool(numberOfThreads) {
            output = certificateIds.collectParallel {
                StringBuffer result = new StringBuffer() 
                def id = it.correlationId
                Sql sql = new Sql(dataSource)
                try {
                    def intyg = sql.firstRow( 'select data from intyghandelse where correlationId = :id' , [id : id])
                    String jsonDoc = intyg.data
                    String anonymiseradJson = anonymiseraJson.anonymiseraIntygsJson(jsonDoc)
                    sql.executeUpdate('update intyghandelse set data = :document where correlationId = :id',
                                          [document: anonymiseradJson, id: id])
                    int current = count.addAndGet(1)
                    if (current % 10000 == 0) {
                        println "${current} certificates anonymized in ${(int)((System.currentTimeMillis()-start) / 1000)} seconds"
                    }
                } catch (Throwable t) {
                    t.printStackTrace()

                    result << "Anonymizing ${id} failed: ${t}"
                    errorCount.incrementAndGet()
                } finally {
                    sql.close()
                }
                result.toString()
            }
        }
        long end = System.currentTimeMillis()
        output.each {line ->
            if (line) println line
        }
        for (p in personIds) {
            StringBuffer result = new StringBuffer()
            def anonymPersonId = anonymiseraPersonId(p.personId)
            def id = p.id
            Sql sql = new Sql(dataSource)
            try {
                sql.executeUpdate('update sjukfall set personId = :personId where id = :id',
                        [personId: anonymPersonId, id: id])
                int current = count.addAndGet(1)
                if (current % 10000 == 0) {
                    println "${current} sjukfall anonymized in ${(int)((System.currentTimeMillis()-start) / 1000)} seconds"
                }
            } catch (Throwable t) {
                t.printStackTrace()

                result << "Anonymizing ${id} failed: ${t}"
                errorCount.incrementAndGet()
            } finally {
                sql.close()
            }
            println p
            result.toString()
        }
        println "Done! ${count} certificates anonymized with ${errorCount} errors in ${(int)((end-start) / 1000)} seconds"
    }
}
    


