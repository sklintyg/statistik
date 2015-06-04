package se.inera.statistik.tools

import groovy.sql.Sql
import groovyx.gpars.GParsPool
import org.apache.commons.dbcp2.BasicDataSource

import java.util.concurrent.atomic.AtomicInteger

class MigreraIntyg {

    static void main(String[] args) {
        int numberOfThreads = args.length > 0 ? Integer.parseInt(args[0]) : 5
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
        BasicDataSource dataSourceIntyg =
                new BasicDataSource(driverClassName: config.dataSourceIntyg.driver, url: config.dataSourceIntyg.url,
                        username: config.dataSourceIntyg.username, password: config.dataSourceIntyg.password,
                        initialSize: numberOfThreads, maxTotal: numberOfThreads)
        def bootstrapSql = new Sql(dataSource)
        def allaIntyg = bootstrapSql.rows("select id from intyghandelse")
        bootstrapSql.close()
        final AtomicInteger count = new AtomicInteger(0)
        def output
        GParsPool.withPool(numberOfThreads) {
            output = allaIntyg.collectParallel {
                StringBuffer result = new StringBuffer()
                def id = it.id
                Sql sql = new Sql(dataSource)
                Sql intygSql = new Sql(dataSourceIntyg)
                def row = sql.firstRow( 'select correlationId from intyghandelse where id = :id' , [id : id])
                def correlationId = row.correlationId
                def intygRow = intygSql.firstRow( 'SELECT document FROM CERTIFICATE WHERE ID = :id' , [id : correlationId])
                def document
                if (intygRow != null) {
                    document = new String(intygRow.document, "UTF-8")
                } else {
                    document = ''
                    println "Missing source document: " + correlationId
                }

                sql.execute('update intyghandelse set data = :document where id = :id' , [document: document, id : id])
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
