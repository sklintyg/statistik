/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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
import se.inera.statistik.tools.anonymisering.base.AnonymiseraXml

import java.util.concurrent.atomic.AtomicInteger

class AnonymiseraIntyg {

    private final int DEFAULT_NUMBEROFTHREADS = 5;

    private BasicDataSource dataSource;

    AnonymiseraIntyg(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    void anonymize(int numberOfThreads, AnonymiseraJson anonymiseraJson, AnonymiseraXml anonymiseraXml) {
        numberOfThreads = numberOfThreads > 0 ? numberOfThreads : DEFAULT_NUMBEROFTHREADS

        final AtomicInteger count = new AtomicInteger(0)
        final AtomicInteger errorCount = new AtomicInteger(0)

        println "Start anonymization of intyg"
        long start = System.currentTimeMillis()

        def query = '''
        SELECT correlationId 
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
                def sql = new Sql(dataSource)
                def id = it.correlationId
                println "correlationId: $id"
                try {
                    def intyg = sql.firstRow('SELECT data FROM intyghandelse WHERE correlationId = :id' , [id : id])
                    try {
                        String jsonDoc = intyg.data
                        String anonymiseradJson = anonymiseraJson.anonymiseraIntygsJson(jsonDoc)
                        sql.executeUpdate('UPDATE intyghandelse SET data = :document WHERE correlationId = :id',
                            [document: anonymiseradJson, id: id])

                    } catch (Exception ignored) {
                        try {
                            String xmlDoc = intyg?.data
                            String anonymiseradXml = xmlDoc ? anonymiseraXml.anonymiseraIntygsXml(xmlDoc) : null
                            if (anonymiseradXml) {
                                sql.executeUpdate('UPDATE intyghandelse SET data = :document WHERE correlationId = :id',
                                    [document: anonymiseradXml, id: id])
                            }

                        } catch (Exception ignore) {
                            println "Failed to parse intyg ${id}"
                        }
                    }

                    int current = count.addAndGet(1)
                    if (current % 10000 == 0) {
                        println "${current} intyg anonymized in ${(int)((System.currentTimeMillis()-start) / 1000)} seconds"
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
        output.each {line ->
            if (line) println line
        }

        println "Done! ${count} intyg anonymized with ${errorCount} errors in ${(int)((end-start) / 1000)} seconds"
    }

}



