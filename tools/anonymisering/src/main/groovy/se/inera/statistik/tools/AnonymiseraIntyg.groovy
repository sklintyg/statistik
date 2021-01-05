/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistik.tools

import groovy.sql.Sql
import groovyx.gpars.GParsPool
import org.apache.commons.dbcp2.BasicDataSource
import se.inera.statistik.tools.anonymisering.base.AnonymiseraJson
import se.inera.statistik.tools.anonymisering.base.AnonymiseraTsBas
import se.inera.statistik.tools.anonymisering.base.AnonymiseraTsDiabetes
import se.inera.statistik.tools.anonymisering.base.AnonymiseraXml

import java.util.concurrent.atomic.AtomicInteger

class AnonymiseraIntyg {

    private final int DEFAULT_NUMBEROFTHREADS = 5;

    private BasicDataSource dataSource
    private AnonymiseraJson anonymiseraJson
    private AnonymiseraXml anonymiseraXml
    private AnonymiseraTsBas anonymiseraTsBas
    private AnonymiseraTsDiabetes anonymiseraTsDiabetes

    AnonymiseraIntyg(BasicDataSource dataSource, AnonymiseraJson anonymiseraJson, AnonymiseraXml anonymiseraXml,
                     AnonymiseraTsBas anonymiseraTsBas, AnonymiseraTsDiabetes anonymiseraTsDiabetes) {
        this.dataSource = dataSource
        this.anonymiseraJson = anonymiseraJson
        this.anonymiseraXml = anonymiseraXml
        this.anonymiseraTsBas = anonymiseraTsBas
        this.anonymiseraTsDiabetes = anonymiseraTsDiabetes
    }

    void anonymize(int numberOfThreads) {
        numberOfThreads = numberOfThreads > 0 ? numberOfThreads : DEFAULT_NUMBEROFTHREADS

        final AtomicInteger count = new AtomicInteger(0)
        final AtomicInteger errorCount = new AtomicInteger(0)

        println "Start anonymization of intyg"
        long start = System.currentTimeMillis()

        def query = '''
        SELECT id 
        FROM intyghandelse
        '''

        def bootStrapsql = new Sql(dataSource)
        def certificateIds = bootStrapsql.rows(query)
        println "${certificateIds.size()} intyg found"
        bootStrapsql.close()

        def output
        GParsPool.withPool(numberOfThreads) {
            output = certificateIds.collectParallel {
                StringBuffer result = new StringBuffer()
                Sql sql = new Sql(dataSource)
                def id = it.id
                println "id: $id"
                try {
                    def intyg = sql.firstRow('SELECT data FROM intyghandelse WHERE id = :id', [id: id])

                    try {
                        String intygData = intyg?.data

                        String anonymizedIntyg = anonymizeIntyg(intygData)

                        updateDatabase(sql, anonymizedIntyg, id)

                    } catch (Exception ignored) {
                        println "Failed to parse intyg with id: ${id}"
                    }

                    int current = count.addAndGet(1)
                    if (current % 10000 == 0) {
                        println "${current} intyg anonymized in ${(int) ((System.currentTimeMillis() - start) / 1000)} seconds"
                    }
                } catch (Throwable t) {
                    t.printStackTrace()

                    result << "Anonymization of intyg ${id} failed: ${t}"
                    errorCount.incrementAndGet()
                } finally {
                    sql.close()
                }
                result.toString()
            }
        }

        long end = System.currentTimeMillis()
        output.each { line ->
            if (line) println line
        }

        println "Done! ${count} intyg anonymized with ${errorCount} errors in ${(int) ((end - start) / 1000)} seconds"
    }

    String anonymizeIntyg(String intygData) {
        if ((intygData ==~ /(?s)^.*<[^>]*RegisterCertificate.*>.*$/)) {
            return intygData ? anonymiseraXml.anonymiseraIntygsXml(intygData) : null
        } else if ((intygData ==~ /(?s)^.*<[^>]*RegisterTSBas.*>.*$/)) {
            return intygData ? anonymiseraTsBas.anonymisera(intygData) : null
        } else if ((intygData ==~ /(?s)^.*<[^>]*RegisterTSDiabetes.*>.*$/)) {
            return intygData ? anonymiseraTsDiabetes.anonymisera(intygData) : null
        } else {
            return anonymiseraJson.anonymiseraIntygsJson(intygData)
        }
    }

    void updateDatabase(sql, document, id) {
        if (document) {
            sql.executeUpdate('UPDATE intyghandelse SET data = :document WHERE id = :id',
                    [document: document, id: id])
        }
    }

}



