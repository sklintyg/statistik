package se.inera.statistik.tools

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovy.sql.Sql
import se.inera.statistik.tools.anonymisering.base.AnonymiseraXml

import groovyx.gpars.GParsPool
import org.apache.commons.dbcp2.BasicDataSource
import se.inera.statistik.tools.anonymisering.base.AnonymiseraDatum
import se.inera.statistik.tools.anonymisering.base.AnonymiseraHsaId
import se.inera.statistik.tools.anonymisering.base.AnonymiseraJson
import se.inera.statistik.tools.anonymisering.base.AnonymiseraPersonId
import se.inera.statistik.tools.anonymisering.base.AnonymizeString

import java.util.concurrent.atomic.AtomicInteger

class AnonymiseraStatistikDatabas {

    static void main(String[] args) {
        println "Starting anonymization"
        
        int numberOfThreads = args.length > 0 ? args[0] : 5
        long start = System.currentTimeMillis()
        AnonymiseraPersonId anonymiseraPersonId = new AnonymiseraPersonId()
        AnonymiseraHsaId anonymiseraHsaId = new AnonymiseraHsaId()
        AnonymiseraDatum anonymiseraDatum = new AnonymiseraDatum()
        AnonymiseraJson anonymiseraJson = new AnonymiseraJson(anonymiseraHsaId, anonymiseraDatum, anonymiseraPersonId)
        AnonymiseraXml anonymiseraXml = new AnonymiseraXml(anonymiseraPersonId, anonymiseraHsaId, anonymiseraDatum)
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
        bootstrapSql.close()
        final AtomicInteger count = new AtomicInteger(0)
        final AtomicInteger errorCount = new AtomicInteger(0)
        def output
        GParsPool.withPool(numberOfThreads) {
            output = certificateIds.collectParallel {
                StringBuffer result = new StringBuffer()
                def id = it.correlationId

                println "Correlation ID: $id"
                Sql sql = new Sql(dataSource)
                try {
                    def intyg = sql.firstRow( 'select data from intyghandelse where correlationId = :id' , [id : id])
                    try {

                        String jsonDoc = intyg.data
                        String anonymiseradJson = anonymiseraJson.anonymiseraIntygsJson(jsonDoc)
                        sql.executeUpdate('update intyghandelse set data = :document where correlationId = :id',
                                              [document: anonymiseradJson, id: id])
                    } catch (Exception ignored) {
                        try {
                            String xmlDoc = intyg?.data
                            String anonymiseradXml = xmlDoc ? anonymiseraXml.anonymiseraIntygsXml(xmlDoc) : null
                            if (anonymiseradXml) sql.executeUpdate('update intyghandelse set data = :document where correlationId = :id',
                                    [document: anonymiseradXml, id: id])

                        } catch (Exception ignore) {
                            println "Failed to parse intyg ${id}"
                        }
                    }

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
        certificateIds = null; //Free up some memory
        long end = System.currentTimeMillis()
        output.each {line ->
            if (line) println line
        }
        println "Done! ${count} certificates anonymized with ${errorCount} errors in ${(int)((end-start) / 1000)} seconds"
        println "Proceeding to wideline"
        Sql sql = new Sql(dataSource)
        try {
            sql.execute('delete from wideline')
            println "Done! Wideline emptied."
        } catch (Throwable t) {
            t.printStackTrace()
            println "Error! Check if wideline was emptied."
        } finally {
            sql.close()
        }
        println "Done! Wideline emptied."
        println "Proceeding to handelsepekare"
        sql = new Sql(dataSource)
        try {
            sql.execute('delete from handelsepekare')
            println "Done! Handelsepekare reser."
        } catch (Throwable t) {
            t.printStackTrace()
            println "Error! Check if handelsepekare was reset."
        } finally {
            sql.close()
        }

        println "Proceeding to hsa"
        count.set(0)
        errorCount.set(0)
        start = System.currentTimeMillis()

        sql = new Sql(dataSource)
        def hsaIds = sql.rows("select id from hsa")
        println "${hsaIds.size()} hsa found to anonymize"
        sql.close()

        output = hsaIds.collect {
            StringBuffer result = new StringBuffer()
            sql = new Sql(dataSource)
            def id = it.id
            try {
                def intyg = sql.firstRow( 'select data from hsa where id = :id' , [id : id])
                String jsonDoc = intyg.data
                String anonymiseradJson = anonymizeHsaJson(jsonDoc, anonymiseraHsaId)
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

        end = System.currentTimeMillis()
        output.each {line ->
            if (line) println line
        }
        println "Done! ${count} hsa:s anonymized with ${errorCount} errors in ${(int)((end-start) / 1000)} seconds"


        println "Proceeding to lakare"

        hsaIds = bootstrapSql.rows("select id from lakare")
        println "${hsaIds.size()} lakare found to anonymize"

        count.set(0)
        errorCount.set(0)
        start = System.currentTimeMillis()

        output = hsaIds.collect {
            StringBuffer result = new StringBuffer()
            sql = new Sql(dataSource)
            def id = it.id
            try {
                def lakare = sql.firstRow( 'select lakareid, tilltalsnamn, efternamn from lakare where id = :id' , [id : id])
                def lakareid = anonymiseraHsaId.anonymisera(lakare.lakareid)
                def tilltalsnamn = AnonymizeString.anonymize(lakare.tilltalsnamn)
                def efternamn = AnonymizeString.anonymize(lakare.efternamn)
                sql.executeUpdate('update lakare set lakareid = :lakareid, tilltalsnamn = :tilltalsnamn, efternamn = :efternamn where id = :id',
                        [lakareid: lakareid, tilltalsnamn: tilltalsnamn, efternamn: efternamn, id: id])
                int current = count.addAndGet(1)
                if (current % 10000 == 0) {
                    println "${current} lakare anonymized in ${(int)((System.currentTimeMillis()-start) / 1000)} seconds"
                }
            } catch (Throwable t) {
                t.printStackTrace()

                result << "Lakare anonymizing ${id} failed: ${t}"
                errorCount.incrementAndGet()
            } finally {
                sql.close()
            }
            result.toString()
        }

        end = System.currentTimeMillis()
        output.each {line ->
            if (line) println line
        }
        println "Done! ${count} lakare anonymized with ${errorCount} errors in ${(int)((end-start) / 1000)} seconds"


    }

    static def anonymizeHsaJson(def s, def anonymiseraHsaId) {
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
    


