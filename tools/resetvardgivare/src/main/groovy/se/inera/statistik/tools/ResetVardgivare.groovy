package se.inera.statistik.tools

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovy.sql.Sql
import org.apache.commons.dbcp2.BasicDataSource

import java.util.concurrent.atomic.AtomicInteger

class ResetVardgivare {

    static void main(String[] args) {
        println "Starting reset"
        
        int numberOfThreads = args.length > 0 ? args[0] : 5
        long start = System.currentTimeMillis()

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

        def hsaIds = bootstrapSql.rows("select id from hsa")
        println "${hsaIds.size()} hsa found to reset"
        bootstrapSql.close()
        final AtomicInteger count = new AtomicInteger(0)
        final AtomicInteger errorCount = new AtomicInteger(0)
        def output

        println "Proceeding to hsa"
        count.set(0)
        errorCount.set(0)
        start = System.currentTimeMillis()

        output = hsaIds.collect {
            StringBuffer result = new StringBuffer()
            Sql sql = new Sql(dataSource)
            def id = it.id
            try {
                def intyg = sql.firstRow( 'select data from hsa where id = :id' , [id : id])
                String jsonDoc = intyg.data
                String resetHsaJson = resetHsaJson(jsonDoc)
                sql.executeUpdate('update hsa set data = :document where id = :id',
                        [document: resetHsaJson, id: id])
                int current = count.addAndGet(1)
                if (current % 10000 == 0) {
                    println "${current} hsa:s reset in ${(int)((System.currentTimeMillis()-start) / 1000)} seconds"
                }
            } catch (Throwable t) {
                t.printStackTrace()

                result << "Reset ${id} failed: ${t}"
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
        println "Done! ${count} hsa:s reset with ${errorCount} errors in ${(int)((end-start) / 1000)} seconds"

        Sql sql = new Sql(dataSource)
        try {
            println "Proceeding to enhet"
            sql.execute('delete from enhet')
            println "Enhet done"
            println "Proceeding to wideline"
            sql.execute('delete from wideline')
            println "Wideline done"
            println "Proceeding to handelsepekare"
            sql.execute('delete from handelsepekare')
            println "Handelsepekare done"
        } finally {
            sql.close()
        }

    }

    static def resetHsaJson(def s) {
        def hsa = new JsonSlurper().parseText(s)
        hsa.remove('vardgivare')
        JsonBuilder builder = new JsonBuilder(hsa)
        return builder.toString()
    }

}
    


