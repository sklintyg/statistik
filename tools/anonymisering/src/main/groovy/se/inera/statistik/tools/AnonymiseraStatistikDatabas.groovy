package se.inera.statistik.tools

import groovy.sql.Sql
import org.apache.commons.dbcp2.BasicDataSource
import se.inera.statistik.tools.anonymisering.base.*

class AnonymiseraStatistikDatabas {

    static void main(String[] args) {
        println ">>>>"
        println "Starting anonymization..."

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

        // Anonymisera Intyg
        AnonymiseraIntyg anonymiseraIntyg = new AnonymiseraIntyg(dataSource);
        anonymiseraIntyg.anonymize(numberOfThreads, anonymiseraJson, anonymiseraXml);

        println "Proceeding to wideline"
        def sql = new Sql(dataSource)
        try {
            sql.execute('DELETE FROM wideline')
            println "Done! Wideline emptied."
        } catch (Throwable t) {
            t.printStackTrace()
            println "Error! Check if wideline was emptied."
        } finally {
            sql.close()
        }

        println "Proceeding to handelsepekare"
        sql = new Sql(dataSource)
        try {
            sql.execute('DELETE FROM handelsepekare')
            println "Done! Handelsepekare reset."
        } catch (Throwable t) {
            t.printStackTrace()
            println "Error! Check if handelsepekare was reset."
        } finally {
            sql.close()
        }

        // Anonymisera HSA
        AnonymiseraHSA anonymiseraHSA = new AnonymiseraHSA(dataSource);
        anonymiseraHSA.anonymize(anonymiseraHsaId);

        // Anonymisera lakare
        AnonymiseraLakare anonymiseraLakare = new AnonymiseraLakare(dataSource);
        anonymiseraLakare.anonymize(anonymiseraHsaId);

        // Anonymisera meddelanden i ärenedkommunikationen
        AnonymiseraMeddelandehandelser anonymiseraMeddelanden = new AnonymiseraMeddelandehandelser(dataSource);
        anonymiseraMeddelanden.anonymize(numberOfThreads, anonymiseraXml);

        println "...ending anonymization after ${(int)((System.currentTimeMillis()-start) / 1000)} seconds"
        println "<<<<"
    }

}



