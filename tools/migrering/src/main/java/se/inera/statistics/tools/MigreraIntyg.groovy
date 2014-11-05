package se.inera.statistics.tools

import groovy.json.JsonBuilder
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.sql.Sql
import groovyx.gpars.GParsPool

import java.util.concurrent.atomic.AtomicInteger

import org.apache.commons.dbcp2.BasicDataSource

class MigreraIntyg {

    static void main(String[] args) {
        int numberOfThreads = args.length > 0 ? args[0] : 5
        long start = System.currentTimeMillis()
        def props = new Properties()
        new File("dataSource.properties").withInputStream { stream ->
            props.load(stream)
        }
        def config = new ConfigSlurper().parse(props)
        BasicDataSource dataSource =
            new BasicDataSource(driverClassName: config.dataSource.driver, url: config.dataSource.url,
                                username: config.dataSource.username, password: config.dataSource.password,
                                initialSize: numberOfThreads, maxTotal: numberOfThreads)
        def bootstrapSql = new Sql(dataSource)
        def allaIntyg = bootstrapSql.rows("select correlationId from intyghandelse")
        bootstrapSql.close()
        final AtomicInteger count = new AtomicInteger(0)
        def output
        GParsPool.withPool(numberOfThreads) {
            output = allaIntyg.collectParallel {
                StringBuffer result = new StringBuffer() 
                def id = it.correlationId
                Sql sql = new Sql(dataSource)
                def row = sql.firstRow( 'select data from intyghandelse where correlationId = :id' , [id : id])
                def document = new JsonSlurper().parseText(row.data)
                // Rename 'start' and 'end' attributes of vardkontaktstid to 'from' and 'tom'
                // to make them consistent with other date intervals
                def vardkontakter = document.vardkontakter
                boolean jsonUpdated = false
                vardkontakter.each {vardkontakt ->
                    if (vardkontakt.vardkontaktstid.start && vardkontakt.vardkontaktstid.end) {
                        vardkontakt.vardkontaktstid.from = vardkontakt.vardkontaktstid.start
                        vardkontakt.vardkontaktstid.remove('start')
                        vardkontakt.vardkontaktstid.tom = vardkontakt.vardkontaktstid.end
                        vardkontakt.vardkontaktstid.remove('end')
                        jsonUpdated = true
                    } else {
                        result << "start/end missing in vardkontakt for certificate ${id}:"
                        result <<  JsonOutput.toJson(vardkontakt)
                    }
                }
                // If json is updated, convert back to string
                def updatedDocument = jsonUpdated ? new JsonBuilder(document).toString() : null
                
                if (updatedDocument) {
                    sql.execute('update intyghandelse set data = :document where correlationId = :id' , [document: updatedDocument, id : id])
                } else {
                    result << "$id lacks start/end attributes - not updated"
                }
                sql.close()
                int current = count.addAndGet(1)
                if (current % 10000 == 0) {
                    println current
                }
                result.toString()
            }
        }
        long end = System.currentTimeMillis()
        output.each {line ->
            if (line) println line
        }
        println "$count certificates uppdated in ${end-start} milliseconds"
    }

}
