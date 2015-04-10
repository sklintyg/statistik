package se.inera.statistik.tools

import groovy.sql.Sql

import org.apache.commons.dbcp2.BasicDataSource
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class UpdateEnhetNamnFromHsaFileService {
    private static final Logger LOG = LoggerFactory.getLogger(UpdateEnhetNamnFromHsaFileService.class);

    static void main(String[] args) {
        long start = System.currentTimeMillis()
        def props = new Properties()
        new File("dataSource.properties").withInputStream { stream ->
            props.load(stream)
        }
        def config = new ConfigSlurper().parse(props)
        BasicDataSource dataSource =
            new BasicDataSource(driverClassName: config.dataSource.driver, url: config.dataSource.url,
                                username: config.dataSource.username, password: config.dataSource.password,
                                initialSize: 1, maxTotal: 1)

        // Read xml
        def enhetsXml = new XmlSlurper().parse(new File("enheter.xml"))
        Sql sql = new Sql(dataSource)
        def count = 0
        def totalEnheter = 0
        println(enhetsXml)
        println(enhetsXml.hsaUnits)
        enhetsXml.hsaUnits.hsaUnit.each {
            def id = it.hsaIdentity.text()
            println(id)
            def namn = it.name.text()
            println(namn)
            count += sql.executeUpdate('update enhet set namn = :namn where enhetId = :id' , [namn: namn, id : id])
            totalEnheter ++
        }
        long end = System.currentTimeMillis()
        println "$count enheter uppdated of $totalEnheter enheter in ${end-start} milliseconds"
    }

}
