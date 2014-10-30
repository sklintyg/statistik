package se.inera.statistics.tools

import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
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
        println "${certificateIds.size()} certificates found to anonymize"
        def personIds
        try {
            personIds = bootstrapSql.rows("select id, personId from sjukfall")
            println "${personIds.size()} sjukfall found to anonymize"
        } catch (MySQLSyntaxErrorException e) {
            personIds = null
        }
        def hsaIds = bootstrapSql.rows("select id from hsa")
        println "${hsaIds.size()} hsa found to anonymize"
        bootstrapSql.close()
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
        println "Done! ${count} certificates anonymized with ${errorCount} errors in ${(int)((end-start) / 1000)} seconds"
        println "Proceeding to sjukfall"
        if (personIds != null) {
            count.set(0)
            errorCount.set(0)
            start = System.currentTimeMillis()
            GParsPool.withPool(numberOfThreads) {
                output = personIds.collectParallel {p ->
                    StringBuffer result = new StringBuffer()
                    def anonymPersonId = anonymiseraPersonId.anonymisera(p.personId)
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
                    result.toString()
                }
            }
            end = System.currentTimeMillis()
            output.each {line ->
                if (line) println line
            }
            println "Done! ${count} certificates anonymized with ${errorCount} errors in ${(int)((end-start) / 1000)} seconds"
        } else {
            println "Sjukfall table missing, anonymization not necessary"
        }
        println "Proceeding to hsa"
        GParsPool.withPool(numberOfThreads) {
            output = hsaIds.collectParallel {
                StringBuffer result = new StringBuffer()
                Sql sql = new Sql(dataSource)
                def id = it.id
                try {
                    def intyg = sql.firstRow( 'select data from hsa where id = :id' , [id : id])
                    String jsonDoc = intyg.data
                    String anonymiseradJson = anonymizeHsaJson(jsonDoc, anonymiseraHsaId, id)
                    sql.executeUpdate('update hsa set data = :document where id = :id',
                            [document: anonymiseradJson, id: id])
                    int current = count.addAndGet(1)
                    if (current % 10000 == 0) {
                        println "${current} hsa:s anonymized in ${(int)((System.currentTimeMillis()-start) / 1000)} seconds"
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
        end = System.currentTimeMillis()
        output.each {line ->
            if (line) println line
        }
        println "Done! ${count} hsa:s anonymized with ${errorCount} errors in ${(int)((end-start) / 1000)} seconds"
    }

    static def anonymizeHsaJson(def s, def anonymiseraHsaId, def idx) {
        def hsa = new JsonSlurper().parseText(s)
        if (hsa.personal) {
            hsa.personal.id = anonymiseraHsaId.anonymisera(hsa.personal.id)
        }
        JsonBuilder builder = new JsonBuilder(hsa)
        return builder.toString()
    }

}
    


